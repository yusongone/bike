#include <avr/eeprom.h>

#ifndef SPEED_H
#define SPEED_H

#include "Arduino.h"
#include "config.h"

class Speed{
  public :
    Speed();
    float getSpeed();
    void init();
    
    void addTotalDist(int meter);
    int getTotalDist();
   
    void writeOnceDist();
    void getOnceDist();
    void clearOnceDist(); 
   
  private :
    int c;
};


#endif
