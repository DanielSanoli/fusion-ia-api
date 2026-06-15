import os
from pathlib import Path

IMAGE_DIR = Path(os.getenv("IMAGE_DIR", "app/data/images")).resolve()
BASE_URL_PREFIX = os.getenv("BASE_URL_PREFIX", "")