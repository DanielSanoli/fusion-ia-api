from typing import List, Optional

def build_prompt(
        pokemons: List[str],
        style: Optional[str] = "sprite",
        dominant_color: Optional[str] = None,
        seed: Optional[int] = None
) -> str:
    style_map = {
        "sprite": "pixel art, clean outlines, 2D game sprite",
        "anime": "anime style, vibrant, cel shading",
        "semi-realistic": "semi-realistic creature concept art"
    }
    style_phrase = style_map.get(style, "creative illustration")

    types_phrase = "combined elemental traits"
    # Futuro: consultar Pokedéx-API para obter tipos reais

    color_phrase = f"dominant color {dominant_color}" if dominant_color else "balanced color palette"

    return (
        f"Fusion of {' and '.join(pokemons)}, {types_phrase}. "
        f"Style: {style_phrase}. {color_phrase}. "
        f"Centered composition, clean silhouette."
    )