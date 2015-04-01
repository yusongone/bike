#include "bike_01.h"



//SoftwareSerial mySerial(11,10);
Protocol protocol;
Speed speedd;
void setup(){
  Serial.begin(9600);
  protocol.init();
}

void loop(){
  protocol.reciveCMD();
  delay(1000);
}



