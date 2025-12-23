# Configuração
$baseUrl = "http://localhost:8080/api/v1"
$ErrorActionPreference = "Stop"

function Test-Endpoint {
    param (
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers,
        [object]$Body,
        [string]$Description,
        [int]$ExpectedStatus = 200
    )

    Write-Host "`n=== $Description ===" -ForegroundColor Cyan
    Write-Host "$Method $Url" -ForegroundColor DarkGray
    
    try {
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $Headers
            ContentType = "application/json; charset=utf-8"
        }
        
        if ($Body -ne $null) {
            $jsonBody = $Body | ConvertTo-Json -Depth 10 -Compress
            Write-Host "Sending Body: $jsonBody" -ForegroundColor DarkGray
            $params.Body = $jsonBody
        } else {
             Write-Host "Body is null" -ForegroundColor DarkGray
        }

        $response = Invoke-RestMethod @params
        Write-Host "Success: $($response | ConvertTo-Json -Depth 2)" -ForegroundColor Green
        return $response
    }
    catch {
        $errorResponse = $_.Exception.Response
        $statusCode = $errorResponse.StatusCode.value__
        
        if ($statusCode -eq $ExpectedStatus) {
            Write-Host "Success (Expected Error): $statusCode" -ForegroundColor Green
            # Read error body if possible
            $reader = New-Object System.IO.StreamReader($errorResponse.GetResponseStream())
            $errorBody = $reader.ReadToEnd()
            Write-Host "Error Body: $errorBody" -ForegroundColor DarkGray
            return $errorBody
        } else {
            Write-Host "Failed with status $statusCode (Expected $ExpectedStatus)" -ForegroundColor Red
            if ($errorResponse) {
                $reader = New-Object System.IO.StreamReader($errorResponse.GetResponseStream())
                Write-Host "Error Body: $($reader.ReadToEnd())" -ForegroundColor Red
            }
            Write-Error $_
        }
    }
}

# 1. Login
$loginBody = @{
    email = "admin@douglas.com"
    password = "admin123"
}
$loginResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/login" -Body $loginBody -Description "Login Admin"
$token = $loginResponse.data.token
$headers = @{
    Authorization = "Bearer $token"
}

# 2. Get Sector (List and pick first active)
$sectors = Test-Endpoint -Method "GET" -Url "$baseUrl/sectors" -Headers $headers -Description "List Sectors"
if ($sectors.data.Count -eq 0) {
    # Create one if none
    $sectorBody = @{ name = "Test Sector"; code = "TEST_SECTOR_$(Get-Random)" }
    $sectorResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/sectors" -Headers $headers -Body $sectorBody -Description "Create Sector"
    $sectorId = $sectorResponse.data.id
} else {
    $sectorId = $sectors.data[0].id
}
Write-Host "Using Sector ID: $sectorId"

# 3. Get or Create Open Period
Write-Host "Checking for existing periods..."
$periods = Test-Endpoint -Method "GET" -Url "$baseUrl/periods?sectorId=$sectorId" -Headers $headers -Description "List Periods"
$openPeriod = $periods.data | Where-Object { $_.status -eq "OPEN" }

if ($openPeriod) {
    Write-Host "Found existing OPEN period: $($openPeriod.month)/$($openPeriod.year)"
    $periodId = $openPeriod.id
} else {
    Write-Host "No open period found, creating new one..."
    # Create Period 07/2026
    $periodMonth = 7
    $periodYear = 2026
    
    $createPeriodBody = @{
        month = $periodMonth
        year = $periodYear
        sectorId = $sectorId
    }
    
    try {
        $periodResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/periods" -Headers $headers -Body $createPeriodBody -Description "Create Period 07/2026"
        $periodId = $periodResponse.data.id
    } catch {
        # Fallback if creation fails (maybe it exists but is closed?)
        $periods = Test-Endpoint -Method "GET" -Url "$baseUrl/periods?sectorId=$sectorId" -Headers $headers -Description "List Periods Again"
        $targetPeriod = $periods.data | Where-Object { $_.month -eq $periodMonth -and $_.year -eq $periodYear }
        $periodId = $targetPeriod.id
        
        if ($targetPeriod.status -eq "CLOSED") {
             Test-Endpoint -Method "POST" -Url "$baseUrl/periods/$periodId/reopen" -Headers $headers -Description "Reopen Period"
        }
    }
}

Write-Host "Using Period ID: $periodId"

# 4. Insert Notification (Should Succeed)
$notificationBody = @{
    periodId = $periodId
    sectorId = $sectorId
    notificationDate = "2026-06-15"
    classification = "INCIDENT_WITHOUT_HARM"
    category = "Test Category"
    subcategory = "Test Subcategory"
    description = "Test Description Open"
    isSelfNotification = $false
    professionalCategory = "Nurse"
    professionalName = "Nurse Joy"
}

Test-Endpoint -Method "POST" -Url "$baseUrl/notifications" -Headers $headers -Body $notificationBody -Description "Insert Notification (Open Period)"

# 5. Close Period
Test-Endpoint -Method "POST" -Url "$baseUrl/periods/$periodId/close" -Headers $headers -Description "Close Period"

# 6. Insert Notification (Should Fail - 409 Conflict)
$notificationBody.description = "Test Description Closed"
Test-Endpoint -Method "POST" -Url "$baseUrl/notifications" -Headers $headers -Body $notificationBody -Description "Insert Notification (Closed Period)" -ExpectedStatus 409

# 7. Reopen Period
Test-Endpoint -Method "POST" -Url "$baseUrl/periods/$periodId/reopen" -Headers $headers -Description "Reopen Period"

# 8. Insert Notification (Should Succeed)
$notificationBody.description = "Test Description Reopened"
Test-Endpoint -Method "POST" -Url "$baseUrl/notifications" -Headers $headers -Body $notificationBody -Description "Insert Notification (Reopened Period)"
