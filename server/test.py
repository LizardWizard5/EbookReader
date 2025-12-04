#Testing mysql connection
from lib.db import connection
import os
from dotenv import load_dotenv
from flask import jsonify
load_dotenv()  # reads variables from a .env file and sets them in os.environ

print("Connecting with details from .env file",os.getenv("HOST"),os.getenv("USER"),os.getenv("PASSWORD"))
db = connection(host=os.getenv("HOST"), user=os.getenv("USER"), password=os.getenv("PASSWORD"))
#db.createBookEntry("Test Book","Test Author","test_book.pdf","test_book.mp3")
#Testing complete  
db.getAllBooks()