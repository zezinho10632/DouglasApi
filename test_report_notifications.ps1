# Test Report Panel Notifications and Adverse Events
# Prerequisites: Backend running on localhost:8080

$baseUrl = "http://localhost:8080/api/v1"
$timestamp = Get-Date -Format "yyyyMMddHHmmss"

# Helper function
function Invoke-DouglasApi {
    param (
        [string]$Method,
        [string]$Uri,
        [hashtable]$Body = $null,
        [hashtable]$Headers = @{},
        [string]$Description
    )

    try {
        $params = @{
            Method = $Method
            Uri = $Uri
            Headers = $Headers
            ContentType = "application/json"
        }

        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
        }

        Write-Host "[$Method] $Uri" -ForegroundColor Cyan
        $response = Invoke-RestMethod @params
        return $response
    } catch {
        Write-Host "Error in $Description : $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $errBody = $reader.ReadToEnd()
            Write-Host "Response Body: $errBody" -ForegroundColor Red
        }
        exit 1
    }
}

# 1. Login
$loginBody = @{ email = "admin@douglas.com"; password = "admin123" }
$loginRes = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/auth/login" -Body $loginBody -Description "Login"
$token = $loginRes.data.token
$headers = @{ Authorization = "Bearer $token" }
Write-Host "Logged in."

# 2. Create Sector
$sectorBody = @{ name = "Report Sector $timestamp"; code = "RPT-$timestamp" }
$sectorRes = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/sectors" -Body $sectorBody -Headers $headers -Description "Create Sector"
$sectorId = $sectorRes.data.id

# 3. Create Period
$periodBody = @{ month = 3; year = 2025; sectorId = $sectorId }
$periodRes = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/periods" -Body $periodBody -Headers $headers -Description "Create Period"
$periodId = $periodRes.data.id

# 4. Create Adverse Event
$aeBody = @{
    periodId = $periodId
    sectorId = $sectorId
    eventDate = "2025-03-15"
    eventType = "FALL"
    description = "Test Event for Report"
}
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/adverse-events" -Body $aeBody -Headers $headers -Description "Create Adverse Event" | Out-Null

# 5. Create Notification
$notifBody = @{
    periodId = $periodId
    sectorId = $sectorId
    notificationDate = "2025-03-16"
    classification = "INCIDENT_WITHOUT_HARM"
    category = "Test"
    subcategory = "Report Test"
    description = "Test Notification for Report"
    isSelfNotification = $false
    professionalCategory = "Nurse"
    professionalName = "Joy"
}
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/notifications" -Body $notifBody -Headers $headers -Description "Create Notification" | Out-Null

# 6. Get Report Panel
$reportRes = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/reports/panel?periodId=$periodId&sectorId=$sectorId" -Headers $headers -Description "Get Report Panel"

# 7. Verify Data
$reportData = $reportRes.data

Write-Host "`nVerifying Report Data:" -ForegroundColor Yellow

if ($reportData.adverseEvents.Count -ge 1) {
    Write-Host "[OK] Adverse Events found: $($reportData.adverseEvents.Count)" -ForegroundColor Green
} else {
    Write-Host "[FAIL] No Adverse Events found!" -ForegroundColor Red
}

if ($reportData.notifications) {
    if ($reportData.notifications.Count -ge 1) {
        Write-Host "[OK] Notifications found: $($reportData.notifications.Count)" -ForegroundColor Green
        Write-Host "     First Notification Category: $($reportData.notifications[0].category)"
    } else {
        Write-Host "[FAIL] Notifications list is empty!" -ForegroundColor Red
    }
} else {
    Write-Host "[FAIL] Notifications field missing from response!" -ForegroundColor Red
}

Write-Host "`nTest Complete."
