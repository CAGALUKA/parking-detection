# Parking Detection API

YOLOv8 tabanlı otopark doluluk tespiti — FastAPI backend.

## Kurulum

```bash
# 1. Bağımlılıkları yükle
pip install -r requirements.txt

# 2. YOLOv8 modelini indir (ilk çalıştırmada otomatik indirilir)
#    veya elle koy: detection/model/yolov8l.pt

# 3. Demo videoyu koy
#    detection/videos/demo1.mp4

# 4. Uygulamayı başlat
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

Swagger UI → http://localhost:8000/docs

## Endpoint'ler

| Metod | Yol | Açıklama |
|-------|-----|----------|
| GET | `/api/v1/health` | Sistem sağlık durumu |
| GET | `/api/v1/detect/snapshot` | Demo video, anlık frame analizi |
| GET | `/api/v1/videos/{filename}/info` | Video metadata |
| POST | `/api/v1/upload` | Yeni video yükle |
| GET | `/api/v1/detect/uploaded/{filename}` | Yüklenen video, frame analizi |
| POST | `/api/v1/process/{filename}` | Tüm videoyu işle |
| GET | `/api/v1/outputs/{filename}` | İşlenmiş videoyu indir |
| GET | `/api/v1/spots` | Tanımlı spot koordinatları |

## Örnek İstekler

```bash
# Sağlık kontrolü
curl http://localhost:8000/api/v1/health

# Demo video 10. frame analizi (görsel dahil)
curl "http://localhost:8000/api/v1/detect/snapshot?frame=10&with_image=true"

# Video yükle
curl -X POST "http://localhost:8000/api/v1/upload" \
     -F "file=@/path/to/video.mp4"

# Yüklenen videoyu işle (her 2. frame)
curl -X POST "http://localhost:8000/api/v1/process/video.mp4?skip_frames=2"

# İşlenmiş videoyu indir
curl -O "http://localhost:8000/api/v1/outputs/out_video.mp4"
```

## Proje Yapısı

```
parking-detection/
├── main.py                          # FastAPI giriş noktası
├── requirements.txt
├── backend/
│   ├── uploads/                     # Yüklenen videolar
│   ├── outputs/                     # İşlenmiş videolar
│   └── app/
│       ├── core/config.py           # Merkezi ayarlar
│       ├── routes/detection_routes.py
│       ├── services/parking_service.py
│       ├── models/response_model.py
│       └── utils/video_utils.py
└── detection/
    ├── model/yolov8l.pt             # YOLO model dosyası
    ├── videos/demo1.mp4             # Demo video
    ├── parking_detection.py         # YOLO + cv2 motoru
    └── parking_slots.py             # Spot koordinatları
```
