
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
        return $null
    }
}

$token = Get-AuthToken -email $adminEmail -password $adminPassword
$headers = @{ Authorization = "Bearer $token" }

# 1. Professional Categories
Write-Host "`n--- Professional Categories ---" -ForegroundColor Cyan
$profCatBody = @{ name = "Doctor" }
$profCat = Test-Endpoint -Method "POST" -Url "$baseUrl/professional-categories" -Headers $headers -Body $profCatBody -Description "Create Professional Category"
$profCatId = $profCat.data.id

Test-Endpoint -Method "GET" -Url "$baseUrl/professional-categories" -Headers $headers -Description "List Professional Categories"

# 2. Classifications & Categories
Write-Host "`n--- Classifications & Categories ---" -ForegroundColor Cyan
$classBody = @{ name = "Incident Test" }
$classRes = Test-Endpoint -Method "POST" -Url "$baseUrl/classifications" -Headers $headers -Body $classBody -Description "Create Classification"
$classId = $classRes.data.id

$catBody = @{ name = "Category Test" }
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
    quantity = 10
}
$notifRes = Test-Endpoint -Method "POST" -Url "$baseUrl/notifications" -Headers $headers -Body $notifBody -Description "Create Notification with Professional Category"
$notifId = $notifRes.data.id

Test-Endpoint -Method "GET" -Url "$baseUrl/notifications/$notifId" -Headers $headers -Description "Get Notification"

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
Test-Endpoint -Method "POST" -Url "$baseUrl/professional-categories" -Headers $mgrHeaders -Body @{ name = "Manager Created Cat" } -Description "Manager Create Prof Category"
Test-Endpoint -Method "POST" -Url "$baseUrl/sectors" -Headers $mgrHeaders -Body @{ name = "Manager Sector"; code = "MGR-01" } -Description "Manager Create Sector"

Write-Host "`nAll tests completed."
