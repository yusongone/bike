#include "bike_01.h"

#include <avr/io.h>
#include "Arduino.h"
#include "SoftwareSerial.h"
#include "Protocol.h"

#include <avr/eeprom.h>

SoftwareSerial mySerial(11,10);

void setup(){
//eeprom_read_block(1,2);
Serial.begin(9600);
mySerial.begin(9600);


  
}

void loop(){
}



