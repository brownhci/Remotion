Remotion is a complete end-to-end open source platform for improving qualitative observations for remote usability testings. The system comprises five components.

1. The client software library for motion sensing on mobile devices (e.g., Android phones and Surface tablets)
enables developers to collect sensing data by simply deploying an application on the participants' phone. 2. The screen projection component captures what the user sees on their screen and projects it to the screen in a software or hardware replay.
3. The lightweight server receives and organizes the data collected with Remotion's client and Remotion's screen provides an interface to control the Remotion's software and hardware visualization replay.
4. The software visualization displays on the experimenter's screen a replay of what the user sees on their screen and the phone's movements.
5. Remotion's hardware (physical) replay replicates the remote user's movement and screen on a one-to-one scale. The robotic mount (with 4-axis degrees of freedom) is offered as blueprints for at-home construction.

Remotion is designed to replicate a mobile device's screen content, movement, and rotation in a remote usability test, especially when the user is stationary (in a sitting or standing position). Five different components deployed together can replicate hand-motion related contextual information that is otherwise not captured in a conventional remote usability testing session.

# Remotion's Client Software Library
The Remotion client is the first component of the Remotion platform. It enables collecting mobile sensing data (e.g., gyroscope and accelerometer) and user interaction data (e.g., taps and scrolls) from remote users. The Remotion client records these data in real time and uploads them to the Remotion server upon the completion of the task. The remote user can choose to start, stop, and upload data at will. The client provides the motion-encoded contextual data that can later be revealed to the experimenters.

# Remotion's Screen Projection Component
The second component provides additional information about the remote user's screen content to the experimenters. The Remotion screen projection component captures the phone's screen changes and sends it to the Remotion server as a compressed image stream.

# Remotion's Lightweight Server
The server functions as a hub to organize, configure, and control the Remotion's visualization ends. The Remotion server supports two modes: 1) offline replay and 2) real-time replay. In the offline replay mode, the server loads data from the Remotion client and the Remotion screen projection components described above, relaying them to Remotion's visualization endpoints within a fixed interval of time. The real-time component decodes the incoming data from Remotion's client and screen projection components and forwards them directly to the visualization endpoints.

The Remotion server has a back-end (i.e., Node.js) and a front-end graphical user interface (GUI) for the experimenter to load, re-save, stream the data, and control the visualization playback. The Remotion server is HTML-based, therefore it does not require additional configuration or installation, once the back-end is running. Remotion's interface has been successfully tested on mainstream browsers without functionality or layout issues.

# Remotion's Software Visualization
Recreating the remote user's motion is the primary functionality the Remotion platform provides. It allows distant users' hand movement data to be presented to the experimenters. Remotion's software visualization projects the mobile device motions and renders live video and audio content on the experimenter's computer screen. The Remotion software visualization contains a 3D model of a mobile phone and a virtual screen showing the video stream. During a replay, the 3D phone model's and the virtual screen's orientations are updated from on incoming data. 

The accuracy of motion replay is ensured with a sensor fusion algorithm package that calculates quaternion rotation. The use of quaternion rotations avoids "dead zones", where the visualized results may be incorrect. Without using sensor fusion, repeated mobile device movements and fast changes in its orientation will render the raw data visualization drastically drifted and may not reflect its true orientation.

# Remotion's Hardware (Physical) Visualization
Remotion Hardware visualization is a fully customizable and low-cost Arduino-controlled robotic mount for motion visualization. Its main component consists of a circuit-board, three motors, a 3D-printed mounting system, and a track. When the experimenter mounts a mobile device to the Remotion hardware, it is able to render the motion data and screen content in the experimenter's physical space. Unlike its software counterpart, the physical visualization offers a one-to-one scale motion playback (i.e., replicating the degree of movement, the form of a physical phone, and the contents of the screen mirror the user's screen). This physicality further enables an understanding of spacial depth and a more naturalistic way of observation.

Remotion's hardware visualization uses the same sensor fusion algorithm with the software counterpart to ensure consistency. On occasion, Remotion needs to swap the x and y-axis in the visualization to make the motion seamless. This is not the case with the Remotion's software visualization since its rotation is quaternion-based. The Remotion's hardware visualization uses Euler angles to rotate the device. Once the user rotates the phone from portrait to landscape, Remotion automatically swaps the x and y-axis to match the visualization.


# Environment Setup
Remotion Software require Apache Server and NodeJS. We are working on to merge these two but for now. Apache server can be install from [Xampp](https://www.apachefriends.org/index.html) and NodeJs can be installed from [Node](https://nodejs.org/en/). 

# Constructing Remotion Hardware
![](https://github.com/brownhci/Remotion/blob/master/img/Blueprint_1.jpg)

# Blueprints
![](https://github.com/brownhci/Remotion/blob/master/img/Blueprint_2.jpg)

## Installation Video
[![View On Youtube](https://img.youtube.com/vi/tM0cK9_nD2s/0.jpg)](https://www.youtube.com/watch?v=tM0cK9_nD2s)

# Hardware Wiring and Arduino Code.
![](https://github.com/brownhci/Remotion/blob/master/img/Remotion_hardwareSchematic.jpg)
For Arduino Code, please check out the ArduinoCodes Folder. The Arduino need to be connected to **both the computer for serial communication and the 9V external power** supply to be able to power three servo motors.
# Where to get the components:
  [Metal Track](https://www.amazon.com/gp/product/B07FDXHC8X/ref=oh_aui_detailpage_o00_s00?ie=UTF8&psc=1)
  
  [Servo Arm Horn Metal Aluminum 25t Silvery](https://www.amazon.com/gp/product/B00NOGMK3M/ref=oh_aui_search_detailpage?ie=UTF8&psc=1)
  
  [MG996R Servo](https://www.amazon.com/gp/product/B013II2ZSU/ref=oh_aui_search_detailpage?ie=UTF8&psc=1)
  
  [Nema 17 Stepper Motor](https://www.amazon.com/gp/product/B01N30ISYC/ref=oh_aui_search_detailpage?ie=UTF8&psc=1)
  
  [Ball Bearing](https://www.adafruit.com/product/1178)
  
  [Stepper Motor Breakout Board](https://www.adafruit.com/product/3297)
  
# Constructing Remotion Software
1. Make sure Remotion_Visualization folder is inside the Xampp's virtual environment folder (i.e., xampp/htdocs/).
2. Remotion visualizations work only in the local network. Make sure all the executing parts are running under the same network.
3. Change the IP address to your computer (internal IP address) inside Visualizer.html, server.js, and PhoneMode.html.


