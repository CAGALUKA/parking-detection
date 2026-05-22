"""
parking_service.py
HTTP katmanı ile tespit motoru arasındaki iş mantığı.
"""

import os
from datetime import datetime

from backend.app.core.config import settings
from backend.app.models.response_model import DetectionResult, SpotStatus, ProcessVideoResponse
from backend.app.utils.video_utils import (
    frame_to_base64,
    get_video_frame,
    get_video_info,
    process_and_save_video,
)
from detection.parking_detection import detector


# ─────────────────────────────────────────────────────────────────────────────
# Frame analizi
# ─────────────────────────────────────────────────────────────────────────────

def analyze_frame(
    video_path: str,
    frame_index: int = 0,
    include_image: bool = False,
) -> DetectionResult:
    """
    Verilen video dosyasından tek bir frame alır ve park doluluk analizi yapar.

    Args:
        video_path:    Video dosyasının tam yolu.
        frame_index:   Analiz edilecek frame numarası (0-tabanlı).
        include_image: True ise işlenmiş frame base64 olarak eklenir.

    Returns:
        DetectionResult pydantic modeli.
    """
    frame = get_video_frame(video_path, frame_index)
    raw = detector.detect_frame(frame)

    spots = [
        SpotStatus(
            spot_id=s["spot_id"],
            occupied=s["occupied"],
            label="DOLU" if s["occupied"] else "BOS",
            coordinates=s["coordinates"],
        )
        for s in raw["spots"]
    ]

    occupied_count = sum(1 for s in spots if s.occupied)
    total = len(spots)
    rate = round(occupied_count / total, 4) if total > 0 else 0.0

    return DetectionResult(
        total_spots=total,
        occupied=occupied_count,
        empty=total - occupied_count,
        occupancy_rate=rate,
        vehicle_count=raw["vehicle_count"],
        spots=spots,
        frame_base64=(
            frame_to_base64(raw["annotated_frame"]) if include_image else None
        ),
        processed_at=datetime.now().isoformat(),
    )


# ─────────────────────────────────────────────────────────────────────────────
# Tam video işleme
# ─────────────────────────────────────────────────────────────────────────────

def process_full_video(filename: str, skip_frames: int = 1) -> ProcessVideoResponse:
    # Önce uploads'a bak, yoksa videos klasörüne bak
    input_path = os.path.join(settings.UPLOAD_DIR, filename)
    
    if not os.path.exists(input_path):
        input_path = os.path.join(settings.VIDEO_DIR, filename)
    
    if not os.path.exists(input_path):
        raise FileNotFoundError(f"Video bulunamadı: {filename}")

    output_filename = f"out_{filename}"
    output_path = os.path.join(settings.OUTPUT_DIR, output_filename)

    result = process_and_save_video(
        input_path=input_path,
        output_path=output_path,
        detector=detector,
        skip_frames=skip_frames,
    )

    return ProcessVideoResponse(
        message="Video başarıyla işlendi.",
        output_filename=output_filename,
        download_url=f"/api/v1/outputs/{output_filename}",
        total_frames=result["total_frames"],
        fps=result["fps"],
    )




# ─────────────────────────────────────────────────────────────────────────────
# Video bilgisi
# ─────────────────────────────────────────────────────────────────────────────

def get_video_metadata(video_path: str) -> dict:
    """Video hakkında temel metadata döndürür."""
    return get_video_info(video_path)


def analyze_video_frames(
    video_path: str,
    skip_frames: int = 5,
    include_image: bool = False,
) -> list:
    info = get_video_info(video_path)
    total_frames = info["total_frames"]

    results = []
    frame_indices = range(0, total_frames, skip_frames)

    for frame_index in frame_indices:
        try:
            result = analyze_frame(video_path, frame_index, include_image)
            # frame_index bilgisini dict olarak ekle
            result_dict = result.model_dump()
            result_dict["frame_index"] = frame_index
            results.append(result_dict)
        except Exception:
            continue

    return results

def analyze_single_frame_at_interval(
    video_path: str,
    interval_seconds: float = 5.0,
    include_image: bool = False,
) -> dict:
    """
    Videodan belirli saniye aralığında tek bir frame çeker ve analiz eder.
    Örnek: interval_seconds=5 → videonun 5. saniyesindeki frame'i alır.
    """
    info = get_video_info(video_path)
    fps = info["fps"]
    total_frames = info["total_frames"]

    # Saniyeyi frame indeksine çevir
    frame_index = int(fps * interval_seconds)

    # Sınır kontrolü
    if frame_index >= total_frames:
        frame_index = total_frames - 1

    result = analyze_frame(video_path, frame_index, include_image)
    result_dict = result.model_dump()
    result_dict["frame_index"] = frame_index
    result_dict["timestamp_seconds"] = round(frame_index / fps, 2)

    return result_dict

import time

async def stream_analyze(
    video_path: str,
    interval_seconds: float = 10.0,
    include_image: bool = False,
):
    import json
    import asyncio

    info = get_video_info(video_path)
    fps = info["fps"]
    total_frames = info["total_frames"]
    duration = info["duration_seconds"]

    frame_step = int(fps * interval_seconds)
    if frame_step < 1:
        frame_step = 1

    current_frame = 0

    while current_frame < total_frames:
        try:
            result = analyze_frame(video_path, current_frame, include_image)
            result_dict = result.model_dump()
            result_dict["frame_index"] = current_frame
            result_dict["timestamp_seconds"] = round(current_frame / fps, 2)
            result_dict["video_duration"] = duration
            result_dict["progress_percent"] = round(
                (current_frame / total_frames) * 100, 1
            )

            yield f"data: {json.dumps(result_dict)}\n\n"
            await asyncio.sleep(0)  # ← event loop'a kontrol ver, anında flush eder

        except Exception as e:
            yield f"data: {json.dumps({'error': str(e), 'frame_index': current_frame})}\n\n"
            await asyncio.sleep(0)

        current_frame += frame_step

    yield f"data: {json.dumps({'status': 'completed', 'total_frames': total_frames})}\n\n"
