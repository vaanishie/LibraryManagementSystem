-- ============================================================
-- LIBRARY MANAGEMENT SYSTEM — FULL SETUP SCRIPT
-- Run this once to create and populate the entire database.
-- ============================================================


-- ============================================================
-- 01. DATABASE
-- ============================================================
DROP DATABASE IF EXISTS library;
CREATE DATABASE library;
USE library;


-- ============================================================
-- 02. TABLES
-- ============================================================
CREATE TABLE users (
  UID INT NOT NULL AUTO_INCREMENT,
  USERNAME VARCHAR(30) NOT NULL UNIQUE,
  PASSWORD VARCHAR(100) NOT NULL,
  USER_TYPE INT NOT NULL,
  PRIMARY KEY (UID)
) ENGINE=InnoDB;

CREATE TABLE books (
  bid INT NOT NULL AUTO_INCREMENT,
  book_isbn VARCHAR(40) NOT NULL UNIQUE,
  book_name VARCHAR(50) NOT NULL,
  book_publisher VARCHAR(50) NOT NULL,
  book_edition VARCHAR(50) NOT NULL,
  book_genre VARCHAR(20) NOT NULL,
  book_price DECIMAL(10,2) NOT NULL,
  book_pages INT NOT NULL,
  PRIMARY KEY (bid)
) ENGINE=InnoDB;

CREATE TABLE issued_books (
  IID INT NOT NULL AUTO_INCREMENT,
  UID INT NOT NULL,
  BID INT NOT NULL,
  ISSUED_DATE DATE NOT NULL,
  PERIOD INT NOT NULL,
  PRIMARY KEY (IID),
  FOREIGN KEY (UID) REFERENCES users(UID),
  FOREIGN KEY (BID) REFERENCES books(bid)
) ENGINE=InnoDB;

CREATE TABLE returned_books (
  rid INT NOT NULL AUTO_INCREMENT,
  bid INT NOT NULL,
  uid INT NOT NULL,
  return_date DATE NOT NULL,
  fine INT NOT NULL DEFAULT 0,
  PRIMARY KEY (rid),
  FOREIGN KEY (uid) REFERENCES users(UID),
  FOREIGN KEY (bid) REFERENCES books(bid)
) ENGINE=InnoDB;


-- ============================================================
-- 03. INDEXES
-- ============================================================
CREATE INDEX idx_book_name ON books(book_name);
CREATE INDEX idx_isbn ON books(book_isbn);


-- ============================================================
-- 04. VIEWS
-- ============================================================
CREATE VIEW issued_books_view AS
SELECT
  i.IID,
  u.USERNAME,
  b.book_name,
  i.ISSUED_DATE,
  i.PERIOD
FROM issued_books i
JOIN users u ON i.UID = u.UID
JOIN books b ON i.BID = b.bid;


-- ============================================================
-- 05. TRIGGERS
-- ============================================================
DELIMITER //

CREATE TRIGGER calculate_fine
BEFORE INSERT ON returned_books
FOR EACH ROW
BEGIN
  DECLARE days_late INT;

  SELECT DATEDIFF(NEW.return_date, ISSUED_DATE) - PERIOD
  INTO days_late
  FROM issued_books
  WHERE BID = NEW.bid AND UID = NEW.uid
  ORDER BY ISSUED_DATE DESC
  LIMIT 1;

  IF days_late > 0 THEN
    SET NEW.fine = days_late * 5;
  ELSE
    SET NEW.fine = 0;
  END IF;
END//

CREATE TRIGGER prevent_duplicate_issue
BEFORE INSERT ON issued_books
FOR EACH ROW
BEGIN
  IF EXISTS (
    SELECT 1 FROM issued_books
    WHERE BID = NEW.BID
  ) THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Book already issued';
  END IF;
END//

DELIMITER ;


-- ============================================================
-- 06. SEED DATA
-- Password is: admin (BCrypt hashed)
-- ============================================================
INSERT INTO users (USERNAME, PASSWORD, USER_TYPE)
VALUES ('admin', '$2a$12$eCF.GZkta7d9QGwuoHHzL.xLhpbBVAEwTIVrqn8SmfJGLR6rIJ1Tu', 1);
