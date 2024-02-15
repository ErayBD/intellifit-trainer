import asyncio
import websockets
import numpy as np
import cv2
import json
from PIL import Image
from pose_module import *

is_legs = False
is_arms = False
is_ankles = False
is_leftLimb = False
is_rightLimb = False
counter_left = 0
counter_right = 0
dir_left = 0
dir_right = 0
min_degree = 40
max_degree = 140

detector = PoseDetector()
current_exercise_function = None

exercise_list = []
active_exercise_index = 0
repCount_list = []
active_repCount_index = 0
setCount_list = []
active_setCount_index = 0

def reset_degrees():
    global min_degree, max_degree
    min_degree = 40
    max_degree = 140

def degree_extender(ext_min=0, ext_max=0):
    global min_degree, max_degree
    min_degree += ext_min
    max_degree += ext_max

def select_limb(which_limb):
    global is_rightLimb, is_leftLimb
    if which_limb.lower() == "right":
        is_rightLimb = True
        is_leftLimb = False
    elif which_limb.lower() == "left":
        is_leftLimb = True
        is_rightLimb = False
    elif which_limb.lower() == "both":
        is_rightLimb = True
        is_leftLimb = True

def select_part(which_part):
    global is_arms, is_legs, is_ankles
    if which_part.lower() == "arms":
        is_arms = True
        is_legs = False
        is_ankles = False
    elif which_part.lower() == "legs":
        is_arms = False
        is_legs = True
        is_ankles = False
    elif which_part.lower() == "ankles":
        is_arms = False
        is_legs = False
        is_ankles = True

# exercises, tekil egzersiz tanimlamalari
# Push Up
def exc_push_up(which_limb="left", which_part="arms"):
    degree_extender(ext_min=20)  # min_degree = 60
    select_limb(which_limb)
    select_part(which_part)

# Dumbell Curl
def exc_dumbbell_curl(which_limb="left", which_part="arms"):
    degree_extender(ext_min=0)  # min_degree = 40
    select_limb(which_limb)
    select_part(which_part)

# High Knees
def exc_high_knees(which_limb="left", which_part="legs"):
    degree_extender(ext_min=30)  # min_degree = 70
    select_limb(which_limb)
    select_part(which_part)

# Cable Triceps
def exc_cable_triceps(which_limb="left", which_part="arms"):
    degree_extender(ext_min=30)  # min_degree = 70
    select_limb(which_limb)
    select_part(which_part)

def exc_mountain_climbers(which_limb="left", which_part="legs"):
    degree_extender(ext_min=20)  # min_degree = 60
    select_limb(which_limb)
    select_part(which_part)

def exc_lunge(which_limb="left", which_part="legs"):
    degree_extender(ext_min=40)  # min_degree = 80
    select_limb(which_limb)
    select_part(which_part)

def exc_pull_up(which_limb="left", which_part="arms"):
    degree_extender(ext_min=10)  # min_degree = 50
    select_limb(which_limb)
    select_part(which_part)

def exc_squat(which_limb="left", which_part="legs"):
    degree_extender(ext_min=40)  # min_degree = 80
    select_limb(which_limb)
    select_part(which_part)

def exc_jumping_rope(which_limb="left", which_part="ankles"):
    degree_extender(ext_min=0, ext_max=(-90))  # min_degree = 40, max_degree = 50
    select_limb(which_limb)
    select_part(which_part)

def exc_jumping_jack(which_limb="left", which_part="ankles"):
    degree_extender(ext_min=0, ext_max=20)  # min_degree = 20, max_degree = 150
    select_limb(which_limb)
    select_part(which_part)

async def echo(websocket, path):
    global current_exercise_function
    global is_legs, is_arms, is_ankles, is_leftLimb, is_rightLimb
    global counter_left, counter_right
    global dir_left, dir_right
    global min_degree, max_degree
    global exercise_list, active_exercise_index
    global repCount_list, active_repCount_index
    global setCount_list, active_setCount_index

    print("Client connected")

    async for message in websocket:
        if message is None:
            continue
        # Burada mesajın bir byte array mi yoksa JSON string mi olduğu kontrol ediliyor
        if isinstance(message, str):
            try:
                data = json.loads(message)

                if data['exercise'] not in exercise_list:
                    exercise_list.append(data['exercise'])
                    repCount_list.append(data['repCount'])
                    setCount_list.append(data['setCount'])

                # Seçilen egzersiz fonksiyonunu değiştir
                if exercise_list[active_exercise_index] == 'push_up':
                    print("Push Up exercise selected")
                    current_exercise_function = exc_push_up

                elif exercise_list[active_exercise_index] == 'dumbbell_curl':
                    print("Dumbbell Curl exercise selected")
                    current_exercise_function = exc_dumbbell_curl

                elif exercise_list[active_exercise_index] == 'high_knees':
                    print("High Knees exercise selected")
                    current_exercise_function = exc_high_knees

                elif exercise_list[active_exercise_index] == 'cable_triceps':
                    print("Cable Triceps exercise selected")
                    current_exercise_function = exc_cable_triceps

                elif exercise_list[active_exercise_index] == 'mountain_climbers':
                    print("Mountain Climbers exercise selected")
                    current_exercise_function = exc_mountain_climbers

                elif exercise_list[active_exercise_index] == 'lunge':
                    print("Lunge exercise selected")
                    current_exercise_function = exc_lunge

                elif exercise_list[active_exercise_index] == 'pull_up':
                    print("Pull Up exercise selected")
                    current_exercise_function = exc_pull_up

                elif exercise_list[active_exercise_index] == 'squat':
                    print("Squat exercise selected")
                    current_exercise_function = exc_squat

                elif exercise_list[active_exercise_index] == 'jumping_rope':
                    print("Jumping Rope exercise selected")
                    current_exercise_function = exc_jumping_rope

                elif exercise_list[active_exercise_index] == 'jumping_jack':
                    print("Jumping Jack exercise selected")
                    current_exercise_function = exc_jumping_jack

            except json.JSONDecodeError:
                print("JSON Decode Error")

        elif isinstance(message, bytes):
            print("Received image data, size:", len(message))
            nparr = np.frombuffer(message, np.uint8)
            img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
            img = detector.findPose(img, False)
            h, w, c = img.shape
            lmList = detector.findPosition(img, False)

            if current_exercise_function is not None:
                reset_degrees()
                current_exercise_function()

                if len(lmList) != 0:
                    # landmarklardaki noktalarin acisini hesaplar ve img uzerine yazar
                    # if -> bacaklar, else -> uzuvlar
                    if is_legs:
                        # angle_left = detector.findAngle(img, 23, 25, 27, is_leftLimb)
                        # angle_right = detector.findAngle(img, 24, 26, 28, is_rightLimb)
                        angle_left = detector.findAngle(img, 23, 25, 27, True)
                        angle_right = detector.findAngle(img, 24, 26, 28, True)

                    elif is_arms:
                        # angle_left = detector.findAngle(img, 11, 13, 15, is_leftLimb)
                        # angle_right = detector.findAngle(img, 12, 14, 16, is_rightLimb)
                        angle_left = detector.findAngle(img, 11, 13, 15, True)
                        angle_right = detector.findAngle(img, 12, 14, 16, True)

                    elif is_ankles:
                        # angle_left = detector.findAngle(img, 17, 11, 27, is_leftLimb)
                        # angle_right = detector.findAngle(img, 18, 12, 28, is_rightLimb)
                        angle_left = detector.findAngle(img, 17, 11, 27, True)
                        angle_right = detector.findAngle(img, 18, 12, 28, True)

                    # sag ve sol uzuv icin interpolasyon islemi yapar, amac yuzdelik gosterim
                    per_right = np.interp(angle_right, (min_degree, max_degree), (100, 0))
                    per_left = np.interp(angle_left, (min_degree, max_degree), (100, 0))

                    # bar dortgeninin img uzerindeki ozellikleri
                    bar_color_left = (0, 255, 0)
                    bar_color_right = (0, 255, 0)
                    # bar dortgenin baslangic koordinati, sag ve sol uzuv icin
                    bar_rect_start_x_left = int(w * 0.90)
                    bar_rect_start_y_left = int(h * 0.20)
                    bar_rect_start_x_right = int(w * 0.05)
                    bar_rect_start_y_right = int(h * 0.20)
                    # bar dortgenin bitis koordinati, sag ve sol uzuv icin
                    bar_rect_end_x_left = int(w * 0.95)
                    bar_rect_end_y_left = int(h * 0.90)
                    bar_rect_end_x_right = int(w * 0.10)
                    bar_rect_end_y_right = int(h * 0.90)
                    # bar dortgeni uzerindeki yuzde yazisi, sag ve sol uzuv icin
                    bar_text_x_left = int(w * 0.84)
                    bar_text_y_left = int(h * 0.15)
                    bar_text_x_right = int(w * 0.03)
                    bar_text_y_right = int(h * 0.15)
                    # bar dortgeni icerisindeki barin ust kısminin y eksenindeki konumu, sag ve sol uzuv icin
                    bar_left = bar_rect_end_y_left - (bar_rect_end_y_left - bar_rect_start_y_left) * per_left / 100
                    bar_right = bar_rect_end_y_right - (bar_rect_end_y_right - bar_rect_start_y_right) * per_right / 100

                    # square dortgeninin boyutu, w veya h'den hangisi kucukse %20'si olarak ayarla
                    sqr_size = int(min(w, h) * 0.30)  # default: 0.20
                    # square dikdortgeninin kenarlardan uzaklik mesafesi, w veya h'den hangisi kucukse %5'si olarak ayarla
                    sqr_padding = int(min(w, h) * 0.075)  # default 0.05
                    # square dikdorgenin baslangic ve bitis koordinatlari
                    if is_leftLimb and is_rightLimb:
                        sqr_rect_start_x = (w - sqr_size) // 2
                    elif is_leftLimb:
                        sqr_rect_start_x = sqr_padding
                    elif is_rightLimb:
                        sqr_rect_start_x = w - sqr_size - sqr_padding

                    sqr_rect_start_y = h - sqr_size - sqr_padding
                    # square dortgeninin baslangic ve bitis koordinatlari, sol ust ve sag alt
                    sqr_rect_start = (sqr_rect_start_x, sqr_rect_start_y)
                    sqr_rect_end = (sqr_rect_start_x + sqr_size, sqr_rect_start_y + sqr_size)
                    # square dortgeninin icinde yazan metin boyutu
                    sqr_font_scale = sqr_size / 250 * 6  # default: x8
                    # square dortgeninin geometrik merkezini hesaplar
                    sqr_text_x = sqr_rect_start_x + sqr_size // 2
                    sqr_text_y = sqr_rect_start_y + sqr_size // 2
                    # square dortgeninin icindeki metnin hizasini ve ne kadar yer kaplayacagini hesaplamak icin kullanilir
                    align_text = \
                        f"{int(counter_left) + int(counter_right)}/{repCount_list[active_repCount_index]}" if is_leftLimb and is_rightLimb \
                        else f"{int(counter_left)}/{repCount_list[active_repCount_index]}" if is_leftLimb \
                        else f"{int(counter_right)}/{repCount_list[active_repCount_index]}"

                    (sqr_text_width, sqr_text_height), _ = cv2.getTextSize(align_text, cv2.FONT_HERSHEY_PLAIN,
                                                                           int(sqr_font_scale),
                                                                           int(sqr_font_scale // 2))
                    # square dortgeninin icindeki metnin koordinati
                    text_x = sqr_rect_start_x + (sqr_size - sqr_text_width)
                    text_y = sqr_rect_start_y + (sqr_size + sqr_text_height) // 2

                    # counter ve bar_color degiskenini gunceller, her bir asagi indiris ve yukari kaldiris icin +0.5
                    # sag ve/veya sol uzvun aktif olup olmadigini kontrol ederek isleme devam eder
                    if is_rightLimb:
                        if per_right == 100:
                            bar_color_right = (0, 255, 0)
                            if dir_right == 0:
                                counter_right += 0.5
                                dir_right = 1
                        elif per_right == 0:
                            bar_color_right = (0, 0, 255)
                            if dir_right == 1:
                                counter_right += 0.5
                                dir_right = 0
                        else:
                            bar_color_right = (0, 255, 255)

                    if is_leftLimb:
                        if per_left == 100:
                            bar_color_left = (0, 255, 0)
                            if dir_left == 0:
                                counter_left += 0.5
                                dir_left = 1
                        elif per_left == 0:
                            bar_color_left = (0, 0, 255)
                            if dir_left == 1:
                                counter_left += 0.5
                                dir_left = 0
                        else:
                            bar_color_left = (0, 255, 255)

                    # bar, tekrar sayisini grafiksel sekilde gosterir
                    # sol uzun icin grafik sol tarafta
                    if is_leftLimb:
                        cv2.rectangle(img, (bar_rect_start_x_left, bar_rect_start_y_left),
                                      (bar_rect_end_x_left, bar_rect_end_y_left), bar_color_left, 5)
                        cv2.rectangle(img, (bar_rect_start_x_left, int(bar_left)),
                                      (bar_rect_end_x_left, bar_rect_end_y_left), bar_color_left, cv2.FILLED)
                        cv2.putText(img, f'{int(per_left)}%', (bar_text_x_left, bar_text_y_left),
                                    cv2.FONT_HERSHEY_PLAIN, 2, (0, 0, 255), 2)
                    # sag uzuv icin grafik sag tarafta
                    if is_rightLimb:
                        cv2.rectangle(img, (bar_rect_start_x_right, bar_rect_start_y_right),
                                      (bar_rect_end_x_right, bar_rect_end_y_right), bar_color_right, 5)
                        cv2.rectangle(img, (bar_rect_start_x_right, int(bar_right)),
                                      (bar_rect_end_x_right, bar_rect_end_y_right), bar_color_right, cv2.FILLED)
                        cv2.putText(img, f'{int(per_right)}%', (bar_text_x_right, bar_text_y_right),
                                    cv2.FONT_HERSHEY_PLAIN, 2, (0, 0, 255), 2)

                    # counter, img uzerine tekrar sayilarini yazar
                    # counter_text_left = f"{int(counter_left)}/{repCount_list[active_repCount_index]}"
                    # counter_text_right = f"{int(counter_right)}/{repCount_list[active_repCount_index]}"
                    # counter_text_both = f"{int(counter_left) + int(counter_right)}/{repCount_list[active_repCount_index]}"

                    cv2.rectangle(img, sqr_rect_start, sqr_rect_end, (0, 255, 0), cv2.FILLED)
                    cv2.rectangle(img, sqr_rect_start, sqr_rect_end, (0, 0, 255), 3)
                    if is_leftLimb and is_rightLimb:
                        cv2.putText(img, align_text, (text_x, text_y),
                                    cv2.FONT_HERSHEY_SIMPLEX, int(sqr_font_scale // 2), (0, 0, 255), int(sqr_font_scale))
                    elif is_leftLimb:
                        cv2.putText(img, align_text, (text_x, text_y),
                                    cv2.FONT_HERSHEY_SIMPLEX, int(sqr_font_scale // 2), (0, 0, 255), int(sqr_font_scale))

                    elif is_rightLimb:
                        cv2.putText(img, align_text, (text_x, text_y),
                                    cv2.FONT_HERSHEY_SIMPLEX, int(sqr_font_scale // 2), (0, 0, 255), int(sqr_font_scale))



                    exercise_name = exercise_list[active_exercise_index].replace("_", " ").title()
                    exercise_name_text = f"Exercise: {exercise_name}"
                    cv2.putText(img, exercise_name_text, (int(w * 0.02), int(h * 0.05)), cv2.FONT_HERSHEY_SIMPLEX, int(sqr_font_scale // 2), (0, 255, 0), 2)

                    remaining_sets_text = f"Remaining Sets: {setCount_list[active_setCount_index]}"
                    cv2.putText(img, remaining_sets_text, (int(w * 0.02), int(h * 0.08)), cv2.FONT_HERSHEY_SIMPLEX, int(sqr_font_scale // 2),
                                (0, 255, 0), 2)

                    if counter_left >= repCount_list[active_repCount_index] or \
                       counter_right >= repCount_list[active_repCount_index] or \
                       counter_left + counter_right >= repCount_list[active_repCount_index]:

                        setCount_list[active_setCount_index] -= 1


                        if setCount_list[active_setCount_index] == 0:
                            print("Exercise completed!")
                            active_exercise_index += 1
                            active_repCount_index += 1
                            active_setCount_index += 1


                            if active_exercise_index < len(exercise_list):
                                reset_degrees()
                                current_exercise_function = globals()['exc_' + exercise_list[active_exercise_index]]
                                print("Next exercise -> " + exercise_list[active_exercise_index])
                            else:
                                print("Workout Completed.")
                                return

                            counter_left = 0
                            counter_right = 0

                        else:
                            counter_left = 0
                            counter_right = 0

            # _, buffer = cv2.imencode('.webp', img)
            # _, buffer = cv2.imencode('.jpg', img)
            _, buffer = cv2.imencode('.webp', img, [cv2.IMWRITE_WEBP_QUALITY, 70])
            # _, buffer = cv2.imencode('.jpg', img, [int(cv2.IMWRITE_JPEG_QUALITY), 40])

            await websocket.send(buffer.tobytes())

start_server = websockets.serve(echo, "192.168.60.81", 12345)

asyncio.get_event_loop().run_until_complete(start_server)
print("Server is running...")
asyncio.get_event_loop().run_forever()
