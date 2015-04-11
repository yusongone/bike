#include "sensor.h"
#include "Wire.h"

#define BMPaddress 0x77  // I2C BMP085_ADDRESS address of BMP085  
#define HMCaddress 0x1E//HMC5883L cichang
#define L3Gaddress 0xD2>>1//L3G4200D tuoluoyi
#define ADXaddress 0x53//ADXL345 jiasu //0x1D


void readData(byte address,byte reg,int* data){
  Wire.beginTransmission(address);
  Wire.write(reg| (1 << 7));
  Wire.endTransmission();
  Wire.requestFrom(address, (byte)6);
  while (Wire.available() < 6){};
  
  uint8_t xlow=Wire.read();
  uint8_t xhig=Wire.read();
  uint8_t ylow=Wire.read();
  uint8_t yhig=Wire.read();
  uint8_t zlow=Wire.read();
  uint8_t zhig=Wire.read();

  data[0]=xhig<<8|xlow;
  data[1]=yhig<<8|ylow;
  data[2]=zhig<<8|zlow;
  //return data;
}

int readIntRegister(unsigned char address,unsigned char r){
  unsigned char msb, lsb;
  Wire.beginTransmission(address);
  Wire.write(r);  // register to read
  Wire.endTransmission();

  Wire.requestFrom((int)address, 2); // request two bytes
  while(!Wire.available()); // wait until data available
  msb = Wire.read();
  while(!Wire.available()); // wait until data available
  lsb = Wire.read();
  return (((int)msb<<8) | ((int)lsb));
};

void writeRegd(byte address,byte reg,byte value){
  Wire.beginTransmission(address);
  Wire.write(reg);
  Wire.write(value);
  Wire.endTransmission();
};

void getHMCData(int* data){
  readData(HMCaddress,0x03,data);
}

void getL3GData(int* data){
  readData(L3Gaddress,0x28,data);
}

void getADXData(int* data){
  readData(ADXaddress,0x32,data);
}


void initGy80(){
   Wire.begin();
  
   writeRegd(BMPaddress,0xF4,0x2E);
   
   writeRegd(L3Gaddress,0x20,0x0F);
   writeRegd(L3Gaddress, 0x20, 0b00001111);    // 设置睡眠模式、x, y, z轴使能
   writeRegd(L3Gaddress, 0x21, 0b00000000);    // 选择高通滤波模式和高通截止频率 
   writeRegd(L3Gaddress, 0x22, 0b00000000);    // 设置中断模式
   writeRegd(L3Gaddress, 0x23, 0b00110000);    // 设置量程(2000dps)、自检状态、SPI模式
   writeRegd(L3Gaddress, 0x24, 0b00000000);
   
   writeRegd(ADXaddress,0x2D,0b00001000);

}
