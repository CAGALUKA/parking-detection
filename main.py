import cv2
import numpy as np
from ultralytics import YOLO

# 🔥 model
model = YOLO("yolov8l.pt")

# 🔥 video
video_path = "demo1.mp4"
cap = cv2.VideoCapture(video_path)

if not cap.isOpened():
    print("Video açılamadı!")
    exit()


parking_spots = [
#    [(1289, 884), (1319, 737), (1462, 811), (1461, 896)],
#    [(1287, 884), (1316, 735), (1203, 671), (1162, 782)],
#    [(1159, 787), (1202, 668), (1105, 609), (1054, 698)],
#    [(1050, 700), (1103, 608), (1029, 559), (971, 632)],
#    [(972, 633), (1031, 560), (962, 517), (897, 578)],
#    [(903, 582), (967, 511), (897, 462), (832, 526)],
#    [(490, 834), (650, 697), (592, 631), (442, 750)],
#    [(841, 531), (900, 475), (855, 439), (783, 487)],
#    [(779, 489), (853, 441), (806, 411), (736, 454)],
#    [(735, 452), (807, 407), (762, 386), (701, 421)],
#    [(634, 702), (579, 897), (489, 829), (698, 776)],
#    [(591, 633), (447, 743), (415, 673), (546, 581)],
#    [(386, 609), (514, 530), (546, 578), (411, 668)]
     [(563, 351), (512, 341), (512, 285), (568, 297)],
     [(512, 341), (461, 316), (474, 261), (512, 275)],
     [(464, 311), (476, 248), (443, 237), (427, 283)],
     [(426, 282), (444, 231), (416, 214), (395, 257)],
     [(394, 257), (418, 212), (392, 200), (369, 236)],
     [(370, 235), (394, 200), (373, 189), (352, 217)],
     [(352, 216), (373, 191), (355, 182), (332, 204)],
     [(245, 324), (293, 277), (268, 250), (224, 291)],
     [(223, 284), (273, 247), (253, 226), (210, 257)],
     [(211, 255), (257, 223), (239, 205), (202, 237)],
     [(254, 357), (305, 298), (336, 339), (323, 360)]
]

vehicle_ids = [2, 3, 5, 7]

def is_inside(cx, cy, polygon):
    return cv2.pointPolygonTest(
        np.array(polygon, np.int32), (cx, cy), False
    ) >= 0


while True:
    ret, frame = cap.read()
    if not ret:
        break

    # 🔥 YOLO çalıştır
    results = model(frame, conf=0.3, verbose=False)

    detections = []

    for r in results:
        for box in r.boxes:
            cls = int(box.cls)

            if cls not in vehicle_ids:
                continue

            x1, y1, x2, y2 = map(int, box.xyxy[0])
            cx = (x1 + x2) // 2
            cy = (y1 + y2) // 2

            detections.append((cx, cy))

            cv2.rectangle(frame, (x1,y1), (x2,y2), (255,255,0), 2)

    # 🔥 PARK KONTROL
    for i, spot in enumerate(parking_spots):

        occupied = False

        for (cx, cy) in detections:
            if is_inside(cx, cy, spot):
                occupied = True
                break

        pts = np.array(spot, np.int32).reshape((-1,1,2))

        if occupied:
            color = (0,0,255)
            text = f"{i+1}: DOLU"
        else:
            color = (0,255,0)
            text = f"{i+1}: BOS"

        cv2.polylines(frame, [pts], True, color, 2)

        cv2.putText(frame, text,
                    (spot[0][0], spot[0][1]-10),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.7, color, 2)

    cv2.imshow("Parking Detection (Video)", frame)

    # ESC ile çık
    if cv2.waitKey(1) & 0xFF == 27:
        break

cap.release()
cv2.destroyAllWindows()