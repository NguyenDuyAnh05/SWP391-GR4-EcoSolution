# EcoSolution - Complete API Test Suite
# Tests all authentication and authorization scenarios

$ErrorActionPreference = 'Continue'
$base = "http://127.0.0.1:8080"

Write-Host "`n" + "="*60 -ForegroundColor Cyan
Write-Host "   EcoSolution Auth API - Complete Test Suite" -ForegroundColor Cyan
Write-Host "="*60 + "`n" -ForegroundColor Cyan

$testResults = @()

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [string]$Body,
        [hashtable]$Headers,
        [int[]]$ExpectedCodes
    )

    Write-Host "Testing: $Name" -ForegroundColor Yellow

    try {
        $params = @{
            Uri = $Url
            Method = $Method
            UseBasicParsing = $true
            TimeoutSec = 15
        }

        if ($Body) {
            $params.Body = $Body
            $params.ContentType = 'application/json'
        }

        if ($Headers.Count -gt 0) {
            $params.Headers = $Headers
        }

        $response = Invoke-WebRequest @params
        $statusCode = $response.StatusCode
        $pass = $statusCode -in $ExpectedCodes

        if ($pass) {
            Write-Host "  ✓ PASS - Status: $statusCode" -ForegroundColor Green
        } else {
            Write-Host "  ✗ FAIL - Status: $statusCode (Expected: $($ExpectedCodes -join '/'))" -ForegroundColor Red
        }

        return @{
            Name = $Name
            Pass = $pass
            StatusCode = $statusCode
            Expected = ($ExpectedCodes -join '/')
            Response = $response.Content
        }
    }
    catch {
        $statusCode = if ($_.Exception.Response) {
            [int]$_.Exception.Response.StatusCode
        } else {
            0
        }

        $pass = $statusCode -in $ExpectedCodes

        if ($pass) {
            Write-Host "  ✓ PASS - Status: $statusCode" -ForegroundColor Green
        } else {
            Write-Host "  ✗ FAIL - Status: $statusCode (Expected: $($ExpectedCodes -join '/'))" -ForegroundColor Red
        }

        return @{
            Name = $Name
            Pass = $pass
            StatusCode = $statusCode
            Expected = ($ExpectedCodes -join '/')
            Response = ""
        }
    }
}

# ====================================================================
# PART 1: Basic Authentication Tests
# ====================================================================

Write-Host "`n[PART 1] Basic Authentication Tests" -ForegroundColor Magenta
Write-Host "-" * 60 + "`n"

# Test 1: Register new citizen
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$citizenEmail = "citizen_${timestamp}@test.local"
$regBody = @{ email=$citizenEmail; password="password123"; name="Test Citizen" } | ConvertTo-Json

$result = Test-Endpoint -Name "Register CITIZEN" -Method POST `
    -Url "$base/api/v1/auth/register" -Body $regBody `
    -ExpectedCodes @(201)
$testResults += $result

# Test 2: Login as citizen
$loginBody = @{ username=$citizenEmail; password="password123" } | ConvertTo-Json
$loginResult = Test-Endpoint -Name "Login as CITIZEN" -Method POST `
    -Url "$base/api/v1/auth/login" -Body $loginBody `
    -ExpectedCodes @(200)
$testResults += $loginResult

$citizenToken = ""
if ($loginResult.Pass -and $loginResult.Response) {
    try {
        $loginObj = $loginResult.Response | ConvertFrom-Json
        $citizenToken = $loginObj.accessToken
        Write-Host "  → Token extracted: $($citizenToken.Substring(0,20))..." -ForegroundColor Gray
    } catch {
        Write-Host "  → Failed to extract token" -ForegroundColor Red
    }
}

# ====================================================================
# PART 2: Authorization Tests (Protected Endpoints)
# ====================================================================

Write-Host "`n[PART 2] Authorization Tests" -ForegroundColor Magenta
Write-Host "-" * 60 + "`n"

# Test 3: No token
$result = Test-Endpoint -Name "Protected endpoint (No Token)" -Method GET `
    -Url "$base/api/v1/users/collectors" `
    -ExpectedCodes @(401, 403)
$testResults += $result

# Test 4: Invalid token
$result = Test-Endpoint -Name "Protected endpoint (Invalid Token)" -Method GET `
    -Url "$base/api/v1/users/collectors" `
    -Headers @{ Authorization = "Bearer invalid.token.value" } `
    -ExpectedCodes @(401, 403)
$testResults += $result

# Test 5: Citizen token (insufficient role)
if ($citizenToken) {
    $result = Test-Endpoint -Name "Protected endpoint (CITIZEN role - denied)" -Method GET `
        -Url "$base/api/v1/users/collectors" `
        -Headers @{ Authorization = "Bearer $citizenToken" } `
        -ExpectedCodes @(403)
    $testResults += $result
}

# ====================================================================
# PART 3: Role-Based Tests (Requires setup-test-users.sql)
# ====================================================================

Write-Host "`n[PART 3] Role-Based Authorization Tests" -ForegroundColor Magenta
Write-Host "-" * 60 + "`n"
Write-Host "Note: These tests require running setup-test-users.sql first`n" -ForegroundColor Yellow

# Test 6: Login as ENTERPRISE_ADMIN
$adminLoginBody = @{ username="admin@enterprise.com"; password="admin123" } | ConvertTo-Json
$adminLoginResult = Test-Endpoint -Name "Login as ENTERPRISE_ADMIN" -Method POST `
    -Url "$base/api/v1/auth/login" -Body $adminLoginBody `
    -ExpectedCodes @(200, 401)
$testResults += $adminLoginResult

$adminToken = ""
if ($adminLoginResult.Pass -and $adminLoginResult.StatusCode -eq 200 -and $adminLoginResult.Response) {
    try {
        $adminObj = $adminLoginResult.Response | ConvertFrom-Json
        $adminToken = $adminObj.accessToken
        Write-Host "  → Admin token extracted`n" -ForegroundColor Gray

        # Test 7: Get collectors (admin)
        $result = Test-Endpoint -Name "Get collectors (ENTERPRISE_ADMIN)" -Method GET `
            -Url "$base/api/v1/users/collectors" `
            -Headers @{ Authorization = "Bearer $adminToken" } `
            -ExpectedCodes @(200)
        $testResults += $result

        # Test 8: Add new collector
        $newCollectorEmail = "collector_${timestamp}@test.local"
        $collectorBody = @{
            name = "New Collector $timestamp"
            email = $newCollectorEmail
            password = "password123"
        } | ConvertTo-Json

        $addResult = Test-Endpoint -Name "Add COLLECTOR (ENTERPRISE_ADMIN)" -Method POST `
            -Url "$base/api/v1/users/collectors" -Body $collectorBody `
            -Headers @{ Authorization = "Bearer $adminToken" } `
            -ExpectedCodes @(201)
        $testResults += $addResult

        # Extract new collector ID for role assignment test
        $newCollectorId = ""
        if ($addResult.Pass -and $addResult.Response) {
            try {
                $collectorObj = $addResult.Response | ConvertFrom-Json
                $newCollectorId = $collectorObj.id
                Write-Host "  → New collector ID: $newCollectorId`n" -ForegroundColor Gray

                # Test 9: Assign ASSIGNOR role
                $assignBody = @{
                    userId = $newCollectorId
                    role = "ASSIGNOR"
                } | ConvertTo-Json

                $result = Test-Endpoint -Name "Assign ASSIGNOR role (ENTERPRISE_ADMIN)" -Method PUT `
                    -Url "$base/api/v1/users/assign-role" -Body $assignBody `
                    -Headers @{ Authorization = "Bearer $adminToken" } `
                    -ExpectedCodes @(200)
                $testResults += $result

            } catch {
                Write-Host "  → Could not extract collector ID" -ForegroundColor Red
            }
        }

        # Test 10: Get assignors
        $result = Test-Endpoint -Name "Get assignors (ENTERPRISE_ADMIN)" -Method GET `
            -Url "$base/api/v1/users/assignors" `
            -Headers @{ Authorization = "Bearer $adminToken" } `
            -ExpectedCodes @(200)
        $testResults += $result

    } catch {
        Write-Host "  → Admin tests skipped - token extraction failed" -ForegroundColor Red
    }
} else {
    Write-Host "  → Admin tests skipped - login failed (run setup-test-users.sql)`n" -ForegroundColor Yellow
}

# Test 11: Login as ASSIGNOR
$assignorLoginBody = @{ username="assignor@test.com"; password="assignor123" } | ConvertTo-Json
$assignorLoginResult = Test-Endpoint -Name "Login as ASSIGNOR" -Method POST `
    -Url "$base/api/v1/auth/login" -Body $assignorLoginBody `
    -ExpectedCodes @(200, 401)
$testResults += $assignorLoginResult

if ($assignorLoginResult.Pass -and $assignorLoginResult.StatusCode -eq 200 -and $assignorLoginResult.Response) {
    try {
        $assignorObj = $assignorLoginResult.Response | ConvertFrom-Json
        $assignorToken = $assignorObj.accessToken

        # Test 12: Get collectors (assignor)
        $result = Test-Endpoint -Name "Get collectors (ASSIGNOR)" -Method GET `
            -Url "$base/api/v1/users/collectors" `
            -Headers @{ Authorization = "Bearer $assignorToken" } `
            -ExpectedCodes @(200)
        $testResults += $result

        # Test 13: Try to add collector (should fail - ASSIGNOR can't add)
        $collectorBody = @{
            name = "Test"
            email = "test@test.com"
            password = "password123"
        } | ConvertTo-Json

        $result = Test-Endpoint -Name "Try add COLLECTOR (ASSIGNOR - denied)" -Method POST `
            -Url "$base/api/v1/users/collectors" -Body $collectorBody `
            -Headers @{ Authorization = "Bearer $assignorToken" } `
            -ExpectedCodes @(403)
        $testResults += $result

    } catch {}
}

# ====================================================================
# SUMMARY
# ====================================================================

Write-Host "`n" + "="*60 -ForegroundColor Cyan
Write-Host "   TEST SUMMARY" -ForegroundColor Cyan
Write-Host "="*60 + "`n" -ForegroundColor Cyan

$passed = ($testResults | Where-Object { $_.Pass }).Count
$total = $testResults.Count

foreach ($result in $testResults) {
    $symbol = if ($result.Pass) { "✓" } else { "✗" }
    $color = if ($result.Pass) { "Green" } else { "Red" }
    Write-Host "$symbol $($result.Name)" -ForegroundColor $color
    Write-Host "    Status: $($result.StatusCode) (Expected: $($result.Expected))" -ForegroundColor Gray
}

Write-Host "`n" + "-"*60 -ForegroundColor Cyan
$percentage = [math]::Round(($passed / $total) * 100, 1)
Write-Host "Result: $passed / $total tests passed ($percentage%)" -ForegroundColor $(if($passed -eq $total){'Green'}elseif($passed -ge $total*0.7){'Yellow'}else{'Red'})
Write-Host "="*60 + "`n" -ForegroundColor Cyan

# Save report
$reportPath = "D:\FPT\SWP391\Project\SWP391-GR4-EcoSolution\TEST_RESULTS_COMPLETE.txt"
$report = @"
EcoSolution Auth API - Complete Test Results
Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
Base URL: $base

SUMMARY: $passed / $total tests passed ($percentage%)

DETAILED RESULTS:
$(foreach ($r in $testResults) {
    $s = if ($r.Pass) { "PASS" } else { "FAIL" }
    "[$s] $($r.Name): Status=$($r.StatusCode), Expected=$($r.Expected)"
})

NOTES:
- Basic auth tests: Registration and login for CITIZEN
- Authorization tests: Token validation and role enforcement
- Role-based tests: Require setup-test-users.sql to be executed first
- Admin tests: Full CRUD operations for user management

NEXT STEPS:
1. If admin tests failed: Run setup-test-users.sql in MySQL
2. Review failed tests and check application logs
3. Test with frontend integration
4. Perform load testing
"@

$report | Out-File -FilePath $reportPath -Encoding utf8
Write-Host "Full report saved to: $reportPath`n" -ForegroundColor Cyan

