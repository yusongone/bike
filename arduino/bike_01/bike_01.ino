/*smart bick open source;*/
#include <Arduino.h>
#include <Wire.h>
#include <EEPROM.h>
#include <SoftwareSerial.h>

#include "protocol.h"
#include "sensor.h"
#include "gps.h"

SoftwareSerial ss(4, 3);


GPS gps;
volatile int state=LOW;

void setup(){
  pinMode(13,OUTPUT);
  pinMode(2,INPUT);
  Serial.begin(9600);
  ss.begin(9600);
  initGy80();
  //initSpeedISR();
  initBMP085();
  attachInterrupt(0,blink,CHANGE);   
}

volatile long a=0;
volatile long b=0;
volatile long aa=0;
volatile long bb=0;
volatile long d=0;
volatile int c=0;
volatile int count=0;
volatile int temp=0;
void blink(){
  temp=digitalRead(2);
  a=millis();
  Serial.print(temp);
  count++;
  if(a-b>60){
      if(count<15){
        c++;
      }
      count=0;
  };
  b=a;
}


void reciveGPS(){
  while(ss.available()){
    char cc=ss.read();
    gps.joinCMD(cc);
  } 
  gps.parseCMD();
}

long f;

void loop(){
  //reciveCMD();
  //reciveGPS();
//Serial.print(12);
  if(c!=f){
//    Serial.print(c);
  }
  f=c;
}
