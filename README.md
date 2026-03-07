
# Encurtador de URLs

API REST desenvolvida em Java 25 + Spring Boot para criação de URLs encurtadas com suporte a:

- alias customizado
- data de expiração
- contagem de cliques
- internacionalização (PT / EN / ES / FR)
- validação de URL
- tratamento global de erros

---

# Tecnologias utilizadas

- Java 25
- Spring Boot 3
- Maven
- Jackson
- Spring Validation
- PowerShell (scripts de teste)

---

# Estrutura do Projeto

src/main/java/br/ciata/encurtador

config  
controller  
dto  
erros  
modelos  
repositorios  
servicos  
util  

Arquitetura em camadas:

Controller → Service → Repository → Model

---

# Executando o Projeto

## Pré-requisitos

- Java 25
- Maven

## Executar aplicação

```
.\mvnw.cmd spring-boot:run
```

Servidor inicia em:

```
http://localhost:8080
```

---

# Configuração

Arquivo:

src/main/resources/application.properties

Exemplo:

```
server.port=8080
app.url-base=http://localhost:8080
```

A propriedade `app.url-base` define a base usada para gerar as URLs encurtadas.

---

# Endpoints da API

Método | Endpoint | Descrição
------ | -------- | ---------
POST | /v1/urls | cria URL encurtada
GET | /v1/urls/{id} | consulta detalhes
GET | /{id} | redireciona para URL original

---

# Criar URL encurtada

Requisição:

POST /v1/urls

Body JSON:

```
{
  "originalUrl": "https://www.ciata.org.br"
}
```

Resposta:

```
{
  "id": "Ab12Cd",
  "shortUrl": "http://localhost:8080/Ab12Cd",
  "originalUrl": "https://www.ciata.org.br",
  "criadoEm": "2026-03-07T10:00:00Z",
  "expiraEm": null,
  "contagemCliques": 0
}
```

---

# Alias customizado

```
{
  "originalUrl": "https://www.ciata.org.br",
  "aliasCustomizado": "ciata"
}
```

Resultado:

http://localhost:8080/ciata

---

# URL com expiração

```
{
  "originalUrl": "https://www.ciata.org.br",
  "expirationDate": "2030-01-01T00:00:00Z"
}
```

Formato da data:

ISO-8601

Exemplo:

2030-12-31T23:59:59Z

---

# Consultar detalhes

GET /v1/urls/{id}

Exemplo:

GET /v1/urls/Ab12Cd

---

# Redirecionamento

GET /{id}

Resposta HTTP:

302 Redirect

Header:

Location: https://www.ciata.org.br

---

# Tratamento de erros

Formato padrão:

```
{
  "codigo": "URL_INVALIDA",
  "mensagem": "A URL informada é inválida",
  "caminho": "/v1/urls",
  "timestamp": "2026-03-07T10:00:00Z"
}
```

Possíveis erros:

Código | HTTP | Descrição
------ | ---- | ---------
URL_INVALIDA | 400 | URL inválida
ALIAS_JA_EXISTE | 409 | alias já utilizado
URL_NAO_ENCONTRADA | 404 | URL não existe
URL_EXPIRADA | 410 | URL expirou
ERRO_INTERNO | 500 | erro inesperado

---

# Internacionalização

A API suporta múltiplos idiomas usando o header:

Accept-Language

Idiomas disponíveis:

Idioma | Código
------ | ------
Português | pt-BR
Inglês | en
Espanhol | es
Francês | fr

Exemplo:

Accept-Language: en

Resposta:

```
{
  "codigo": "URL_INVALIDA",
  "mensagem": "The provided URL is invalid"
}
```

---

# Testando via PowerShell

Criar URL:

```
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/v1/urls" -ContentType "application/json" -Body '{"originalUrl":"https://www.ciata.org.br"}'
```

Consultar detalhes:

```
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/v1/urls/{id}"
```

Testar redirecionamento:

```
Invoke-WebRequest -Uri "http://localhost:8080/{id}" -MaximumRedirection 0
```

---

# Script automatizado de testes

Arquivo:

testar-encurtador.ps1

Executa automaticamente:

- criação de URL
- alias customizado
- validação
- expiração
- testes de idioma
- consulta
- redirecionamento

Executar:

```
powershell -ExecutionPolicy Bypass -File .\testar-encurtador.ps1
```

---

# Acessibilidade

A API foi projetada para ser utilizada facilmente via terminal, facilitando uso com leitores de tela como:

- JAWS
- NVDA

Boas práticas adotadas:

- comandos em uma linha
- respostas em JSON simples
- mensagens de erro padronizadas
- script automatizado para execução sequencial

---

# Melhorias futuras

Possíveis evoluções do projeto:

- persistência em banco de dados
- autenticação de usuários
- analytics de acesso
- dashboard administrativo
- documentação automática com Swagger
- rate limiting

---

# Autor

Cássio Santos

Projeto desenvolvido como exercício de arquitetura e desenvolvimento de APIs utilizando Spring Boot.
