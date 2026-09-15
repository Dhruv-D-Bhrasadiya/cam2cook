from contextlib import asynccontextmanager

from fastapi import FastAPI
from sqlalchemy import select

from app.api.routes import auth, health
from app.core.config import get_settings
from app.core.security import hash_password
from app.db.models import Base, User
from app.db.session import SessionLocal, engine


async def initialize_database() -> None:
    settings = get_settings()
    async with engine.begin() as connection:
        await connection.run_sync(Base.metadata.create_all)

    async with SessionLocal() as session:
        existing_user = await session.scalar(select(User).where(User.email == settings.seed_user_email.lower()))
        if existing_user is None:
            session.add(
                User(
                    name=settings.seed_user_name,
                    email=settings.seed_user_email.lower(),
                    password_hash=hash_password(settings.seed_user_password),
                )
            )
            await session.commit()


@asynccontextmanager
async def lifespan(_: FastAPI):
    await initialize_database()
    yield
    await engine.dispose()


settings = get_settings()
app = FastAPI(title=settings.app_name, version="0.1.0", lifespan=lifespan)
app.include_router(health.router, prefix="/api/v1")
app.include_router(auth.router, prefix="/api/v1")


@app.get("/", tags=["meta"])
async def root() -> dict[str, str]:
    return {"name": settings.app_name, "docs": "/docs", "health": "/api/v1/health"}