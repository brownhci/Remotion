'use strict';
// Remotion Server
/* This server includes:
    Websocket => Communicate with Android Nativ
    Serial Communication => Communicate with Arduino Hardware
    PeerJS => Communicate among different HTML pages
    socket.io => Communicate between server and the visualizer.html
    UDP_SERVER => receive UDP income message from any client
*/

// Set IP

//const serverIP = "192.168.1.164";
const serverIP = "172.18.143.153";
//const serverIP = "172.16.109.54";
//const serverIP = "10.0.0.6";

/*
    Web socket server establishment
*/
const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 9999 });

/*
    Serial Communication
*/
var port;
var portName = 'COM3';
var SerialPort = require('serialport');

/*
    File system
*/
var dgram = require('dgram');
var fs = require("fs");

/*
    Peer JS server
*/
var PeerServer = require('peer').PeerServer;
var server = PeerServer({port: 8888, path: '/'});

/*
    Socket.IO server
*/
var io = require('socket.io')(9000);

/*
    UDP Server establish
*/
var UDP_PORT = 5110;
var UDP_HOST = serverIP;
var UDP_server = dgram.createSocket('udp4');
var forwardUDP = true;
var myGlobalSocket;
var UDP_PhoneConnected = false;
var serialData = "";
var realtimeFeatures = {};
var serialConnected = false;
var sessionID = Math.floor( new Date().getTime() / 1000);
//  Default Motor position
var motorPos = [180,50,105];
//  Counter used for Socket.io sending speed
var SIOCont = 0;

/*
/*
/*
    UDP implementation
    OBSOLETE
*/
UDP_server.on('listening', function () {
    var address = UDP_server.address();
    console.log('UDP Server listening on ' + address.address + ":" + address.port);
});

UDP_server.on('message', function (message, remote) {
    if(!UDP_PhoneConnected){
        console.log("phone terminal connected!");
        UDP_PhoneConnected = true;
    }
    var data = message.toString().split(" ");
    if(myGlobalSocket != undefined){
        myGlobalSocket.emit("message",{x:toDegree(data[2]).toFixed(1), y:toDegree(data[3]).toFixed(1), z:toDegree(data[1]).toFixed(1)});
    }
});

UDP_server.bind(UDP_PORT, UDP_HOST);

/*
/*
/*
    Websocket implementation
*/
var pmessage = "";
wss.on('connection', function connection(ws) {
    console.log("Android terminal Connected!");
  ws.on('message', function incoming(message) {
    //console.log('received: %s', message);
    if(myGlobalSocket != undefined && pmessage != message){
         myGlobalSocket.emit("quaternionRaw",message);
         //console.log('received: %s', message);
    }
     pmessage = message;
        // SIOCont ++;
        // if(SIOCont > 1000)
        //     SIOCont = 0;
  });
});

/*
/*
/*
    PeerJS implementation
*/
server.on('connection', function(id) {
       console.log("peerClient: " + id + " connected!!");
 });

/*
/*
/*
    Socket io connections
    establish connection with html page
    this is the websocket session
*/
io.on('connection', function (socket) {
    console.log(socket.id + " Connected!! ");
    myGlobalSocket = socket;
    socket.on('PHONE_SENSOR_FUSION', function(data){
        console.log(data);
    });

    socket.on('message', function (data) {
        if(data == ""){
            console.log("empty data received, check the code");
            return;
        }
        if(data.flag == undefined)
            console.log("incorrectly formatted data: require data.flag to indicate type. e.g: "+
                     " 'flag' : 'MOTOR_UPDATE' means the message is for updating motor");
        else
            switch(data.flag){
                case 'MOTOR_UPDATE':
                var steps = 0;
                var moving_index;
                          // write initial value
                //console.log("received socket data");
                var date = new Date();
                motorPos[0] = data.rx;
                motorPos[1] = data.ry;
                motorPos[2] = data.rz;
                if(motorPos[0] == undefined){
                    console.log("unabled to get motorpos,bouncing back");
                    return;
                }
                var portwrite = parseInt(motorPos[0].toFixed(0)) + ','+parseInt(motorPos[1].toFixed(0))+','+parseInt(motorPos[2].toFixed(0));
                console.log(portwrite);
                if(serialConnected)
                    port.write(portwrite);
                break;

                case 'PHONDATA_UPDATE':
                     realtimeFeatures['phonedata'] = data.message;
                break;

                case 'SHOW_MOTOR_STATUS':
                    console.log(motorPos);
                break;

                default:
                break;
            }
    });

    socket.on('disconnect', function () {
        console.log("socket disconnected!")
    });
});

/*
/*
/*
    Serial Communication Implement
*/
port = new SerialPort(portName);
port.on('open', function() {
    port.update({baudRate:115200});
    // port.write('main screen turn on', function(err) {
    //     if (err) {
    //         console.log('Error on write: ', err.message);
    //     } else {
    //         console.log("Serial is connected! ready to transmit")
    //         serialConnected = true;
    //     }
    // });
    console.log("Serial Communication Ready!!!");
    serialConnected = true;
    // var t = setInterval(function(){
    //     if(serialConnected){
    //         console.log("got here");
    //         var t = parseInt(Math.random() * 60 + 100);
    //         port.write(t+",45,96");
    //     }
    // },33);
});

port.on('error', function(err) {
    console.log('Error: ', err.message);
})

/*
/*
/*
    Utility and Helper functions
*/
function toRad(degree){
    return (degree / 180) * 3.1415926;
}

function toDegree(rad){
    return (rad / 3.1415926) * 180;
}

/*
var t = setInterval(function(){
    if(serialConnected){
        port.write(Math.floor(finalValue)+"\n");
    }
}, 100);
*/