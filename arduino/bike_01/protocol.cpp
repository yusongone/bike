#include "Protocol.h"


#define bufferSize 10
#define RECIVE_BUFFER_SIZE 64
#define SET_RAW_RC 200
#define TEST 99

#define GET_SPEED 100


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
            Protocol::switchCMD();
          };
          startData=0;
          subIndex=0;
        }else{
            buf[subIndex++]=tempByte;
            dataLength--;
        }
    }
    if(tempByte=='$'||tempByte=='M'||tempByte=='>'||tempByte=='<'){
      headString+= (char)tempByte;
      if(headString=="$M>"||headString=="$M<"){
        startData=1;
        Protocol::switchCMD();
      }
    }else{
      headString="";
    }
  }
};

void Protocol::switchCMD(){
   switch (msgId){
    case GET_SPEED:
     // setRAWData();
      
    break;
    case TEST:
    break;
  }
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
