$ErrorActionPreference = "Stop"
$baseUrl = "http://localhost:8080/api/v1"
$headers = @{ "Content-Type" = "application/json" }

function Test-Route {
    param ($Name, $Method, $Url, $Body = $null)
    Write-Output "Testing $Method $Url ($Name)..."
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body $Body
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers
        }
        Write-Output " OK"
        return $response
    } catch {
        Write-Output " FAILED"
        Write-Output "   Error: $_"
        if ($_.Exception.Response) {
             $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
             Write-Output "   Details: $($reader.ReadToEnd())"
        }
        return $null
    }
}

# 1. Login Admin
Write-Output "`n=== 1. LOGIN ADMIN ==="
$loginBody = '{"email": "admin@douglas.com", "password": "admin123"}'
$loginResponse = Test-Route -Name "Login" -Method "Post" -Url "$baseUrl/auth/login" -Body $loginBody

if (-not $loginResponse) { exit }
$token = $loginResponse.data.token
$headers.Add("Authorization", "Bearer $token")

# 2. Testar Filtros
Write-Output "`n=== 2. TEST FILTERS ==="

# 2.0 List All First
Write-Output "--- Listing ALL users to verify data ---"
$allUsers = Test-Route -Name "List All" -Method "Get" -Url "$baseUrl/users"

if ($allUsers -and $allUsers.data -and $allUsers.data.Count -gt 0) {
    Write-Output "   Total Users: $($allUsers.data.Count)"
    Write-Output "   Sample User 1: Name='$($allUsers.data[0].name)', Email='$($allUsers.data[0].email)', Role='$($allUsers.data[0].role)'"
} else {
    Write-Output "   No users found!"
}

# 2.1 By Name (Partial)
Write-Output "--- Filtering by Name (istrator) ---"
$usersName = Test-Route -Name "Filter Name" -Method "Get" -Url "$baseUrl/users?name=istrator"
if ($usersName -and $usersName.data) { Write-Output "   Found $($usersName.data.Count) users with 'istrator'" }

# 2.2 By Email (Partial)
Write-Output "--- Filtering by Email (douglas) ---"
$usersEmail = Test-Route -Name "Filter Email" -Method "Get" -Url "$baseUrl/users?email=douglas"
if ($usersEmail -and $usersEmail.data) { Write-Output "   Found $($usersEmail.data.Count) users with 'douglas'" }

# 2.3 By Role (Exact)
Write-Output "--- Filtering by Role (ADMIN) ---"
$usersRole = Test-Route -Name "Filter Role" -Method "Get" -Url "$baseUrl/users?role=ADMIN"
if ($usersRole -and $usersRole.data) { Write-Output "   Found $($usersRole.data.Count) users with role 'ADMIN'" }

# 2.4 Combined
Write-Output "--- Filtering by Name AND Role ---"
$usersCombined = Test-Route -Name "Filter Combined" -Method "Get" -Url "$baseUrl/users?name=Admin&role=ADMIN"
if ($usersCombined -and $usersCombined.data) { Write-Output "   Found $($usersCombined.data.Count) users matching both" }

# 2.5 JobTitle
Write-Output "--- Filtering by JobTitle (NURSE) ---"
$usersJob = Test-Route -Name "Filter JobTitle" -Method "Get" -Url "$baseUrl/users?jobTitle=NURSE"
if ($usersJob -and $usersJob.data) { Write-Output "   Found $($usersJob.data.Count) users with JobTitle 'NURSE'" }

# 2.6 Combined Role + JobTitle
Write-Output "--- Filtering by Role (OPERATOR) AND JobTitle (NURSE) ---"
$usersRoleJob = Test-Route -Name "Filter Role+Job" -Method "Get" -Url "$baseUrl/users?role=OPERATOR&jobTitle=NURSE"
if ($usersRoleJob -and $usersRoleJob.data) { Write-Output "   Found $($usersRoleJob.data.Count) users matching both" }

# 2.7 No Match
Write-Output "--- Filtering No Match ---"
$usersNone = Test-Route -Name "Filter None" -Method "Get" -Url "$baseUrl/users?name=NonExistentUserXYZ"
if ($usersNone -and $usersNone.data) { Write-Output "   Found $($usersNone.data.Count) users (Should be 0)" }
