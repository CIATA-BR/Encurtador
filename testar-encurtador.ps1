$BaseUrl = "http://localhost:8080"

function Escrever-Titulo {
    param([string]$Texto)
    Write-Host ""
    Write-Host "==================================================" 
    Write-Host $Texto
    Write-Host "=================================================="
}

function Escrever-Sucesso {
    param([string]$Texto)
    Write-Host "[OK] $Texto"
}

function Escrever-Falha {
    param([string]$Texto)
    Write-Host "[ERRO] $Texto"
}

function Converter-JsonSeguro {
    param([Parameter(ValueFromPipeline = $true)]$Objeto)

    try {
        return ($Objeto | ConvertTo-Json -Depth 10)
    }
    catch {
        return $Objeto
    }
}

function Ler-CorpoErro {
    param($Erro)

    try {
        if ($Erro.Exception.Response -ne $null) {
            $stream = $Erro.Exception.Response.GetResponseStream()
            if ($stream -ne $null) {
                $reader = New-Object System.IO.StreamReader($stream)
                $body = $reader.ReadToEnd()
                $reader.Close()
                return $body
            }
        }
    }
    catch {
    }

    if ($Erro.ErrorDetails -and $Erro.ErrorDetails.Message) {
        return $Erro.ErrorDetails.Message
    }

    return $Erro.Exception.Message
}

function Testar-PostJson {
    param(
        [string]$Nome,
        [string]$Uri,
        [string]$Json,
        [hashtable]$Headers = @{}
    )

    Escrever-Titulo $Nome

    try {
        $resposta = Invoke-RestMethod -Method Post -Uri $Uri -Headers $Headers -ContentType "application/json" -Body $Json
        Escrever-Sucesso "Requisição concluída com sucesso."
        $resposta | Converter-JsonSeguro | Write-Host
        return $resposta
    }
    catch {
        $corpo = Ler-CorpoErro $_
        Escrever-Falha "A requisição retornou erro."
        Write-Host $corpo
        return $null
    }
}

function Testar-Get {
    param(
        [string]$Nome,
        [string]$Uri,
        [hashtable]$Headers = @{}
    )

    Escrever-Titulo $Nome

    try {
        $resposta = Invoke-RestMethod -Method Get -Uri $Uri -Headers $Headers
        Escrever-Sucesso "Requisição concluída com sucesso."
        $resposta | Converter-JsonSeguro | Write-Host
        return $resposta
    }
    catch {
        $corpo = Ler-CorpoErro $_
        Escrever-Falha "A requisição retornou erro."
        Write-Host $corpo
        return $null
    }
}

function Testar-Redirecionamento {
    param(
        [string]$Nome,
        [string]$Uri
    )

    Escrever-Titulo $Nome

    try {
        $resposta = Invoke-WebRequest -Method Get -Uri $Uri -MaximumRedirection 0 -ErrorAction Stop
        Escrever-Sucesso "Resposta recebida."
        Write-Host "StatusCode: $($resposta.StatusCode)"
        Write-Host "Location: $($resposta.Headers.Location)"
    }
    catch {
        $response = $_.Exception.Response
        if ($response -ne $null) {
            Escrever-Sucesso "Redirecionamento interceptado."
            Write-Host "StatusCode: $([int]$response.StatusCode)"
            Write-Host "Location: $($response.Headers['Location'])"
        }
        else {
            Escrever-Falha "Não foi possível verificar o redirecionamento."
            Write-Host $_.Exception.Message
        }
    }
}

Escrever-Titulo "INÍCIO DOS TESTES DA API DE ENCURTAMENTO"

# 1. Criar URL simples
$respostaCriacao = Testar-PostJson `
    -Nome "1. Criar URL simples" `
    -Uri "$BaseUrl/v1/urls" `
    -Json '{"originalUrl":"https://www.ciata.org.br"}'

$id = $null
if ($respostaCriacao -ne $null) {
    $id = $respostaCriacao.id
    Escrever-Sucesso "ID gerado: $id"
} else {
    Escrever-Falha "Não foi possível obter o ID da URL criada."
}

# 2. Consultar detalhes
if ($id) {
    Testar-Get `
        -Nome "2. Consultar detalhes da URL criada" `
        -Uri "$BaseUrl/v1/urls/$id"
}

# 3. Testar redirecionamento
if ($id) {
    Testar-Redirecionamento `
        -Nome "3. Testar redirecionamento da URL curta" `
        -Uri "$BaseUrl/$id"
}

# 4. Criar URL com alias customizado
$respostaAlias = Testar-PostJson `
    -Nome "4. Criar URL com alias customizado" `
    -Uri "$BaseUrl/v1/urls" `
    -Json '{"originalUrl":"https://www.ciata.org.br","aliasCustomizado":"ciata"}'

# 5. Testar alias duplicado
Testar-PostJson `
    -Nome "5. Testar alias duplicado" `
    -Uri "$BaseUrl/v1/urls" `
    -Json '{"originalUrl":"https://www.ciata.org.br","aliasCustomizado":"ciata"}'

# 6. Testar alias inválido
Testar-PostJson `
    -Nome "6. Testar alias inválido" `
    -Uri "$BaseUrl/v1/urls" `
    -Json '{"originalUrl":"https://www.ciata.org.br","aliasCustomizado":"***"}'

# 7. Testar data de expiração válida
Testar-PostJson `
    -Nome "7. Testar data de expiração válida" `
    -Uri "$BaseUrl/v1/urls" `
    -Json '{"originalUrl":"https://www.ciata.org.br","expirationDate":"2030-01-01T00:00:00Z"}'

# 8. Testar data de expiração inválida
Testar-PostJson `
    -Nome "8. Testar data de expiração inválida" `
    -Uri "$BaseUrl/v1/urls" `
    -Json '{"originalUrl":"https://www.ciata.org.br","expirationDate":"banana"}'

# 9. Testar data de expiração no passado
Testar-PostJson `
    -Nome "9. Testar data de expiração no passado" `
    -Uri "$BaseUrl/v1/urls" `
    -Json '{"originalUrl":"https://www.ciata.org.br","expirationDate":"2020-01-01T00:00:00Z"}'

# 10. Testar URL inválida em português
Testar-PostJson `
    -Nome "10. Testar URL inválida em português" `
    -Uri "$BaseUrl/v1/urls" `
    -Headers @{ "Accept-Language" = "pt-BR" } `
    -Json '{"originalUrl":"banana"}'

# 11. Testar URL inválida em inglês
Testar-PostJson `
    -Nome "11. Testar URL inválida em inglês" `
    -Uri "$BaseUrl/v1/urls" `
    -Headers @{ "Accept-Language" = "en" } `
    -Json '{"originalUrl":"banana"}'

# 12. Testar URL inválida em espanhol
Testar-PostJson `
    -Nome "12. Testar URL inválida em espanhol" `
    -Uri "$BaseUrl/v1/urls" `
    -Headers @{ "Accept-Language" = "es" } `
    -Json '{"originalUrl":"banana"}'

# 13. Testar URL inválida em francês
Testar-PostJson `
    -Nome "13. Testar URL inválida em francês" `
    -Uri "$BaseUrl/v1/urls" `
    -Headers @{ "Accept-Language" = "fr" } `
    -Json '{"originalUrl":"banana"}'

# 14. Consultar ID inexistente
Testar-Get `
    -Nome "14. Consultar URL inexistente" `
    -Uri "$BaseUrl/v1/urls/id-inexistente-123"

# 15. Redirecionar ID inexistente
Testar-Redirecionamento `
    -Nome "15. Redirecionar URL inexistente" `
    -Uri "$BaseUrl/id-inexistente-123"

Escrever-Titulo "FIM DOS TESTES"