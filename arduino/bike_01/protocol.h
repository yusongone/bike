#ifndef PROTOCOL_H
#define PROTOCOL_H

#include "SoftwareSerial.h"

#include "Arduino.h"
#include "config.h"
#include "speed.h"


class Protocol{
  public: 
    void init();
    void reciveCMD();
    void setRAWData();
    uint8_t read8(int index);
    uint16_t read16(int index);
    uint32_t read32(int index);
    
};
    
#endif
