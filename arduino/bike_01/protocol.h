#ifndef PROTOCOL_H
#define PROTOCOL_H

#include "SoftwareSerial.h"

#include "Arduino.h"
#include "config.h"
class Protocol{
  public: 
    void initSerial();
    void reciveCMD();
    void switchCMD();
    boolean checkSum();
    void setRAWData();
    uint8_t read8(int index);
    uint16_t read16(int index);
    uint32_t read32(int index);
  private:
    
    
};
    
#endif
