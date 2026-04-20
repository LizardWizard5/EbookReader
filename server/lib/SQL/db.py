#Handles all mySQL connection related stuff.
from datetime import datetime, time

import mysql.connector
from mysql.connector import pooling


db_pool = None

def init_db_pool(host, user, password,pool_size):
    global db_pool
    db_pool = pooling.MySQLConnectionPool(
        pool_name="ebookreader_pool",
        pool_size=pool_size,  
        pool_reset_session=True,
        host=host,
        user=user,
        password=password,
        database="ebookreader"
    )

class connection:
    def __init__(self,host, user, password,pool_size=5):
        # Initialize pool if not already done
        if db_pool is None and host:
            init_db_pool(host, user, password,pool_size)
        
    def getBookInfo(self, book_id):
        conn = db_pool.get_connection()
        try:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT * FROM books WHERE id = %s;", (book_id,))
            res = cursor.fetchone()
            cursor.close()
            return res
        finally:
            conn.close()

    def getBookPaths(self, book_id):
        pass
    def getAllBooks(self):
        conn = db_pool.get_connection()
        try:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT * FROM books;")
            res = cursor.fetchall()
            cursor.close()
            return res
        finally:
            conn.close()

    def createBookEntry(self, book_name, author_name, pdf_name, audio_name,audio_length,cover_name=""):

        conn = db_pool.get_connection()
        try:
            cursor = conn.cursor()
            cursor.execute("INSERT INTO books (name, author, pdfName, audioName,coverName,audioLength) VALUES (%s, %s, %s, %s,%s,%s)", ( book_name, author_name, pdf_name, audio_name,cover_name,audio_length))
            conn.commit()
            cursor.close()
        finally:
            conn.close()
    #Removed until user system is implemented
    #def getUserBooks(self, user_id):
    #    self.cursor.execute("SELECT * FROM books WHERE user_id = %s;", (user_id,))
    #    res = self.cursor.fetchall()
    #    return res
    def createUserEntry(self, user_name, password):
        pass

    def getRecentlyListened(self):
        conn = db_pool.get_connection()
        try:
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT * FROM recently_listened ORDER BY last_listened_date DESC;")
            res = cursor.fetchall()
            cursor.close()
            return res
        finally:
            conn.close()

    def updateRecentlyListened(self, book_id):
        conn = db_pool.get_connection()
        try:
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM recently_listened WHERE book_id = %s;", (book_id,))
            existing_entry = cursor.fetchone()
            current_timestamp = int(time.time())
            current_datetime = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            if existing_entry:
                cursor.execute("UPDATE recently_listened SET timestamp = %s, last_listened_date = %s WHERE book_id = %s;", (current_timestamp, current_datetime, book_id))
            else:
                cursor.execute("INSERT INTO recently_listened (book_id, timestamp, last_listened_date) VALUES (%s, %s, %s);", (book_id, current_timestamp, current_datetime))
            conn.commit()
            cursor.close()
        finally:
            conn.close()

    