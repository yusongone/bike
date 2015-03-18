#ifndef SPEED_H
#define SPEED_H

#include "Arduino.h"
#include "config.h"

class Speed{
  public :
    Speed();
    void init();
    int getCount();
    float getSpeed();
  private :
    int c;
};


#endif
