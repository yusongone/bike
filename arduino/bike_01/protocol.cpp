#include "Protocol.h"

String headString="";
int dataLength=-1;
int startData=0;
int subIndex=0;
int msgId;
boolean Hard=true;
uint8_t  buf[RECIVE_BUFFER_SIZE];
//SoftwareSerial softSerial =  SoftwareSerial(SOFTWARE_SERIAL_RX, SOFTWARE_SERIAL_TX);




byte getSum(byte b[]){
  byte temp=0x00;
  int length=b[3];
  for(int i=3;i<5+length;i++){
    temp^=(b[i]&0xff);
  }
  return temp;
}
void write_speed_pressure_shake(uint16_t speed,int32_t pressure,uint16_t shake,int16_t temperature,int lap){
  byte buffer[16];
  buffer[0]=0x24;
  buffer[1]=0x42;
  buffer[2]=0x3C;
  buffer[3]=10;
  buffer[4]=GET_SPEED_PRESSURE_SHAKE;
  buffer[5]=speed>>0&0xff;
  buffer[6]=speed>>8&0xff;
  buffer[7]=pressure>>0&0xff;
  buffer[8]=pressure>>8&0xff;
  buffer[9]=pressure>>16&0xff;
  buffer[10]=shake>>0&0xff;
  buffer[11]=shake>>8&0xff;
  buffer[12]=temperature>>0&0xff;
  buffer[13]=temperature>>8&0xff;
  buffer[14]=lap;
  buffer[15]=getSum(buffer);
  Serial.write(buffer,16);
}

void write_pressure(int32_t tempValue){
  byte buffer[8];
  buffer[0]=0x24;
  buffer[1]=0x42;
  buffer[2]=0x3C;
  buffer[3]=0x03;
  buffer[4]=GET_SPEED;
  buffer[5]=tempValue>>0&0xff;
  buffer[6]=tempValue>>8&0xff;
  buffer[7]=tempValue>>16&0xff;
  buffer[8]=getSum(buffer);
  Serial.write(buffer,9);
}

void write_speed(uint16_t tempValue){
  byte buffer[8];
  buffer[0]=0x24;
  buffer[1]=0x42;
  buffer[2]=0x3C;
  buffer[3]=0x02;
  buffer[4]=GET_SPEED;
  buffer[5]=tempValue>>0&0xff;
  buffer[6]=tempValue>>8&0xff;
  buffer[7]=getSum(buffer);
  Serial.write(buffer,8);
}

void write_total_dist(long tempValue){
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
  Serial.write(buffer,9);
}

void write_trip_dist(){
  long tempValue=getTripDist();
  //tempValue=65534;
  byte buffer[8];
  buffer[0]=0x24;
  buffer[1]=0x42;
  buffer[2]=0x3C;
  buffer[3]=0x02;
  buffer[4]=GET_TRIP_DISTANCE;
  buffer[5]=tempValue>>0&0xff;
  buffer[6]=tempValue>>8&0xff;
  buffer[7]=getSum(buffer);
  Serial.write(buffer,8);
}


void switchCMD(){
   switch (msgId){
    case GET_SPEED:
      write_speed(4);      
    break;
    case GET_TOTAL_DISTANCE:
      write_total_dist(5);      
    break;
    case GET_TRIP_DISTANCE:
      write_trip_dist();      
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



void reciveCMD(){
  //int dl=Serial.available();
  while(Serial.available()>0){
    byte tempByte=Serial.read();
    //Serial.write(tempByte);
    if(startData==1){//get dataLength;
      dataLength=(int)tempByte;
      buf[subIndex++]=dataLength;
      startData++;
    }else if(startData==2){//get msgId
      msgId=tempByte; 
      buf[subIndex++]=msgId;
      startData++;
    }else if(startData>2){ 
        if(dataLength==0){
          buf[subIndex++]=tempByte;
          if(checkSum()){
            switchCMD();
          };
          startData=0;
          subIndex=0;
        }else{
            buf[subIndex++]=tempByte;
            dataLength--;
        }
    }else if(tempByte=='$'||tempByte=='B'||tempByte=='>'||tempByte=='<'){
      headString+= (char)tempByte;
      if(headString=="$B>"||headString=="$B<"){
        startData=1;
        headString="";
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
