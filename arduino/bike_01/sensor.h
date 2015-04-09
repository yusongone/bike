
#ifndef SENSOR_H 
#define SENSOR_H

#include "Arduino.h"
#include "Wire.h"

void initGy80();

void getADXData(int* data);
void getL3GData(int* data);
void getHMCData(int* data);

#endif
