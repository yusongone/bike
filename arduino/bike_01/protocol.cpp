#include "Protocol.h"

#define bufferSize 10
#define RECIVE_BUFFER_SIZE 64
#define SET_RAW_RC 200
#define TEST 99
String headString;
int dataLength=-1;
int startData=0;
int subIndex=0;
int msgId;
uint8_t  buf[RECIVE_BUFFER_SIZE];

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

void reciveCMD(){
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
    if(tempByte=='$'||tempByte=='M'||tempByte=='>'){
      headString+= (char)tempByte;
      if(headString=="$M>"){
        startData=1;
      }
    }else{
      headString="";
    }
  }
};

void switchCMD(){
   switch (msgId){
    case SET_RAW_RC:
      setRAWData();
    break;
    case TEST:
    break;
  }
}

void setRAWData(){
   //ROLL/PITCH/YAW/THROTTLE/AUX1/AUX2/AUX3/AUX4
   int dataLength=read8(0);
   int msgId=read8(1);
   int ROLL=read16(2);
   int PITCH=read16(4);
   int YAW=read16(6);
   int THROTTLE=read16(8);
   int AUX1=read16(10);
   int AUX2=read16(12);
   int AUX3=read16(14);
   int AUX4=read16(16);
  // robotUpdateCMD(ROLL,PITCH,YAW,THROTTLE);
}

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
