#include "bike_01.h"



//SoftwareSerial mySerial(11,10);
void setup(){
  Serial.begin(9600);
  initSpeedISR();
}

void loop(){
  reciveCMD();
}



