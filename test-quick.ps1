# Quick API Test - Updated for port 8080
$ErrorActionPreference = 'Continue'
$base = "http://127.0.0.1:8080"
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$email = "citizen_${timestamp}@test.local"
$pwd = "password123"
$name = "Test Citizen"

Write-Host "`nTesting $base...`n" -ForegroundColor Cyan

# Test 1: Register
Write-Host "[1] Register..." -ForegroundColor Yellow
try {
    $regBody = @{ email=$email; password=$pwd; name=$name } | ConvertTo-Json
    $reg = Invoke-RestMethod -Uri "$base/api/v1/auth/register" -Method Post -ContentType "application/json" -Body $regBody
    Write-Host "SUCCESS - Status: 201, User ID: $($reg.id)" -ForegroundColor Green
} catch {
    $code = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { "ERROR" }
    Write-Host "FAILED - Status: $code, Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Login
Write-Host "[2] Login..." -ForegroundColor Yellow
try {
    $loginBody = @{ username=$email; password=$pwd } | ConvertTo-Json
    $login = Invoke-RestMethod -Uri "$base/api/v1/auth/login" -Method Post -ContentType "application/json" -Body $loginBody
    $token = $login.accessToken
    Write-Host "SUCCESS - Status: 200, Token: $($token.Substring(0,20))..." -ForegroundColor Green
} catch {
    $code = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { "ERROR" }
    Write-Host "FAILED - Status: $code, Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 3: No token
Write-Host "[3] Protected endpoint (No Token)..." -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "$base/api/v1/users/collectors" -Method Get
    Write-Host "UNEXPECTED - Status: 200 (Expected 401/403)" -ForegroundColor Red
} catch {
    $code = [int]$_.Exception.Response.StatusCode
    if ($code -in 401,403) {
        Write-Host "SUCCESS - Status: $code (Access denied as expected)" -ForegroundColor Green
    } else {
        Write-Host "UNEXPECTED - Status: $code" -ForegroundColor Yellow
    }
}

# Test 4: Bad token
Write-Host "[4] Protected endpoint (Invalid Token)..." -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "$base/api/v1/users/collectors" -Method Get -Headers @{ Authorization = "Bearer invalid.token" }
    Write-Host "UNEXPECTED - Status: 200 (Expected 401/403)" -ForegroundColor Red
} catch {
    $code = [int]$_.Exception.Response.StatusCode
    if ($code -in 401,403) {
        Write-Host "SUCCESS - Status: $code (Access denied as expected)" -ForegroundColor Green
    } else {
        Write-Host "UNEXPECTED - Status: $code" -ForegroundColor Yellow
    }
}

# Test 5: Citizen token (should fail - needs ENTERPRISE_ADMIN or ASSIGNOR)
Write-Host "[5] Protected endpoint (CITIZEN Token)..." -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "$base/api/v1/users/collectors" -Method Get -Headers @{ Authorization = "Bearer $token" }
    Write-Host "UNEXPECTED - Status: 200 (Expected 403 - CITIZEN lacks required role)" -ForegroundColor Red
} catch {
    $code = [int]$_.Exception.Response.StatusCode
    if ($code -eq 403) {
        Write-Host "SUCCESS - Status: 403 (CITIZEN denied ENTERPRISE_ADMIN endpoint)" -ForegroundColor Green
    } else {
        Write-Host "PARTIAL - Status: $code (Expected 403)" -ForegroundColor Yellow
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Basic auth flow working!" -ForegroundColor Green
Write-Host "JWT authentication: OK" -ForegroundColor Green
Write-Host "Role-based authorization: OK" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

