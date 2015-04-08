#include "speed.h"

volatile boolean speedStatus=false;
volatile boolean footStatus=false;
volatile int wheelSignal;
volatile int footSignal;
volatile long oldTime;

volatile long speedOldTime=millis();
volatile long footOldTime=millis();


volatile long now;
volatile long TotalTimeInterval=100000000;
volatile long timeCount=0;

int wheelLapCounter=0;




int speedSensorCheckCounter=0;// use to muli speed sensor;
int tripDistCounter=0;
#include "protocol.h"



//------------------------- total dist---------------------------------
int _getTotalDist(){
  return ((EEPROM.read(1)&0xff)<<0)+((EEPROM.read(2)&0xff)<<8)+((EEPROM.read(3)&0xff)<<16);
}

void addTotalDist(int meter){
  meter/=10;
  //EEPROM.read();
  meter+=_getTotalDist();
  EEPROM.write(1,meter>>0&0xff);
  EEPROM.write(2,meter>>8&0xff);
  EEPROM.write(3,meter>>16&0xff);
  
}

int getTotalDist(){
  return _getTotalDist();
}
 
 //------------------------- trip dist---------------------------------
int _getTripDist(){
  return ((EEPROM.read(4)&0xff)<<0)+((EEPROM.read(5)&0xff)<<8);
};

void addTripDist(int meter){
  meter/=10;
  meter+=_getTripDist();
  EEPROM.write(4,meter>>0);
  EEPROM.write(5,meter>>8);
  addTotalDist(100);
};

void resetTripDist(){
  EEPROM.write(4,0);
  EEPROM.write(5,0);
}

int getTripDist(){
  return _getTripDist();
}

void initSpeedISR(){
  pinMode(SPEED_READ_PIN,INPUT);
  TCCR2A = 0x02;     // DISABLE PWM ON DIGITAL PINS 3 AND 11, AND GO INTO CTC MODE
  TCCR2B = 0x06;     // DON'T FORCE COMPARE, 256 PRESCALER 
  OCR2A = 0X7C;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE --2HZ
  //OCR2A = 1249;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE
  TIMSK2 = 0x02;     // ENABLE INTERRUPT ON MATCH BETWEEN TIMER2 AND OCR2A
  sei();    
};

float getSpeed(){
  if(timeCount>MIN_TIME){
    timeCount=0;
    TotalTimeInterval=2000000000;
  }
  //return signal;
  return (float)WHEEL_PERIMETER/(float)SPEED_POINT_COUNT/(float)(TotalTimeInterval); //   (m/s)
}

void computerDist(){
  tripDistCounter+=WHEEL_PERIMETER;
  if(tripDistCounter>1000*100){//100m
    addTripDist(100);//add
    tripDistCounter-=100000;// 100m
  }
}


void onSpeedAction(){// whell one lap action;
   computerDist();
   wheelLapCounter++;
}

//one time foot sensor action;
void onFootAction(){
  long now=millis();
  (60*1000)/(now-footOldTime);// lap/min
  footOldTime=now;
  write_trip_dist();      
  
}

ISR(TIMER2_COMPA_vect){
  cli();
    now=millis();
    wheelSignal=analogRead(SPEED_READ_PIN);
    if(wheelSignal>SPEED_ANALOGREAD_HIGH){
        speedStatus=true;
    }else if(wheelSignal<SPEED_ANALOGREAD_LOW&&speedStatus){
        speedStatus=false;
        TotalTimeInterval=timeCount;
        timeCount=0;
        
      if(speedSensorCheckCounter==0){//one lap
        speedSensorCheckCounter=SPEED_POINT_COUNT;
        onSpeedAction();
      }else{
        speedSensorCheckCounter--;
      }  
  };
    
    footSignal=analogRead(SPEED_READ_PIN);
    if(footSignal>SPEED_ANALOGREAD_HIGH){
      footStatus=true;
    }else if(footSignal<SPEED_ANALOGREAD_LOW&&footStatus){
      footStatus=false;
      onFootAction();
      
    }
    
    timeCount+=now-oldTime;
    oldTime=now;
    
  sei();
}




