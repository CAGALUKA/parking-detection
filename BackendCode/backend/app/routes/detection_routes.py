"""
detection_routes.py
Tüm /api/v1 endpoint'leri burada tanımlanır.
"""

import os
import shutil

from fastapi import APIRouter, File, HTTPException, Query, UploadFile
from fastapi.responses import FileResponse

from backend.app.core.config import settings
from backend.app.models.response_model import (
    DetectionResult,
    HealthResponse,
    ProcessVideoResponse,
    VideoUploadResponse,
)
from backend.app.services.parking_service import (
    analyze_frame,
    analyze_single_frame_at_interval,  # ← ekle
    analyze_video_frames,
    get_video_metadata,
    process_full_video,
)
from detection.parking_detection import detector
from detection.parking_slots import PARKING_SPOTS

router = APIRouter(prefix="/api/v1", tags=["Parking Detection"])

ALLOWED_EXTENSIONS = {".mp4", ".avi", ".mov", ".mkv"}


# ─────────────────────────────────────────────────────────────────────────────
# Sağlık kontrolü
# ─────────────────────────────────────────────────────────────────────────────

@router.get("/health", response_model=HealthResponse, summary="Sistem sağlık durumu")
def health_check():
    """Model yüklü mü, klasörler var mı kontrol eder."""
    return HealthResponse(
        status="ok",
        model_loaded=detector._initialized,
        total_spots=len(PARKING_SPOTS),
        upload_dir_exists=os.path.isdir(settings.UPLOAD_DIR),
        output_dir_exists=os.path.isdir(settings.OUTPUT_DIR),
    )


# ─────────────────────────────────────────────────────────────────────────────
# Demo video — anlık analiz
# ─────────────────────────────────────────────────────────────────────────────

@router.get(
    "/detect/snapshot",
    response_model=DetectionResult,
    summary="Demo video üzerinden anlık frame analizi",
)
def detect_snapshot(
    frame: int = Query(0, ge=0, description="Analiz edilecek frame numarası"),
    with_image: bool = Query(False, description="Yanıta işlenmiş frame ekle (base64)"),
    video: str = Query("demo1.mp4", description="detection/videos/ altındaki dosya adı"),
):
    video_path = os.path.join(settings.VIDEO_DIR, video)
    if not os.path.exists(video_path):
        raise HTTPException(404, f"Video bulunamadı: {video}")
    try:
        return analyze_frame(video_path, frame, with_image)
    except (ValueError, FileNotFoundError) as exc:
        raise HTTPException(400, str(exc))


# ─────────────────────────────────────────────────────────────────────────────
# Demo video — metadata
# ─────────────────────────────────────────────────────────────────────────────

@router.get("/videos/{filename}/info", summary="Video metadata")
def video_info(filename: str):
    """Toplam frame sayısı, FPS, çözünürlük gibi bilgileri döndürür."""
    path = os.path.join(settings.VIDEO_DIR, filename)
    if not os.path.exists(path):
        # uploads klasörüne de bak
        path = os.path.join(settings.UPLOAD_DIR, filename)
    if not os.path.exists(path):
        raise HTTPException(404, f"Video bulunamadı: {filename}")
    try:
        return get_video_metadata(path)
    except FileNotFoundError as exc:
        raise HTTPException(404, str(exc))


# ─────────────────────────────────────────────────────────────────────────────
# Video yükleme
# ─────────────────────────────────────────────────────────────────────────────

@router.post(
    "/upload",
    response_model=VideoUploadResponse,
    summary="Yeni video yükle",
)
async def upload_video(file: UploadFile = File(...)):
    ext = os.path.splitext(file.filename or "")[-1].lower()
    if ext not in ALLOWED_EXTENSIONS:
        raise HTTPException(
            400,
            f"Desteklenmeyen format: {ext}. İzin verilenler: {ALLOWED_EXTENSIONS}",
        )

    dest = os.path.join(settings.UPLOAD_DIR, file.filename)
    try:
        with open(dest, "wb") as f:
            shutil.copyfileobj(file.file, f)
    except Exception as exc:
        raise HTTPException(500, f"Dosya kaydedilemedi: {exc}")

    return VideoUploadResponse(
        message="Video başarıyla yüklendi.",
        filename=file.filename,
        path=dest,
    )


# ─────────────────────────────────────────────────────────────────────────────
# Yüklenen video — anlık analiz
# ─────────────────────────────────────────────────────────────────────────────

@router.get(
    "/detect/uploaded/{filename}",
    response_model=DetectionResult,
    summary="Yüklenen video üzerinden frame analizi",
)
def detect_uploaded(
    filename: str,
    frame: int = Query(0, ge=0),
    with_image: bool = Query(False),
):
    path = os.path.join(settings.UPLOAD_DIR, filename)
    if not os.path.exists(path):
        raise HTTPException(404, f"Yüklenen video bulunamadı: {filename}")
    try:
        return analyze_frame(path, frame, with_image)
    except (ValueError, FileNotFoundError) as exc:
        raise HTTPException(400, str(exc))


# ─────────────────────────────────────────────────────────────────────────────
# Tam video işleme (arka plan değil, senkron — büyük videolar için task queue önerilir)
# ─────────────────────────────────────────────────────────────────────────────

@router.post(
    "/process/{filename}",
    response_model=ProcessVideoResponse,
    summary="Yüklenen videoyu tamamen işle",
)
def process_video(
    filename: str,
    skip_frames: int = Query(
        1, ge=1,
        description="Her N. frame'i analiz et (1=hepsi, 2=bir atla…)"
    ),
):
    try:
        return process_full_video(filename, skip_frames)
    except FileNotFoundError as exc:
        raise HTTPException(404, str(exc))
    except Exception as exc:
        raise HTTPException(500, f"Video işleme hatası: {exc}")


# ─────────────────────────────────────────────────────────────────────────────
# İşlenmiş video indirme
# ─────────────────────────────────────────────────────────────────────────────

@router.get("/outputs/{filename}", summary="İşlenmiş videoyu indir")
def download_output(filename: str):
    path = os.path.join(settings.OUTPUT_DIR, filename)
    if not os.path.exists(path):
        raise HTTPException(404, f"Çıktı dosyası bulunamadı: {filename}")
    return FileResponse(
        path,
        media_type="video/mp4",
        filename=filename,
    )


# ─────────────────────────────────────────────────────────────────────────────
# Spot listesi
# ─────────────────────────────────────────────────────────────────────────────

@router.get("/spots", summary="Tanımlı park yeri koordinatlarını listele")
def list_spots():
    return {
        "total": len(PARKING_SPOTS),
        "spots": [
            {"spot_id": i + 1, "coordinates": spot}
            for i, spot in enumerate(PARKING_SPOTS)
        ],
    }


from backend.app.services.parking_service import (
    analyze_frame,
    analyze_video_frames,   # ← bunu da import et
    get_video_metadata,
    process_full_video,
)

@router.get(
    "/detect/frames/{filename}",
    summary="Videodan belli aralıklarla frame alıp analiz et",
)
def detect_frames(
    filename: str,
    skip_frames: int = Query(30, ge=1, description="Kaç frame'de bir analiz yapılsın"),
    include_image: bool = Query(False, description="Her frame için base64 görsel ekle"),
):
    # Önce videos/ klasörüne bak, sonra uploads/
    path = os.path.join(settings.VIDEO_DIR, filename)
    if not os.path.exists(path):
        path = os.path.join(settings.UPLOAD_DIR, filename)
    if not os.path.exists(path):
        raise HTTPException(404, f"Video bulunamadı: {filename}")

    results = analyze_video_frames(path, skip_frames, include_image)

    return {
        "filename": filename,
        "skip_frames": skip_frames,
        "total_analyzed": len(results),
        "results": results,
    }


@router.get(
    "/detect/at/{filename}",
    summary="Videodan belirli saniyedeki tek frame'i analiz et",
)
def detect_at_second(
    filename: str,
    second: float = Query(0.0, ge=0.0, description="Videonun kaçıncı saniyesi alınsın"),
    include_image: bool = Query(False, description="Base64 görsel ekle"),
):
    path = os.path.join(settings.VIDEO_DIR, filename)
    if not os.path.exists(path):
        path = os.path.join(settings.UPLOAD_DIR, filename)
    if not os.path.exists(path):
        raise HTTPException(404, f"Video bulunamadı: {filename}")

    from backend.app.services.parking_service import analyze_single_frame_at_interval
    return analyze_single_frame_at_interval(path, second, include_image)

from fastapi.responses import StreamingResponse

@router.get(
    "/detect/stream/{filename}",
    summary="Canlı yayın gibi her N saniyede bir frame analiz et",
)
async def detect_stream(        # ← async ekle
    filename: str,
    interval: float = Query(10.0, ge=1.0, description="Kaç saniyede bir analiz yapılsın"),
    include_image: bool = Query(False),
):
    path = os.path.join(settings.VIDEO_DIR, filename)
    if not os.path.exists(path):
        path = os.path.join(settings.UPLOAD_DIR, filename)
    if not os.path.exists(path):
        raise HTTPException(404, f"Video bulunamadı: {filename}")

    from backend.app.services.parking_service import stream_analyze

    return StreamingResponse(
        stream_analyze(path, interval, include_image),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "X-Accel-Buffering": "no",
            "Connection": "keep-alive",  # ← ekle
        },
    )