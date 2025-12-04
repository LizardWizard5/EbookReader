CREATE DATABASE ebookReader;
USE ebookReader;
CREATE TABLE people(
id int AUTO_INCREMENT,
name varchar(50) NOT NULL,
password varchar(50),
PRIMARY KEY (id)
);

CREATE TABLE books(
id int AUTO_INCREMENT,
name varchar(100) NOT NULL,
author varchar(100) NOT NULL,
description varchar (500),
pdfName varchar(50) NOT NULL,
audioName varchar (50) NOT NULL,
coverName varchar (50) NOT NULL,
PRIMARY KEY (id)
);