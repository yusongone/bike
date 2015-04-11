/*smart bick open source;*/
#include <Arduino.h>
#include <Wire.h>
#include <EEPROM.h>

#include "protocol.h"
#include "sensor.h"

//SoftwareSerial mySerial(11,10);
void setup(){
  Serial.begin(9600);
  initGy80();
  initSpeedISR();
}

void loop(){
  reciveCMD();
  delay(1000);
}
