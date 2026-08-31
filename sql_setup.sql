CREATE DATABASE ebookreader;
USE ebookreader;
CREATE TABLE users(
id BIGINT AUTO_INCREMENT,
name varchar(50) NOT NULL,
email varchar(255) NOT NULL,
password varchar(50) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    pdf_name VARCHAR(50) NOT NULL,
    audio_name VARCHAR(50) NOT NULL,
    cover_name VARCHAR(50) NOT NULL,
    audio_length BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE recently_listened(
id BIGINT AUTO_INCREMENT,
book_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
timestamp BIGINT NOT NULL,
date datetime NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (user_id) REFERENCES users(id),
FOREIGN KEY (book_id) REFERENCES books(id)
);


CREATE TABLE genres(
id BIGINT AUTO_INCREMENT,
name VARCHAR(50),
PRIMARY KEY (id)
);


CREATE TABLE book_genres(
id BIGINT AUTO_INCREMENT,
genre_id BIGINT,
book_id BIGINT,
PRIMARY KEY (id),
FOREIGN KEY (genre_id) REFERENCES genres(id),
FOREIGN KEY (book_id) REFERENCES books(id)
);