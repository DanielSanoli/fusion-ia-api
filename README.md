# fusion-ia-api

Microserviço Java/Spring Boot para criação de fusões de Pokémon com arquitetura preparada para geração de imagens por IA.

Nesta primeira versão migrada, o serviço roda localmente sem credenciais externas usando o provider `stub` por padrão. A integração real com Spring AI fica isolada por abstração (`ImageGenerationProvider`) e pode ser ativada futuramente por profile/configuração.

## Arquitetura resumida

Camadas principais:

- `controller`: endpoints REST versionados em `/api/v1`.
- `service`: orquestração da criação/consulta de fusões e montagem de prompt.
- `domain/model`: modelo de domínio (`Fusion`, `FusionStatus`).
- `dto`: requests, responses e payloads de erro.
- `repository`: armazenamento em memória via `ConcurrentHashMap`.
- `provider`: contrato de geração de imagem e providers (`stub` e classe futura para Spring AI).
- `config`: propriedades da aplicação (`fusion.*`).
- `exception`: tratamento global padronizado com `@RestControllerAdvice`.

> A implementação FastAPI anterior foi preservada em `legacy-fastapi/` apenas como referência histórica da migração.

## Stack utilizada

- Java 21
- Spring Boot 3
- Maven
- Spring Web
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
| `GET` | `/api/v1/fusions/{id}/image` | Retorna resposta JSON stub da imagem |

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

### Response

```json
{
  "id": "f2f96f09-3b34-45f0-a622-59ab2c7d63d5",
  "pokemons": ["charizard", "gengar"],
  "status": "READY",
  "prompt": "Create a Pokémon-inspired fusion between Charizard and Gengar.\nStyle: dark fantasy sprite.\nDominant color: purple.\nSeed: 123.\nKeep the design consistent with monster game aesthetics.\nGenerate a clean character concept.",
  "imageUrl": "/api/v1/fusions/f2f96f09-3b34-45f0-a622-59ab2c7d63d5/image",
  "provider": "stub",
  "style": "dark fantasy sprite",
  "dominantColor": "purple",
  "seed": 123,
  "createdAt": "2026-06-14T10:00:00Z"
}
```

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

Não coloque chaves reais no repositório. Para uma futura integração real com Spring AI, use variáveis de ambiente ou secret manager.

## Rodando localmente

Pré-requisitos:

- JDK 21
- Maven 3.9+

No PowerShell:

```powershell
mvn clean test
mvn spring-boot:run
```

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

```powershell
docker build -t fusion-ia-api .
docker run --rm -p 8000:8000 fusion-ia-api
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

## Integração futura com Spring AI

A aplicação já possui a abstração:

- `ImageGenerationProvider`
- `StubImageGenerationProvider` (`fusion.provider=stub`, padrão)
- `SpringAiImageGenerationProvider` (`spring-ai` profile + `fusion.provider=spring-ai`)

A classe Spring AI ainda não chama um provider real. Para evoluir:

1. adicionar o BOM/dependências oficiais do Spring AI ao `pom.xml`;
2. injetar o client/model de imagem desejado;
3. ler credenciais exclusivamente de variáveis de ambiente/secret manager;
4. persistir status `PENDING`, `READY` e `FAILED` conforme o fluxo real de geração.

## Roadmap

- Integração real com Spring AI.
- Geração real de imagem.
- Persistência com PostgreSQL via Spring Data JPA.
- Armazenamento de imagens em volume ou object storage.
- Integração formal com `porygonz-gateway`.
- Observabilidade com logs estruturados, correlation-id e métricas.
- Rate limiting real por cliente/rota.
