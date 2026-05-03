import cv2
from ultralytics import YOLO

# 🔥 MODEL
model = YOLO("yolov8l.pt")

# 🔗 SENİN M3U8 LİNKİN (hazır gömülü)
stream_url = "https://manifest.googlevideo.com/api/manifest/hls_playlist/expire/1776646483/ei/8yTlaYfxGseLgMMPoKfZ0AE/ip/212.227.226.140/id/EPKWu223XEg.6/itag/96/source/yt_live_broadcast/requiressl/yes/ratebypass/yes/live/1/sgoap/gir%3Dyes%3Bitag%3D140/sgovp/gir%3Dyes%3Bitag%3D137/rqh/1/hls_chunk_host/rr3---sn-5uh5o-f5fs.googlevideo.com/xpc/EgVo2aDSNQ%3D%3D/playlist_duration/30/manifest_duration/30/bui/AUUZDGJTPMzKy-ODCUWDwseIngEWAcoYtl23fVRDlKysq4-Tlf3VQ2KYhlcx1bPkhUi_RMl2qdwiZXNi/spc/jlWavTdxia0beQbYHdXCCfGRWaxplE1rRlBpawpp5Ey9imskXuYbVqzzpyShsx1_h32D1w/vprv/1/ns/fVbExiQeKXHQ7oz8XSjWyvEU/reg/0/playlist_type/DVR/initcwndbps/75000/met/1776624886,/mh/dB/mm/44/mn/sn-5uh5o-f5fs/ms/lva/mv/m/mvi/3/pl/23/rms/lva,lva/dover/11/pacing/0/keepalive/yes/fexp/51565116,51565682,51869322/n/r2H9iaHk6pCz-w/mt/1776624277/sparams/expire,ei,ip,id,itag,source,requiressl,ratebypass,live,sgoap,sgovp,rqh,xpc,playlist_duration,manifest_duration,bui,spc,vprv,ns,reg,playlist_type/sig/AHEqNM4wRQIgNoaFk4HdaNmxB5WDYueNEf1SfosN1ksIXVA2TBkiHY8CIQCmiFqqlGHlquCQGbO1pUrP41KKAMCtk8TNRXyPqfCdqA%3D%3D/lsparams/hls_chunk_host,initcwndbps,met,mh,mm,mn,ms,mv,mvi,pl,rms/lsig/APaTxxMwRgIhAMn4z6d3N_TsIKVUf-RPqX_JQIV7FmpsgAz1JbtqZJ_JAiEA4X-_RpzVIz_AYGtg4To89TUk5k8-df9jCYNrvAyMZFQ%3D/playlist/index.m3u8"

cap = cv2.VideoCapture(stream_url)

# 📏 çizgi
line_y = 300

# 🚗 sayaç
vehicle_count = 0
counted_ids = set()

# araç classları
vehicle_classes = [2, 3, 5, 7]

while True:
    ret, frame = cap.read()

    if not ret:
        print("❌ Stream koptu (link süresi dolmuş olabilir)")
        break

    frame = cv2.resize(frame, (640, 360))

    results = model.track(frame, persist=True, verbose=False)

    if results[0].boxes is not None:
        for box in results[0].boxes:
            cls = int(box.cls[0])
            track_id = int(box.id[0]) if box.id is not None else -1

            if cls in vehicle_classes:
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                cx = int((x1 + x2) // 2)
                cy = int((y1 + y2) // 2)

                cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
                cv2.circle(frame, (cx, cy), 4, (0, 0, 255), -1)

                if abs(cy - line_y) < 5:
                    if track_id not in counted_ids:
                        counted_ids.add(track_id)
                        vehicle_count += 1

    cv2.line(frame, (0, line_y), (640, line_y), (255, 0, 0), 2)

    cv2.putText(frame, f"Arac Sayisi: {vehicle_count}", (10, 30),
                cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 255), 2)

    cv2.imshow("YOLO Trafik Analizi", frame)

    if cv2.waitKey(1) & 0xFF == 27:
        break

cap.release()
cv2.destroyAllWindows()