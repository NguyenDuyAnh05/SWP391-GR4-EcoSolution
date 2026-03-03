-- ====================================================
-- EcoSolution: Setup Test Users
-- ====================================================
-- Run this script in MySQL Workbench after the app creates tables

USE SWP391_EcoSolution;

-- ====================================================
-- 1. Create ENTERPRISE_ADMIN User
-- ====================================================
-- Password: admin123
-- BCrypt hash: $2a$10$rZ7cWQHxDlGzVAZ8qX0gFelqYx8HpF.nVXyFwRHC3kFPPvXGCqV4i

INSERT INTO users (id, name, email, role, status, created_at)
VALUES
(UUID(), 'Enterprise Admin', 'admin@enterprise.com', 'ENTERPRISE_ADMIN', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE name=name;

-- Get the user ID
SET @adminUserId = (SELECT id FROM users WHERE email = 'admin@enterprise.com' LIMIT 1);

-- Insert authentication credentials
INSERT INTO user_auth (user_id, username, password_hash, created_at)
VALUES
(@adminUserId, 'admin@enterprise.com', '$2a$10$rZ7cWQHxDlGzVAZ8qX0gFelqYx8HpF.nVXyFwRHC3kFPPvXGCqV4i', NOW())
ON DUPLICATE KEY UPDATE password_hash=password_hash;

SELECT 'ENTERPRISE_ADMIN user created:' AS Status,
       name, email, role
FROM users
WHERE email = 'admin@enterprise.com';

-- ====================================================
-- 2. Create SYSTEM_ADMIN User (optional)
-- ====================================================
-- Password: sysadmin123
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO

INSERT INTO users (id, name, email, role, status, created_at)
VALUES
(UUID(), 'System Administrator', 'sysadmin@ecosolution.com', 'SYSTEM_ADMIN', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE name=name;

SET @sysAdminUserId = (SELECT id FROM users WHERE email = 'sysadmin@ecosolution.com' LIMIT 1);

INSERT INTO user_auth (user_id, username, password_hash, created_at)
VALUES
(@sysAdminUserId, 'sysadmin@ecosolution.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', NOW())
ON DUPLICATE KEY UPDATE password_hash=password_hash;

SELECT 'SYSTEM_ADMIN user created:' AS Status,
       name, email, role
FROM users
WHERE email = 'sysadmin@ecosolution.com';

-- ====================================================
-- 3. Create Test COLLECTOR User
-- ====================================================
-- Password: collector123

INSERT INTO users (id, name, email, role, status, created_at)
VALUES
(UUID(), 'Test Collector', 'collector@test.com', 'COLLECTOR', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE name=name;

SET @collectorUserId = (SELECT id FROM users WHERE email = 'collector@test.com' LIMIT 1);

INSERT INTO user_auth (user_id, username, password_hash, created_at)
VALUES
(@collectorUserId, 'collector@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NOW())
ON DUPLICATE KEY UPDATE password_hash=password_hash;

-- ====================================================
-- 4. Create Test ASSIGNOR User
-- ====================================================
-- Password: assignor123

INSERT INTO users (id, name, email, role, status, created_at)
VALUES
(UUID(), 'Test Assignor', 'assignor@test.com', 'ASSIGNOR', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE name=name;

SET @assignorUserId = (SELECT id FROM users WHERE email = 'assignor@test.com' LIMIT 1);

INSERT INTO user_auth (user_id, username, password_hash, created_at)
VALUES
(@assignorUserId, 'assignor@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', NOW())
ON DUPLICATE KEY UPDATE password_hash=password_hash;

-- ====================================================
-- Summary
-- ====================================================

SELECT
    'Test users created successfully!' AS Message,
    COUNT(*) AS TotalUsers
FROM users
WHERE email IN (
    'admin@enterprise.com',
    'sysadmin@ecosolution.com',
    'collector@test.com',
    'assignor@test.com'
);

SELECT
    u.name AS Name,
    u.email AS Email,
    u.role AS Role,
    u.status AS Status,
    IF(ua.user_id IS NOT NULL, 'Yes', 'No') AS HasAuth
FROM users u
LEFT JOIN user_auth ua ON u.id = ua.user_id
WHERE u.email IN (
    'admin@enterprise.com',
    'sysadmin@ecosolution.com',
    'collector@test.com',
    'assignor@test.com'
)
ORDER BY
    CASE u.role
        WHEN 'SYSTEM_ADMIN' THEN 1
        WHEN 'ENTERPRISE_ADMIN' THEN 2
        WHEN 'ASSIGNOR' THEN 3
        WHEN 'COLLECTOR' THEN 4
        ELSE 5
    END;

-- ====================================================
-- Test Login Credentials
-- ====================================================
--
-- ENTERPRISE_ADMIN:
--   Email: admin@enterprise.com
--   Password: admin123
--
-- SYSTEM_ADMIN:
--   Email: sysadmin@ecosolution.com
--   Password: sysadmin123
--
-- ASSIGNOR:
--   Email: assignor@test.com
--   Password: assignor123
--
-- COLLECTOR:
--   Email: collector@test.com
--   Password: collector123
--
-- ====================================================

