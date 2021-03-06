#include "speed.h"

volatile boolean dir=false;
volatile int signal;
volatile long oldTime;
volatile long now;
volatile long TotalTimeInterval=100000000;
volatile long timeCount=0;

int checkCounter=0;
int uploadDist=0;
//------------------------- total dist---------------------------------
long _getTotalDist(){
  Serial.print("1-------------");
  Serial.println(EEPROM.read(1)<<0&0xff);
  Serial.print("2-------------");
  Serial.println(EEPROM.read(2)<<8&0xff);
  Serial.print("3-------------");
  Serial.println(EEPROM.read(3)<<16&0xff);
  return (EEPROM.read(1)<<0&0xff)+(EEPROM.read(2)<<8&0xff)+(EEPROM.read(3)<<16&0xff);
}

void addTotalDist(int meter){
  meter/=10;
  //EEPROM.read();
  meter+=_getTotalDist();
    Serial.print("0-------------");
  Serial.println(meter);
  Serial.print("1-------------");
  Serial.println(meter>>0&0xff);
  Serial.print("2-------------");
  Serial.println(meter>>8&0xff);
  Serial.print("3-------------");
  Serial.println(meter>>16&0xff);
  EEPROM.write(1,meter>>0);
  EEPROM.write(2,meter>>8);
  EEPROM.write(3,meter>>16);
  
}

long Speed::getTotalDist(){
  return _getTotalDist();
}
 
 //------------------------- trip dist---------------------------------
int _getTripDist(){
  return (EEPROM.read(4)<<0&0xff)+(EEPROM.read(5)<<8&0xff);
};

void addTripDist(int meter){
  meter/=10;
  meter+=_getTripDist();
  EEPROM.write(4,meter>>0);
  EEPROM.write(5,meter>>8);
  addTotalDist(100);
};

void Speed::resetTripDist(){
  EEPROM.write(4,0);
  EEPROM.write(5,0);
}

int Speed::getTripDist(){
  return _getTripDist();
}

Speed::Speed(){
}

void Speed::init(){
  pinMode(SPEED_READ_PIN,INPUT);
  TCCR2A = 0x02;     // DISABLE PWM ON DIGITAL PINS 3 AND 11, AND GO INTO CTC MODE
  TCCR2B = 0x06;     // DON'T FORCE COMPARE, 256 PRESCALER 
  OCR2A = 0X7C;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE --2HZ
  //OCR2A = 1249;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE
  TIMSK2 = 0x02;     // ENABLE INTERRUPT ON MATCH BETWEEN TIMER2 AND OCR2A
  sei();    
};

float Speed::getSpeed(){
  if(timeCount>MIN_TIME){
    timeCount=0;
    TotalTimeInterval=2000000000;
  }
  //return signal;
  return (float)WHEEL_PERIMETER/(float)SPEED_POINT_COUNT/(float)(TotalTimeInterval); //   (m/s)
}

void onOneCheck(){
  if(checkCounter==0){//one lap
    checkCounter=SPEED_POINT_COUNT;
    uploadDist+=WHEEL_PERIMETER;
    if(uploadDist>1000*100){
      Serial.print("0000000000000000000000000");
      addTripDist(100);//add
      uploadDist-=1000*100;
    }
  }else{
    checkCounter--;
  }
}


ISR(TIMER2_COMPA_vect){
  cli();
    now=millis();
    signal=analogRead(SPEED_READ_PIN);
    if(signal>SPEED_ANALOGREAD_HIGH){
        dir=true;
    }else if(signal<SPEED_ANALOGREAD_LOW&&dir){
        dir=false;
        TotalTimeInterval=timeCount;
        timeCount=0;
        onOneCheck();
    };
    timeCount+=now-oldTime;
    oldTime=now;
  sei();
}
