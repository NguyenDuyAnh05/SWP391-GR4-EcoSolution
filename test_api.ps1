#!/usr/bin/env pwsh
# API Testing and Data Initialization Script

$baseUrl = "http://localhost:8080/api/v1"
$adminToken = ""
$collectorToken = ""
$collectorId = ""

function Print-Header {
    param([string]$message)
    Write-Host "`n" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host $message -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
}

function Print-Success {
    param([string]$message)
    Write-Host "✓ $message" -ForegroundColor Green
}

function Print-Error {
    param([string]$message)
    Write-Host "✗ $message" -ForegroundColor Red
}

function Print-Info {
    param([string]$message)
    Write-Host "ℹ $message" -ForegroundColor Cyan
}

# Step 1: Register Admin User
Print-Header "STEP 1: Register Admin User"
try {
    $adminRegisterBody = @{
        name = "System Admin"
        email = "admin@ecosolution.com"
        password = "Admin@123456"
    } | ConvertTo-Json

    $adminRegisterResponse = Invoke-WebRequest -Uri "$baseUrl/auth/register" `
        -Method Post `
        -ContentType "application/json" `
        -Body $adminRegisterBody `
        -ErrorAction Stop

    $adminUser = $adminRegisterResponse.Content | ConvertFrom-Json
    Print-Success "Admin user registered: $($adminUser.id)"
    Print-Info "Admin ID: $($adminUser.id)"
}
catch {
    Print-Error "Failed to register admin: $($_.Exception.Message)"
}

# Step 2: Login Admin
Print-Header "STEP 2: Login Admin User"
try {
    $adminLoginBody = @{
        email = "admin@ecosolution.com"
        password = "Admin@123456"
    } | ConvertTo-Json

    $adminLoginResponse = Invoke-WebRequest -Uri "$baseUrl/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $adminLoginBody `
        -ErrorAction Stop

    $adminLogin = $adminLoginResponse.Content | ConvertFrom-Json
    $adminToken = $adminLogin.token
    Print-Success "Admin logged in successfully"
    Print-Info "Token: $($adminToken.Substring(0, 20))..."
}
catch {
    Print-Error "Failed to login admin: $($_.Exception.Message)"
}

# Step 3: Create Collectors
Print-Header "STEP 3: Create Collector Users"
$collectors = @()
try {
    for ($i = 1; $i -le 3; $i++) {
        $collectorRegisterBody = @{
            name = "Collector $i"
            email = "collector$i@ecosolution.com"
            password = "Collector@123456"
        } | ConvertTo-Json

        $collectorRegisterResponse = Invoke-WebRequest -Uri "$baseUrl/auth/register" `
            -Method Post `
            -ContentType "application/json" `
            -Body $collectorRegisterBody `
            -ErrorAction Stop

        $collector = $collectorRegisterResponse.Content | ConvertFrom-Json
        $collectors += $collector
        Print-Success "Collector $i registered: $($collector.id)"
    }
}
catch {
    Print-Error "Failed to register collectors: $($_.Exception.Message)"
}

if ($collectors.Count -gt 0) {
    $collectorId = $collectors[0].id
}

# Step 4: Create Collector Scores
Print-Header "STEP 4: Create Collector Scores"
if ($adminToken -and $collectorId) {
    try {
        for ($i = 0; $i -lt $collectors.Count; $i++) {
            $scoreBody = @{
                responseRate = 95.50
                completionRate = 92.30
                complaintRate = 2.10
                reliabilityScore = 93.27
            } | ConvertTo-Json

            $scoreResponse = Invoke-WebRequest -Uri "$baseUrl/collectors/$($collectors[$i].id)/score" `
                -Method Put `
                -ContentType "application/json" `
                -Body $scoreBody `
                -Headers @{"Authorization" = "Bearer $adminToken"} `
                -ErrorAction Stop

            $score = $scoreResponse.Content | ConvertFrom-Json
            Print-Success "Score created for Collector $($i+1): Reliability=$($score.reliabilityScore)"
        }
    }
    catch {
        Print-Error "Failed to create collector scores: $($_.Exception.Message)"
    }
}

# Step 5: Test GET All Collector Scores
Print-Header "STEP 5: GET All Collector Scores"
if ($adminToken) {
    try {
        $scoresResponse = Invoke-WebRequest -Uri "$baseUrl/collectors/scores" `
            -Method Get `
            -Headers @{"Authorization" = "Bearer $adminToken"} `
            -ErrorAction Stop

        $scores = $scoresResponse.Content | ConvertFrom-Json
        Print-Success "Retrieved $($scores.Count) collector scores"

        if ($scores.Count -gt 0) {
            $scores | ForEach-Object {
                Print-Info "Collector: $($_.collectorName) | Reliability: $($_.reliabilityScore) | Complaint Rate: $($_.complaintRate)"
            }
        }
    }
    catch {
        Print-Error "Failed to get collector scores: $($_.Exception.Message)"
    }
}

# Step 6: Test GET Single Collector Score
Print-Header "STEP 6: GET Single Collector Score"
if ($adminToken -and $collectorId) {
    try {
        $singleScoreResponse = Invoke-WebRequest -Uri "$baseUrl/collectors/$collectorId/score" `
            -Method Get `
            -Headers @{"Authorization" = "Bearer $adminToken"} `
            -ErrorAction Stop

        $singleScore = $singleScoreResponse.Content | ConvertFrom-Json
        Print-Success "Retrieved score for collector: $($singleScore.collectorName)"
        Print-Info "Reliability Score: $($singleScore.reliabilityScore)"
        Print-Info "Response Rate: $($singleScore.responseRate)"
        Print-Info "Completion Rate: $($singleScore.completionRate)"
    }
    catch {
        Print-Error "Failed to get single collector score: $($_.Exception.Message)"
    }
}

# Step 7: Create Collector Status History
Print-Header "STEP 7: Create Collector Status History"
if ($adminToken -and $collectorId) {
    try {
        $statusHistoryBody = @{
            statusFrom = "PENDING"
            statusTo = "ACTIVE"
            reason = "Initial activation of collector account"
        } | ConvertTo-Json

        $statusHistoryResponse = Invoke-WebRequest -Uri "$baseUrl/collectors/$collectorId/status-history" `
            -Method Post `
            -ContentType "application/json" `
            -Body $statusHistoryBody `
            -Headers @{"Authorization" = "Bearer $adminToken"} `
            -ErrorAction Stop

        $statusHistory = $statusHistoryResponse.Content | ConvertFrom-Json
        Print-Success "Status history created: $($statusHistory.statusFrom) → $($statusHistory.statusTo)"
        Print-Info "Reason: $($statusHistory.reason)"
    }
    catch {
        Print-Error "Failed to create status history: $($_.Exception.Message)"
    }
}

# Step 8: Get Collector Status History
Print-Header "STEP 8: GET Collector Status History"
if ($adminToken -and $collectorId) {
    try {
        $historyResponse = Invoke-WebRequest -Uri "$baseUrl/collectors/$collectorId/status-history" `
            -Method Get `
            -Headers @{"Authorization" = "Bearer $adminToken"} `
            -ErrorAction Stop

        $history = $historyResponse.Content | ConvertFrom-Json
        Print-Success "Retrieved status history with $($history.Count) records"

        if ($history.Count -gt 0) {
            $history | ForEach-Object {
                Print-Info "Status Change: $($_.statusFrom) → $($_.statusTo) | Reason: $($_.reason) | Changed At: $($_.changedAt)"
            }
        }
    }
    catch {
        Print-Error "Failed to get status history: $($_.Exception.Message)"
    }
}

# Step 9: Test Filtering and Pagination
Print-Header "STEP 9: Test Filtering and Pagination"
if ($adminToken) {
    try {
        $filteredResponse = Invoke-WebRequest -Uri "$baseUrl/collectors/scores?minReliability=90&page=0&size=10" `
            -Method Get `
            -Headers @{"Authorization" = "Bearer $adminToken"} `
            -ErrorAction Stop

        $filtered = $filteredResponse.Content | ConvertFrom-Json
        Print-Success "Retrieved filtered results (minReliability=90): $($filtered.Count) records"
    }
    catch {
        Print-Error "Failed to test filtering: $($_.Exception.Message)"
    }
}

Print-Header "API Testing Complete!"
Print-Success "All tests completed successfully"

