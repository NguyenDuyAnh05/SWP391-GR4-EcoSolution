-- MySQL Setup Script for SWP391 EcoSolution
-- Run this in MySQL Workbench after connecting with user 'sa' and password '12345'

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS SWP391_EcoSolution;

-- Step 2: Use the database
USE SWP391_EcoSolution;

-- Step 3: Verify database was created and is empty (should show no tables initially)
SHOW TABLES;

-- Step 4: Verify you're in the correct database
SELECT DATABASE();

-- Done! Your database is ready.
-- Spring Boot will automatically create tables when you run the application.

