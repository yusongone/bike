#ifndef PROTOCOL_H
#define PROTOCOL_H

#include "Arduino.h"
#include "config.h"
#include "speed.h"

  void reciveCMD();
  void write_trip_dist();      
  void write_total_dist();
  void write_speed(int speed);

#endif
