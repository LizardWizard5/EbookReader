#This file will handle API calls utilizing flask
#A electron frontend will be used to interact with it.
import os
from flask import Flask, jsonify, Response
from dotenv import load_dotenv
from lib.db import connection

load_dotenv()  # reads variables from a .env file and sets them in os.environ
currentDirectory = os.getcwd()
bookDirectory = currentDirectory + "\\books\\pdf"
audioDirectory = currentDirectory + "\\books\\audio"

#Initialize mySQL connection here
db = connection(host=os.getenv("HOST"), user=os.getenv("USER"), password=os.getenv("PASSWORD"))
    
app = Flask(__name__)

@app.route('/stream_audio/<book_id>')
def stream_audio(book_id):
    def generate_audio():
        bookInfo = db.getBookInfo(book_id)
        audioFileName = bookInfo['audioName']   
        print(f"Streaming audio file: {audioFileName}")
        with open(f'{audioDirectory}\\{audioFileName}.wav', 'rb') as f:
            while True:
                chunk = f.read(1024000)  # Read in 1MB chunks
                if not chunk:
                    break
                yield chunk

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