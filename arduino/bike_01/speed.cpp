#include "speed.h"
#include "Adafruit_BMP085.h"
#include "protocol.h"

volatile boolean speedStatus=false;
volatile boolean footStatus=false;
volatile int wheelSignal;
volatile int footSignal;

volatile long speedOldTime=0;
volatile long speedNowTime=0;
volatile long footOldTime=0;


volatile long speedFreeTime=0;
volatile long footFreeTime=0;
volatile long nowISRTime=0;
volatile long oldISRTime=0;


uint16_t shakeOffset=0;

int wheelLapCounter=0;
uint16_t speedCache[10]={0};
uint32_t pressureCache[10]={0};
uint16_t shakeCache[10]={0};
float tempCache[10]={0};

bool stoped=false;




int speedSensorCheckCounter=0;// use to muli speed sensor;
int tripDistCounter=0;

Adafruit_BMP085 bmp;

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

void initBMP085(){
  if (!bmp.begin()) {
  }
}

void initSpeedISR(){
  shakeOffset=getShake();
  pinMode(SPEED_READ_PIN,INPUT);
  TCCR2A = 0x02;     // DISABLE PWM ON DIGITAL PINS 3 AND 11, AND GO INTO CTC MODE
  TCCR2B = 0x06;     // DON'T FORCE COMPARE, 256 PRESCALER 
  OCR2A = 0X7C;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE --2HZ
  //OCR2A = 1249;      // SET THE TOP OF THE COUNT TO 124 FOR 500Hz SAMPLE RATE
  TIMSK2 = 0x02;     // ENABLE INTERRUPT ON MATCH BETWEEN TIMER2 AND OCR2A
  sei();    
};

float getSpeed(){
  return 0;
}

void computerDist(){
  tripDistCounter+=WHEEL_PERIMETER;
  if(tripDistCounter>1000*100){//100m
    addTripDist(100);//add
    tripDistCounter-=100000;// 100m
  }
}



int status=HIGH;

uint32_t computePressure(){
    uint32_t sumPressure=0; 
    for(int i=0;i<wheelLapCounter;i++){
      sumPressure+=pressureCache[i]; 
    }
    if(wheelLapCounter==0){
      return bmp.readPressure();
    }
    return sumPressure/wheelLapCounter;
}

int computeTemp(){
    float sumTemp=0; 
    for(int i=0;i<wheelLapCounter;i++){
      sumTemp+=tempCache[i]; 
    }
    if(wheelLapCounter==0){
      return bmp.readTemperature();
    }
    return (int)(sumTemp*10)/wheelLapCounter;
}

uint16_t computeAvgSpeed(){
    uint16_t sumSpeed=0; 
    for(int i=0;i<wheelLapCounter;i++){
      sumSpeed+=speedCache[i]; 
    }
    if(wheelLapCounter==0){
      return 0;
    }
    return sumSpeed/wheelLapCounter;
}

uint16_t computeShake(){
    uint16_t sumShake=0; 
    for(int i=0;i<wheelLapCounter;i++){
      sumShake+=shakeCache[i]; 
    }
    if(wheelLapCounter==0){
      return 0;
    }
    return abs((long)(sumShake/wheelLapCounter)-shakeOffset);
}

uint16_t getShake(){
  int XYZ[3]={0};
  getADXData(XYZ);
  int x=abs(XYZ[0]);
  int y=abs(XYZ[1]);
  int z=abs(XYZ[2]);
  return x+y+z;
}


void saveData(){
    uint16_t shake=computeShake();
    int temp=computeTemp();
    uint32_t pressure=computePressure();
    uint16_t speed=computeAvgSpeed();
    write_speed_pressure_shake(speed,pressure,shake,temp,wheelLapCounter);
}

void onSpeedAction(){// whell one lap action;
  computerDist();
  speedNowTime=nowISRTime;
  long subTime=speedNowTime-speedOldTime;
  uint32_t speed;
  if(stoped){
    speed=0;
    saveData();
    wheelLapCounter=0;
  }else{
      speedCache[wheelLapCounter]=((long)WHEEL_PERIMETER*36)/subTime; // 10*m/s
      pressureCache[wheelLapCounter]=bmp.readPressure();
      tempCache[wheelLapCounter]=bmp.readTemperature();
      shakeCache[wheelLapCounter]=getShake();
      wheelLapCounter++;
  }
  if(wheelLapCounter==5){
    status=!status;
    saveData();
    digitalWrite(13,status);
    wheelLapCounter=0;
  }
  speedOldTime=speedNowTime;
}


void get(){
  //Serial.println((long)(bmp.readPressure()/100));
  //write_speed_pressure_shake(100,1002,0,321);
}


//one time foot sensor action;
void onFootAction(){
}
ISR(TIMER2_COMPA_vect){
  cli();
    nowISRTime=millis();
    speedFreeTime+=(nowISRTime-oldISRTime);
    digitalWrite(13,HIGH);
    footFreeTime+=(nowISRTime-oldISRTime);

    wheelSignal=analogRead(SPEED_READ_PIN);
    if(wheelSignal>SPEED_ANALOGREAD_HIGH){
        speedStatus=true;
    }else if(wheelSignal<SPEED_ANALOGREAD_LOW&&speedStatus){
        speedStatus=false;
        speedFreeTime = 0;
        if(speedSensorCheckCounter==0){//one lap
            speedSensorCheckCounter=SPEED_POINT_COUNT;
            stoped=false;
            sei();
            onSpeedAction();
        }else{
          speedSensorCheckCounter--;
        }
   };
    footSignal=analogRead(FOOT_READ_PIN);
    if(footSignal>FOOT_ANALOGREAD_HIGH){
      footStatus=true;
    }else if(footSignal<FOOT_ANALOGREAD_LOW&&footStatus){
      footStatus=false;
      onFootAction();
    }

    if(!stoped&&speedFreeTime>5000){
        stoped=true;
        speedFreeTime=0;
        sei();
        onSpeedAction();
        cli();
    }
    if(footFreeTime>3000){
        onFootAction();
    }
    oldISRTime=nowISRTime;
  sei();
}





