#include "gps.h"
#include "SoftwareSerial.h"

//String CMDHead="$GPGGA";
String CMDHead="$GPRMC";
bool startCMD=0;
String heading="";
String CMD="";

String UTC;
String lat;
String lon;
String ait;
String star;
String cleanCMD;

void cmdToAry(String str){
  String tempString="";
  int cmdCursor=0;
  for(int i=0;i<str.length();i++){
    char temp=str[i];
    if(temp==','){
      cmdCursor++;
      switch(cmdCursor){
        case 2://lat
          UTC=tempString;
        break;
        case 3://lat
          lat=tempString;
        break;
        case 4://lon
        //  lon=tempString;
        case 5://lon
          lon=tempString;
        case 8://lon
          star=tempString;
        case 10://lon
          ait=tempString;
        break;
      }
      tempString="";
    }else{
      tempString+=str[i]; 
    }

  }
    Serial.print("lat=");
    Serial.println(lat);
    Serial.print("lat=");
    Serial.println(UTC);
    Serial.println(lon);
}

void parse(char tempChar){
    if(CMDHead.indexOf(tempChar)>-1){
      heading+=tempChar;
      if(tempChar=='$'&&CMD!=""){
          startCMD=false;
          cmdToAry(CMD);
      }else if(heading==CMDHead){
        startCMD=true;
      }
    }else{
      if(startCMD){
        CMD+=tempChar;
      }else{
        CMD="";
      };
      heading="";
    }
}
 //------------------------- trip dist---------------------------------
 

void GPS::parseCMD(){
  if(cleanCMD!=""){
   // Serial.println(cleanCMD);
    for(int i=0;i<cleanCMD.length();i++){
      parse(cleanCMD[i]);
    }
    cleanCMD="";
  }
}

void GPS::joinCMD(char c){
    cleanCMD+=c;
}
