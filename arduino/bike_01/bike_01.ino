#include "speed.h"

Speed speed_sensor;


void setup(){
  speed_sensor.init();
  Serial.begin(9600);
  pinMode(13,OUTPUT);
}

void loop(){
  Serial.println(speed_sensor.getSpeed());
  delay(100);
}



