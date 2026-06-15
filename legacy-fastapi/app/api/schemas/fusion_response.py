from pydantic import BaseModel
from typing import List, Optional

class FusionCreatedResponse(BaseModel):
    id: str
    pokemons: List[str]
    style: Optional[str]
    dominantColor: Optional[str]
    seed: Optional[int]
    status: str
    requestedAt: str
    prompt: Optional[str] = None
    imageUrl: Optional[str] = None
    backend: Optional[str] = None
    baseTypes: Optional[List[str]] = None
    suggestedName: Optional[str] = None
    completedAt: Optional[str] = None
    errorMessage: Optional[str] = None

class FusionDetailResponse(FusionCreatedResponse):
    pass