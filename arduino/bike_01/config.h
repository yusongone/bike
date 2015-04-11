#define SPEED_READ_PIN A7

#define SPEED_ANALOGREAD_HIGH 650
#define SPEED_ANALOGREAD_LOW 530

#define FOOT_READ_PIN A6
#define FOOT_ANALOGREAD_HIGH 650
#define FOOT_ANALOGREAD_LOW 530


#define SPEED_POINT_COUNT 1  //check point count
#define WHEEL_PERIMETER 2075 // mm
#define MIN_TIME 1000*5


#define HARDWARE_SERIAL_RATE 9600

#define SOFTWARE_SERIAL_RATE 9600
#define SOFTWARE_SERIAL_RX 11
#define SOFTWARE_SERIAL_TX 10

//#define HARDWARE_SERIAL_TEST



/*
eeprom
1-3 total dist //uint 32   $val/10=km  //max 1677721.5km
2-5 trip dist  //uint 16   $val/10=km
6   max-speed  //uint8     $val m/s
*/
