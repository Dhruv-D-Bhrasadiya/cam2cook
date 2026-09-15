from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = "Cam2Cook API"
    environment: str = "development"
    database_url: str = "postgresql+asyncpg://user:password@db:5432/cam2cook"
    jwt_secret: str = "change-this-secret-in-production"
    jwt_algorithm: str = "HS256"
    access_token_expire_minutes: int = 60
    seed_user_name: str = "Cam2Cook Admin"
    seed_user_email: str = "admin@cam2cook.com"
    seed_user_password: str = "ChangeMe123!"

    model_config = SettingsConfigDict(env_file=".env", case_sensitive=False, extra="ignore")


@lru_cache
def get_settings() -> Settings:
    return Settings()