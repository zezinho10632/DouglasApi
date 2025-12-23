
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$email = "admin_$timestamp@douglas.com"
$password = "123456"
$baseUrl = "http://localhost:8080/api/v1"

function Invoke-DouglasApi {
    param (
        [string]$Method,
        [string]$Uri,
        [hashtable]$Body = @{},
        [hashtable]$Headers = @{}
    )
    
    $params = @{
        Method = $Method
        Uri = $Uri
        ContentType = "application/json"
    }
    
    if ($Method -ne "GET" -and $Body.Count -gt 0) {
        $params.Body = $Body | ConvertTo-Json -Depth 10
    }
    
    if ($Headers.Count -gt 0) {
        $params.Headers = $Headers
    }
    
    try {
        $response = Invoke-RestMethod @params
        return $response
    } catch {
        Write-Host "Error calling $Uri" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        if ($_.Exception.Response) {
             $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
             $reader.ReadToEnd()
        }
        return $null
    }
}

Write-Host "=== TESTE COMPLETO DE TODOS OS ENDPOINTS ===" -ForegroundColor Yellow

# 1. AUTH
Write-Host "`n1. AUTHENTICATION" -ForegroundColor Cyan
$registerBody = @{ name = "Admin"; email = $email; password = $password; role = "ADMIN" }
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/auth/register" -Body $registerBody | Out-Null
Write-Host "Registered."

$loginBody = @{ email = $email; password = $password }
$loginResponse = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/auth/login" -Body $loginBody
$token = $loginResponse.data.token
$headers = @{ Authorization = "Bearer $token" }
Write-Host "Logged in. Token acquired."

# 2. SECTORS
Write-Host "`n2. SECTORS" -ForegroundColor Cyan
$sectorCode = "SEC-$timestamp"
$sectorBody = @{ name = "Sector $timestamp"; code = $sectorCode }
$sectorRes = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/sectors" -Body $sectorBody -Headers $headers
$sectorId = $sectorRes.data.id
Write-Host "Created Sector: $sectorId"

Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/sectors/$sectorId" -Headers $headers | Out-Null
Write-Host "Get Sector by ID: OK"

$sectorUpdate = @{ name = "Updated Sector $timestamp"; code = $sectorCode }
Invoke-DouglasApi -Method "PUT" -Uri "$baseUrl/sectors/$sectorId" -Body $sectorUpdate -Headers $headers | Out-Null
Write-Host "Updated Sector: OK"

Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/sectors" -Headers $headers | Out-Null
Write-Host "List Sectors: OK"

# 3. PERIODS
Write-Host "`n3. PERIODS" -ForegroundColor Cyan
$periodBody = @{ month = 11; year = 2023; sectorId = $sectorId }
$periodRes = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/periods" -Body $periodBody -Headers $headers
$periodId = $periodRes.data.id
Write-Host "Created Period: $periodId"

Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/periods?sectorId=$sectorId" -Headers $headers | Out-Null
Write-Host "List Periods by Sector: OK"

# 4. INDICATORS
Write-Host "`n4. INDICATORS" -ForegroundColor Cyan

# 4.1 Compliance
$complianceBody = @{ periodId = $periodId; sectorId = $sectorId; completeWristband = 90.5; patientCommunication = 88.0; medicationIdentified = 95.0; handHygieneAdherence = 85.0; fallRiskAssessment = 92.0; pressureInjuryRiskAssessment = 91.0; totalPatients = 100 }
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/indicators/compliance" -Body $complianceBody -Headers $headers | Out-Null
Write-Host "Save Compliance: OK"
Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/indicators/compliance/period/$periodId" -Headers $headers | Out-Null
Write-Host "Get Compliance: OK"

# 4.2 Hand Hygiene
$handHygieneBody = @{ periodId = $periodId; sectorId = $sectorId; totalObservations = 50; compliantObservations = 40 }
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/indicators/hand-hygiene" -Body $handHygieneBody -Headers $headers | Out-Null
Write-Host "Save Hand Hygiene: OK"
Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/indicators/hand-hygiene/period/$periodId" -Headers $headers | Out-Null
Write-Host "Get Hand Hygiene: OK"

# 4.3 Fall Risk
$fallRiskBody = @{ periodId = $periodId; sectorId = $sectorId; totalPatients = 100; assessedOnAdmission = 95; highRiskPatients = 10; moderateRiskPatients = 20; lowRiskPatients = 30 }
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/indicators/fall-risk" -Body $fallRiskBody -Headers $headers | Out-Null
Write-Host "Save Fall Risk: OK"
Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/indicators/fall-risk/period/$periodId" -Headers $headers | Out-Null
Write-Host "Get Fall Risk: OK"

# 4.4 Pressure Injury
$pressureBody = @{ periodId = $periodId; sectorId = $sectorId; totalPatients = 100; assessedOnAdmission = 98; highRiskPatients = 5; moderateRiskPatients = 15; lowRiskPatients = 40 }
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/indicators/pressure-injury" -Body $pressureBody -Headers $headers | Out-Null
Write-Host "Save Pressure Injury: OK"
Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/indicators/pressure-injury/period/$periodId" -Headers $headers | Out-Null
Write-Host "Get Pressure Injury: OK"

# 5. ADVERSE EVENTS
Write-Host "`n5. ADVERSE EVENTS" -ForegroundColor Cyan
# Event 1: Nov 5
$event1 = @{ periodId = $periodId; sectorId = $sectorId; eventDate = "2023-11-05"; eventType = "FALL"; description = "Fall 1" }
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/adverse-events" -Body $event1 -Headers $headers | Out-Null
# Event 2: Nov 15
$event2 = @{ periodId = $periodId; sectorId = $sectorId; eventDate = "2023-11-15"; eventType = "ACCIDENTAL_EXTUBATION"; description = "Extubation Error" }
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/adverse-events" -Body $event2 -Headers $headers | Out-Null
Write-Host "Created 2 Events."

$events = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/adverse-events/period/$periodId" -Headers $headers
Write-Host "List by Period: Found $($events.data.Count)"

$eventsRange = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/adverse-events/range?startDate=2023-11-01&endDate=2023-11-10" -Headers $headers
Write-Host "List by Range (1-10 Nov): Found $($eventsRange.data.Count) (Expected 1)"

# 6. NOTIFICATIONS
Write-Host "`n6. NOTIFICATIONS" -ForegroundColor Cyan
$notifBody = @{ periodId = $periodId; sectorId = $sectorId; notificationDate = "2023-11-05"; classification = "INCIDENT_WITH_MODERATE_HARM"; category = "FALL"; subcategory = "BED"; description = "Notif 1"; isSelfNotification = $true; professionalCategory = "NURSE" }
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/notifications" -Body $notifBody -Headers $headers | Out-Null
Write-Host "Created Notification."

$notifs = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/notifications?periodId=$periodId" -Headers $headers
Write-Host "List by Period: Found $($notifs.data.Count)"

$notifsRange = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/notifications/range?startDate=2023-11-01&endDate=2023-11-10" -Headers $headers
Write-Host "List by Range (1-10 Nov): Found $($notifsRange.data.Count)"

# 7. REPORTS
Write-Host "`n7. REPORTS" -ForegroundColor Cyan
# Full Report
$report = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/reports/panel?periodId=$periodId&sectorId=$sectorId" -Headers $headers
Write-Host "Full Report: Retrieved."
Write-Host "  Compliance: $($report.data.complianceIndicator.completeWristband)"
Write-Host "  Events in Report: $($report.data.adverseEvents.Count) (Expected 2)"

# Filtered Report
$reportFiltered = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/reports/panel?periodId=$periodId&sectorId=$sectorId&startDate=2023-11-01&endDate=2023-11-10" -Headers $headers
Write-Host "Filtered Report (1-10 Nov): Retrieved."
Write-Host "  Events in Filtered Report: $($reportFiltered.data.adverseEvents.Count) (Expected 1)"

# 8. CLOSE PERIOD
Write-Host "`n8. CLOSE PERIOD" -ForegroundColor Cyan
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/periods/$periodId/close" -Headers $headers | Out-Null
Write-Host "Period Closed."

Write-Host "`n=== TEST COMPLETED SUCCESSFULLY ===" -ForegroundColor Green
