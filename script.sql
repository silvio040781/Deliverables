CREATE DATABASE PARSE_LOG

USE PARSE_LOG
 
CREATE TABLE log_informations (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    date_request DATETIME,
    ip varchar(15),
	description_request varchar(100),
    system_information varchar(500),
	comments varchar(100)
);


