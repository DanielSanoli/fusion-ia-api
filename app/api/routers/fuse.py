from fastapi import APIRouter, HTTPException

from app.api.schemas.fusion_request import FusionRequest
from app.api.schemas.fusion_response import FusionCreatedResponse, FusionDetailResponse

from app.services.prompt_builder import build_prompt
from app.services.image_generator import enqueue_generation

import uuid
from datetime import datetime

router = APIRouter()

_fake_db = {}

@router.post("", response_model=FusionCreatedResponse, status_code=202)
def create_fusion(req: FusionRequest):
    if not (2 <= len(req.pokemons) <= 3):
        raise HTTPException(status_code=400, detail="Must send 2 or 3 pokemons")

    fusion_id = str(uuid.uuid4())
    prompt = build_prompt(
        pokemons=req.pokemons,
        style=req.style,
        dominant_color=req.dominantColor,
        seed=req.seed
    )

    record = {
        "id": fusion_id,
        "pokemons": req.pokemons,
        "style": req.style,
        "dominantColor": req.dominantColor,
        "seed": req.seed,
        "status": "pending",
        "requestedAt": datetime.utcnow().isoformat() + "Z",
        "prompt": prompt,
        "imageUrl": None,
        "backend": None,
        "baseTypes": None,
        "suggestedName": None,
        "completedAt": None,
        "errorMessage": None
    }
    _fake_db[fusion_id] = record

    enqueue_generation(fusion_id, record)
    return record

@router.get("/{fusion_id}", response_model=FusionDetailResponse)
def get_fusion(fusion_id: str):
    record = _fake_db.get(fusion_id)
    if not record:
        raise HTTPException(status_code=404, detail="Fusion not found")
    return record