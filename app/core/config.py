from pydantic import BaseSettings

class Settings(BaseSettings):
    ENV: str = "dev"
    RATE_LIMIT_PER_HOUR: int = 10

    class Config:
        env_file = ".env"

settings = Settings()