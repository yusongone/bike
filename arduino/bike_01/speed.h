#include <avr/eeprom.h>

#ifndef SPEED_H
#define SPEED_H

#include "Arduino.h"
#include "config.h"
#include "EEPROM.h"
#include "sensor.h"

float getSpeed();
int getTotalDist();
int getTripDist();
void initSpeedISR();
void writeOnceDist();
void resetTripDist(); 
void initBMP085();
void get(); 
uint16_t getShake();

#endif
