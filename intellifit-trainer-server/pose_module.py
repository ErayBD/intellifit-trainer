import mediapipe as mp
import math
import cv2

# goruntu islemeyi yapacak olan sinif
class PoseDetector:
    def __init__(self, static_image_mode=False, model_complexity=1,
                 smooth_landmarks=True, enable_segmentation=False,
                 smooth_segmentation=True, min_detection_confidence=0.5,
                 min_tracking_confidence=0.5):

        # her bir landmark'in gercek koordinatlarini tutan liste
        self.lmList = None
        # img uzerindeki pose islemlerini yapar
        self.results = None
        # mediapipe kutuphanesindeki Pose sinifi degiskenleri
        self.mode = static_image_mode
        self.complexity = model_complexity
        self.smooth = smooth_landmarks
        self.e_segmentation = enable_segmentation
        self.s_segmentation = smooth_segmentation
        self.d_confidence = min_detection_confidence
        self.t_confidence = min_tracking_confidence
        # algilanan noktalari, cizgileri ve diger sekilleri goruntulerin uzerine cizer
        self.mpDraw = mp.solutions.drawing_utils
        # mediapipe'in pose modulune erisilir
        self.mpPose = mp.solutions.pose
        # mediapipe'in pose objesi
        self.pose = self.mpPose.Pose(self.mode, self.complexity, self.smooth,
                                     self.e_segmentation, self.s_segmentation,
                                     self.d_confidence, self.t_confidence)

    # img üzerinde pose-detection ve ardindan connectionlari yapar, sonra bu img'i dondurur
    def findPose(self, img, draw=True):
        # okunan goruntunun renk formatini BGR'den RGB'ye cevirir
        # ek: OpenCV img'leri BGR formatında okur, ancak MediaPipe gibi bazi kutuphaneler RGB formatında calisir
        imgRGB = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        # donusturulmus img üzerinde pose detection islemi yapar
        self.results = self.pose.process(imgRGB)

        # img uzerinde herhangi bir landmark tespit edilip edilmedigin kontrol eder
        if self.results.pose_landmarks:
            if draw:
                # pose detection islemi sonucunda algilanan her bir vucut noktasini belirler
                drawing_spec = self.mpDraw.DrawingSpec(thickness=5, circle_radius=2, color=(0, 0, 255))
                # alginan noktalari (landmark'lari) birbirlerine baglayan cizgileri belirler
                connection_drawing_spec = self.mpDraw.DrawingSpec(thickness=2, color=(0, 255, 0))
                # algılanan landmark'lari ve bu landmark'lari birbirine baglayan cizgileri img uzerine cizer
                self.mpDraw.draw_landmarks(img, self.results.pose_landmarks, self.mpPose.POSE_CONNECTIONS,
                                           drawing_spec, connection_drawing_spec)
        # else:
        #     print("No pose landmarks detected.")
        # duzenlenmis olan gorsel return edilir
        return img

    # img üzerindeki her bir pose landmark'ların pixel koordinatlarını bulur ve list halinde dondurur
    def findPosition(self, img, draw=True):
        # landmark'lari tutacak olan liste olusturulur
        self.lmList = []

        # img uzerinde herhangi bir landmark tespit edilip edilmedigin kontrol eder
        if self.results.pose_landmarks:
            # id'ler ve landmark'lar (lm) uzerinde gezinir
            for id, lm in enumerate(self.results.pose_landmarks.landmark):
                # img.shape ile görüntünün boyutları alınır (h - yükseklik, w - genişlik, c - renk kanalları)
                h, w, c = img.shape
                # landmark'in normalize edilmiş x ve y koordinatlari...(1)
                # goruntunun gercek boyutlari ile carpılarak pixel cinsinden koordinatlara donusturulur.(2)
                cx, cy = int(lm.x*w), int(lm.y*h)
                # gercek boyutlu koordinatlar lmList listesine eklenir
                self.lmList.append([id, cx, cy])
                # koordinatlar img uzerinde isaretlenir
                if draw:
                    cv2.circle(img, (cx, cy), 5, (0, 0, 255), cv2.FILLED)
        # guncellenmis liste geri dondurulur
        return self.lmList

    # 3 landmark noktasi arasindaki aciyi hesaplar
    def findAngle(self, img, p1, p2, p3, draw=True):
        # fonksiyona gonderilmis olan 3 noktanin x, y bilgileri degiskenlere atanir
        x1, y1 = self.lmList[p1][1:]
        x2, y2 = self.lmList[p2][1:]
        x3, y3 = self.lmList[p3][1:]

        # 3 nokta arasindaki aciyi hesaplar...(1)
        # p2->p3 ve p2->p1 giden vektorler arasindaki aci hesaplanir, p2 pivot (donme) noktasidir
        angle = math.degrees(math.atan2(y3 - y2, x3 - x2) - math.atan2(y1 - y2, x1 - x2))
        # acinin mutlak degerini alarak pozitif bir degerde kalmasini saglar
        if angle < 0:
            angle = abs(angle)
        # aci 180 dereceden buyukse, tumleyici degeri alinir
        if angle > 180:
            angle -= 360
            angle = abs(angle)

        if draw:
            # noktalari birbirine baglayan iki dogru cizer
            cv2.line(img, (x1, y1), (x2, y2), (0, 255, 0), 5)
            cv2.line(img, (x2, y2), (x3, y3), (0, 255, 0), 5)
            # icte kucuk, dista buyuk nokta olmak uzere her noktayi iki kez isaretler
            cv2.circle(img, (x1, y1), 5, (255, 0, 0), cv2.FILLED)
            cv2.circle(img, (x1, y1), 10, (255, 0, 0), 3)

            cv2.circle(img, (x2, y2), 5, (255, 0, 0), cv2.FILLED)
            cv2.circle(img, (x2, y2), 10, (255, 0, 0), 3)

            cv2.circle(img, (x3, y3), 5, (255, 0, 0), cv2.FILLED)
            cv2.circle(img, (x3, y3), 10, (255, 0, 0), 3)

            # noktalar arasindaki acinin derecesini img uzerine yazdirir
            cv2.putText(img, str(int(angle)), (x2, y2), cv2.FONT_HERSHEY_PLAIN,
                                          2, (0, 0, 255), 2)
        # hesaplanan aci degeri dondurulur
        return angle



