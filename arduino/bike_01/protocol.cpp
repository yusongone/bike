#include "Protocol.h"


#define bufferSize 10
#define RECIVE_BUFFER_SIZE 64
#define SET_RAW_RC 200
#define TEST 99

#define CLEAR_TRIP_DISTANCE 100
#define SET_WHEEL_PERIMETER 101
#define SET_MAGNET_COUNTER 102

#define VERSION 200
#define GET_SPEED 201
#define GET_TRIP_DISTANCE 202
#define GET_TOTAL_DISTANCE 203


String headString;
int dataLength=-1;
int startData=0;
int subIndex=0;
int msgId;
boolean Hard=true;
uint8_t  buf[RECIVE_BUFFER_SIZE];
SoftwareSerial softSerial =  SoftwareSerial(SOFTWARE_SERIAL_RX, SOFTWARE_SERIAL_TX);

Speed mySpeed;

void Protocol::init(){
  pinMode(13,OUTPUT);
  mySpeed.init();
  softSerial.begin(SOFTWARE_SERIAL_RATE);
}


byte getSum(byte b[]){
  byte temp=0x00;
  int length=b[3];
  for(int i=3;i<4+length;i++){
    temp^=b[i]&0xff;
  }
  return temp;
}

void write_speed(){
  int tempValue=(mySpeed.getSpeed()*36);
  tempValue=1;
  byte buffer[8];
  buffer[0]=0x24;
  buffer[1]=0x42;
  buffer[2]=0x3C;
  buffer[3]=0x02;
  buffer[4]=GET_SPEED;
  buffer[5]=tempValue>>0&0xff;
  buffer[6]=tempValue>>8&0xff;
  buffer[7]=getSum(buffer);
  if(Hard){
    Serial.write(buffer,8);
  }else{
    softSerial.write(buffer,8);
  }
}

void write_total_dist(){
  long tempValue=mySpeed.getTotalDist();
  tempValue=1;
  byte buffer[9];
  buffer[0]=0x24;
  buffer[1]=0x42;
  buffer[2]=0x3C;
  buffer[3]=0x03;
  buffer[4]=GET_TOTAL_DISTANCE;
  buffer[5]=tempValue>>0&0xff;
  buffer[6]=tempValue>>8&0xff;
  buffer[7]=tempValue>>16&0xff;
  buffer[8]=getSum(buffer);
  if(Hard){
    Serial.write(buffer,8);
  }else{
    softSerial.write(buffer,9);
  }
}

void write_trip_dist(){
  long tempValue=mySpeed.getTripDist();
  tempValue=1;
  byte buffer[9];
  buffer[0]=0x24;
  buffer[1]=0x42;
  buffer[2]=0x3C;
  buffer[3]=0x03;
  buffer[4]=GET_TRIP_DISTANCE;
  buffer[5]=tempValue>>0&0xff;
  buffer[6]=tempValue>>8&0xff;
  buffer[7]=getSum(buffer);
  if(Hard){
    Serial.write(buffer,8);
  }else{
    softSerial.write(buffer,8);
  }
}


void switchCMD(){
   switch (msgId){
    case GET_SPEED:
      write_speed();      
    break;
    case GET_TOTAL_DISTANCE:
      write_total_dist();      
    break;
    case GET_TRIP_DISTANCE:
      write_trip_dist();      
    break;
    case TEST:
    break;
  }
}

boolean checkSum(){
  byte c=buf[0];
  for(int i=1;i<=subIndex-2;i++){
    c^=buf[i]&0xff;
  };
  if(c==buf[subIndex-1]){
    return true;
  }
  return false;
}



void Protocol::reciveCMD(){
  int dl;//=softSerial.available();
  if(Hard){
    dl=Serial.available();
  }else{
    dl=softSerial.available();
  }
  while(dl--){
    byte tempByte;
    
     if(Hard){
        tempByte=Serial.read();
     }else{
        tempByte=softSerial.read();
      }
    if(startData==1){//get dataLength;
      dataLength=(int)tempByte;
      buf[subIndex++]=dataLength;
      startData++;
    }else if(startData==2){//get msgId
      msgId=tempByte; 
      buf[subIndex++]=msgId;
      startData++;
    }else if(startData>2){ 
        if(dataLength==-1){
          if(checkSum()){
            switchCMD();
          };
          startData=0;
          subIndex=0;
        }else{
            buf[subIndex++]=tempByte;
            dataLength--;
        }
    }
    if(tempByte=='$'||tempByte=='B'||tempByte=='>'||tempByte=='<'){
      headString+= (char)tempByte;
      if(headString=="$B>"||headString=="$B<"){
        startData=1;
      }
    }else{
      headString="";
    }
  }
};




uint8_t read8(int index)  {
  return buf[index]&0xff;
}

uint16_t read16(int index) {
  uint16_t t = read8(index);
  t+= (uint16_t)read8(index+1)<<8;
  return t;
}
uint32_t read32(int index) {
  uint32_t t = read16(index);
  t+= (uint32_t)read16(index+3)<<16;
  return t;
}
