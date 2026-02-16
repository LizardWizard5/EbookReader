#This file will handle API calls utilizing flask
#A electron frontend will be used to interact with it. (This was not true.)
import os
import re
import uuid
import threading
from flask import Flask, jsonify, Response, Request, make_response, send_file, stream_with_context,request
import flask
from lib.db import connection
from lib.AudioManager import getRange
import lib.ParseGenerate

#Windows + Linux compatible path handling
currentDirectory = os.getcwd()
bookDirectory = currentDirectory + "\\books\\pdf"
audioDirectory = currentDirectory + "\\books\\audio"
coverDirectory = currentDirectory + "\\books\\covers"

#Initialize mySQL connection here
db = connection(host=os.getenv("HOST"), user=os.getenv("USER"), password=os.getenv("PASSWORD"))

def process_book(fileName, title, author, cover_saved):
    try:
        lib.ParseGenerate.grabAndGenerate(fileName)
        cover_name = f"{fileName}.jpg" if cover_saved else ""
        db.createBookEntry(title, author, f"{fileName}.pdf", f"{fileName}.mp3", 0, cover_name)
    except Exception as e:
        print(f"Error processing book {fileName}: {e}")

app = Flask(__name__)
BITRATE_KBPS = 64
BYTES_PER_SEC = (BITRATE_KBPS * 1000) // 8
#Unimplemented for now.
@app.route('/stream/<book_id>')
def stream_audio(book_id):
    book = db.getBookInfo(book_id)
    path = f"{audioDirectory}/{book['audioName']}"
    
    if not os.path.exists(path):
        return "Not Found", 404

    file_size = os.path.getsize(path)
    range_header = request.headers.get('Range')

    # --- PART A: THE HANDSHAKE (No Range Header) ---
    if not range_header:
        print("DEBUG: Initial Handshake - Sending Accept-Ranges")
        
        def full_file_stream():
            with open(path, 'rb') as f:
                while chunk := f.read(64 * 1024):
                    yield chunk

        res = Response(full_file_stream(), status=200, mimetype='audio/mpeg')
        res.headers['Accept-Ranges'] = 'bytes'
        res.headers['Content-Length'] = str(file_size)
        return res

    # --- PART B: THE SEEK (Range Header Present) ---
    print(f"DEBUG: Seek Requested! Header: {range_header}")
    match = re.search(r'bytes=(\d+)-(\d*)', range_header)
    if match:
        start = int(match.group(1))
        end = int(match.group(2)) if match.group(2) else file_size - 1
        end = min(end, file_size - 1)
        chunk_len = (end - start) + 1

        def generate_range():
            with open(path, 'rb') as f:
                f.seek(start)
                remaining = chunk_len
                while remaining > 0:
                    data = f.read(min(remaining, 64 * 1024))
                    if not data: break
                    yield data
                    remaining -= len(data)

        res = Response(generate_range(), status=206, mimetype='audio/mpeg')
        res.headers['Content-Range'] = f'bytes {start}-{end}/{file_size}'
        res.headers['Content-Length'] = str(chunk_len)
        res.headers['Accept-Ranges'] = 'bytes'
        return res

    return "Invalid Range", 416
@app.route("/users")
def users_api():
    return {"msg":"Hello World!"}
@app.route("/books/<book_id>")
def bookInfo_api(book_id):
    book = db.getBookInfo(book_id)
    return jsonify(book)
@app.route("/books")
def books_api():
    books = db.getAllBooks()
    return jsonify(books)

@app.route("/books/<book_id>/cover")
def book_cover_api(book_id):
    cover = db.getBookInfo(book_id)['coverName']
    #Get image file and return it
    coverImage = open(f'{currentDirectory}\\books\\covers\\{cover}', 'rb')
    return Response(coverImage, mimetype='image/jpeg')

@app.route("/books/<book_id>/audio")
def book_audio_api(book_id):
    audio = db.getBookInfo(book_id)['audioName']
    #Get audio file and return it
    audioFile = open(f'{audioDirectory}\\{audio}', 'rb')
    return Response(audioFile, mimetype='audio/wav')
@app.route("/books/<book_id>/pdf")
def book_pdf_api(book_id):
    pdf = db.getBookInfo(book_id)['pdfName']
    #Get pdf file and return it
    pdfFile = open(f'{bookDirectory}\\{pdf}', 'rb')
    return Response(pdfFile, mimetype='application/pdf')

#Books by a user id commented out until user system is implemented
"""
@app.route("/books/<user_id>")
def user_books_api(user_id):
    books = db.getUserBooks(user_id)
    return jsonify(books)
"""
#POST request to upload pdf file for parsing and audio generation
@app.route("/upload", methods=["POST"])
def upload_pdf():
    #Print post request data for debugging
    try:
        print(request.form)
        print(request.files)
        #Save pdf file to books/pdf directory
        file = request.files['pdf']
        fileName = str(uuid.uuid4())#Generate random uuid for file name to avoid collisions
        file.save(f"{bookDirectory}\\{fileName}.pdf")
        #Save book cover if it exists
        cover_saved = 'cover' in request.files
        if cover_saved: 
            cover = request.files['cover']
            cover.save(f"{coverDirectory}\\{fileName}.jpg")
        #Start background processing
        threading.Thread(target=process_book, args=(fileName, request.form['title'], request.form['author'], cover_saved)).start()

        return Response("Upload accepted, processing in background", status=200)
    except Exception as e:
        print(f"Error in upload endpoint: {e}")
        return Response("Error processing upload, please ensure all fields are filled appropriately", status=500)


if __name__ == "__main__":
    app.run(debug=True)

