# EcoSolution Auth API Test Script (PowerShell)
# Run after starting Spring Boot app on port 8081

$ErrorActionPreference = 'Continue'
$base = "http://127.0.0.1:8081"
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$email = "citizen_${timestamp}@test.local"
$pwd = "password123"
$name = "Test Citizen $timestamp"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "EcoSolution Auth API Smoke Test" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Test User: $email" -ForegroundColor Yellow
Write-Host "Base URL: $base`n" -ForegroundColor Yellow

# Helper function
function Call-API {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body,
        [hashtable]$Headers = @{}
    )

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
        return @{
            Success = $true
            StatusCode = $response.StatusCode
            Body = $response.Content
        }
    }
    catch {
        $statusCode = if ($_.Exception.Response) {
            [int]$_.Exception.Response.StatusCode
        } else {
            -1
        }

        $errorBody = ""
        if ($_.Exception.Response) {
            try {
                $stream = $_.Exception.Response.GetResponseStream()
                $reader = New-Object System.IO.StreamReader($stream)
                $errorBody = $reader.ReadToEnd()
            } catch {}
        }

        return @{
            Success = $false
            StatusCode = $statusCode
            Body = $errorBody
            Error = $_.Exception.Message
        }
    }
}

# Test 1: Register
Write-Host "[TEST 1] POST /api/v1/auth/register" -ForegroundColor Green
$regBody = @{ email=$email; password=$pwd; name=$name } | ConvertTo-Json
$reg = Call-API -Method POST -Url "$base/api/v1/auth/register" -Body $regBody

Write-Host "Status: $($reg.StatusCode)" -ForegroundColor $(if($reg.StatusCode -eq 201){'Green'}else{'Red'})
if ($reg.Success) {
    Write-Host "Response: $($reg.Body)" -ForegroundColor Gray
} else {
    Write-Host "Error: $($reg.Error)" -ForegroundColor Red
    Write-Host "Body: $($reg.Body)" -ForegroundColor Red
}
Write-Host ""

# Test 2: Login
Write-Host "[TEST 2] POST /api/v1/auth/login" -ForegroundColor Green
$loginBody = @{ username=$email; password=$pwd } | ConvertTo-Json
$login = Call-API -Method POST -Url "$base/api/v1/auth/login" -Body $loginBody

Write-Host "Status: $($login.StatusCode)" -ForegroundColor $(if($login.StatusCode -eq 200){'Green'}else{'Red'})
$token = ""
if ($login.Success) {
    Write-Host "Response: $($login.Body)" -ForegroundColor Gray
    try {
        $loginObj = $login.Body | ConvertFrom-Json
        $token = $loginObj.accessToken
        Write-Host "JWT Token extracted: ${token.Substring(0, [Math]::Min(50, $token.Length))}..." -ForegroundColor Yellow
    } catch {
        Write-Host "Failed to extract token" -ForegroundColor Red
    }
} else {
    Write-Host "Error: $($login.Error)" -ForegroundColor Red
}
Write-Host ""

# Test 3: Protected endpoint - No token
Write-Host "[TEST 3] GET /api/v1/users/collectors (No Token)" -ForegroundColor Green
$noToken = Call-API -Method GET -Url "$base/api/v1/users/collectors"

Write-Host "Status: $($noToken.StatusCode)" -ForegroundColor $(if($noToken.StatusCode -in 401,403){'Green'}else{'Red'})
Write-Host "Expected: 401 or 403 (Unauthorized/Forbidden)" -ForegroundColor Gray
if (!$noToken.Success) {
    Write-Host "Error Body: $($noToken.Body)" -ForegroundColor Gray
}
Write-Host ""

# Test 4: Protected endpoint - Bad token
Write-Host "[TEST 4] GET /api/v1/users/collectors (Invalid Token)" -ForegroundColor Green
$badToken = Call-API -Method GET -Url "$base/api/v1/users/collectors" -Headers @{ Authorization = "Bearer invalid.token.value" }

Write-Host "Status: $($badToken.StatusCode)" -ForegroundColor $(if($badToken.StatusCode -in 401,403){'Green'}else{'Red'})
Write-Host "Expected: 401 or 403 (Unauthorized/Forbidden)" -ForegroundColor Gray
if (!$badToken.Success) {
    Write-Host "Error Body: $($badToken.Body)" -ForegroundColor Gray
}
Write-Host ""

# Test 5: Protected endpoint - Citizen token
if ($token) {
    Write-Host "[TEST 5] GET /api/v1/users/collectors (Valid CITIZEN Token)" -ForegroundColor Green
    $citizenToken = Call-API -Method GET -Url "$base/api/v1/users/collectors" -Headers @{ Authorization = "Bearer $token" }

    Write-Host "Status: $($citizenToken.StatusCode)" -ForegroundColor $(if($citizenToken.StatusCode -eq 403){'Green'}else{'Red'})
    Write-Host "Expected: 403 (Forbidden - CITIZEN lacks ENTERPRISE_ADMIN/ASSIGNOR role)" -ForegroundColor Gray
    if (!$citizenToken.Success) {
        Write-Host "Error Body: $($citizenToken.Body)" -ForegroundColor Gray
    }
    Write-Host ""
} else {
    Write-Host "[TEST 5] SKIPPED - No token available" -ForegroundColor Yellow
    Write-Host ""
}

# Summary
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$results = @(
    @{ Test="Register (CITIZEN)"; Expected="201"; Actual=$reg.StatusCode; Pass=($reg.StatusCode -eq 201) }
    @{ Test="Login"; Expected="200"; Actual=$login.StatusCode; Pass=($login.StatusCode -eq 200) }
    @{ Test="Protected (No Token)"; Expected="401/403"; Actual=$noToken.StatusCode; Pass=($noToken.StatusCode -in 401,403) }
    @{ Test="Protected (Bad Token)"; Expected="401/403"; Actual=$badToken.StatusCode; Pass=($badToken.StatusCode -in 401,403) }
)

if ($token) {
    $results += @{ Test="Protected (CITIZEN Token)"; Expected="403"; Actual=$citizenToken.StatusCode; Pass=($citizenToken.StatusCode -eq 403) }
}

foreach ($result in $results) {
    $status = if ($result.Pass) { "[PASS]" } else { "[FAIL]" }
    $color = if ($result.Pass) { "Green" } else { "Red" }
    Write-Host "$status $($result.Test): Expected=$($result.Expected), Actual=$($result.Actual)" -ForegroundColor $color
}

$passCount = ($results | Where-Object { $_.Pass }).Count
$totalCount = $results.Count

Write-Host "`nResult: $passCount/$totalCount tests passed" -ForegroundColor $(if($passCount -eq $totalCount){'Green'}else{'Yellow'})
Write-Host "========================================`n" -ForegroundColor Cyan

# Save to file
$reportPath = "D:\FPT\SWP391\Project\SWP391-GR4-EcoSolution\TEST_RESULTS.txt"
$report = @"
EcoSolution Auth API Test Results
Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
Test User: $email
Base URL: $base

TEST RESULTS:
$(foreach ($r in $results) {
    $s = if ($r.Pass) { "PASS" } else { "FAIL" }
    "[$s] $($r.Test): Expected=$($r.Expected), Actual=$($r.Actual)"
})

Summary: $passCount/$totalCount tests passed

DETAILS:
---------
1. Register: Status=$($reg.StatusCode)
2. Login: Status=$($login.StatusCode), Token=$(if($token){"Retrieved"}else{"FAILED"})
3. No Token: Status=$($noToken.StatusCode)
4. Bad Token: Status=$($badToken.StatusCode)
5. Citizen Token: Status=$(if($token){$citizenToken.StatusCode}else{"SKIPPED"})

"@

$report | Out-File -FilePath $reportPath -Encoding utf8
Write-Host "Report saved to: $reportPath" -ForegroundColor Cyan

