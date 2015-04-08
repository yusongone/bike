#include <avr/eeprom.h>

#ifndef SPEED_H
#define SPEED_H

#include "Arduino.h"
#include "config.h"
#include "EEPROM.h"

float getSpeed();
int getTotalDist();
int getTripDist();
void initSpeedISR();
void writeOnceDist();
void resetTripDist(); 

#endif
