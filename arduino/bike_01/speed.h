#include <avr/eeprom.h>

#ifndef SPEED_H
#define SPEED_H

#include "Arduino.h"
#include "config.h"
#include "EEPROM.h"

class Speed{
  public :
    Speed();
    float getSpeed();
    void init();
    
    long getTotalDist();
   
    void writeOnceDist();
    int getTripDist();
    void resetTripDist(); 
   
  private :
    int c;
};


#endif
