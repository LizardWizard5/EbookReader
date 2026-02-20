#This file will handle API calls utilizing flask
#A electron frontend will be used to interact with it. (This was not true.)
from concurrent.futures import ThreadPoolExecutor
import os
import re
import uuid
from flask_cors import CORS
from flask import Flask, jsonify, Response,request
from lib.SQL.db import connection

import lib.ParseGenerate

#Windows + Linux compatible path handling
currentDirectory = os.getcwd()
baseBookDirectory= os.path.join(currentDirectory, "books")
bookDirectory= os.path.join(currentDirectory, "books", "pdf")
audioDirectory = os.path.join(currentDirectory, "books", "audio")
coverDirectory = os.path.join(currentDirectory, "books", "covers")

#Initialize mySQL connection here
#Adjust pool size as needed, it is used to handle multiple requests at once. 5 is good for a self hosted system
if not os.getenv("HOST") or not os.getenv("USER") or not os.getenv("PASSWORD"):
    print("Database credentials not found in environment variables. Please set HOST, USER, and PASSWORD.")
    exit(1)
db = connection(host=os.getenv("HOST"), user=os.getenv("USER"), password=os.getenv("PASSWORD"),pool_size=5)

#Setup cors and flask app

#Pulls ip list from allowedips.txt and returns the list for CORS setup.
def load_cors_whitelist():
    if not os.path.exists('allowedips.txt'):
        print("allowedips.txt not found, server will continue with only allowing localhost")
        return ["http://localhost"]
    whitelist = []
    try:
        with open('allowedips.txt', 'r') as f:
            for line in f:
                if line.startswith("#") or not line.strip():
                    continue  # Skip comments and empty lines
                ip = line.strip()
                if ip:
                    whitelist.append(f"http://{ip}")
    except Exception as e:
        print(f"Error loading CORS whitelist: {e}")
    return whitelist

app = Flask(__name__)
print(f"CORS whitelist added: {len(load_cors_whitelist())} entries")
CORS(app, origins=load_cors_whitelist(), allow_headers=["Content-Type"])

#Setup threading so that server can handle multiple uploads at once without locking up.
executor = ThreadPoolExecutor(max_workers=5)  # Create once at startup
def process_book(fileName, title, author, cover_saved):
    try:
        lib.ParseGenerate.grabAndGenerate(fileName)
        cover_name = f"{fileName}.jpg" if cover_saved else ""
        db.createBookEntry(title, author, f"{fileName}.pdf", f"{fileName}.mp3", 0, cover_name)
    except Exception as e:
        print(f"Error processing book {fileName}: {e}")

BITRATE_KBPS = 64
BYTES_PER_SEC = (BITRATE_KBPS * 1000) // 8

@app.route('/stream/<book_id>')
def stream_audio(book_id):
    book = db.getBookInfo(book_id)
    try:
        path = os.path.join(audioDirectory, book['audioName'])
    except Exception as e:
        print(f"Error retrieving book info for streaming: {e}")
        return "Not Found", 404


    file_size = os.path.getsize(path)
    range_header = request.headers.get('Range')

    # --- PART A: THE HANDSHAKE (No Range Header) ---
    if not range_header:
        
        def full_file_stream():
            with open(path, 'rb') as f:
                while chunk := f.read(64 * 1024):
                    yield chunk

        res = Response(full_file_stream(), status=200, mimetype='audio/mpeg')
        res.headers['Accept-Ranges'] = 'bytes'
        res.headers['Content-Length'] = str(file_size)
        return res
    match = re.search(r'bytes=(\d+)-(\d*)', range_header)
    if match:
        start = int(match.group(1))
        end = int(match.group(2)) if match.group(2) else file_size - 1
        end = min(end, file_size - 1)
        chunk_len = (end - start) + 1

        def generate_range():
            try:
                with open(path, 'rb') as f:
                    f.seek(start)
                    remaining = chunk_len
                    while remaining > 0:
                        data = f.read(min(remaining, 64 * 1024))
                        if not data: break
                        yield data
                        remaining -= len(data)
            except Exception as e:
                print(f"Error streaming audio range: {e}")
                yield b"Error streaming audio"

        res = Response(generate_range(), status=206, mimetype='audio/mpeg')
        res.headers['Content-Range'] = f'bytes {start}-{end}/{file_size}'
        res.headers['Content-Length'] = str(chunk_len)
        res.headers['Accept-Ranges'] = 'bytes'
        return res

    return "Invalid Range", 416
@app.route("/books/<book_id>")
def bookInfo_api(book_id):
    book = db.getBookInfo(book_id)
    if book is None:
        return Response("{}", 200)
    return jsonify(book),200
@app.route("/books")
def books_api():
    books = db.getAllBooks()
    if books is None:
        return Response("{}", 200)
    return jsonify(books)
@app.route("/books/<book_id>/cover")
def book_cover_api(book_id):
    cover = db.getBookInfo(book_id)
    if cover is None:
        return Response("Not Found", 404)
    #Get image file and return it
    try:
        coverImage = open(os.path.join(coverDirectory, cover['coverName']), 'rb')
    except FileNotFoundError:
        coverImage = open(os.path.join(currentDirectory, "missingcover.jpg"), 'rb')
    return Response(coverImage, mimetype='image/jpeg')
@app.route("/books/<book_id>/pdf")
def book_pdf_api(book_id):
    pdf = db.getBookInfo(book_id)['pdfName']
    #Get pdf file and return it
    pdfFile = open(os.path.join(bookDirectory, pdf), 'rb')
    if pdfFile is None:
        return Response("Not Found", 404)
    return Response(pdfFile, mimetype='application/pdf')

#POST request to upload pdf file for parsing and audio generation
@app.route("/upload", methods=["POST"])
def upload_pdf():
    #Print post request data for debugging
    try:
        #Basic validation
        if 'pdf' not in request.files:
            return Response("No PDF file part in the request", status=400)
        if 'title' not in request.form or 'author' not in request.form:
            return Response("Missing title or author in the form data", status=400)
        if request.files['pdf'].filename == '':
            return Response("No selected PDF file", status=400)
        if request.files['pdf'].content_type != 'application/pdf':
            return Response("Uploaded file is not a PDF", status=400)
        if 'cover' in request.files and request.files['cover'].content_type != 'image/jpeg':
            return Response("Uploaded cover file is not a JPEG image", status=400)
        if 'cover' in request.files and request.files['cover'].filename == '':
            return Response("Cover file part is present but no file selected", status=400)
        
        #Save pdf file to books/pdf directory
        file = request.files['pdf']
        fileName = str(uuid.uuid4())#Generate random uuid for file name to avoid collisions
        file.save(os.path.join(bookDirectory, f"{fileName}.pdf"))
        #Save book cover if it exists
        cover_saved = 'cover' in request.files
        if cover_saved: 
            cover = request.files['cover']
            cover.save(os.path.join(coverDirectory, f"{fileName}.jpg"))
        #Start background processing
        executor.submit(process_book, fileName, request.form['title'], request.form['author'], cover_saved)

        return Response("Upload accepted, processing in background", status=200)
    except Exception as e:
        print(f"Error in upload endpoint: {e}")
        return Response("Error processing upload, please ensure all fields are filled appropriately", status=500)

if __name__ == "__main__":
    if os.getenv("IP") is None or os.getenv("PORT") is None:
        print("IP and PORT environment variables not set. Please set IP and PORT in the .env file.")
        exit(1)
    app.run(host=os.getenv("IP"), port=os.getenv("PORT"), debug=False)

