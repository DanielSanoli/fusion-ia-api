import time
from typing import Dict

# Stub de geração assíncrona (sincronamente para MVP)
# Futuro: mover para Celery/RQ

def enqueue_generation(fusion_id: str, record: Dict):
    # Simula geração
    time.sleep(0.1)
    # Atualiza estado fake
    record["status"] = "ready"
    record["imageUrl"] = f"/media/fusions/{fusion_id}.png"
    record["backend"] = "stub"
    record["completedAt"] = record["requestedAt"]