"""
parking_detection.py
YOLO + OpenCV ile tek frame veya video üzerinde park yeri tespiti yapar.
"""

import cv2
import numpy as np
from ultralytics import YOLO

from backend.app.core.config import settings
from detection.parking_slots import PARKING_SPOTS


class ParkingDetector:
    _instance = None  # Singleton

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return
        print(f"[ParkingDetector] Model yükleniyor: {settings.MODEL_PATH}")
        self.model = YOLO(settings.MODEL_PATH)
        self.spots = PARKING_SPOTS
        self._initialized = True
        print(f"[ParkingDetector] Model hazır. {len(self.spots)} spot tanımlı.")

    # ─────────────────────────────────────────────────────────────────────────
    # Yardımcı
    # ─────────────────────────────────────────────────────────────────────────

    @staticmethod
    def _point_in_polygon(cx: int, cy: int, polygon: list) -> bool:
        return (
            cv2.pointPolygonTest(
                np.array(polygon, np.int32), (float(cx), float(cy)), False
            )
            >= 0
        )

    # ─────────────────────────────────────────────────────────────────────────
    # Ana tespit
    # ─────────────────────────────────────────────────────────────────────────

    def detect_frame(self, frame: np.ndarray) -> dict:
        """
        Tek bir frame üzerinde araç + park yeri tespiti yapar.

        Returns:
            {
                "spots": [{"spot_id", "occupied", "coordinates"}, ...],
                "vehicle_count": int,
                "annotated_frame": np.ndarray
            }
        """
        results = self.model(
            frame, conf=settings.CONFIDENCE, verbose=False
        )

        # Tespit edilen araçların merkez noktaları
        centers: list[tuple[int, int]] = []
        vehicle_count = 0

        for r in results:
            for box in r.boxes:
                cls = int(box.cls)
                if cls not in settings.VEHICLE_IDS:
                    continue

                x1, y1, x2, y2 = map(int, box.xyxy[0])
                cx, cy = (x1 + x2) // 2, (y1 + y2) // 2
                centers.append((cx, cy))
                vehicle_count += 1

                # Araç kutusunu çiz (sarı)
                cv2.rectangle(frame, (x1, y1), (x2, y2), (255, 255, 0), 2)

        # Park yeri doluluk kontrolü
        spot_results = []
        for i, spot in enumerate(self.spots):
            occupied = any(
                self._point_in_polygon(cx, cy, spot) for cx, cy in centers
            )

            spot_results.append(
                {
                    "spot_id": i + 1,
                    "occupied": occupied,
                    "coordinates": spot,
                }
            )

            # Poligon rengi: kırmızı=dolu, yeşil=boş
            color = (0, 0, 255) if occupied else (0, 255, 0)
            pts = np.array(spot, np.int32).reshape((-1, 1, 2))
            cv2.polylines(frame, [pts], True, color, 2)
            cv2.putText(
                frame,
                f"{i + 1}: {'DOLU' if occupied else 'BOS'}",
                (spot[0][0], spot[0][1] - 10),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.6,
                color,
                2,
            )

        # Özet bilgi: sol üst köşe
        total = len(self.spots)
        occ = sum(1 for s in spot_results if s["occupied"])
        cv2.putText(
            frame,
            f"Dolu: {occ}/{total}  Bos: {total - occ}/{total}",
            (10, 30),
            cv2.FONT_HERSHEY_SIMPLEX,
            0.8,
            (255, 255, 255),
            2,
        )

        return {
            "spots": spot_results,
            "vehicle_count": vehicle_count,
            "annotated_frame": frame,
        }

def analyze_video_frames(
    video_path: str,
    skip_frames: int = 5,
    include_image: bool = False,
) -> list:   # ← list[DetectionResult] yerine sadece list
    """
    Videodan her skip_frames'de bir frame alır ve analiz eder.
    Örnek: skip_frames=30 → her 30 frame'de bir analiz yapar.
    """
    from backend.app.utils.video_utils import get_video_info

    info = get_video_info(video_path)
    total_frames = info["total_frames"]

    results = []
    frame_indices = range(0, total_frames, skip_frames)

    for frame_index in frame_indices:
        try:
            result = analyze_frame(video_path, frame_index, include_image)
            result.frame_index = frame_index  # hangi frame olduğunu ekle
            results.append(result)
        except Exception:
            continue

    return results    




# Uygulama genelinde tek örnek
detector = ParkingDetector()
