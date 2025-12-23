$baseUrl = "http://localhost:8080/api/v1"
$timestamp = Get-Date -Format "yyyyMMddHHmmss"

function Invoke-DouglasApi {
    param (
        [string]$Method,
        [string]$Uri,
        [hashtable]$Body = $null,
        [hashtable]$Headers = @{}
    )
    
    $params = @{
        Method = $Method
        Uri = $Uri
        ContentType = "application/json"
        Headers = $Headers
    }

    if ($Body) {
        $params.Body = $Body | ConvertTo-Json -Depth 10
    }

    try {
        $response = Invoke-RestMethod @params
        return $response
    } catch {
        Write-Host "Error calling $Uri ($Method): $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $stream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            Write-Host "Response Body: $($reader.ReadToEnd())" -ForegroundColor Red
        }
        return $null
    }
}

Write-Host "Starting Douglas API Tests..." -ForegroundColor Green

# 1. Login as Admin (Default Admin should be created by DataInitializer)
Write-Host "1. Logging in as Admin..." -ForegroundColor Cyan
$adminBody = @{
    email = "admin@douglas.com"
    password = "admin123"
}
$adminLogin = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/auth/login" -Body $adminBody
if (-not $adminLogin) {
    Write-Host "Admin login failed. Exiting." -ForegroundColor Red
    exit
}
$adminToken = $adminLogin.data.token
$adminHeaders = @{ Authorization = "Bearer $adminToken" }
Write-Host "Admin Logged In. Job Title: $($adminLogin.data.jobTitle)" -ForegroundColor Green

# 2. Register Nurse (using Admin Token)
Write-Host "2. Registering Nurse User..." -ForegroundColor Cyan
$nurseEmail = "nurse_$timestamp@douglas.com"
$nurseBody = @{
    name = "Nurse Joy"
    email = $nurseEmail
    password = "123456"
    role = "OPERATOR"
    jobTitle = "NURSE"
}
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/auth/register" -Body $nurseBody -Headers $adminHeaders
Write-Host "Nurse Registered." -ForegroundColor Green

# 3. Register Doctor (using Admin Token)
Write-Host "3. Registering Doctor User..." -ForegroundColor Cyan
$doctorEmail = "doctor_$timestamp@douglas.com"
$doctorBody = @{
    name = "Dr. House"
    email = $doctorEmail
    password = "123456"
    role = "OPERATOR"
    jobTitle = "DOCTOR"
}
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/auth/register" -Body $doctorBody -Headers $adminHeaders
Write-Host "Doctor Registered." -ForegroundColor Green

# 4. Login as Nurse
Write-Host "4. Logging in as Nurse..." -ForegroundColor Cyan
$nurseLoginBody = @{
    email = $nurseEmail
    password = "123456"
}
$nurseLogin = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/auth/login" -Body $nurseLoginBody
$nurseToken = $nurseLogin.data.token
$nurseId = $nurseLogin.data.userId
$nurseHeaders = @{ Authorization = "Bearer $nurseToken" }
Write-Host "Nurse Logged In. Job Title: $($nurseLogin.data.jobTitle)" -ForegroundColor Yellow

# 5. Login as Doctor
Write-Host "5. Logging in as Doctor..." -ForegroundColor Cyan
$doctorLoginBody = @{
    email = $doctorEmail
    password = "123456"
}
$doctorLogin = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/auth/login" -Body $doctorLoginBody
$doctorToken = $doctorLogin.data.token
$doctorId = $doctorLogin.data.userId
$doctorHeaders = @{ Authorization = "Bearer $doctorToken" }
Write-Host "Doctor Logged In. Job Title: $($doctorLogin.data.jobTitle)" -ForegroundColor Yellow

# 6. Create Sector (Nurse)
Write-Host "6. Creating Sector (Nurse)..." -ForegroundColor Cyan
$sectorBody = @{
    name = "Emergency Room $timestamp"
    code = "ER-$timestamp"
}
$sectorResponse = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/sectors" -Body $sectorBody -Headers $nurseHeaders
$sectorId = $sectorResponse.data.id
Write-Host "Sector Created. ID: $sectorId" -ForegroundColor Yellow

# 7. Create Period (Nurse)
Write-Host "7. Creating Period (Nurse)..." -ForegroundColor Cyan
$periodBody = @{
    month = 11
    year = 2023
    sectorId = $sectorId
}
$periodResponse = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/periods" -Body $periodBody -Headers $nurseHeaders
$periodId = $periodResponse.data.id
Write-Host "Period Created. ID: $periodId" -ForegroundColor Yellow

# 8. Create Adverse Event (Nurse)
Write-Host "8. Creating Adverse Event (Nurse)..." -ForegroundColor Cyan
$eventBody = @{
    periodId = $periodId
    sectorId = $sectorId
    eventDate = "2023-11-05"
    eventType = "FALL"
    description = "Patient fell from bed"
}
$eventResponse = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/adverse-events" -Body $eventBody -Headers $nurseHeaders
Write-Host "Adverse Event Created. Created By: $($eventResponse.data.createdByName) ($($eventResponse.data.createdByJobTitle))" -ForegroundColor Yellow

# 9. Create Notification (Doctor)
Write-Host "9. Creating Notification (Doctor)..." -ForegroundColor Cyan
$notificationBody = @{
    periodId = $periodId
    sectorId = $sectorId
    notificationDate = "2023-11-15"
    classification = "INCIDENT_WITH_MODERATE_HARM"
    category = "MEDICATION"
    subcategory = "WRONG_DOSE"
    description = "Wrong dose administered"
    isSelfNotification = $false
    professionalCategory = "DOCTOR"
    professionalName = "Dr. House"
}
$notificationResponse = Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/notifications" -Body $notificationBody -Headers $doctorHeaders
Write-Host "Notification Created. Created By: $($notificationResponse.data.createdByName) ($($notificationResponse.data.createdByJobTitle))" -ForegroundColor Yellow

# 10. Filter by User (Nurse viewing self)
Write-Host "10. Filtering Events by Nurse (Self)..." -ForegroundColor Cyan
$nurseEvents = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/adverse-events/user/$nurseId" -Headers $nurseHeaders
Write-Host "Nurse Events Found (Self): $($nurseEvents.data.Count)" -ForegroundColor Yellow

# 10.1 Filter by User (Admin viewing Nurse)
Write-Host "10.1 Filtering Events by Nurse (Admin)..." -ForegroundColor Cyan
$adminNurseEvents = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/adverse-events/user/$nurseId" -Headers $adminHeaders
Write-Host "Nurse Events Found (Admin): $($adminNurseEvents.data.Count)" -ForegroundColor Yellow

# 11. Filter by User (Doctor viewing self)
Write-Host "11. Filtering Notifications by Doctor (Self)..." -ForegroundColor Cyan
$doctorNotifications = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/notifications/user/$doctorId" -Headers $doctorHeaders
Write-Host "Doctor Notifications Found (Self): $($doctorNotifications.data.Count)" -ForegroundColor Yellow

# 12. List by Date Range (Admin)
Write-Host "12. Listing by Date Range (Nov 1 - Nov 10)..." -ForegroundColor Cyan
$rangeEvents = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/adverse-events/range?startDate=2023-11-01&endDate=2023-11-10" -Headers $adminHeaders
Write-Host "Events in range: $($rangeEvents.data.Count)" -ForegroundColor Yellow

# 13. Indicators (Nurse)
Write-Host "13. Creating Compliance Indicator (Nurse)..." -ForegroundColor Cyan
$complianceBody = @{
    periodId = $periodId
    sectorId = $sectorId
    completeWristband = 100.0
    patientCommunication = 100.0
    medicationIdentified = 100.0
    handHygieneAdherence = 100.0
    fallRiskAssessment = 100.0
    pressureInjuryRiskAssessment = 100.0
    totalPatients = 10
    observations = "Perfect"
}
Invoke-DouglasApi -Method "POST" -Uri "$baseUrl/indicators/compliance" -Body $complianceBody -Headers $nurseHeaders
Write-Host "Compliance Indicator Created." -ForegroundColor Green

# 14. Report Panel
Write-Host "14. Generating Report Panel..." -ForegroundColor Cyan
$report = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/reports/panel?periodId=$periodId&sectorId=$sectorId" -Headers $adminHeaders
if ($report.data) { Write-Host "Report Generated Successfully." -ForegroundColor Green }

# 15. Security Tests (Negative Tests)
Write-Host "15. Running Security Tests..." -ForegroundColor Cyan

# 15.1 Nurse accessing Doctor's Notifications (Should Fail)
Write-Host "15.1 Nurse accessing Doctor's Notifications (Should Fail)..." -ForegroundColor Cyan
$forbiddenAccess1 = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/notifications/user/$doctorId" -Headers $nurseHeaders
if (-not $forbiddenAccess1) {
    Write-Host "Access correctly denied (403/500)." -ForegroundColor Green
} else {
    Write-Host "WARNING: Nurse accessed Doctor's notifications!" -ForegroundColor Red
}

# 15.2 Nurse accessing Report Panel (Should Fail)
Write-Host "15.2 Nurse accessing Report Panel (Should Fail)..." -ForegroundColor Cyan
$forbiddenAccess2 = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/reports/panel?periodId=$periodId&sectorId=$sectorId" -Headers $nurseHeaders
if (-not $forbiddenAccess2) {
    Write-Host "Access correctly denied (403/500)." -ForegroundColor Green
} else {
    Write-Host "WARNING: Nurse accessed Report Panel!" -ForegroundColor Red
}

# 15.3 Nurse accessing Date Range (Should Fail)
Write-Host "15.3 Nurse accessing Date Range (Should Fail)..." -ForegroundColor Cyan
$forbiddenAccess3 = Invoke-DouglasApi -Method "GET" -Uri "$baseUrl/adverse-events/range?startDate=2023-11-01&endDate=2023-11-10" -Headers $nurseHeaders
if (-not $forbiddenAccess3) {
    Write-Host "Access correctly denied (403/500)." -ForegroundColor Green
} else {
    Write-Host "WARNING: Nurse accessed Date Range List!" -ForegroundColor Red
}

Write-Host "All Tests Completed." -ForegroundColor Green
