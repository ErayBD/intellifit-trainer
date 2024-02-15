from exercises import *
import numpy as np
import time

# capture, goruntulenecek olan medyayi belirler
# cap = cv2.VideoCapture(0)
cap = cv2.VideoCapture('PoseVideos/squat_1.mp4')

# goruntu islemeyi yapacak olan nesne tanimlanir
detector = PoseDetector()

while True:
    # video/kamera uzerinde calisacaksa:
    success, img = cap.read()

    # fotograf uzerinde calisilacaksa:
    # img = cv2.imread('PosePictures/pic_1.jpg')

    # gorsel uzerindeki landmark noktalarini isaretler, connectionlari cizebilir
    img = detector.findPose(img, False)
    # img'in gercek boyutlarini ceker
    h, w, c = img.shape
    # img'deki tum landmark koordinatlari lmList degiskenine atanir, noktalari gosterebilir
    lmList = detector.findPosition(img, False)

    # herhangi bir landmark noktasi bulunduysa
    if len(lmList) != 0:
        # landmarklardaki noktalarin acisini hesaplar ve img uzerine yazar
        # if -> bacaklar, else -> uzuvlar
        if is_legs:
            angle_left = detector.findAngle(img, 23, 25, 27, is_leftLimb)
            angle_right = detector.findAngle(img, 24, 26, 28, is_rightLimb)

        elif is_arms:
            angle_left = detector.findAngle(img, 11, 13, 15, is_leftLimb)
            angle_right = detector.findAngle(img, 12, 14, 16, is_rightLimb)

        elif is_ankles:
            angle_left = detector.findAngle(img, 17, 11, 27, is_leftLimb)
            angle_right = detector.findAngle(img, 18, 12, 28, is_rightLimb)

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
        sqr_size = int(min(w, h) * 0.20)
        # square dikdortgeninin kenarlardan uzaklik mesafesi, w veya h'den hangisi kucukse %5'si olarak ayarla
        sqr_padding = int(min(w, h) * 0.05)
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
        sqr_font_scale = sqr_size / 250 * 8
        # square dortgeninin geometrik merkezini hesaplar
        sqr_text_x = sqr_rect_start_x + sqr_size // 2
        sqr_text_y = sqr_rect_start_y + sqr_size // 2
        # square dortgeninin icindeki metnin hizasini ve ne kadar yer kaplayacagini hesaplamak icin kullanilir
        align_text = str(int(counter_left) + int(counter_right)) if is_leftLimb and is_rightLimb else str(
            int(counter_left)) if is_leftLimb else str(int(counter_right))
        (sqr_text_width, sqr_text_height), _ = cv2.getTextSize(align_text, cv2.FONT_HERSHEY_PLAIN,
                                                               int(sqr_font_scale), int(sqr_font_scale // 2))
        # square dortgeninin icindeki metnin koordinati
        text_x = sqr_rect_start_x + (sqr_size - sqr_text_width) // 2
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
        cv2.rectangle(img, sqr_rect_start, sqr_rect_end, (0, 255, 0), cv2.FILLED)
        cv2.rectangle(img, sqr_rect_start, sqr_rect_end, (0, 0, 255), 3)
        if is_leftLimb and is_rightLimb:
            cv2.putText(img, str(int(counter_left) + int(counter_right)), (text_x, text_y),
                        cv2.FONT_HERSHEY_PLAIN, int(sqr_font_scale), (0, 0, 255), int(sqr_font_scale))
        elif is_leftLimb:
            cv2.putText(img, str(int(counter_left)), (text_x, text_y),
                        cv2.FONT_HERSHEY_PLAIN, int(sqr_font_scale), (0, 0, 255), int(sqr_font_scale))

        elif is_rightLimb:
            cv2.putText(img, str(int(counter_right)), (text_x, text_y),
                        cv2.FONT_HERSHEY_PLAIN, int(sqr_font_scale), (0, 0, 255), int(sqr_font_scale))

        # img uzerinde gorunecek olan FPS hesaplanir, previous-time, current-time, frame-per-second
        # pTime = 0
        # cTime = time.time()
        # fps = 1/(cTime-pTime)
        # pTime = cTime
        # medyanin FPS degerini img uzerine yazdirir
        # cv2.putText(img, str(int(fps)), (50, 100), cv2.FONT_HERSHEY_PLAIN, 6, (0, 255, 255), 5)

    # medyanin goruntulenmesi icin kullanilir
    cv2.imshow("Image", img)

    # program q tusuyla sonlandirilir
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

