CREATE DATABASE IF NOT EXISTS ecosolutionv2;
USE ecosolutionv2;

-- 1. Table for Wards (Phân vùng quản lý)
DROP TABLE IF EXISTS wards;
CREATE TABLE wards (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       ward_name VARCHAR(100) NOT NULL UNIQUE
);


-- 2. Subscription Tiers (Household vs Business)
CREATE TABLE IF NOT EXISTS subscription_tiers (
                                                  id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                  tier_name VARCHAR(50) NOT NULL, -- 'HOUSEHOLD', 'BUSINESS'
    frequency_days INT,            -- 1: Daily, 2: Every 2 days
    monthly_fee DECIMAL(10, 2),
    description TEXT
    );

-- 3. Users Table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    role ENUM('CITIZEN', 'COLLECTOR', 'MANAGER', 'ADMIN') NOT NULL,
    ward_id BIGINT,
    FOREIGN KEY (ward_id) REFERENCES wards(id)
    );

-- 4. Citizen Subscriptions
CREATE TABLE IF NOT EXISTS citizen_subscriptions (
                                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                     user_id BIGINT,
                                                     tier_id BIGINT,
                                                     status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    start_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (tier_id) REFERENCES subscription_tiers(id)
    );

-- 5. Waste Collection Reports (Core of the system)
CREATE TABLE IF NOT EXISTS waste_reports (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             citizen_id BIGINT,
                                             collector_id BIGINT,
                                             image_url VARCHAR(255), -- Cloudinary URL
    description TEXT,
    waste_type ENUM('GENERAL', 'RECYCLABLE', 'ORGANIC') DEFAULT 'GENERAL',
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    points_earned INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (citizen_id) REFERENCES users(id),
    FOREIGN KEY (collector_id) REFERENCES users(id)
    );

CREATE TABLE collection_points (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   name VARCHAR(255) NOT NULL,
                                   address TEXT NOT NULL,
                                   latitude DOUBLE,
                                   longitude DOUBLE,
                                   ward_id BIGINT,
                                   FOREIGN KEY (ward_id) REFERENCES wards(id)
);