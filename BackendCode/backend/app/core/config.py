import os
from pathlib import Path

# Proje kök dizini (main.py'nin bulunduğu yer)
BASE_DIR = Path(__file__).resolve().parents[4]  # parking-detection/

class Settings:
    # Model & video yolları
    MODEL_PATH: str = str(BASE_DIR / "detection" / "model" / "yolov8l.pt")
    VIDEO_DIR: str = r"E:\PARK404\parking-detection-2\detection\videos"
    UPLOAD_DIR: str = str(BASE_DIR / "backend" / "uploads")
    OUTPUT_DIR: str = str(BASE_DIR / "backend" / "outputs")

    # YOLO ayarları
    CONFIDENCE: float = 0.3
    VEHICLE_IDS: list = [2, 3, 5, 7]  # COCO: car, motorcycle, bus, truck

    # API ayarları
    APP_TITLE: str = "Parking Detection API"
    APP_VERSION: str = "1.0.0"
    APP_DESCRIPTION: str = "YOLOv8 tabanlı otopark doluluk tespiti"
    ALLOWED_ORIGINS: list = ["*"]
    MAX_UPLOAD_SIZE_MB: int = 500

settings = Settings()
