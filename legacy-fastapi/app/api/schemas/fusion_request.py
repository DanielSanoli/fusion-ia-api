from pydantic import BaseModel, Field
from typing import List, Optional

class FusionRequest(BaseModel):
    pokemons: List[str] = Field(min_items=2, max_items=3)
    style: Optional[str] = "sprite"
    dominantColor: Optional[str] = None
    seed: Optional[int] = None