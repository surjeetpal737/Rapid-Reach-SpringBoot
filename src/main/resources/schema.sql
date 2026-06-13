-- Rapid Reach — Reference DDL
-- Hibernate (ddl-auto=update) will handle CREATE/ALTER automatically.
CREATE DATABASE IF NOT EXISTS Rapidreach CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE Rapidreach;

-- ─── Users (Customers) ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
                                     user_id  BIGINT       AUTO_INCREMENT PRIMARY KEY,
                                     name     VARCHAR(120) NOT NULL,
    email    VARCHAR(160) NOT NULL UNIQUE,
    phone    VARCHAR(20)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    address  VARCHAR(255),
    city     VARCHAR(100)
    );

-- ─── Service Providers ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS service_provider (
                                                sp_id                  BIGINT       AUTO_INCREMENT PRIMARY KEY,
                                                name                   VARCHAR(120) NOT NULL,
    service_type           VARCHAR(100) NOT NULL,
    phone                  VARCHAR(20)  NOT NULL,
    email                  VARCHAR(160) NOT NULL UNIQUE,
    city                   VARCHAR(100) NOT NULL,
    area                   VARCHAR(100),
    password               VARCHAR(255) NOT NULL,
    status                 VARCHAR(30)  NOT NULL DEFAULT 'Pending',
    age                    INT          NOT NULL,
    gender                 VARCHAR(20)  NOT NULL,
    pastexperience         VARCHAR(255) NOT NULL,
    highest_qualification  VARCHAR(160) NOT NULL,
    experties              VARCHAR(160) NOT NULL,
    id_proof_path          VARCHAR(255),
    rejection_reason       VARCHAR(255)
    );

-- ─── Admins ──────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS admins (
                                      admin_id BIGINT       AUTO_INCREMENT PRIMARY KEY,
                                      name     VARCHAR(120) NOT NULL,
    email    VARCHAR(160) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
    );

-- ─── Service Categories ──────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS services (
                                        service_id  BIGINT       AUTO_INCREMENT PRIMARY KEY,
                                        name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    active      TINYINT(1)   NOT NULL DEFAULT 1
    );

-- ─── Bookings ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS bookings (
                                        booking_id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
                                        user_id             BIGINT,
                                        user_name           VARCHAR(120) NOT NULL,
    provider_id         BIGINT       NOT NULL,
    provider_name       VARCHAR(120) NOT NULL,
    provider_phone      VARCHAR(20),
    service_type        VARCHAR(100) NOT NULL,
    city                VARCHAR(100) NOT NULL,
    area                VARCHAR(100) NOT NULL,
    booking_date        DATE         NOT NULL,
    preferred_time      TIME         NOT NULL,
    problem_description VARCHAR(800) NOT NULL,
    status              VARCHAR(30)  NOT NULL DEFAULT 'Pending',
    completion_otp      VARCHAR(6)   NOT NULL,
    created_at          DATETIME     NOT NULL,
    INDEX idx_bookings_provider_id (provider_id),
    INDEX idx_bookings_user_id     (user_id),
    CONSTRAINT fk_bookings_provider
    FOREIGN KEY (provider_id) REFERENCES service_provider(sp_id) ON DELETE CASCADE
    );

-- ─── Payments ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payments (
                                        payment_id      BIGINT         AUTO_INCREMENT PRIMARY KEY,
                                        booking_id      BIGINT         NOT NULL UNIQUE,
                                        amount          DECIMAL(10, 2) NOT NULL,
    mode            VARCHAR(40)    NOT NULL,
    status          VARCHAR(40)    NOT NULL,
    transaction_ref VARCHAR(80),
    created_at      DATETIME       NOT NULL,
    CONSTRAINT fk_payments_booking
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
    );

-- ─── Feedback ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS feedback (
                                        feedback_id BIGINT    AUTO_INCREMENT PRIMARY KEY,
                                        booking_id  BIGINT    NOT NULL UNIQUE,
                                        user_id     BIGINT    NOT NULL,
                                        provider_id BIGINT    NOT NULL,
                                        rating      INT       NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments    VARCHAR(800),
    created_at  DATETIME  NOT NULL,
    INDEX idx_feedback_provider_id (provider_id),
    CONSTRAINT fk_feedback_booking
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
    );
