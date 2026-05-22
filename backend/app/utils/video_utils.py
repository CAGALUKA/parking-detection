"""
video_utils.py
OpenCV tabanlı video / frame yardımcı fonksiyonları.
"""

import base64
import os

import cv2
import numpy as np


# ─────────────────────────────────────────────────────────────────────────────
# Frame ↔ base64
# ─────────────────────────────────────────────────────────────────────────────

def frame_to_base64(frame: np.ndarray, quality: int = 85) -> str:
    """OpenCV frame → JPEG base64 string (frontend'e göndermek için)."""
    encode_params = [cv2.IMWRITE_JPEG_QUALITY, quality]
    success, buffer = cv2.imencode(".jpg", frame, encode_params)
    if not success:
        raise RuntimeError("Frame JPEG'e encode edilemedi.")
    return base64.b64encode(buffer).decode("utf-8")


# ─────────────────────────────────────────────────────────────────────────────
# Frame okuma
# ─────────────────────────────────────────────────────────────────────────────

def get_video_frame(video_path: str, frame_index: int = 0) -> np.ndarray:
    """
    Video dosyasından belirli bir frame'i okur.

    Args:
        video_path:  Video dosyasının tam yolu.
        frame_index: Okunacak frame numarası (0-tabanlı).

    Returns:
        BGR numpy dizisi.

    Raises:
        FileNotFoundError: Dosya yoksa.
        ValueError:        Frame okunamazsa.
    """
    if not os.path.exists(video_path):
        raise FileNotFoundError(f"Video bulunamadı: {video_path}")

    cap = cv2.VideoCapture(video_path)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))

    if frame_index >= total_frames:
        cap.release()
        raise ValueError(
            f"frame_index={frame_index} geçersiz. "
            f"Video toplam {total_frames} frame içeriyor."
        )

    cap.set(cv2.CAP_PROP_POS_FRAMES, frame_index)
    ret, frame = cap.read()
    cap.release()

    if not ret or frame is None:
        raise ValueError(f"Frame {frame_index} okunamadı: {video_path}")

    return frame


def get_video_info(video_path: str) -> dict:
    """Video hakkında temel metadata döndürür."""
    if not os.path.exists(video_path):
        raise FileNotFoundError(f"Video bulunamadı: {video_path}")

    cap = cv2.VideoCapture(video_path)
    info = {
        "total_frames": int(cap.get(cv2.CAP_PROP_FRAME_COUNT)),
        "fps": cap.get(cv2.CAP_PROP_FPS),
        "width": int(cap.get(cv2.CAP_PROP_FRAME_WIDTH)),
        "height": int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT)),
        "duration_seconds": 0.0,
    }
    fps = info["fps"]
    if fps and fps > 0:
        info["duration_seconds"] = round(info["total_frames"] / fps, 2)
    cap.release()
    return info


# ─────────────────────────────────────────────────────────────────────────────
# Video işleme & kaydetme
# ─────────────────────────────────────────────────────────────────────────────

def process_and_save_video(
    input_path: str,
    output_path: str,
    detector,
    skip_frames: int = 1,
) -> dict:
    """
    Tüm videoyu kare kare işleyip yeni bir MP4 olarak kaydeder.

    Args:
        input_path:   Kaynak video yolu.
        output_path:  Çıktı video yolu.
        detector:     ParkingDetector örneği.
        skip_frames:  Her N. frame'i işle (1 = hepsi, 2 = bir atla …).

    Returns:
        {"total_frames", "processed_frames", "fps", "output_path"}
    """
    if not os.path.exists(input_path):
        raise FileNotFoundError(f"Video bulunamadı: {input_path}")

    cap = cv2.VideoCapture(input_path)
    fps = cap.get(cv2.CAP_PROP_FPS) or 25.0
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))

    fourcc = cv2.VideoWriter_fourcc(*"mp4v")
    out = cv2.VideoWriter(output_path, fourcc, fps, (width, height))

    processed = 0
    frame_idx = 0
    last_annotated = None  # skip edilen frame'lere önceki annotasyon uygulanır

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        if frame_idx % skip_frames == 0:
            result = detector.detect_frame(frame)
            last_annotated = result["annotated_frame"]
            processed += 1
        else:
            # skip edilen frame: son annotasyonu kullan (fps tutarlılığı için)
            last_annotated = frame if last_annotated is None else last_annotated

        out.write(last_annotated)
        frame_idx += 1

    cap.release()
    out.release()

    return {
        "total_frames": total_frames,
        "processed_frames": processed,
        "fps": fps,
        "output_path": output_path,
    }
