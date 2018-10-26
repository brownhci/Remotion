#include <Servo.h>

Servo servoX,servoZ,servoY;  // create servo object to control a servo
// twelve servo objects can be created on most boards
int servoX_sv = 0;
int servoY_sv = 0;   
int servoZ_sv = 0;
int seperater_index_a = 0;
int seperater_index_b = 0;
String tmpx = "";
String tmpy = "";
String tmpz = "";
String inputString = "";

void setup() {
  Serial.begin(115200);
  Serial.setTimeout(25);
  /* attach ports */
  servoX.attach(9);  
  servoZ.attach(10);
  servoY.attach(11);
  
  /* write defaul values */
  /* make sure you calibrate first*/
  servoX.write(180); 
  servoY.write(45); 
  servoZ.write(96); 
}

void serialEvent() {
  while (Serial.available()) {
    inputString = Serial.readStringUntil('\n');
    seperater_index_a = inputString.indexOf(',');
    seperater_index_b = inputString.lastIndexOf(',');
    tmpx = inputString.substring(0,seperater_index_a);
    tmpy = inputString.substring(seperater_index_a+1,seperater_index_b);
    tmpz = inputString.substring(seperater_index_b+1);
    //Serial.println(tmpx);
    //Serial.println(tmpy);
    //Serial.println(tmpz);
    // this step is important since the incoming text might not be full numbers
    if(isValidNumber(tmpx) && isValidNumber(tmpy) && isValidNumber(tmpz)){
      servoX.write(tmpx.toInt());
      servoY.write(tmpy.toInt());
      servoZ.write(tmpz.toInt());
    }
    inputString = "";
    delay(25);
  }
}

void loop(){
}

boolean isValidNumber(String str){
   for(byte i=0;i<str.length();i++)
   {
      if(isDigit(str.charAt(i))) return true;
        }
   return false;
} 

