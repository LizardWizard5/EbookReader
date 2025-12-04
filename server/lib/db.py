

#Handles all mySQL connection related stuff.
import mysql.connector
import json
from flask import jsonify

class connection:
    def __init__(self,host, user, password):
        self.conn = mysql.connector.connect(
            host=host,
            user=user,
            password=password,
            database="ebookreader"
        )
        self.cursor = self.conn.cursor()
        self.cursor = self.conn.cursor(dictionary=True)
        print("Database connection established.")
        print(self.conn.is_connected())
    def getBookInfo(self, book_id):
        pass
    def getBookPaths(self, book_id):
        pass
    def getAllBooks(self):
        self.cursor.execute("SELECT * FROM books;")
        res = self.cursor.fetchall()
        return res

    def createBookEntry(self, book_name, author_name, pdf_name, audio_name):
        self.cursor.execute("INSERT INTO books (name, author, pdfName, audioName) VALUES (%s, %s, %s, %s)", ( book_name, author_name, pdf_name, audio_name))
        self.conn.commit()
    #Removed until user system is implemented
    #def getUserBooks(self, user_id):
    #    self.cursor.execute("SELECT * FROM books WHERE user_id = %s;", (user_id,))
    #    res = self.cursor.fetchall()
    #    return res
    def createUserEntry(self, user_name, password):
        pass

    