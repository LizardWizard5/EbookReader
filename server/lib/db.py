

#Handles all mySQL connection related stuff.
import mysql.connector
import json
from flask import jsonify

class connection:
    def __init__(self,host, user, password):
        try:
            self.conn = mysql.connector.connect(
                host=host,
                user=user,
                password=password,
                database="ebookreader"
            )
            self.cursor = self.conn.cursor(dictionary=True)
        except mysql.connector.Error as e:
            print(f"Error connecting to database: {e}")
            # Option 1: mark as failed but keep object
            self.conn = None
            self.cursor = None
        
    def getBookInfo(self, book_id):
        self.cursor.execute("SELECT * FROM books WHERE id = %s;", (book_id,))
        res = self.cursor.fetchone()
        return res

    def getBookPaths(self, book_id):
        pass
    def getAllBooks(self):
        self.cursor.execute("SELECT * FROM books;")
        res = self.cursor.fetchall()
        return res

    def createBookEntry(self, book_name, author_name, pdf_name, audio_name,audio_length,cover_name=""):

        self.cursor.execute("INSERT INTO books (name, author, pdfName, audioName,coverName,audioLength) VALUES (%s, %s, %s, %s,%s,%s)", ( book_name, author_name, pdf_name, audio_name,cover_name,audio_length))
        self.conn.commit()
    #Removed until user system is implemented
    #def getUserBooks(self, user_id):
    #    self.cursor.execute("SELECT * FROM books WHERE user_id = %s;", (user_id,))
    #    res = self.cursor.fetchall()
    #    return res
    def createUserEntry(self, user_name, password):
        pass

    