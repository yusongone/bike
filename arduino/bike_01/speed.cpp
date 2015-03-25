#include "speed.h"

volatile boolean dir=false;
volatile int signal;
volatile long oldTime;
volatile long now;
volatile long TotalTimeInterval=100000000;
volatile long timeCount=0;

int checkCounter=0;
int uploadDist=0;

long _getTotalDist(){
  return (EEPROM.read(1)<<0&0xff)+(EEPROM.read(2)<<8&0xff)+(EEPROM.read(3)<<16&0xff);
}

void addTotalDist(int meter){
  meter/=100;
  //EEPROM.read();
  meter+=_getTotalDist();
  Serial.print("m");
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



int Speed::getTotalDist(){
  return _getTotalDist();
}

void addTripDist(){

}

int Speed::getTripDist(){
  return 1;
}

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
      addTotalDist(100);
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
