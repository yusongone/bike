#ifndef PROTOCOL_H
#define PROTOCOL_H

#include "Arduino.h"
#include "config.h"
#include "speed.h"

#define bufferSize 10
#define RECIVE_BUFFER_SIZE 64

#define CLEAR_TRIP_DISTANCE 100
#define SET_WHEEL_PERIMETER 101
#define SET_MAGNET_COUNTER 102

#define VERSION 200
#define GET_SPEED 201
#define GET_TRIP_DISTANCE 202
#define GET_TOTAL_DISTANCE 203
#define GET_STEPPED 204
#define GET_SPEED_PRESSURE_SHAKE 205

  void reciveCMD();
  void write_trip_dist();      
  void write_total_dist();
  void write_speed(uint16_t speed);
  void write_pressure(int32_t pressure);
  void write_speed_pressure_shake(uint16_t speed,int32_t pressure,uint16_t shake,int16_t temperature,int lap);

#endif
