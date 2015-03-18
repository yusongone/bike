#include "speed.h"


volatile boolean dir=false;
volatile int signal;
volatile long oldTime;
volatile long now;
volatile long TotalTimeInterval=100000000;
volatile long timeCount=0;
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

int Speed::getCount(){
  return 0;
};

float Speed::getSpeed(){
  if(timeCount>MIN_TIME){
    timeCount=0;
    TotalTimeInterval=1000000000;
  }
  return (float)WHEEL_PERIMETER/(float)SPEED_POINT_COUNT/(float)(TotalTimeInterval); //   (m/s)
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
    };
    timeCount+=now-oldTime;
    oldTime=now;
  sei();
}
