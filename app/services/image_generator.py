import time
from typing import Dict
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

from app.core.settings import IMAGE_DIR

def _render_placeholder_png(output_path: Path, title: str, subtitle: str) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)

    img = Image.new("RGBA", (512, 512), (20, 24, 32, 255))
    draw = ImageDraw.Draw(img)

    draw.rectangle([24, 24, 488, 120], outline=(120, 220, 120, 255), width=3)
    draw.text((36, 44), title, fill=(235, 235, 235, 255))

    draw.rectangle([24, 150, 488, 240], outline=(80, 160, 240, 255), width=3)
    draw.text((36, 170), subtitle, fill=(235, 235, 235, 255))

    draw.text((24, 480), "fusion-ia-api (stub)", fill=(160, 160, 160, 255))

    img.save(output_path, format="PNG")

def enqueue_generation(fusion_id: str, record: Dict):
    time.sleep(0.1)

    pokes = record.get("pokemons") or []
    title = "Fusion ready"
    subtitle = " + ".join(pokes) if pokes else fusion_id

    image_path = IMAGE_DIR / f"{fusion_id}.png"
    _render_placeholder_png(image_path, title=title, subtitle=subtitle)

    record["status"] = "ready"
    record["backend"] = "stub"
    record["completedAt"] = record["requestedAt"]

    record["imagePath"] = str(image_path)
    record["imageUrl"] = f"/api/v1/fusions/{fusion_id}/image"