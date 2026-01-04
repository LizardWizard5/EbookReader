#Testing mysql connection
from lib.db import connection
import os
from dotenv import load_dotenv
from flask import jsonify
import wave
import contextlib
load_dotenv()  # reads variables from a .env file and sets them in os.environ

audioDirectory = os.getcwd() + "\\books\\audio"


db = connection(host=os.getenv("HOST"), user=os.getenv("USER"), password=os.getenv("PASSWORD"))
print("Test.py passed db connection")
#db.createBookEntry("Test Book","Test Author","test_book.pdf","test_book.mp3")
#Testing complete  
db.getAllBooks()
print("Passed Get all books")
#audioName = lib.ParseGenerate.grabAndGenerate("The-Great-Gatsby")
audioName = "7f70b24a-1398-43f4-8361-3a71dff77382"
with contextlib.closing(wave.open(audioDirectory + "\\" + audioName + '.wav','r')) as f:
    frames = f.getnframes()
    rate = f.getframerate()
    duration = (frames / float(rate))*1_000_000  #in microseconds
    print(duration)
#createBookEntry(self, book_name, author_name, pdf_name, audio_name,audio_length,cover_name="")
db.createBookEntry("The Great Gatsby","F. Scott Fitzgerald","The-Great-Gatsby",audioName,duration,"ggatsby.png")   