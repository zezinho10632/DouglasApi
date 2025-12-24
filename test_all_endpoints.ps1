# Test All Endpoints
# This script verifies the main flows and the new search filters implemented in the API.

$baseUrl = "http://localhost:8080/api/v1"
$adminEmail = "admin@douglas.com"
$adminPassword = "admin123"

# Function to get Auth Token
function Get-AuthToken {
    param (
        [string]$email,
        [string]$password
    )
    $body = @{
        email = $email
        password = $password
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $body -ContentType "application/json"
        return $response.data.token
    } catch {
        Write-Error "Login failed: $_"
        exit 1
    }
}

# Function to test endpoint
function Test-Endpoint {
    param (
        [string]$Url,
        [string]$Method = "GET",
        [hashtable]$Headers,
        [object]$Body = $null,
        [string]$Description
    )
    Write-Host "Testing: $Description" -ForegroundColor Yellow
    Write-Host "URL: $Url" -ForegroundColor DarkGray
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $Headers
            ContentType = "application/json"
        }
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
        }

        $response = Invoke-RestMethod @params
        Write-Host "Success: $($response.message)" -ForegroundColor Green
        return $response
    } catch {
        Write-Host "Failed: $_" -ForegroundColor Red
        if ($_.Exception.Response) {
             $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
             Write-Host "Error Body: $($reader.ReadToEnd())" -ForegroundColor Red
        }
        return $null
    }
}

# Main Execution
Write-Host "Starting All Endpoint Tests..." -ForegroundColor Cyan

# 1. Login
$token = Get-AuthToken -email $adminEmail -password $adminPassword
$headers = @{
    Authorization = "Bearer $token"
}
Write-Host "Login Successful. Token acquired." -ForegroundColor Green

# 2. Sectors
$sectors = Test-Endpoint -Url "$baseUrl/sectors" -Headers $headers -Description "List Sectors"
if ($sectors.data.Count -eq 0) {
    Write-Host "No sectors found. Cannot proceed with period tests." -ForegroundColor Red
    exit
}
$sectorId = $sectors.data[0].id
Write-Host "Using Sector ID: $sectorId" -ForegroundColor Gray

# 3. Periods
# List Periods
$periods = Test-Endpoint -Url "$baseUrl/periods?sectorId=$sectorId" -Headers $headers -Description "List Periods"

# Create Period (if none or just to test) - skipping to avoid conflict if open exists, or just list existing
# We'll use the first available period for notification tests
$periodId = $null
if ($periods.data.Count -gt 0) {
    $periodId = $periods.data[0].id
    Write-Host "Using existing Period ID: $periodId" -ForegroundColor Gray
    
    # Test Period Filter
    Test-Endpoint -Url "$baseUrl/periods?sectorId=$sectorId&status=OPEN" -Headers $headers -Description "Filter Periods (Status=OPEN)" | Out-Null
} else {
    Write-Host "No periods found. Attempting to create one." -ForegroundColor Yellow
    $createPeriodBody = @{
        sectorId = $sectorId
        month = 1
        year = 2025
    }
    $newPeriod = Test-Endpoint -Url "$baseUrl/periods" -Method "POST" -Headers $headers -Body $createPeriodBody -Description "Create Period"
    if ($newPeriod) {
        $periodId = $newPeriod.data.id
    }
}

if (-not $periodId) {
    Write-Host "Could not get a valid Period ID. Aborting dependent tests." -ForegroundColor Red
    exit
}

# 4. Notifications
# Create Notification
$notifBody = @{
    periodId = $periodId
    sectorId = $sectorId
    notificationDate = (Get-Date).ToString("yyyy-MM-dd")
    classification = "INCIDENT_WITHOUT_HARM"
    category = "Test Category"
    subcategory = "Test Subcategory"
    description = "Test Description from automated script"
    isSelfNotification = $true
    professionalCategory = "NURSE"
    professionalName = "Nurse Joy"
}
$createdNotif = Test-Endpoint -Url "$baseUrl/notifications" -Method "POST" -Headers $headers -Body $notifBody -Description "Create Notification"

if ($createdNotif) {
    $notifId = $createdNotif.data.id
    
    # Get Notification
    Test-Endpoint -Url "$baseUrl/notifications/$notifId" -Headers $headers -Description "Get Notification by ID" | Out-Null
    
    # Update Notification
    $updateNotifBody = @{
        notificationDate = (Get-Date).ToString("yyyy-MM-dd")
        classification = "RISK_CIRCUMSTANCE"
        category = "Updated Category"
        subcategory = "Updated Subcategory"
        description = "Updated Description"
        isSelfNotification = $true
        professionalCategory = "NURSE"
        professionalName = "Updated Nurse"
    }
    Test-Endpoint -Url "$baseUrl/notifications/$notifId" -Method "PUT" -Headers $headers -Body $updateNotifBody -Description "Update Notification" | Out-Null
    
    # Filter Notifications (Classification)
    Test-Endpoint -Url "$baseUrl/notifications?periodId=$periodId&classification=RISK_CIRCUMSTANCE" -Headers $headers -Description "Filter Notifications (Classification=RISK_CIRCUMSTANCE)" | Out-Null
    
    # Filter Notifications (Category)
    Test-Endpoint -Url "$baseUrl/notifications?periodId=$periodId&category=Updated" -Headers $headers -Description "Filter Notifications (Category='Updated')" | Out-Null

    # Delete Notification
    Test-Endpoint -Url "$baseUrl/notifications/$notifId" -Method "DELETE" -Headers $headers -Description "Delete Notification" | Out-Null
}

# 5. Adverse Events
# Create Adverse Event
$aeBody = @{
    periodId = $periodId
    sectorId = $sectorId
    eventDate = (Get-Date).ToString("yyyy-MM-dd")
    eventType = "FALL"
    description = "Patient fell from bed"
}
$createdAe = Test-Endpoint -Url "$baseUrl/adverse-events" -Method "POST" -Headers $headers -Body $aeBody -Description "Create Adverse Event"

if ($createdAe) {
    $aeId = $createdAe.data.id
    
    # Get Adverse Event
    Test-Endpoint -Url "$baseUrl/adverse-events/$aeId" -Headers $headers -Description "Get Adverse Event by ID" | Out-Null
    
    # Update Adverse Event
    $updateAeBody = @{
        eventDate = (Get-Date).ToString("yyyy-MM-dd")
        eventType = "PRESSURE_INJURY"
        description = "Pressure injury stage 2"
    }
    Test-Endpoint -Url "$baseUrl/adverse-events/$aeId" -Method "PUT" -Headers $headers -Body $updateAeBody -Description "Update Adverse Event" | Out-Null
    
    # Filter Adverse Events
    Test-Endpoint -Url "$baseUrl/adverse-events/period/${periodId}?eventType=PRESSURE_INJURY" -Headers $headers -Description "Filter Adverse Events (Type=PRESSURE_INJURY)" | Out-Null

    # Delete Adverse Event
    Test-Endpoint -Url "$baseUrl/adverse-events/$aeId" -Method "DELETE" -Headers $headers -Description "Delete Adverse Event" | Out-Null
}

# 6. Audit Logs
# Since we just performed actions, there should be logs.
Test-Endpoint -Url "$baseUrl/audit-logs?action=CREATE" -Headers $headers -Description "Filter Audit Logs (Action=CREATE)" | Out-Null
Test-Endpoint -Url "$baseUrl/audit-logs?resource=Notification" -Headers $headers -Description "Filter Audit Logs (Resource=Notification)" | Out-Null

# 7. Reports (Cumulative)
Write-Host "Testing Cumulative Reports..." -ForegroundColor Cyan
$startDate = (Get-Date).AddMonths(-2).ToString("yyyy-MM-dd")
$endDate = (Get-Date).ToString("yyyy-MM-dd")

# Custom Range
Test-Endpoint -Url "$baseUrl/reports/panel/cumulative?sectorId=$sectorId&periodicity=CUSTOM&startDate=$startDate&endDate=$endDate" -Headers $headers -Description "Cumulative Report (Custom Range)" | Out-Null

# Annual
$currentYear = (Get-Date).Year
Test-Endpoint -Url "$baseUrl/reports/panel/cumulative?sectorId=$sectorId&periodicity=ANNUAL&year=$currentYear" -Headers $headers -Description "Cumulative Report (Annual)" | Out-Null

Write-Host "All tests completed." -ForegroundColor Cyan
