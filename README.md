QUADCOPTER
==========

A quadcopter controller on a Raspberry PI
------------------------------------------

This project is the development of a code to control a quadcopter
with a Raspberry Pi.

The main idea is to use the RPI as much as possible. The only external
devices needed are a MPU6050 for attitude measurment and a Wifi dongle.
The quadcopter is remotely controlled via wifi and an Android App.


The Raspberry PI hosts:
- PID controller
- ESC signal generator (ServoBlaster)
- UDP server to get command from an android device
- Communication with the MPU6050 through I2C.

The repository has several folders, that I initially made to code and test
the different functions of the controller. The entire controller is in the
PILOT folder.

Folders :
- AND2RPI  : a first version of socket programming for communication with
a remote control over network
- MODELS   : some C programs that simulate the dyanmics of Quadcopter
- MPU6050s : Richard Ghirst's MPU6050 tools with wrappers in order to simplify
its use
- PILOT  : the complete controller currently under development
- SERVOBLASTER : a cpp class used to write the ESC comands into /dev/servoblaster


    This project is forked from https://github.com/vjaunet/QUADCOPTER

    All credit to vjaunet.

This project is greatly inspired and using source code from :
- https://github.com/richardghirst/PiBits
for ServoBlaster that I use to control the ESCs and the MPU6050 Digital motion processing.

- https://github.com/big5824/Picopter
for the Timing and the code structure

- https://code.google.com/p/owenquad/
for the Android app

Many thanks to these people.

    Successful fork on 23-03-2014
    (Everything working: sensors, ESC, socket and App)

First fully working version 22-01-2014

Next Steps:
- PID fine Tunning
- Flight Tests
- Implementation of RaspiCam
(Controller + Raspivid seem not to overload the Rpi)


Documentation
-------------



Hardware
--------

This projects includes :
- 4 brushless motors (NTM Prop Drive 28-30S 800KV / 300W Brushless Motor (short shaft))
- 4 Electronic Speed Controllers (Afro ESC 20Amp Multi-rotor Motor Speed Controller) (2 Reversed by Firmware)
- 1 LiPo 3s 2200 mAh battery
- 1 MPU6050 6DOF Sensor Modul 3 Axis Achse Gyroscope Accelerometer
- 1 QuadCopter frame
- 1 Android device for remote
- 2 9047SF Slow Fly Electric Prop CW
- 2 9047SF Slow Fly Electric Prop CCW

Wiring
------

MPU6050 :
-VDD -> 3.3V
-GND -> GND
-SDA -> SDA
-SCL -> SCL


ESCs:
-GND -> GND
-Signal Servo 0 -> pin 4
-Signal Servo 1 -> pin 23
-Signal Servo 2 -> pin 24
-Signal Servo 3 -> pin 25

Note that the use of this code requires some modifications of the Servoblaster configuration.







