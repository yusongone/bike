#include "bike_01.h"



//SoftwareSerial mySerial(11,10);
Protocol protocol;
void setup(){
//eeprom_read_block(1,2);
Serial.begin(9600);
//mySerial.begin(9600);


  
}

void loop(){
  protocol.reciveCMD();
}



