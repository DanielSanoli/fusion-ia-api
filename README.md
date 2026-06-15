# fusion-ia-api

Microserviço Java/Spring Boot para criação de fusões de Pokémon com geração de imagem por IA.

O serviço mantém o provider `stub` como padrão para desenvolvimento local sem credenciais externas e também oferece provider real `openai`, implementado com Spring AI e OpenAI Image Model.

## Arquitetura resumida

Camadas principais:

- `controller`: endpoints REST versionados em `/api/v1`.
- `service`: orquestração da criação/consulta de fusões e montagem de prompt.
- `domain/model`: modelo de domínio (`Fusion`, `FusionStatus`).
- `dto`: requests, responses e payloads de erro.
- `repository`: armazenamento em memória via `ConcurrentHashMap`.
- `provider`: contrato de geração de imagem e providers (`stub` e `openai`).
- `config`: propriedades da aplicação (`fusion.*`).
- `exception`: tratamento global padronizado com `@RestControllerAdvice`.

> A implementação FastAPI anterior foi preservada em `legacy-fastapi/` apenas como referência histórica da migração.

## Stack utilizada

- Java 21
- Spring Boot 3
- Maven
- Spring Web
- Spring AI OpenAI
- Bean Validation / Jakarta Validation
- JUnit 5 + Spring Boot Test
- Docker multi-stage build

## Porta

- `8000` por padrão, compatível com o `porygonz-gateway`.

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/v1/health` | Health check simples |
| `POST` | `/api/v1/fusions` | Cria uma fusão |
| `GET` | `/api/v1/fusions/{id}` | Consulta metadados de uma fusão |
| `GET` | `/api/v1/fusions` | Lista fusões criadas em memória |
| `GET` | `/api/v1/fusions/{id}/image` | Retorna JSON com placeholder stub, URL real ou base64 |

## Providers disponíveis

### `stub` — padrão/local

- Não exige `OPENAI_API_KEY`.
- Simula a geração e retorna `status = READY`.
- `imageUrl` aponta para `/api/v1/fusions/{id}/image`.
- O endpoint de imagem retorna JSON informando que a imagem é stub.

### `openai` — Spring AI/OpenAI real

- Exige `OPENAI_API_KEY` via variável de ambiente.
- Usa o `ImageModel` do Spring AI para chamar o modelo configurado.
- Retorna `provider = "openai"`.
- Se a OpenAI retornar URL, ela é salva em `imageUrl`.
- Se retornar base64, o base64 fica disponível apenas em `GET /api/v1/fusions/{id}/image`, evitando resposta pesada no `POST`.

## Criar fusão

### Request

```json
{
  "pokemons": ["charizard", "gengar"],
  "style": "dark fantasy sprite",
  "dominantColor": "purple",
  "seed": 123
}
```

Validações:

- `pokemons` é obrigatório.
- mínimo de 2 Pokémon.
- máximo de 4 Pokémon.
- nomes não podem ser vazios.
- `style`, `dominantColor` e `seed` são opcionais.

### Response com provider `stub`

```json
{
  "id": "f2f96f09-3b34-45f0-a622-59ab2c7d63d5",
  "pokemons": ["charizard", "gengar"],
  "status": "READY",
  "prompt": "Create a Pokémon-inspired fusion between Charizard and Gengar.\nStyle: dark fantasy sprite.\nDominant color: purple.\nSeed: 123.\nKeep the design consistent with monster game aesthetics.\nGenerate a clean character concept.",
  "imageUrl": "/api/v1/fusions/f2f96f09-3b34-45f0-a622-59ab2c7d63d5/image",
  "provider": "stub",
  "imageContentType": null,
  "metadata": {
    "mode": "stub",
    "generated": false
  },
  "style": "dark fantasy sprite",
  "dominantColor": "purple",
  "seed": 123,
  "createdAt": "2026-06-14T10:00:00Z"
}
```

### Response com provider `openai`

```json
{
  "id": "f2f96f09-3b34-45f0-a622-59ab2c7d63d5",
  "pokemons": ["charizard", "gengar"],
  "status": "READY",
  "prompt": "Create a Pokémon-inspired fusion between Charizard and Gengar...",
  "imageUrl": "https://...",
  "provider": "openai",
  "imageContentType": null,
  "metadata": {
    "model": "gpt-image-1",
    "size": "1024x1024",
    "hasImageUrl": true,
    "hasImageBase64": false,
    "resultCount": 1
  },
  "style": "dark fantasy sprite",
  "dominantColor": "purple",
  "seed": 123,
  "createdAt": "2026-06-14T10:00:00Z"
}
```

Se o provider retornar base64, o `POST` não expõe o campo grande; consulte `/api/v1/fusions/{id}/image`.

## Respostas de erro

Erros de validação retornam HTTP `400` com payload padronizado:

```json
{
  "timestamp": "2026-06-14T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/api/v1/fusions",
  "fieldErrors": [
    {
      "field": "pokemons",
      "message": "pokemons must contain between 2 and 4 items"
    }
  ]
}
```

## Configurações

Arquivo principal: `src/main/resources/application.yml`.

| Propriedade | Variável de ambiente | Padrão | Descrição |
|---|---|---|---|
| `server.port` | `SERVER_PORT` | `8000` | Porta HTTP |
| `fusion.provider` | `FUSION_PROVIDER` | `stub` | Provider de geração |
| `fusion.image-dir` | `FUSION_IMAGE_DIR` | `app/data/images` | Diretório futuro para imagens |
| `fusion.rate-limit-per-hour` | `FUSION_RATE_LIMIT_PER_HOUR` | `10` | Configuração preparada para rate limit |
| `spring.ai.openai.api-key` | `OPENAI_API_KEY` | vazio | Chave OpenAI exigida apenas quando `FUSION_PROVIDER=openai` |
| `spring.ai.openai.image.options.model` | `OPENAI_IMAGE_MODEL` | `gpt-image-1` | Modelo de imagem OpenAI |
| `spring.ai.openai.image.options.size` | `OPENAI_IMAGE_SIZE` | `1024x1024` | Tamanho da imagem |

Trecho relevante do `application.yml`:

```yaml
spring:
  ai:
    model:
      image: ${FUSION_PROVIDER:stub}
      chat: none
      embedding: none
      moderation: none
      audio:
        speech: none
        transcription: none
    openai:
      api-key: ${OPENAI_API_KEY:}
      image:
        options:
          model: ${OPENAI_IMAGE_MODEL:gpt-image-1}
          size: ${OPENAI_IMAGE_SIZE:1024x1024}

fusion:
  provider: ${FUSION_PROVIDER:stub}
```

Nunca coloque chaves reais no repositório. Use variável de ambiente, Docker secret, secret manager ou configuração segura do ambiente.

## Rodando localmente

Pré-requisitos:

- JDK 21
- Maven 3.9+

### Rodar com stub

```powershell
mvn clean test
mvn spring-boot:run
```

### Rodar com OpenAI real

No PowerShell:

```powershell
$env:FUSION_PROVIDER = "openai"
$env:OPENAI_API_KEY = "sua-chave-aqui"
$env:OPENAI_IMAGE_MODEL = "gpt-image-1"
$env:OPENAI_IMAGE_SIZE = "1024x1024"
mvn spring-boot:run
```

Se `FUSION_PROVIDER=openai` e `OPENAI_API_KEY` não estiver definida, a aplicação falha na inicialização ou na chamada com mensagem clara de configuração ausente.

Teste rápido:

```powershell
Invoke-RestMethod -Method Get -Uri http://localhost:8000/api/v1/health
```

Criar fusão:

```powershell
$body = @{
  pokemons = @("charizard", "gengar")
  style = "dark fantasy sprite"
  dominantColor = "purple"
  seed = 123
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri http://localhost:8000/api/v1/fusions -ContentType "application/json" -Body $body
```

## Rodando com Docker

### Stub

```powershell
docker build -t fusion-ia-api .
docker run --rm -p 8000:8000 fusion-ia-api
```

### OpenAI

```powershell
docker build -t fusion-ia-api .
docker run --rm -p 8000:8000 `
  -e FUSION_PROVIDER=openai `
  -e OPENAI_API_KEY=$env:OPENAI_API_KEY `
  -e OPENAI_IMAGE_MODEL=gpt-image-1 `
  -e OPENAI_IMAGE_SIZE=1024x1024 `
  fusion-ia-api
```

## Comandos úteis

```powershell
mvn test
mvn clean package
mvn spring-boot:run
```

Com `make`, se disponível no ambiente:

```powershell
make test
make build
make run
```

## Endpoint de imagem

`GET /api/v1/fusions/{id}/image`

Stub:

```json
{
  "id": "uuid",
  "status": "READY",
  "provider": "stub",
  "imageUrl": "/api/v1/fusions/{id}/image",
  "imageBase64": null,
  "imageContentType": null,
  "metadata": {
    "mode": "stub",
    "generated": false
  },
  "message": "Image generation is currently served by the stub provider. No binary image is generated yet."
}
```

OpenAI com URL:

```json
{
  "id": "uuid",
  "status": "READY",
  "provider": "openai",
  "imageUrl": "https://...",
  "imageBase64": null,
  "imageContentType": null,
  "metadata": {
    "model": "gpt-image-1",
    "size": "1024x1024",
    "hasImageUrl": true,
    "hasImageBase64": false
  },
  "message": "Image generated by OpenAI. Use imageUrl to access the generated asset."
}
```

OpenAI com base64:

```json
{
  "id": "uuid",
  "status": "READY",
  "provider": "openai",
  "imageUrl": null,
  "imageBase64": "iVBORw0KGgo...",
  "imageContentType": "image/png",
  "metadata": {
    "hasImageBase64": true
  },
  "message": "Image generated by OpenAI and returned as base64 payload."
}
```

## Troubleshooting

- **A aplicação pede chave OpenAI no modo local**: confirme que `FUSION_PROVIDER` está ausente ou igual a `stub`.
- **`OPENAI_API_KEY is required when fusion.provider=openai`**: defina a variável `OPENAI_API_KEY` antes de iniciar.
- **Erro 502 `IMAGE_GENERATION_FAILED`**: falha retornada pelo provider externo, autenticação, limite, timeout ou resposta inválida.
- **Docker não sobe na porta 8000**: verifique se outro processo já usa a porta.
- **Modelo/tamanho inválido**: ajuste `OPENAI_IMAGE_MODEL` e `OPENAI_IMAGE_SIZE` conforme opções suportadas pela OpenAI/Spring AI.

## Roadmap

- Persistência com PostgreSQL via Spring Data JPA.
- Armazenamento de imagens em volume ou object storage.
- Integração formal com `porygonz-gateway`.
- Observabilidade com logs estruturados, correlation-id e métricas.
- Rate limiting real por cliente/rota.
