# IntelliFit Trainer
IntelliFit Trainer is an Android-based AI-powered mobile app that functions like a smart fitness assistant, monitoring the user during exercise to help them maintain optimal posture.

<br>
<p align="center"><img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/6b5e55be-b7a7-4917-9c2f-4fac6867f7dd" style="width: 33%;"></p>
<br>

## 1. Introduction
Obesity and wrong sports habits have become an increasing health problem. Many people do not fully realize the importance of sports and struggle to find enough time and resources. This has increased the demand for gyms and private trainers, but the high cost of these services has become a deterrent for many people. In addition, individuals who exercise on their own without the support of a personal trainer can suffer serious short or long-term injuries due to improper body form, especially in bodybuilding exercises such as weight lifting. The 'IntelliFit Trainer' application aims to offer a modern solution to these problems. Working like a personal fitness trainer, this innovative app allows users to exercise accurately and effectively at home or anywhere they want, without the need for a personal trainer. In this way, it aims to make sports more accessible and sustainable for everyone by saving the user time and money. The app, which detects incorrect body positions during exercise by real-time motion tracking and provides feedback to maintain correct form, aims to minimize the risk of injury while making it easier for users to achieve a healthy and fit body. OpenCV and MediaPipe libraries were used as the basis for these operations. OpenCV is used to process the images from the users' cameras, while the MediaPipe pose estimation module is used to analyze body movements and positions in these images. The server side is developed in Python and uses these libraries to process and analyze the incoming image data. The client side is a mobile application developed using Android Studio and displays the data received from the server to the user in real time. The integration and communication of these two technologies is provided by the WebSocket tool, and it is aimed to help users achieve their health and fitness goals by offering them the opportunity to exercise properly at home or wherever they wish. The app is extremely simple and impressive to use as it combines technology and user-friendly design. In addition to providing a safe and effective sports experience, the app also raises awareness about the importance of exercise tracking, movement analysis and proper form. It also provides a valuable alternative for individuals with limited access to gyms and trainers.

## 2. Main Technologies Used
* **OpenCV:** This library is primarily used for image processing and handling video streams. It converts image formats, draws graphical elements on images (like rectangles, text, and lines), and displays video frames. OpenCV facilitates the interface with the camera and the overlay of real-time feedback onto the video feed.
* **MediaPipe:** It is used for advanced pose detection. MediaPipe processes video frames to detect human poses, identifying and marking key landmarks on the human body. This is crucial for analyzing and guiding the user's movements during exercises, as it allows the system to understand and track the posture and movement of the user in real-time.
* **WebSocket:** This is enables communication between the client and server. It facilitates the transfer of screenshots, captured by the user's phone camera, to the server and then sends these images back to the client from the server.
* **Firebase:** This feature is used to manage user membership interactions, including a variety of functions such as creating accounts, password recovery, logging in, and verifying emails.
* **Camera2 API:** It is used to access the user's phone camera and continuously transmit images, and it is also used to optimize the received image by processing it before sending it.
* **Android Studio:** It is used to develop the mobile application.

## 3. Operating Logic
1. **User Installs the Mobile Application:** The user downloads and installs the mobile app on their android phone.
2. **Account Creation and Verification:** The user creates a new account and verifies it through a confirmation email received in their inbox, then logs into their account.
3. **Creating a Workout Program:** Under the "Create Workout" tab, the user can design a personalized workout program. This program is then saved in the "My Workouts" section.
4. **Selecting a Workout:** From the "My Workouts" screen, the user selects the desired workout program.
5. **Starting the Workout:** The user initiates the process by clicking the "Start Workout" button. The only requirement for the user is to position their phone’s camera so that it captures their movements.
6. **Image Transmission to the Server:** Continuously, images captured by the user's camera (client-side) are transmitted to the server.
7. **Image Processing on the Server:** The images sent to the server undergo various preprocessing steps and then are subjected to pose estimation analysis.
8. **Displaying Modified Images on the Client Side:** The processed images are sent back to the client and displayed as a preview on the user’s phone screen.
9. **Providing Feedback on Exercise Form:** This allows the user to know what percentage of the exercise is performed with the correct body form.
10. **Tracking and Feedback:** The app tracks repetitions, automatically transitions to the next exercise, and monitors when the workout is finished, providing continuous feedback throughout the session.

## 4. Supported Exercises
There are 10 exercises that are optimized for the app to detect. These are:
1. **Push Up**
2. **Dummbell Curl**
3. **High Knees**
4. **Cable Triceps**
5. **Mountain Climbers**
6. **Lunge**
7. **Pull Up**
8. **Squat**
9. **Jumping Rope**
10. **Jumping Jack**

## 5. In-app Screenshots
Below are some screenshots of the IntelliFit Trainer application to give you an overview of its interface:

### Splash Screen
The first screen that opens after pressing the application logo. <br>
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/08683d7f-0e85-4c7c-8977-9faeeb0aff1c" style="width: 33%;"/> <br>

### Main Screen
The main screen of the app. Can log in as a user, or switch to the account creation or forgot password screen. <br>
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/a3990a70-f608-47ca-b836-41ea6057c752" style="width: 33%;"/> <br>

### User Screen
The page that opens after user login. <br>
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/aaf1c27b-4f6d-4a99-8f07-1826c13a29af" style="width: 33%;"/> <br>

### Create Workout
Exercise program creation page. The number of sets and repetitions for the desired exercises are entered and added to the program with the "add" button, then this program is saved. <br>
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/260ccb3d-f94d-45fb-b2df-6584740e06bb" style="width: 33%;"/> <br>

### My Workouts
This is the page where saved exercises are stored. If the user wants to start an exercise, then he/she must first select an exercise program. <br>
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/24007c9d-34a9-4dd3-a00a-eceead17b898" style="width: 33%;"/> <br>

### Learn Exercises
A page of animated GIFs showing how exercises should be done in the best form. <br>
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/8ee70940-76bc-4f5d-8744-1c7aa533be34" style="width: 33%;"/> <br>

### My Profile
The logged in user can view and update their profile information. Each update takes place instantly in the Firebase database. <br>
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/2b8a9576-bffc-4e7d-becf-04e3eda9cce3" style="width: 33%;"/> <br>

### Support
Users can send their suggestions, complaints or thanks directly by e-mail in-app. <br>
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/62432d19-a4f6-4747-927c-09f3523028d5" style="width: 33%;"/> <br>

## 6. Pose Estimation Example Images
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/973dcb6c-2f67-4cf0-abbc-da27fee8316d" style="width: 33%;"/>  
<img src="https://github.com/ErayBD/intellifit-trainer/assets/71061070/0365fdc9-0667-4527-84ae-dcf7e8030634" style="width: 33%;"/>

## 7. Contact and Support
Do you have a question or need some support? Contact me at erayberkdalkiran@gmail.com









