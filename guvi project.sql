create database smartbanksystem;
   use smartbanksystem;
CREATE TABLE logggins (card_number VARCHAR(20) PRIMARY KEY,pin VARCHAR(10),created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
   select * from logggins ;
CREATE TABLE signupp1 (form_no VARCHAR(20) PRIMARY KEY,name VARCHAR(100),fname VARCHAR(100),dob DATE,
gender VARCHAR(10),email VARCHAR(100),marital VARCHAR(20),address VARCHAR(200),city VARCHAR(50),pin VARCHAR(6),state VARCHAR(50));
select * from signupp1 ;
CREATE TABLE ssignup2 (form_no VARCHAR(20) PRIMARY KEY,religion VARCHAR(50),category VARCHAR(50),income VARCHAR(50),education VARCHAR(50),
occupation VARCHAR(50),pan VARCHAR(10),aadhar VARCHAR(12),senior_citizen VARCHAR(5),existing_account VARCHAR(5));
select * from ssignup2 ;
CREATE TABLE signuup3 (form_no VARCHAR(20) PRIMARY KEY,account_type VARCHAR(50),card_number VARCHAR(20) UNIQUE,
pin VARCHAR(10),services TEXT);
select * from signuup3 ;
CREATE TABLE bankss (id INT AUTO_INCREMENT PRIMARY KEY,pin VARCHAR(10) NOT NULL,date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
type VARCHAR(20) NOT NULL,amount DECIMAL(10, 2) NOT NULL);
select * from bankss ;
CREATE TABLE balances (card_number VARCHAR(20),balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00,PRIMARY KEY (card_number),
FOREIGN KEY (card_number) REFERENCES logggins(card_number) ON DELETE CASCADE);
select * from balances ;