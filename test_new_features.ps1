
$baseUrl = "http://localhost:8080/api/v1"
$adminEmail = "admin@douglas.com"
$adminPassword = "admin123"

function Get-AuthToken {
    param ([string]$email, [string]$password)
    $body = @{ email = $email; password = $password } | ConvertTo-Json
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $body -ContentType "application/json"
        return $response.data.token
    } catch {
        Write-Error "Login failed: $_"
        exit 1
    }
}

function Test-Endpoint {
    param ([string]$Url, [string]$Method = "GET", [hashtable]$Headers, [object]$Body = $null, [string]$Description)
    Write-Host "Testing: $Description" -ForegroundColor Yellow
    try {
        $params = @{ Uri = $Url; Method = $Method; Headers = $Headers; ContentType = "application/json" }
        if ($Body) { $params.Body = ($Body | ConvertTo-Json -Depth 10) }
        $response = Invoke-RestMethod @params
        Write-Host "Success" -ForegroundColor Green
        return $response
    } catch {
        Write-Error "Failed: $_"
        if ($_.Exception.Response) {
            $stream = $_.Exception.Response.GetResponseStream()
            if ($stream) {
                $reader = New-Object System.IO.StreamReader($stream)
                Write-Host "Response Body: $($reader.ReadToEnd())" -ForegroundColor Red
            }
        }
        return $null
    }
}

$token = Get-AuthToken -email $adminEmail -password $adminPassword
$headers = @{ Authorization = "Bearer $token" }
$rand = Get-Random

# 1. Professional Categories
Write-Host "`n--- Professional Categories ---" -ForegroundColor Cyan
$profCatBody = @{ name = "Doctor_$rand" }
$profCat = Test-Endpoint -Method "POST" -Url "$baseUrl/professional-categories" -Headers $headers -Body $profCatBody -Description "Create Professional Category"
$profCatId = $profCat.data.id

Test-Endpoint -Method "GET" -Url "$baseUrl/professional-categories" -Headers $headers -Description "List Professional Categories"

# 2. Classifications & Categories
Write-Host "`n--- Classifications & Categories ---" -ForegroundColor Cyan
$classBody = @{ name = "Incident Test_$rand" }
$classRes = Test-Endpoint -Method "POST" -Url "$baseUrl/classifications" -Headers $headers -Body $classBody -Description "Create Classification"
$classId = $classRes.data.id

$catBody = @{ name = "Category Test_$rand" }
$catRes = Test-Endpoint -Method "POST" -Url "$baseUrl/categories" -Headers $headers -Body $catBody -Description "Create Category"
$catId = $catRes.data.id

# 3. Notification
Write-Host "`n--- Notifications ---" -ForegroundColor Cyan
# Get Period & Sector (Assuming defaults exist from DataInitializer)
$sectors = Test-Endpoint -Method "GET" -Url "$baseUrl/sectors" -Headers $headers -Description "Get Sectors"
$sectorId = $sectors.data[0].id

# Create a Period if needed, or find open one
$periods = Test-Endpoint -Method "GET" -Url "$baseUrl/periods?sectorId=$sectorId" -Headers $headers -Description "Get Periods"
if ($periods.data.Count -gt 0) {
    $periodId = $periods.data[0].id
} else {
    $periodBody = @{ sectorId = $sectorId; month = 12; year = 2025 }
    $periodRes = Test-Endpoint -Method "POST" -Url "$baseUrl/periods" -Headers $headers -Body $periodBody -Description "Create Period"
    $periodId = $periodRes.data.id
}

$notifBody = @{
    periodId = $periodId
    sectorId = $sectorId
    classificationId = $classId
    categoryId = $catId
    professionalCategoryId = $profCatId
    quantityClassification = 10
    quantityCategory = 10
    quantityProfessional = 5
}
$notifRes = Test-Endpoint -Method "POST" -Url "$baseUrl/notifications" -Headers $headers -Body $notifBody -Description "Create Notification with Professional Category"
$notifId = $notifRes.data.id

Test-Endpoint -Method "GET" -Url "$baseUrl/notifications/$notifId" -Headers $headers -Description "Get Notification"

# Self Notification
Write-Host "`n--- Self Notifications ---" -ForegroundColor Cyan

# Check if exists
$existing = Invoke-RestMethod -Uri "$baseUrl/self-notifications/period/$periodId" -Method Get -Headers $headers -ErrorAction SilentlyContinue

if ($existing.data) {
    Write-Host "Self Notification already exists. Testing Update..."
    $selfNotifId = $existing.data.id
    $updateBody = @{
        quantity = 25
        percentage = 80.0
    }
    Test-Endpoint -Method "PUT" -Url "$baseUrl/self-notifications/$selfNotifId" -Headers $headers -Body $updateBody -Description "Update Self Notification"
} else {
    Write-Host "Creating new Self Notification..."
    $selfNotifBody = @{
        periodId = $periodId
        sectorId = $sectorId
        quantity = 20
        percentage = 71.5
    }
    Test-Endpoint -Method "POST" -Url "$baseUrl/self-notifications" -Headers $headers -Body $selfNotifBody -Description "Create Self Notification"
}

Test-Endpoint -Method "GET" -Url "$baseUrl/self-notifications/period/$periodId" -Headers $headers -Description "Get Self Notification"

    # Meta Compliance
    Write-Host "`n--- Meta Compliance ---" -ForegroundColor Cyan
    $metaExisting = Invoke-RestMethod -Uri "$baseUrl/indicators/meta-compliance/period/$periodId" -Method Get -Headers $headers -ErrorAction SilentlyContinue

    if ($metaExisting.data) {
        Write-Host "Meta Compliance already exists. Testing Update..."
        $metaId = $metaExisting.data.id
# Try Update
    $body = @{
        goalValue = 100
        percentage = 95.5
    }
    
    Test-Endpoint -Method "PUT" -Url "$baseUrl/indicators/meta-compliance/$metaId" -Headers $headers -Body $body -Description "Update Meta Compliance"
    } else {
        Write-Host "Creating Meta Compliance..."
        $metaBody = @{
            periodId = $periodId
            sectorId = $sectorId
            goalValue = 100.0
            percentage = 93.0
        } | ConvertTo-Json
        Test-Endpoint -Method "POST" -Url "$baseUrl/indicators/meta-compliance" -Headers $headers -Body $metaBody -Description "Create Meta Compliance"
    }

    # Medication Compliance
    Write-Host "`n--- Medication Compliance ---" -ForegroundColor Cyan
    $medExisting = Invoke-RestMethod -Uri "$baseUrl/indicators/medication-compliance/period/$periodId" -Method Get -Headers $headers -ErrorAction SilentlyContinue

    if ($medExisting.data) {
        Write-Host "Medication Compliance already exists. Testing Update..."
        $medId = $medExisting.data.id
        $medBody = @{
            percentage = 99.9
        }
        Test-Endpoint -Method "PUT" -Url "$baseUrl/indicators/medication-compliance/$medId" -Headers $headers -Body $medBody -Description "Update Medication Compliance"
    } else {
        Write-Host "Creating Medication Compliance..."
        $medBody = @{
            periodId = $periodId
            sectorId = $sectorId
            percentage = 100.0
        }
        Test-Endpoint -Method "POST" -Url "$baseUrl/indicators/medication-compliance" -Headers $headers -Body $medBody -Description "Create Medication Compliance"
    }

    # 4. Verify Permissions (Manager)
    Write-Host "`n--- Manager Permissions ---" -ForegroundColor Cyan
# Create Manager User
$mgrEmail = "manager_test_$(Get-Random)@douglas.com"
$mgrBody = @{ name = "Manager Test"; email = $mgrEmail; password = "password"; role = "MANAGER" }
Test-Endpoint -Method "POST" -Url "$baseUrl/auth/register" -Headers $headers -Body $mgrBody -Description "Register Manager"

# Login as Manager
$mgrToken = Get-AuthToken -email $mgrEmail -password "password"
$mgrHeaders = @{ Authorization = "Bearer $mgrToken" }

# Manager Actions
Test-Endpoint -Method "POST" -Url "$baseUrl/professional-categories" -Headers $mgrHeaders -Body @{ name = "Manager Created Cat_$(Get-Random)" } -Description "Manager Create Prof Category"
$mgrSector = Test-Endpoint -Method "POST" -Url "$baseUrl/sectors" -Headers $mgrHeaders -Body @{ name = "Manager Sector_$(Get-Random)"; code = "MGR-$(Get-Random)" } -Description "Manager Create Sector"
$mgrSectorId = $mgrSector.data.id

Write-Host "Testing Manager Delete Sector (Expect Failure)..."
try {
    Invoke-RestMethod -Uri "$baseUrl/sectors/$mgrSectorId" -Method DELETE -Headers $mgrHeaders
    Write-Error "Manager WAS able to delete sector! (Security Violation)"
} catch {
    if ($_.Exception.Response.StatusCode -eq [System.Net.HttpStatusCode]::Forbidden) {
        Write-Host "Success: Manager Forbidden (403)" -ForegroundColor Green
    } else {
        Write-Error "Unexpected error: $_"
    }
}

Write-Host "Testing Admin Delete Sector (Expect Success)..."
Test-Endpoint -Method "DELETE" -Url "$baseUrl/sectors/$mgrSectorId" -Headers $headers -Description "Admin Delete Sector"

Write-Host "`nAll tests completed."
