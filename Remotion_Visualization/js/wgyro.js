//============================================================
//
// The MIT License
//
// Copyright (C) 2014 Matthew Wagerfield - @wagerfield
//
// Permission is hereby granted, free of charge, to any
// person obtaining a copy of this software and associated
// documentation files (the "Software"), to deal in the
// Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute,
// sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do
// so, subject to the following conditions:
//
// The above copyright notice and this permission notice
// shall be included in all copies or substantial portions
// of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
// LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
// EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
// FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
// AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
// OR OTHER DEALINGS IN THE SOFTWARE.
//
//============================================================

/**
 * Parallax.js
 * @author Matthew Wagerfield - @wagerfield
 * @description Creates a parallax effect between an array of layers,
 *              driving the motion from the gyroscope output of a smartdevice.
 *              If no gyroscope is available, the cursor position is used.
 */

 /**
    PARALLAX Rewritten by Jing.Q
 */
;(function(window, document, undefined) {

    'use strict';

    var NAME = 'WGyro';
    var MAX = 0;
    var PHONE_ANGLES_PARAMS = [];
    var PHONE_ACCE_PARAMS = [];
    var PHONE_ORIGINAL_READINGS = [];
    var OUTPUTDIV = '';
    var MAIN_CURRENTPAGE = '';
    var OUTPUT_VELOCITY_ONLY = false;
    var FPS = 90;
    var SEND_TO_SERVER = false;
    var lastUpdate = 0;
    function WGyro(element, options) {

        // DOM Context
        if(options.studyID != undefined)
            WEBGAZER_STUDYID = options.studyID;
        if(options.deviceId != undefined)
            WEBGAZER_DEVICEID = options.deviceId;
        if(options.currentPage != undefined)
           MAIN_CURRENTPAGE = options.currentPage;

        this.element = element;
        OUTPUTDIV = element;
        this.calibrateX = false;
        this.calibrateY = true;
        this.scalarX = 10;
        this.scalarY = 10;
        this.limitX = false;
        this.limity = false;
        this.frictionX = 0.1;
        this.frictionY = 0.1;
        this.originX = 0.5;
        this.supportDelay = 500;
        this.calibrationDelay = 500;
        this.calibrationThreshold = 100;
        this.originX = 0.5;
        this.originY = 0.5;
        this.portrait = true;
        this.motionSupport = !!window.DeviceMotionEvent;
        this.orientationSupport = !!window.DeviceOrientationEvent;
        this.calibrationTimer = null;
        this.calibrationFlag = true;
        this.enabled = false;
        this.bounds = null;
        this.ex = 0;
        this.ey = 0;
        this.ew = 0;
        this.eh = 0;
        this.ecx = 0;
        this.ecy = 0;
        this.erx = 0;
        this.ery = 0;
        this.calibX = 0;
        this.calibY = 0;
        this.inputX = 0;
        this.inputY = 0;
        this.motionX = 0;
        this.motionY = 0;
        this.velX = 0;
        this.velY = 0;

        // binding events
        this.onDeviceOrientation = this.onDeviceOrientation.bind(this);

        this.ondevicemotion = this.ondevicemotion.bind(this);
        // reset timer
        this.onCalibrationTimer = this.onCalibrationTimer.bind(this);

        this.init();

    }

    WGyro.prototype.orientationStatus = 0;
    WGyro.prototype.propertyCache = {};
    WGyro.prototype.init = function() {
        if (this.orientationSupport)
            window.addEventListener('deviceorientation', this.onDeviceOrientation);
        if(this.motionSupport)
            window.addEventListener('devicemotion', this.ondevicemotion);
        // Setup
        this.updateDimensions();
        this.queueCalibration(this.calibrationDelay);
    };

    WGyro.prototype.setAngleParams = function(ax,ay,az) {
        PHONE_ANGLES_PARAMS = [ax.toFixed(5),ay.toFixed(5),az.toFixed(5)];
    }

    WGyro.prototype.setAngleParams = function(ax,ay) {
        PHONE_ORIGINAL_READINGS = [ax.toFixed(5),ay.toFixed(5)];
    }

    WGyro.prototype.setAcceParams = function(x,y,z){
        PHONE_ACCE_PARAMS = [parseFloat(x.toFixed(5)),parseFloat(y.toFixed(5)),parseFloat(z.toFixed(5))];
    }

    WGyro.prototype.getAcceParams = function(){
        return PHONE_ACCE_PARAMS;
    }

    WGyro.prototype.getAngleParams = function(){
        return PHONE_ANGLES_PARAMS.join(',');
    }

    WGyro.prototype.getAngleParamsObj = function(){
        return PHONE_ANGLES_PARAMS;
    }

    WGyro.prototype.getPhoneGyroInfo = function(){
        return PHONE_ANGLES_PARAMS.concat(PHONE_ACCE_PARAMS);
    }

    WGyro.prototype.isEnabled = function(){
        return true;
    }

    WGyro.prototype.update = function(){
        if(this.isEnabled()){
            if(OUTPUTDIV != undefined)
              if(OUTPUT_VELOCITY_ONLY)
                OUTPUTDIV.innerHTML = 'vx:' + this.velX.toFixed(5) + ' vy: ' + this.velY.toFixed(5);
              else
                OUTPUTDIV.innerHTML = "Original: " + this.getAngleParams();
            if(performance.now() - lastUpdate > (1000 / FPS)){
                var f ={
                  beta:  PHONE_ANGLES_PARAMS[0],
                  gamma: PHONE_ANGLES_PARAMS[1],
                  alpha: PHONE_ANGLES_PARAMS[2],
                  ax:    PHONE_ACCE_PARAMS[0],
                  ay:    PHONE_ACCE_PARAMS[1],
                  az:    PHONE_ACCE_PARAMS[2],
                }
                var d = {
                  'event' : 'gyro',
                  'page' : MAIN_CURRENTPAGE,
                  'type' : 'auto',
                  'studyId' : WEBGAZER_STUDYID,
                  'deviceId': WEBGAZER_DEVICEID,
                  'timeSinceRunning': performance.now().toFixed(3),
                  'timestamp': this.getTimestamp(),
                  'queryData' : f
                };
                try{
                  if(SEND_TO_SERVER){
                    sendToServer(d);
                    lastUpdate = performance.now();
                  }
                } catch (e){
                    SEND_TO_SERVER = false;
                }
              }
        }
    }

    WGyro.prototype.setOriginalReading = function(vx,vy,vz){
        PHONE_ANGLES_PARAMS[0] = parseFloat(vx.toFixed(5));
        PHONE_ANGLES_PARAMS[1] = parseFloat(vy.toFixed(5));
        PHONE_ANGLES_PARAMS[2] = parseFloat(vz.toFixed(5));
    }

    WGyro.prototype.updateDimensions = function() {
        this.ww = window.innerWidth;
        this.wh = window.innerHeight;
        this.wcx = this.ww * this.originX;
        this.wcy = this.wh * this.originY;
        this.wrx = Math.max(this.wcx, this.ww - this.wcx);
        this.wry = Math.max(this.wcy, this.wh - this.wcy);
    };

    WGyro.prototype.updateBounds = function() {
        this.bounds = this.element.getBoundingClientRect();
        this.ex = this.bounds.left;
        this.ey = this.bounds.top;
        this.ew = this.bounds.width;
        this.eh = this.bounds.height;
        this.ecx = this.ew * this.originX;
        this.ecy = this.eh * this.originY;
        this.erx = Math.max(this.ecx, this.ew - this.ecx);
        this.ery = Math.max(this.ecy, this.eh - this.ecy);
    };

    WGyro.prototype.getTimestamp = function(){
        var timestamp = Date.now();
        return timestamp
    }

    WGyro.prototype.queueCalibration = function(delay) {
        clearTimeout(this.calibrationTimer);
        this.calibrationTimer = setTimeout(this.onCalibrationTimer, delay);
    };

    WGyro.prototype.confine = function(value, min, max) {
        value = Math.max(value, min);
        value = Math.min(value, max);
        return value;
    };

    WGyro.prototype.onCalibrationTimer = function() {
        this.calibrationFlag = true;
    };
    // Obsolete
    WGyro.prototype.sendToServer = function(posX,posY,beta,gamma,alpha){
        var current = performance.now().toFixed(5);
        var url = WEBGAZER_SERVER_URL.replace('#studyID#',WEBGAZER_STUDYID).replace('#timestamp#',current).
                  replace('#posX#',posX).replace('#posY#',posY).replace('#beta#',beta).replace('#gamma#',gamma).replace('#alpha#',alpha);
    }

    WGyro.prototype.ondevicemotion = function(event){
        if(event.accelerationIncludingGravity.x != undefined && event.accelerationIncludingGravity.y != undefined && event.accelerationIncludingGravity.z != undefined)
        this.setAcceParams(parseFloat(event.accelerationIncludingGravity.x),
        parseFloat(event.accelerationIncludingGravity.y)
        ,parseFloat(event.accelerationIncludingGravity.z));
    }

    WGyro.prototype.onDeviceOrientation = function(event) {

        if (event.beta !== null && event.gamma !== null) {

            //deal with empty alpha
            var alpha = (event.alpha == undefined) ? 0 : event.alpha;

            this.orientationStatus = 1;

            var x = (event.beta || 0) / 30;
            var y = (event.gamma || 0) / 30;

            // Set Calibration
            if (this.calibrationFlag) {
                this.calibrationFlag = false;
                this.calibX = x;
                this.calibY = y;
            }

            // Set Input
            this.inputX = x;
            this.inputY = y;
            var dx = this.inputX - this.calibX;
            var dy = this.inputY - this.calibY;

            if (this.portrait) {
                this.motionX = this.calibrateX ? dy : this.inputY;
                this.motionY = this.calibrateY ? dx : this.inputX;
            } else {
                this.motionX = this.calibrateX ? dx : this.inputX;
                this.motionY = this.calibrateY ? dy : this.inputY;
            }
            if (!isNaN(parseFloat(this.limitX))) {
                this.motionX = this.confine(this.motionX, -this.limitX, this.limitX);
            }
            if (!isNaN(parseFloat(this.limitY))) {
                this.motionY = this.confine(this.motionY, -this.limitY, this.limitY);
            }

            this.velX += (this.motionX - this.velX) * this.frictionX;
            this.velY += (this.motionY - this.velY) * this.frictionY;

            this.setAngleParams(this.velX,this.velY);

            //this.getPhoneGyroInfo();

            this.setOriginalReading(event.beta, event.gamma, alpha);

            this.update();

         }
    };

    window[NAME] = WGyro;

})(window, document);
