#This file will handle API calls utilizing flask
#A electron frontend will be used to interact with it.
import os
from flask import Flask, jsonify, Response
from dotenv import load_dotenv
from lib.db import connection
from lib.Audio import getChunkFromMS

load_dotenv()  # reads variables from a .env file and sets them in os.environ
currentDirectory = os.getcwd()
bookDirectory = currentDirectory + "\\books\\pdf"
audioDirectory = currentDirectory + "\\books\\audio"

#Initialize mySQL connection here
db = connection(host=os.getenv("HOST"), user=os.getenv("USER"), password=os.getenv("PASSWORD"))
    
app = Flask(__name__)
"""
Returns a chunk of audio based on ms input. 
"""
@app.route('/stream_audio/<book_id>/<int:ms>')
def getChunk(book_id,ms):
    if ms < 0 or ms is None:
        ms = 0  # Ensure ms is non-negative
    def generate_audio():
        bookInfo = db.getBookInfo(book_id)
        print(bookInfo)
        audioFileName = bookInfo['audioName']   
        print(f"Streaming audio file: {audioFileName}")
        audioFilePath = f'{audioDirectory}\\{audioFileName}.wav'

        chunk = getChunkFromMS(audioFilePath, ms)
        bitrate_kbps = 1411.2  # matches getChunkFromMS
        bytes_per_ms = (bitrate_kbps * 1000) / 8 / 1000
        duration_ms = len(chunk) / bytes_per_ms
        print(f"Chunk duration: {duration_ms/1000:.2f} seconds ({duration_ms/60000:.2f} minutes)")
        yield chunk
    print(f"Generating audio chunk for book ID: {book_id} at {ms} ms")
    
    

    
    return Response(generate_audio(), mimetype='audio/wav')


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
    pass
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
    pass


if __name__ == "__main__":
    app.run(debug=True)