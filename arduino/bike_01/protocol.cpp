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
uint8_t  buf[RECIVE_BUFFER_SIZE];
//SoftwareSerial softSerial(1,2);
Speed mySpeed;

void Protocol::init(){
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
  int tempValue=mySpeed.getSpeed();
  tempValue=1234;
  byte buffer[8];
  buffer[0]=0x24;
  buffer[1]=0x42;
  buffer[2]=0x3C;
  buffer[3]=0x02;
  buffer[4]=201;
  buffer[5]=tempValue>>0&0xff;
  buffer[6]=tempValue>>8&0xff;
  buffer[7]=getSum(buffer);
  //softSerial.write(buffer,8);
  Serial.write(buffer,8);
}


void switchCMD(){
   switch (msgId){
    case GET_SPEED:
      write_speed();      
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
  int dl=Serial.available();
  while(dl--){
    byte tempByte=Serial.read();
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
