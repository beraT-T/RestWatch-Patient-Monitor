# RestWatch-Patient-Monitor
IEEEDuino Finalists Project repo. Smart Raspberry Pi monitor enhancing care for bedridden patients. Uses ultrasonic & ML for fall/bedsore prevention, with instant mobile alerts to caregivers for improved patient safety.

RestWatch is an innovative, Raspberry Pi-based monitoring system designed to enhance the safety and well-being of bedridden patients while providing crucial support to their caregivers. Many bedridden individuals, including the elderly or those with chronic illnesses, are at risk of falls if they attempt to get out of bed unsupervised and are susceptible to pressure sores (bedsores) from prolonged immobility. RestWatch aims to mitigate these risks through intelligent, real-time alerts and remote monitoring capabilities.

Core Features:

Fall Prevention & Unattended Bed Exit Alerts:

Utilizes an HC-SR04 ultrasonic sensor connected to a Raspberry Pi to detect if a patient attempts to sit up or leave the bed.
Instantly triggers a push notification (via Firebase Cloud Messaging) to the caregiver's Android mobile application.
Allows caregivers to remotely view the patient's status through a live camera feed (or snapshot) accessible within the Android app, enabling quick assessment and intervention.
Pressure Sore (Bedsore) Prevention System:

Employs a camera module and a Convolutional Neural Network (CNN) based machine learning model running on the Raspberry Pi to identify the patient's lying position (e.g., left side, right side, back).
Logs the duration the patient remains in each position.
Sends timely alerts to the caregiver's Android app, prompting them to reposition the patient if they have remained in one position for an extended period (e.g., 2-3 hours), thereby helping to prevent the development of pressure sores.
Technology Stack:

Hardware: Raspberry Pi 5, Raspberry Pi Camera Module 3, HC-SR04 Ultrasonic Sensor.
Software : Python, Firebase Admin SDK, Flask, RPi.GPIO, OpenCV and a CNN model (TensorFlow) for position detection.
Software (Mobile): Native Android application (Kotlin) to receive FCM notifications, display alerts, and view the camera feed.

The primary goal of RestWatch is to provide an affordable, reliable, and user-friendly solution that empowers caregivers with timely information, improves patient safety by reducing fall risks, and contributes to better health outcomes by preventing pressure sores. This project is developed for IEEEDuino Hardware Contest. And been selected for the 10 final Projects.
by
-Berat Mehmet Bakı berat_baki@outlook.com
-Abdurrahman Furkan Dingin
-Hüseyin Esat Kara
-Hatice Aydın
