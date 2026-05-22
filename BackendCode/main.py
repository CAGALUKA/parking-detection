"""
main.py  —  FastAPI uygulama giriş noktası
Çalıştırmak için:
    uvicorn main:app --reload --host 0.0.0.0 --port 8000
Swagger UI: http://localhost:8000/docs
"""

import os

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from backend.app.core.config import settings
from backend.app.routes.detection_routes import router

# ─────────────────────────────────────────────────────────────────────────────
# Uygulama oluştur
# ─────────────────────────────────────────────────────────────────────────────

app = FastAPI(
    title=settings.APP_TITLE,
    description=settings.APP_DESCRIPTION,
    version=settings.APP_VERSION,
    docs_url="/docs",
    redoc_url="/redoc",
)

# ─────────────────────────────────────────────────────────────────────────────
# CORS
# ─────────────────────────────────────────────────────────────────────────────

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ─────────────────────────────────────────────────────────────────────────────
# Başlangıçta klasörleri oluştur
# ─────────────────────────────────────────────────────────────────────────────

@app.on_event("startup")
async def startup_event():
    os.makedirs(settings.UPLOAD_DIR, exist_ok=True)
    os.makedirs(settings.OUTPUT_DIR, exist_ok=True)
    print(f"[startup] Upload klasörü : {settings.UPLOAD_DIR}")
    print(f"[startup] Output klasörü : {settings.OUTPUT_DIR}")
    print(f"[startup] Model yolu     : {settings.MODEL_PATH}")

# ─────────────────────────────────────────────────────────────────────────────
# Router
# ─────────────────────────────────────────────────────────────────────────────

app.include_router(router)

# ─────────────────────────────────────────────────────────────────────────────
# Kök endpoint
# ─────────────────────────────────────────────────────────────────────────────

@app.get("/", include_in_schema=False)
def root():
    return JSONResponse({
        "message": "Parking Detection API çalışıyor.",
        "docs": "/docs",
        "health": "/api/v1/health",
    })
