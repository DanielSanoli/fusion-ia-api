from app.services.prompt_builder import build_prompt

def test_prompt_basic():
    prompt = build_prompt(["charizard","venusaur"], style="sprite", dominant_color="emerald")
    assert "Fusion of charizard and venusaur" in prompt.lower()
    assert "pixel art" in prompt.lower()