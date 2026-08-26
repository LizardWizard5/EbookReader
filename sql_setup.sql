CREATE DATABASE ebookreader;
USE ebookreader;
CREATE TABLE user(
id int AUTO_INCREMENT,
name varchar(50) NOT NULL,
email varchar(255) NOT NULL,
password varchar(50) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    pdf_name VARCHAR(50) NOT NULL,
    audio_name VARCHAR(50) NOT NULL,
    cover_name VARCHAR(50) NOT NULL,
    audio_length INT NOT NULL,
    user_id int NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);
CREATE TABLE recently_listened(
id int AUTO_INCREMENT,
book_id int NOT NULL,
user_id int NOT NULL,
timestamp int NOT NULL,
date datetime NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (user_id) REFERENCES user(id),
FOREIGN KEY (book_id) REFERENCES books(id)
);


CREATE TABLE genres(
id int AUTO_INCREMENT,
genre VARCHAR(50),
PRIMARY KEY (id)
);


CREATE TABLE book_genres(
id int AUTO_INCREMENT,
genre_id int,
book_id int,
PRIMARY KEY (id),
FOREIGN KEY (genre_id) REFERENCES genres(id),
FOREIGN KEY (book_id) REFERENCES books(id)
);