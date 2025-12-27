
$baseUrl = "http://localhost:8080/api/v1"
$adminEmail = "admin@douglas.com"
$adminPassword = "admin123"

# Login
$loginBody = @{ email = $adminEmail; password = $adminPassword } | ConvertTo-Json
try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.data.token
    $headers = @{ Authorization = "Bearer $token" }
} catch {
    Write-Error "Login failed: $_"
    exit
}

# Get Sector
$sectors = Invoke-RestMethod -Uri "$baseUrl/sectors" -Headers $headers
$sectorId = $sectors.data[0].id
Write-Host "Sector ID: $sectorId"

# Get Period
try {
    $periods = Invoke-RestMethod -Uri "$baseUrl/periods?sectorId=$sectorId" -Headers $headers
    $periodId = $periods.data[0].id
    Write-Host "Period ID: $periodId"
} catch {
    Write-Error "Get Period Failed: $_"
    $stream = $_.Exception.Response.GetResponseStream()
    if ($stream) {
        $reader = New-Object System.IO.StreamReader($stream)
        Write-Host "Response Body: $($reader.ReadToEnd())"
    }
    exit
}

# Get Meta Compliance
try {
    $meta = Invoke-RestMethod -Uri "$baseUrl/indicators/meta-compliance/period/$periodId" -Headers $headers
    if ($meta.data) {
        $metaId = $meta.data.id
        Write-Host "Meta Compliance ID: $metaId"
        
        # Try Update
        $body = @{
            goalValue = 98.5
            percentage = 96.0
        } | ConvertTo-Json
        
        Write-Host "Sending Body: $body"
        
        try {
            $response = Invoke-RestMethod -Uri "$baseUrl/indicators/meta-compliance/$metaId" -Method Put -Headers $headers -Body $body -ContentType "application/json"
            Write-Host "Update Success!"
            $response
        } catch {
            Write-Error "Update Failed: $_"
            $stream = $_.Exception.Response.GetResponseStream()
            if ($stream) {
                $reader = New-Object System.IO.StreamReader($stream)
                Write-Host "Response Body: $($reader.ReadToEnd())"
            }
        }
    } else {
        Write-Host "No Meta Compliance found."
    }
} catch {
    Write-Error "Error fetching meta compliance: $_"
}
