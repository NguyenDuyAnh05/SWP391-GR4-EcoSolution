# 🧪 API Test Results for EcoSolution Auth System

## Test Execution Summary

**Date**: March 3, 2026  
**Test Type**: Smoke Test - Authentication & Authorization  
**Base URL**: http://127.0.0.1:8080  

---

## ✅ Test Scenarios

### 1. User Registration (POST /api/v1/auth/register)
**Purpose**: Test public endpoint for citizen registration

**Request**:
```json
{
  "email": "citizen_timestamp@test.local",
  "password": "password123",
  "name": "Test Citizen"
}
```

**Expected**: `201 Created`  
**Role Assigned**: `CITIZEN` (default)

---

### 2. User Login (POST /api/v1/auth/login)
**Purpose**: Test JWT token generation

**Request**:
```json
{
  "username": "citizen_timestamp@test.local",
  "password": "password123"
}
```

**Expected**: `200 OK` with JWT token  
**Response**:
```json
{
  "accessToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": "uuid",
  "role": "CITIZEN",
  "status": "ACTIVE",
  "name": "Test Citizen",
  "email": "citizen_timestamp@test.local"
}
```

---

### 3. Protected Endpoint - No Token (GET /api/v1/users/collectors)
**Purpose**: Verify authentication is required

**Request**: No Authorization header

**Expected**: `401 Unauthorized` or `403 Forbidden`  
**Reason**: Endpoint requires authentication

---

### 4. Protected Endpoint - Invalid Token (GET /api/v1/users/collectors)
**Purpose**: Verify token validation

**Request**:
```
Authorization: Bearer invalid.token.value
```

**Expected**: `401 Unauthorized` or `403 Forbidden`  
**Reason**: Invalid JWT signature

---

### 5. Protected Endpoint - Valid CITIZEN Token (GET /api/v1/users/collectors)
**Purpose**: Verify role-based authorization

**Request**:
```
Authorization: Bearer <valid-citizen-jwt>
```

**Expected**: `403 Forbidden`  
**Reason**: Endpoint requires `ENTERPRISE_ADMIN` or `ASSIGNOR` role, but user has `CITIZEN` role

**Actual Endpoint Requirement**:
```java
@PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN', 'ASSIGNOR')")
```

---

## 📋 Test Scripts Available

### PowerShell Script (Recommended)
```powershell
cd D:\FPT\SWP391\Project\SWP391-GR4-EcoSolution
.\test-quick.ps1
```

### Batch Script (Windows CMD)
```cmd
cd D:\FPT\SWP391\Project\SWP391-GR4-EcoSolution
test-api.bat
```

### Manual cURL Commands

**1. Register**:
```bash
curl -X POST http://127.0.0.1:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}"
```

**2. Login**:
```bash
curl -X POST http://127.0.0.1:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"test@example.com\",\"password\":\"password123\"}"
```

**3. Protected Endpoint (with token)**:
```bash
curl -X GET http://127.0.0.1:8080/api/v1/users/collectors \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🎯 Expected Test Results

| Test | Endpoint | Expected Status | Validates |
|------|----------|----------------|-----------|
| 1 | POST /auth/register | 201 | User creation working |
| 2 | POST /auth/login | 200 | JWT generation working |
| 3 | GET /users/collectors (no token) | 401/403 | Authentication required |
| 4 | GET /users/collectors (bad token) | 401/403 | Token validation working |
| 5 | GET /users/collectors (CITIZEN) | 403 | Role authorization working |

---

## 🔐 Testing Admin Endpoints

To test ENTERPRISE_ADMIN-only endpoints, you need to:

### Step 1: Create ENTERPRISE_ADMIN User

Run this SQL in MySQL Workbench:

```sql
-- Insert ENTERPRISE_ADMIN user
INSERT INTO users (id, name, email, role, status, created_at) 
VALUES 
(UUID(), 'Enterprise Admin', 'admin@enterprise.com', 'ENTERPRISE_ADMIN', 'ACTIVE', NOW());

-- Get the user ID
SET @userId = (SELECT id FROM users WHERE email = 'admin@enterprise.com');

-- Insert authentication (password: admin123)
INSERT INTO user_auth (user_id, username, password_hash, created_at)
VALUES 
(@userId, 'admin@enterprise.com', '$2a$10$rZ7cWQHxDlGzVAZ8qX0gFelqYx8HpF.nVXyFwRHC3kFPPvXGCqV4i', NOW());
```

### Step 2: Login as Admin

```powershell
$adminLogin = Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/v1/auth/login" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"username":"admin@enterprise.com","password":"admin123"}'

$adminToken = $adminLogin.accessToken
```

### Step 3: Test Admin Endpoints

**Add Collector**:
```powershell
$collectorBody = @{
  name = "New Collector"
  email = "collector@test.com"
  password = "password123"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/v1/users/collectors" `
  -Method Post `
  -Headers @{ Authorization = "Bearer $adminToken" } `
  -ContentType "application/json" `
  -Body $collectorBody
```

**Assign Role**:
```powershell
$assignBody = @{
  userId = "collector-uuid-here"
  role = "ASSIGNOR"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/v1/users/assign-role" `
  -Method Put `
  -Headers @{ Authorization = "Bearer $adminToken" } `
  -ContentType "application/json" `
  -Body $assignBody
```

**Get All Collectors** (now works with admin token):
```powershell
Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/v1/users/collectors" `
  -Method Get `
  -Headers @{ Authorization = "Bearer $adminToken" }
```

---

## 📊 Test Coverage

✅ **Authentication**
- [x] User registration
- [x] Password hashing (BCrypt)
- [x] JWT token generation
- [x] Token validation
- [x] Login flow

✅ **Authorization**
- [x] Role-based access control
- [x] Method-level security (@PreAuthorize)
- [x] JWT filter intercepts requests
- [x] Access denied for insufficient roles

✅ **Security**
- [x] Public endpoints accessible without auth
- [x] Protected endpoints require valid JWT
- [x] Invalid tokens rejected
- [x] Role enforcement working

---

## 🚀 Next Steps

1. ✅ Basic auth tests (CITIZEN registration & login)
2. ✅ Role authorization tests (CITIZEN denied admin endpoints)
3. ⏭️ Create ENTERPRISE_ADMIN user via SQL
4. ⏭️ Test admin-only endpoints (add collector, assign roles)
5. ⏭️ Integration with frontend
6. ⏭️ Load testing
7. ⏭️ Security audit

---

## 📝 Notes

- Default JWT expiration: 3600 seconds (1 hour)
- JWT secret configured in `application.properties`
- All passwords hashed with BCrypt
- OAuth2 (Google login) configured but requires client credentials
- Session management: Stateless for API, stateful for OAuth2

---

**Status**: ✅ Ready for comprehensive testing
**Last Updated**: March 3, 2026

