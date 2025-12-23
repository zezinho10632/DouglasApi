$baseUrl = "http://localhost:8080/api/v1"
$headers = @{ "Content-Type" = "application/json" }

function Test-Endpoint {
    param (
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers,
        [object]$Body,
        [string]$Description
    )
    Write-Host "Testando: $Description" -ForegroundColor Cyan
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
        Write-Host "SUCESSO" -ForegroundColor Green
        return $response
    } catch {
        Write-Host "FALHA: $_" -ForegroundColor Red
        if ($_.Exception.Response) {
            $stream = $_.Exception.Response.GetResponseStream()
            $reader = New-Object System.IO.StreamReader($stream)
            Write-Host $reader.ReadToEnd() -ForegroundColor Red
        }
        return $null
    }
}

# 1. Login (Usando Admin Padrao)
$userEmail = "admin@douglas.com"
$loginBody = @{
    email = $userEmail
    password = "admin123"
}
$loginResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/login" -Headers $headers -Body $loginBody -Description "Realizar Login"

if (-not $loginResponse) {
    Write-Error "Login falhou, abortando testes."
    exit
}

$token = $loginResponse.data.token
$authHeaders = @{ 
    "Content-Type" = "application/json"
    "Authorization" = "Bearer $token" 
}

# 3. Criar Setor (UTI Adulto)
$sectorCode = "UTI_ADULTO_$(Get-Random)"
$sectorBody = @{
    name = "UTI Adulto - Ala Norte"
    code = $sectorCode
}
$sectorResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/sectors" -Headers $authHeaders -Body $sectorBody -Description "Criar Setor (UTI Adulto)"

if (-not $sectorResponse) {
    Write-Error "Criacao de setor falhou, abortando testes."
    exit
}

$sectorId = $sectorResponse.data.id

# 4. Criar Setor Secundario (UTI Pediatrica)
$sectorPedBody = @{
    name = "UTI Pediatrica"
    code = "UTI_PED_$(Get-Random)"
}
Test-Endpoint -Method "POST" -Url "$baseUrl/sectors" -Headers $authHeaders -Body $sectorPedBody -Description "Criar Setor Secundario (UTI Pediatrica)"

# 5. Listar Setores Ativos
Test-Endpoint -Method "GET" -Url "$baseUrl/sectors" -Headers $authHeaders -Description "Listar Setores Ativos"

# 6. Buscar Setor por ID
Test-Endpoint -Method "GET" -Url "$baseUrl/sectors/$sectorId" -Headers $authHeaders -Description "Buscar Setor por ID"

# 7. Atualizar Setor
$updateSectorBody = @{
    name = "UTI Adulto - Ala Sul (Renomeada)"
    isActive = $true
}
Test-Endpoint -Method "PUT" -Url "$baseUrl/sectors/$sectorId" -Headers $authHeaders -Body $updateSectorBody -Description "Atualizar Setor"

# 8. Criar Periodo (Mes Atual)
$periodBody = @{
    sectorId = $sectorId
    month = (Get-Date).Month
    year = (Get-Date).Year
}
$periodResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/periods" -Headers $authHeaders -Body $periodBody -Description "Criar Periodo"

if (-not $periodResponse) {
    Write-Error "Criacao de periodo falhou, abortando testes."
    exit
}

$periodId = $periodResponse.data.id

# 9. Listar Periodos por Setor
Test-Endpoint -Method "GET" -Url "$baseUrl/periods?sectorId=$sectorId" -Headers $authHeaders -Description "Listar Periodos por Setor"

# 10. Salvar Indicador de Conformidade
$complianceBody = @{
    periodId = $periodId
    sectorId = $sectorId
    completeWristband = 98.5
    patientCommunication = 95.0
    medicationIdentified = 100.0
    handHygieneAdherence = 90.0
    fallRiskAssessment = 99.0
    pressureInjuryRiskAssessment = 98.0
    totalPatients = 50
    observations = "Otima aderencia aos protocolos este mes."
}
Test-Endpoint -Method "POST" -Url "$baseUrl/indicators/compliance" -Headers $authHeaders -Body $complianceBody -Description "Salvar Indicador de Conformidade"

# 11. Salvar Avaliacao de Higiene das Maos
$handHygieneBody = @{
    periodId = $periodId
    sectorId = $sectorId
    totalObservations = 200
    compliantObservations = 180
    observations = "Campanha de conscientizacao realizada na semana 2."
}
Test-Endpoint -Method "POST" -Url "$baseUrl/indicators/hand-hygiene" -Headers $authHeaders -Body $handHygieneBody -Description "Salvar Avaliacao de Higiene das Maos"

# 12. Salvar Avaliacao de Risco de Queda
$fallRiskBody = @{
    periodId = $periodId
    sectorId = $sectorId
    totalPatients = 50
    assessedOnAdmission = 50
    highRiskPatients = 10
    moderateRiskPatients = 20
    lowRiskPatients = 20
    observations = "Aumento de pacientes de alto risco devido a sazonalidade."
}
Test-Endpoint -Method "POST" -Url "$baseUrl/indicators/fall-risk" -Headers $authHeaders -Body $fallRiskBody -Description "Salvar Avaliacao de Risco de Queda"

# 13. Salvar Avaliacao de Risco de Lesao por Pressao
$pressureInjuryBody = @{
    periodId = $periodId
    sectorId = $sectorId
    totalPatients = 50
    assessedOnAdmission = 48
    highRiskPatients = 5
    moderateRiskPatients = 15
    lowRiskPatients = 30
    observations = "Dois pacientes nao avaliados na admissao devido a emergencia cirurgica."
}
Test-Endpoint -Method "POST" -Url "$baseUrl/indicators/pressure-injury" -Headers $authHeaders -Body $pressureInjuryBody -Description "Salvar Avaliacao de Risco de Lesao por Pressao"

# 14. Relatar Evento Adverso
$adverseEventBody = @{
    periodId = $periodId
    sectorId = $sectorId
    eventDate = (Get-Date).ToString("yyyy-MM-dd")
    eventType = "FALL"
    description = "Paciente escorregou no banheiro durante o banho."
}
Test-Endpoint -Method "POST" -Url "$baseUrl/adverse-events" -Headers $authHeaders -Body $adverseEventBody -Description "Relatar Evento Adverso"

# 15. Listar Eventos Adversos
Test-Endpoint -Method "GET" -Url "$baseUrl/adverse-events/period/$periodId" -Headers $authHeaders -Description "Listar Eventos Adversos por Periodo"

# 16. Criar Notificacao
$notificationBody = @{
    periodId = $periodId
    sectorId = $sectorId
    notificationDate = (Get-Date).ToString("yyyy-MM-dd")
    classification = "INCIDENT_WITH_MINOR_HARM"
    category = "FALL"
    subcategory = "FROM_Height"
    description = "Queda da propria altura durante caminhada no corredor."
    isSelfNotification = $true
    professionalCategory = "NURSE"
    professionalName = "Enfermeira Maria Silva"
}
Test-Endpoint -Method "POST" -Url "$baseUrl/notifications" -Headers $authHeaders -Body $notificationBody -Description "Criar Notificacao"

# 17. Listar Notificacoes
Test-Endpoint -Method "GET" -Url "$baseUrl/notifications?periodId=$periodId" -Headers $authHeaders -Description "Listar Notificacoes por Periodo"

# 18. Gerar Relatorio Completo
$reportUrl = "$baseUrl/reports/panel?periodId=$periodId&sectorId=$sectorId"
Test-Endpoint -Method "GET" -Url $reportUrl -Headers $authHeaders -Description "Gerar Relatorio Completo do Painel"

# 19. Fechar Periodo
Test-Endpoint -Method "POST" -Url "$baseUrl/periods/$periodId/close" -Headers $authHeaders -Description "Fechar Periodo"

# 20. Verificar Swagger
try {
    $swaggerResponse = Invoke-WebRequest -Uri "http://localhost:8080/swagger-ui/index.html" -ErrorAction Stop
    Write-Host "Swagger UI esta acessivel (Status: $($swaggerResponse.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "Falha ao acessar Swagger UI: $_" -ForegroundColor Red
}

Write-Host "Todos os testes foram concluidos." -ForegroundColor Yellow