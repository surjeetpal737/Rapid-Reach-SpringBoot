CREATE DATABASE IF NOT EXISTS Rapidreach;

USE Rapidreach;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS service_provider (
    sp_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    city VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'pending',
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    pastexperience VARCHAR(255) NOT NULL,
    highest_qualification VARCHAR(160) NOT NULL,
    experties VARCHAR(160) NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(120) NOT NULL,
    provider_id BIGINT NOT NULL,
    provider_name VARCHAR(120) NOT NULL,
    provider_phone VARCHAR(20),
    service_type VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    booking_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    INDEX idx_bookings_provider_id (provider_id),
    CONSTRAINT fk_bookings_provider
        FOREIGN KEY (provider_id)
        REFERENCES service_provider(sp_id)
        ON DELETE CASCADE
);
