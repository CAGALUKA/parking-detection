import cv2
import numpy as np

video_path = "demo1.mp4"
cap = cv2.VideoCapture(video_path)

if not cap.isOpened():
    print("Video açılamadı!")
    exit()

cap.set(cv2.CAP_PROP_POS_FRAMES, 200)

ret, frame = cap.read()
if not ret:
    print("Frame alınamadı!")
    exit()

img_original = frame.copy()
zoom = 1.0

all_parks = []
current_park = []

def redraw():
    resized = cv2.resize(img_original, None, fx=zoom, fy=zoom)
    display = resized.copy()

    for park in all_parks:
        pts = (np.array(park) * zoom).astype(int).reshape((-1,1,2))
        cv2.polylines(display, [pts], True, (0,255,0), 2)

    for pt in current_park:
        x = int(pt[0] * zoom)
        y = int(pt[1] * zoom)
        cv2.circle(display, (x,y), 5, (0,0,255), -1)

    cv2.imshow("Frame", display)

def mouse(event, x, y, flags, param):
    global zoom, current_park, all_parks

    if event == cv2.EVENT_MOUSEWHEEL:
        if flags > 0:
            zoom *= 1.1
        else:
            zoom /= 1.1

        zoom = max(0.5, min(zoom, 5))
        redraw()

    elif event == cv2.EVENT_LBUTTONDOWN:
        real_x = int(x / zoom)
        real_y = int(y / zoom)

        if len(current_park) < 4:
            current_park.append((real_x, real_y))
            print("Şu anki park:", current_park)

        if len(current_park) == 4:
            all_parks.append(current_park.copy())
            print(f"✅ Park tamamlandı: {current_park}")
            print(f"Toplam park: {len(all_parks)}\n")
            current_park.clear()

        redraw()

# FULL SCREEN
cv2.namedWindow("Frame", cv2.WND_PROP_FULLSCREEN)
cv2.setWindowProperty("Frame", cv2.WND_PROP_FULLSCREEN, cv2.WINDOW_FULLSCREEN)

cv2.setMouseCallback("Frame", mouse)

redraw()

print("Mouse wheel = zoom | Sol tık = nokta | ESC = çık")

while True:
    if cv2.waitKey(1) == 27:
        break

cv2.destroyAllWindows()

print("\n🎯 TÜM PARKLAR:")
for i, park in enumerate(all_parks):
    print(f"Park {i+1}: {park}")