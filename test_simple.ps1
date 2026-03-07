#!/usr/bin/env pwsh
# Simple API Test Script

$baseUrl = "http://localhost:8080/api/v1"

# Test 1: Register Admin
Write-Host "========== TEST 1: Register Admin ==========" -ForegroundColor Green
$adminPayload = @{
    name = "System Admin"
    email = "admin@ecosolution.com"
    password = "Admin@123456"
} | ConvertTo-Json

try {
    $adminResp = Invoke-WebRequest -Uri "$baseUrl/auth/register" -Method Post -ContentType "application/json" -Body $adminPayload -UseBasicParsing
    $adminData = $adminResp.Content | ConvertFrom-Json
    Write-Host "✓ Admin registered" -ForegroundColor Green
    Write-Host "Admin ID: $($adminData.id)" -ForegroundColor Cyan
}
catch {
    Write-Host "✗ Admin registration failed: $_" -ForegroundColor Red
    exit
}

# Test 2: Login Admin
Write-Host "`n========== TEST 2: Login Admin ==========" -ForegroundColor Green
$loginPayload = @{
    email = "admin@ecosolution.com"
    password = "Admin@123456"
} | ConvertTo-Json

try {
    $loginResp = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $loginPayload -UseBasicParsing
    $loginData = $loginResp.Content | ConvertFrom-Json
    $token = $loginData.token
    Write-Host "✓ Admin logged in" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 30))..." -ForegroundColor Cyan
}
catch {
    Write-Host "✗ Login failed: $_" -ForegroundColor Red
    exit
}

# Test 3: Register Collectors
Write-Host "`n========== TEST 3: Register Collectors ==========" -ForegroundColor Green
$collectorIds = @()

for ($i = 1; $i -le 3; $i++) {
    $collectorPayload = @{
        name = "Collector $i"
        email = "collector$i@ecosolution.com"
        password = "Collector@123456"
    } | ConvertTo-Json

    try {
        $collResp = Invoke-WebRequest -Uri "$baseUrl/auth/register" -Method Post -ContentType "application/json" -Body $collectorPayload -UseBasicParsing
        $collData = $collResp.Content | ConvertFrom-Json
        $collectorIds += $collData.id
        Write-Host "✓ Collector $i registered: $($collData.id)" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Collector $i registration failed: $_" -ForegroundColor Red
    }
}

# Test 4: Create Collector Scores
Write-Host "`n========== TEST 4: Create Collector Scores ==========" -ForegroundColor Green
$headers = @{"Authorization" = "Bearer $token"}

foreach ($id in $collectorIds) {
    $scorePayload = @{
        responseRate = 95.50
        completionRate = 92.30
        complaintRate = 2.10
        reliabilityScore = 93.27
    } | ConvertTo-Json

    try {
        $scoreResp = Invoke-WebRequest -Uri "$baseUrl/collectors/$id/score" -Method Put -ContentType "application/json" -Body $scorePayload -Headers $headers -UseBasicParsing
        $scoreData = $scoreResp.Content | ConvertFrom-Json
        Write-Host "✓ Score created for $($scoreData.collectorName): Reliability=$($scoreData.reliabilityScore)" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Score creation failed for $id: $_" -ForegroundColor Red
    }
}

# Test 5: Get All Scores
Write-Host "`n========== TEST 5: Get All Collector Scores ==========" -ForegroundColor Green
try {
    $allScoresResp = Invoke-WebRequest -Uri "$baseUrl/collectors/scores" -Method Get -Headers $headers -UseBasicParsing
    $allScores = $allScoresResp.Content | ConvertFrom-Json
    Write-Host "✓ Retrieved $($allScores.Count) scores" -ForegroundColor Green

    $allScores | ForEach-Object {
        Write-Host "  - $($_.collectorName): Reliability=$($_.reliabilityScore), ComplaintRate=$($_.complaintRate)%" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "✗ Get scores failed: $_" -ForegroundColor Red
}

# Test 6: Get Single Collector Score
if ($collectorIds.Count -gt 0) {
    Write-Host "`n========== TEST 6: Get Single Collector Score ==========" -ForegroundColor Green
    try {
        $singleResp = Invoke-WebRequest -Uri "$baseUrl/collectors/$($collectorIds[0])/score" -Method Get -Headers $headers -UseBasicParsing
        $single = $singleResp.Content | ConvertFrom-Json
        Write-Host "✓ Retrieved score for: $($single.collectorName)" -ForegroundColor Green
        Write-Host "  - Reliability Score: $($single.reliabilityScore)" -ForegroundColor Cyan
        Write-Host "  - Response Rate: $($single.responseRate)%" -ForegroundColor Cyan
        Write-Host "  - Completion Rate: $($single.completionRate)%" -ForegroundColor Cyan
        Write-Host "  - Complaint Rate: $($single.complaintRate)%" -ForegroundColor Cyan
    }
    catch {
        Write-Host "✗ Get single score failed: $_" -ForegroundColor Red
    }
}

# Test 7: Create Status History
if ($collectorIds.Count -gt 0) {
    Write-Host "`n========== TEST 7: Create Status History ==========" -ForegroundColor Green
    $statusPayload = @{
        statusFrom = "PENDING"
        statusTo = "ASSIGNED"
        reason = "Collector account activated"
    } | ConvertTo-Json

    try {
        $statusResp = Invoke-WebRequest -Uri "$baseUrl/collectors/$($collectorIds[0])/status-history" -Method Post -ContentType "application/json" -Body $statusPayload -Headers $headers -UseBasicParsing
        $statusData = $statusResp.Content | ConvertFrom-Json
        Write-Host "✓ Status history created" -ForegroundColor Green
        Write-Host "  - $($statusData.statusFrom) → $($statusData.statusTo)" -ForegroundColor Cyan
        Write-Host "  - Reason: $($statusData.reason)" -ForegroundColor Cyan
    }
    catch {
        Write-Host "✗ Create status history failed: $_" -ForegroundColor Red
    }
}

# Test 8: Get Status History
if ($collectorIds.Count -gt 0) {
    Write-Host "`n========== TEST 8: Get Status History ==========" -ForegroundColor Green
    try {
        $historyResp = Invoke-WebRequest -Uri "$baseUrl/collectors/$($collectorIds[0])/status-history" -Method Get -Headers $headers -UseBasicParsing
        $history = $historyResp.Content | ConvertFrom-Json
        Write-Host "✓ Retrieved $($history.Count) status history records" -ForegroundColor Green

        $history | ForEach-Object {
            Write-Host "  - $($_.statusFrom) → $($_.statusTo) | Reason: $($_.reason)" -ForegroundColor Cyan
        }
    }
    catch {
        Write-Host "✗ Get status history failed: $_" -ForegroundColor Red
    }
}

# Test 9: Test Filtering
Write-Host "`n========== TEST 9: Test Filtering (minReliability=90) ==========" -ForegroundColor Green
try {
    $filteredResp = Invoke-WebRequest -Uri "$baseUrl/collectors/scores?minReliability=90&page=0&size=10" -Method Get -Headers $headers -UseBasicParsing
    $filtered = $filteredResp.Content | ConvertFrom-Json
    Write-Host "✓ Filtered results: $($filtered.Count) records with reliability >= 90" -ForegroundColor Green
}
catch {
    Write-Host "✗ Filtering failed: $_" -ForegroundColor Red
}

Write-Host "`n========== ALL TESTS COMPLETED ==========" -ForegroundColor Green

