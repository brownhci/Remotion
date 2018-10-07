.. Sensor-fusion Demo documentation master file, created by
   sphinx-quickstart on Mon Apr  3 16:50:08 2017.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Welcome to Sensor-fusion Demo's documentation!
==============================================

This application demonstrates the capabilities of various sensors and sensor-fusions. Data from the Gyroscope, Accelerometer and compass are combined in different ways and the result is shown as a cube that can be rotated by rotating the device.

The major novelty in this application is the fusion of virtual sensors: **Improved Orientation Sensor 1** and **Improved Orientation Sensor 2** fuse the Android Rotation Vector with the virtual Gyroscope sensor to achieve a pose estimation with a previously unknown stability and precision.

Apart from these two sensors, the following sensors are available for comparison:

- Improved Orientation Sensor 1 (Sensor fusion of Android Rotation Vector and Calibrated Gyroscope - less stable but more accurate)
- Improved Orientation Sensor 2 (Sensor fusion of Android Rotation Vector and Calibrated Gyroscope - more stable but less accurate)
- Android Rotation Vector (Kalman filter fusion of Accelerometer + Gyroscope + Compass)
- Calibrated Gyroscope (Separate result of Kalman filter fusion of Accelerometer + Gyroscope + Compass)
- Gravity + Compass
- Accelerometer + Compass

This application was developed for demonstrating the sensor fusion approach developed for my `Master Thesis "Sensor fusion for robust outdoor Augmented Reality tracking on mobile devices <http://my-it.at/media/MasterThesis-Pacha.pdf>`_ at the `Human Interface Technology Laboratory New Zealand <http://www.hitlabnz.org>`_.


Euler Angles
============

Euler-Angles are also often referred to as Azimuth, Pitch and Roll and describe the rotation of an object in the three-dimensional space with respect to three axis that are simple to understand and visualize. However, they have certain limitations and have therefore been removed from this project.

If you want to obtain the rotation using Euler-Angles, check out `the last tag that contained the Euler angles <https://bitbucket.org/apacha/sensor-fusion-demo/commits/tag/BeforePerformanceImprovements>`_. Notice that they have been removed, because many people don't understand Euler Angles fully and are surprised, when they get results, that they did not expect (because the representation is ambiguous and suffers from Gimbal Lock). Try to use a Rotation Matrix, a Rotation Vector or Quaternions instead.

A short summary can also be found in this `Stackoverflow answer <http://stackoverflow.com/a/18800083/448357>`_.


Installation
============

This project is an Gradle-based Android Studio project. It is also published in the `Google Play Store <https://play.google.com/store/apps/details?id=org.hitlabnz.sensor_fusion_demo>`_, if you just want to try it out. 

Contribute
==========

- `Issue Tracker <http://bitbucket.org/apacha/sensor-fusion-demo/issues>`_
- `Source Code <http://bitbucket.org/apacha/sensor-fusion-demo/>`_

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

License
=======

Released under the MIT license.

Copyright, 2016, by `Alexander Pacha <http://my-it.at>`_ and the `Human Technology Laboratory New Zealand <http://www.hitlabnz.org>`_ .

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

This application also uses parts from the the Android Open Source Project, licensed under the `Apache License, Version 2.0 <http://www.apache.org/licenses/LICENSE-2.0>`_.


.. toctree::
   :maxdepth: 2
   :caption: Contents:

