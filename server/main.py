#This file will handle API calls utilizing flask
#A electron frontend will be used to interact with it.
import os
from flask import Flask, jsonify
from dotenv import load_dotenv
from lib.db import connection

load_dotenv()  # reads variables from a .env file and sets them in os.environ

#Initialize mySQL connection here
db = connection(host=os.getenv("HOST"), user=os.getenv("USER"), password=os.getenv("PASSWORD"))
    
app = Flask(__name__)

#Initialize mySQL db connection

@app.route("/users")
def users_api():
    return {"msg":"Hello World!"}
@app.route("/books")
def books_api():
    books = db.getAllBooks()
    return jsonify(books)
#Books by a user id commented out until user system is implemented
"""
@app.route("/books/<user_id>")
def user_books_api(user_id):
    books = db.getUserBooks(user_id)
    return jsonify(books)
"""
#POST request to upload pdf file
@app.route("/upload_pdf", methods=["POST"])
def upload_pdf():
    pass


if __name__ == "__main__":
    app.run(debug=True)