from fastapi import FastAPI
from app.api.routers import health, fuse

app = FastAPI(
    title="Fusion IA API",
    version="0.1.0",
    description="Serviço de fusão de Pokémon utilizando geração de imagens por IA."
)

# Registrar rotas
app.include_router(health.router, tags=["health"])
app.include_router(fuse.router, prefix="/fuse", tags=["fuse"])