#include <avr/eeprom.h>

#ifndef GPS_H 
#define GPS_H 

#include "Arduino.h"
#include "config.h"
#include "EEPROM.h"

class GPS{
  public:
    void joinCMD(char c);
    void parseCMD();
};

#endif
