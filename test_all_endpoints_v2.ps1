$baseUrl = "http://localhost:8080/api/v1"
$headers = @{ "Content-Type" = "application/json" }

function Test-Endpoint {
    param (
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers,
        [object]$Body,
        [string]$Description,
        [bool]$ExpectError = $false
    )
    Write-Host "Testing: $Description" -ForegroundColor Cyan
    try {
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $Headers
            ErrorAction = "Stop"
        }
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 10)
        }
        
        $response = Invoke-RestMethod @params
        if ($ExpectError) {
            Write-Host "FAILED: Expected error but got success" -ForegroundColor Red
            return $null
        }
        Write-Host "SUCCESS" -ForegroundColor Green
        return $response
    } catch {
        if ($ExpectError) {
             Write-Host "SUCCESS (Expected Error): $($_.Exception.Message)" -ForegroundColor Green
             return $_
        }
        Write-Host "FAILED: $_" -ForegroundColor Red
        if ($_.Exception.Response) {
            $stream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            Write-Host $reader.ReadToEnd() -ForegroundColor Red
        }
        return $null
    }
}

# 1. Login
$userEmail = "admin@douglas.com"
$loginBody = @{
    email = $userEmail
    password = "admin123"
}
$loginResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/login" -Headers $headers -Body $loginBody -Description "Login"

if (-not $loginResponse) {
    Write-Error "Login failed. Aborting."
    exit
}

$token = $loginResponse.data.token
$authHeaders = @{ 
    "Content-Type" = "application/json"
    "Authorization" = "Bearer $token" 
}

# 2. Sector Operations (GET, PUT, DELETE)
# 2.1 Get Default Sector
$sectorsResponse = Test-Endpoint -Method "GET" -Url "$baseUrl/sectors" -Headers $authHeaders -Description "List Sectors"
$sector = $sectorsResponse.data | Where-Object { $_.code -eq "UTI-01" }

if (-not $sector) {
    $sectorBody = @{ name = "UTI Adulto"; code = "UTI-01" }
    $sectorResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/sectors" -Headers $authHeaders -Body $sectorBody -Description "Create Sector"
    $sectorId = $sectorResponse.data.id
} else {
    $sectorId = $sector.id
    Write-Host "Using existing sector: $($sector.name) ($sectorId)" -ForegroundColor Yellow
}

# 2.2 Get Sector by ID
Test-Endpoint -Method "GET" -Url "$baseUrl/sectors/$sectorId" -Headers $authHeaders -Description "Get Sector by ID"

# 2.3 Update Sector (PUT)
$sectorUpdateBody = @{ name = "UTI Adulto Updated"; code = "UTI-01" }
Test-Endpoint -Method "PUT" -Url "$baseUrl/sectors/$sectorId" -Headers $authHeaders -Body $sectorUpdateBody -Description "Update Sector"
$sectorRevertBody = @{ name = "UTI Adulto"; code = "UTI-01" }
Test-Endpoint -Method "PUT" -Url "$baseUrl/sectors/$sectorId" -Headers $authHeaders -Body $sectorRevertBody -Description "Revert Sector Update"

# 2.4 Delete Sector (Create dummy first)
$dummySectorBody = @{ name = "Dummy Sector"; code = "DUMMY-01" }
$dummySector = Test-Endpoint -Method "POST" -Url "$baseUrl/sectors" -Headers $authHeaders -Body $dummySectorBody -Description "Create Dummy Sector for Deletion"
if ($dummySector) {
    $dummySectorId = $dummySector.data.id
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/sectors/$dummySectorId" -Headers $authHeaders -Description "Delete Dummy Sector"
}

# 3. Period Operations (GET, Close, Reopen)
# 3.1 Get or Create Open Period
$periodsResponse = Test-Endpoint -Method "GET" -Url "$baseUrl/periods?sectorId=$sectorId" -Headers $authHeaders -Description "List Periods"
$openPeriod = $periodsResponse.data | Where-Object { $_.status -eq "OPEN" }

if ($openPeriod) {
    $periodId = $openPeriod.id
    Write-Host "Using existing OPEN period: $($openPeriod.month)/$($openPeriod.year) ($periodId)" -ForegroundColor Yellow
} else {
    $periodMonth = (Get-Date).Month
    $periodYear = (Get-Date).Year
    $periodBody = @{ sectorId = $sectorId; month = $periodMonth; year = $periodYear }
    $periodResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/periods" -Headers $authHeaders -Body $periodBody -Description "Create Period"
    $periodId = $periodResponse.data.id
}

# 4. Indicators (GET, POST, PUT)
# 4.1 Compliance
$complianceBody = @{
    periodId = $periodId
    sectorId = $sectorId
    completeWristband = 95.5
    patientCommunication = 88.0
    medicationIdentified = 92.5
    handHygieneAdherence = 85.0
    fallRiskAssessment = 90.0
    pressureInjuryRiskAssessment = 89.5
    totalPatients = 150
    observations = "Updated observations via test script"
}
$complianceCheck = Test-Endpoint -Method "GET" -Url "$baseUrl/indicators/compliance/period/$periodId" -Headers $authHeaders -Description "Check Compliance Existence"
if ($complianceCheck.data) {
    $complianceId = $complianceCheck.data.id
    Test-Endpoint -Method "PUT" -Url "$baseUrl/indicators/compliance/$complianceId" -Headers $authHeaders -Body $complianceBody -Description "Update Compliance"
    Test-Endpoint -Method "GET" -Url "$baseUrl/indicators/compliance/$complianceId" -Headers $authHeaders -Description "Get Compliance by ID"
} else {
    Test-Endpoint -Method "POST" -Url "$baseUrl/indicators/compliance" -Headers $authHeaders -Body $complianceBody -Description "Create Compliance"
}

# 5. Notifications (CRUD)
Write-Host "`nTesting Notifications..." -ForegroundColor Cyan

# 5.1 Create Standard Notification
$notifBody = @{
    periodId = $periodId
    sectorId = $sectorId
    notificationDate = (Get-Date).ToString("yyyy-MM-dd")
    classification = "INCIDENT_WITH_MODERATE_HARM"
    category = "Patient Safety"
    subcategory = "Fall"
    description = "Patient fell from bed."
    isSelfNotification = $false
    professionalCategory = "Nurse"
    professionalName = "Nurse Joy"
}
$notifResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/notifications" -Headers $authHeaders -Body $notifBody -Description "Create Notification (Standard)"
$notifId = $notifResponse.data.id

# 5.2 Get Notification by ID
Test-Endpoint -Method "GET" -Url "$baseUrl/notifications/$notifId" -Headers $authHeaders -Description "Get Notification by ID"

# 5.3 Update Notification (PUT)
if ($notifId) {
    $notifUpdateBody = @{
        periodId = $periodId
        sectorId = $sectorId
        notificationDate = (Get-Date).ToString("yyyy-MM-dd")
        classification = "INCIDENT_WITHOUT_HARM"
        category = "Patient Safety"
        subcategory = "Fall"
        description = "Patient fell from bed (Updated)."
        isSelfNotification = $false
        professionalCategory = "Nurse"
        professionalName = "Nurse Joy"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/notifications/$notifId" -Headers $authHeaders -Body $notifUpdateBody -Description "Update Notification"
}

# 5.4 Delete Notification
if ($notifId) {
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/notifications/$notifId" -Headers $authHeaders -Description "Delete Notification"
}

# 6. Adverse Events (CRUD)
Write-Host "`nTesting Adverse Events..." -ForegroundColor Cyan

# 6.1 Create Adverse Event
$aeBody = @{
    periodId = $periodId
    sectorId = $sectorId
    eventDate = (Get-Date).ToString("yyyy-MM-dd")
    eventType = "FALL"
    description = "Adverse Event Test"
}
$aeResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/adverse-events" -Headers $authHeaders -Body $aeBody -Description "Create Adverse Event"
$aeId = $aeResponse.data.id

# 6.2 Get Adverse Event by ID
Test-Endpoint -Method "GET" -Url "$baseUrl/adverse-events/$aeId" -Headers $authHeaders -Description "Get Adverse Event by ID"

# 6.3 Update Adverse Event
if ($aeId) {
    $aeUpdateBody = @{
        eventDate = (Get-Date).ToString("yyyy-MM-dd")
        eventType = "FALL"
        description = "Adverse Event Test Updated"
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/adverse-events/$aeId" -Headers $authHeaders -Body $aeUpdateBody -Description "Update Adverse Event"
}

# 6.4 Delete Adverse Event
if ($aeId) {
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/adverse-events/$aeId" -Headers $authHeaders -Description "Delete Adverse Event"
}

# 7. Users (CRUD)
Write-Host "`nTesting Users..." -ForegroundColor Cyan

# 7.1 Register Dummy User
$dummyUserBody = @{
    name = "Dummy User"
    email = "dummy@douglas.com"
    password = "dummy123"
    role = "MANAGER"
    sectorId = $sectorId
}
# Need to use auth endpoint to create user (Requires Admin)
Test-Endpoint -Method "POST" -Url "$baseUrl/auth/register" -Headers $authHeaders -Body $dummyUserBody -Description "Register Dummy User"

# Fetch the created user to get ID
$dummyUserList = Test-Endpoint -Method "GET" -Url "$baseUrl/users?email=$($dummyUserBody.email)" -Headers $authHeaders -Description "Fetch Dummy User ID"
$dummyUser = $dummyUserList.data | Select-Object -First 1

if ($dummyUser) {
    $dummyUserId = $dummyUser.id
    
    # 7.2 Update Dummy User
    $userUpdateBody = @{
        name = "Dummy User Updated"
        role = "MANAGER"
        sectorId = $sectorId
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/users/$dummyUserId" -Headers $authHeaders -Body $userUpdateBody -Description "Update Dummy User"
    
    # 7.3 Delete Dummy User
    Test-Endpoint -Method "DELETE" -Url "$baseUrl/users/$dummyUserId" -Headers $authHeaders -Description "Delete Dummy User"
} else {
    Write-Host "FAILED: Could not find registered dummy user" -ForegroundColor Red
}

# 8. Close and Reopen Period Flow
Write-Host "`nTesting Close/Reopen Period Flow..." -ForegroundColor Cyan

# 8.1 Close Period
Test-Endpoint -Method "POST" -Url "$baseUrl/periods/$periodId/close" -Headers $authHeaders -Description "Close Period"

# 8.2 Try to Update Compliance (Should Fail)
$failBody = $complianceBody.Clone()
$failBody.observations = "Should fail"
Test-Endpoint -Method "PUT" -Url "$baseUrl/indicators/compliance/$complianceId" -Headers $authHeaders -Body $failBody -Description "Update Compliance in Closed Period (Expect Fail)" -ExpectError $true

# 8.3 Reopen Period
Test-Endpoint -Method "POST" -Url "$baseUrl/periods/$periodId/reopen" -Headers $authHeaders -Description "Reopen Period"

# 8.4 Try to Update Compliance (Should Success)
Test-Endpoint -Method "PUT" -Url "$baseUrl/indicators/compliance/$complianceId" -Headers $authHeaders -Body $complianceBody -Description "Update Compliance in Reopened Period (Expect Success)"

Write-Host "`nAll tests completed." -ForegroundColor Cyan