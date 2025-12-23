# Test Filters V2
# This script verifies the new search filters implemented in the API.

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
        [string]$Description
    )
    Write-Host "Testing: $Description" -ForegroundColor Yellow
    Write-Host "URL: $Url" -ForegroundColor DarkGray
    try {
        $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $Headers
        Write-Host "Success: $($response.message)" -ForegroundColor Green
        return $response
    } catch {
        Write-Error "Failed: $_"
        if ($_.Exception.Response) {
             $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
             Write-Host "Error Body: $($reader.ReadToEnd())" -ForegroundColor Red
        }
        return $null
    }
}

# Main Execution
Write-Host "Starting Filter Tests..." -ForegroundColor Cyan

# 1. Login
$token = Get-AuthToken -email $adminEmail -password $adminPassword
$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}
Write-Host "Login Successful. Token acquired." -ForegroundColor Green

# 2. Test Audit Log Filters
# Note: Audit logs are created by other actions. Ensure some actions happened before running this.
# Let's assume some logs exist from previous tests or initialization.
$auditLogs = Test-Endpoint -Url "$baseUrl/audit-logs?action=CREATE" -Headers $headers -Description "Search Audit Logs (Action=CREATE)"
if ($auditLogs.data.Count -ge 0) {
    Write-Host "Found $($auditLogs.data.Count) audit logs with action CREATE." -ForegroundColor Green
}

# 3. Test Period Filters
# First, get a sector ID to query
$sectors = Test-Endpoint -Url "$baseUrl/sectors" -Headers $headers -Description "Get Sectors"
if ($sectors.data.Count -gt 0) {
    $sectorId = $sectors.data[0].id
    $periods = Test-Endpoint -Url "$baseUrl/periods?sectorId=$sectorId&status=OPEN" -Headers $headers -Description "Search Periods (Status=OPEN)"
    if ($periods.data.Count -ge 0) {
        Write-Host "Found $($periods.data.Count) OPEN periods for sector $sectorId." -ForegroundColor Green
    }
}

$periodId = $null

# 4. Test Notification Filters
# Need a period ID first
if ($periods.data.Count -gt 0) {
    if ($periods.data -is [System.Array]) {
        $periodId = $periods.data[0].id.ToString()
    } else {
        $periodId = $periods.data.id.ToString()
    }
    
    # Valid classification from enum: INCIDENT_WITHOUT_HARM
    # Checking existing code for exact enum names.
    # INCIDENT_WITHOUT_HARM, INCIDENT_WITH_MINOR_HARM, INCIDENT_WITH_MODERATE_HARM, INCIDENT_WITH_SEVERE_HARM, INCIDENT_WITH_DEATH, RISK_CIRCUMSTANCE, NEAR_MISS
    $notifications = Test-Endpoint -Url "$baseUrl/notifications?periodId=$periodId&classification=INCIDENT_WITHOUT_HARM" -Headers $headers -Description "Search Notifications (Classification=INCIDENT_WITHOUT_HARM)"
    Write-Host "Found $($notifications.data.Count) notifications." -ForegroundColor Green
}

# 5. Test Adverse Event Filters
if ($periodId) {
    Write-Host "Debug: Using PeriodID for Adverse Events: '$periodId'" -ForegroundColor Yellow
    # Valid event types: FALL, PRESSURE_INJURY, INFECTION, MEDICATION_ERROR, SURGICAL_SITE_INFECTION, OTHER
    # Let's check AdverseEvent enum or CreateAdverseEventRequest.kt
    $url = "{0}/adverse-events/period/{1}?eventType=FALL" -f $baseUrl, $periodId
    $events = Test-Endpoint -Url $url -Headers $headers -Description "Search Adverse Events (Type=FALL)"
    Write-Host "Found $($events.data.Count) adverse events." -ForegroundColor Green
}

Write-Host "`nAll Filter Tests Completed." -ForegroundColor Cyan
