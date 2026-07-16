-- Database creation command
CREATE DATABASE IF NOT EXISTS coffee_co;
USE coffee_co;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
    created_at DATETIME NOT NULL
);

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- 3. Products Table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    stock INT NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    image_path VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- 4. Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    customer_name VARCHAR(255),
    order_date DATETIME NOT NULL,
    sub_total DOUBLE NOT NULL,
    gst_amount DOUBLE NOT NULL,
    grand_total DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- 5. Order Items Table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

-- 6. Initial Seed Data
-- Default Users:
-- Admin Password: admin | Customer Password: customer
INSERT INTO users (full_name, email, password, phone, role, created_at)
VALUES 
('Admin Coffee', 'admin@coffeeandco.com', 'admin', '1234567890', 'ADMIN', NOW())
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO users (full_name, email, password, phone, role, created_at)
VALUES 
('John Doe', 'customer@coffeeandco.com', 'customer', '9876543210', 'CUSTOMER', NOW())
ON DUPLICATE KEY UPDATE id=id;
