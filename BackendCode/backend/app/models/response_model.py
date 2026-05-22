"""
response_model.py
FastAPI endpoint'lerinin döndürdüğü Pydantic şemaları.
"""

from typing import Optional
from pydantic import BaseModel, Field


class SpotStatus(BaseModel):
    spot_id: int = Field(..., description="Spot numarası (1'den başlar)")
    occupied: bool = Field(..., description="True = dolu, False = boş")
    label: str = Field(..., description="'DOLU' veya 'BOS'")
    coordinates: list = Field(..., description="Poligon köşe koordinatları")


class DetectionResult(BaseModel):
    total_spots: int = Field(..., description="Toplam park yeri sayısı")
    occupied: int = Field(..., description="Dolu spot sayısı")
    empty: int = Field(..., description="Boş spot sayısı")
    occupancy_rate: float = Field(..., description="Doluluk oranı (0.0–1.0)")
    vehicle_count: int = Field(..., description="Tespit edilen araç sayısı")
    spots: list[SpotStatus]
    frame_base64: Optional[str] = Field(
        None, description="İşlenmiş frame (JPEG, base64). with_image=true ile dolar."
    )
    processed_at: str = Field(..., description="ISO 8601 zaman damgası")


class VideoUploadResponse(BaseModel):
    message: str
    filename: str
    path: str


class ProcessVideoResponse(BaseModel):
    message: str
    output_filename: str
    download_url: str
    total_frames: int
    fps: float


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool
    total_spots: int
    upload_dir_exists: bool
    output_dir_exists: bool

class DetectionResult(BaseModel):
    total_spots: int
    occupied: int
    empty: int
    occupancy_rate: float
    vehicle_count: int
    spots: list[SpotStatus]
    frame_base64: Optional[str] = None
    processed_at: str
    frame_index: Optional[int] = None   # ← ekle
