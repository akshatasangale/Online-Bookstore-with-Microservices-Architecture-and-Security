# Online-Book-Store Website Using SPRING BOOT and REST API 
### Online Book Store
<span style="color:blue">**This Website is built for following purpose:-**</span>
- For Selling books online
- Maintaining books selling history
- Adding and managing books
- User Friendly
- For Implemention of Generic Servlets in Java
- This is a Mini-project developed using Java, Jdbc, And Generic Servlets

<span style="color:blue">**Admin Have Following Access for this online store site:-**</span>
- Add New Books
- View Books Available
- Remove Books
- Increase Books Amount

<span style="color:blue">**Users Have Following Access for this online store site:-**</span>
- Create New Account or Register
- Login
- View Available Books
- Select Books to Buy
- Add Books to Shopping Cart
- Remove Books from shopping Cart
- Select and Update Book Quantity
- Purchased Books List
- Transactions Hitory
- Get Payment Receipt
- Profile view
- Profile Update
- Password Change or update
- Logout

### Technologies used:-
1. Front-End Development:
- Html
- Css
- Javascript

2. Back-End Development
- Java
- JPA-Repository (for API)
- Spring Boot
- Spring REST
- Spring Security

3. Database used
- MySQL

### Database Creation:


create database onlinebookstore1;
use onlinebookstore1;


CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
);



CREATE TABLE authorities (
    username VARCHAR(50),
    authority VARCHAR(50),
    PRIMARY KEY (username, authority),
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);



CREATE TABLE customer (
    id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100),
    mob BIGINT,
    address VARCHAR(255),
    FOREIGN KEY (id) REFERENCES users(username) ON DELETE CASCADE
);


CREATE TABLE book (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    quantity INT,
    price DECIMAL(10, 2),
    book_detail_id INT,
    FOREIGN KEY (book_detail_id) REFERENCES book_detail(id) ON DELETE SET NULL
);


CREATE TABLE book_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50),
    detail TEXT,
    sold INT
);
select * from book_detail;


CREATE TABLE shopping_cart (
    customer_id VARCHAR(50),
    book_id INT,
    count INT,
    PRIMARY KEY (customer_id, book_id),
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);



CREATE TABLE purchase_history (
    id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50),
    date DATETIME,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);


CREATE TABLE purchase_detail (
    purchase_history_id VARCHAR(50),
    book_id INT,
    price DECIMAL(10, 2),
    quantity INT,
    PRIMARY KEY (purchase_history_id, book_id),
    FOREIGN KEY (purchase_history_id) REFERENCES purchase_history(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);


