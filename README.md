# fusion-ia-api ⚠️ (INCOMPLETO / EM DESENVOLVIMENTO)

API em Python (FastAPI) para realizar fusões de Pokémon com suporte a geração de imagens (atualmente com backend *stub*), com objetivo de evoluir para um gerador por IA.

## Visão geral

O serviço expõe endpoints para solicitar uma fusão (com estilo, seed, cor dominante, etc.) e consultar o resultado.

Atualmente existe um pipeline inicial:
- Recebe request de fusão
- Monta um prompt (prompt builder)
- Executa uma “geração” (stub) e retorna status/URL

A intenção é evoluir para:
- Persistência em Postgres (metadados)
- Armazenamento de imagens em volume filesystem
- Geração real via IA (local ou provider externo)

## Stack
- Python 3.11+
- FastAPI + Uvicorn
- Pydantic
- (planejado) Postgres + SQLAlchemy/Alembic

## Portas
- **8000** (padrão)

## Endpoints

### Health
- `GET /api/v1/health` *(recomendado padronizar)*  
  *(no seu código atual pode estar como `/health` dependendo do prefixo no `main.py`)*

### Fusões
- `POST /api/v1/fusions`
- `GET /api/v1/fusions/{fusion_id}`
- `GET /api/v1/fusions/{fusion_id}/image` *(se implementado o endpoint de imagem)*

## Payloads

### Criar fusão (exemplo)
`POST /api/v1/fusions`

```json
{
  "pokemons": ["charizard", "venusaur"],
  "style": "sprite",
  "dominantColor": "emerald",
  "seed": 123
}
```

### Resposta (exemplo)
```json
{
  "id": "uuid",
  "pokemons": ["charizard", "venusaur"],
  "status": "ready",
  "prompt": "Fusion of ...",
  "imageUrl": "/api/v1/fusions/<id>/image",
  "backend": "stub"
}
```

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `ENV` | `dev` | Ambiente |
| `RATE_LIMIT_PER_HOUR` | `10` | Rate limit (se usado) |
| `IMAGE_DIR` | `app/data/images` | Diretório de saída das imagens (quando habilitado) |

> Se você usar `.env`, o `python-dotenv` pode carregar automaticamente dependendo da sua inicialização.

## Rodando local

### 1) Criar e ativar virtualenv
No Windows (PowerShell):

```bash
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
```

### 2) Rodar
```bash
uvicorn app.main:app --reload --port 8000
```

Acesse:
- http://127.0.0.1:8000/docs

## Rodando com Docker

```bash
docker build -t fusion-ia-api .
docker run --rm -p 8000:8000 fusion-ia-api
```

## Rodando com Docker Compose (via microservices-infra)
O recomendado é subir tudo pelo repo `microservices-infra`.

## Testes / Qualidade
Comandos (via Makefile):
- `make install`
- `make fmt`
- `make lint`
- `make test`

## Roadmap
- Persistência em Postgres (metadados das fusões)
- Salvar imagens em volume e servir via endpoint
- Correlation-Id e logs estruturados
- Backend de geração por IA (local/externo)