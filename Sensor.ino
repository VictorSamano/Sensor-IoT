// EmonLibrary examples openenergymonitor.org, Licence GNU GPL V3
volatile signed long time1,time2,time3,time4,time_aux2,time_aux3,time1_aux1,time1_aux2;
float time_aux1=0.1;
int conta=0;
int suma=0;
boolean EstadoPrev=false;
#include "EmonLib.h"             		// Include Emon Library
EnergyMonitor emon1;     		       	// Create an instance
//ESP8266
#include <SoftwareSerial.h>
#define RX 3
#define TX 2
String AP = "IZZI-5D81";       			// AP NAME
String PASS = "8871B1CB5D81";           // AP PASSWORD
String API = "PQ0CO6NEH8LBCDSN";      	// Write API KEY
String HOST = "api.thingspeak.com";   	// Server
String PORT = "80";                   	// Port
String field = "field1";              	// Campo 1 en ThingSpeak
String field2 = "field2";             	// Campo 2 en ThingSpeak
String field3= "field3";              	// Campo 3 en ThingSpeak
String field4= "field4";              	// Campo 4 en ThingSpeak
String field5= "field5";              	// Campo 5 en ThingSpeak
int countTrueCommand;
int countTimeCommand; 
boolean found = false; 
float valSensor1 = 1;                 	// Variable 1 en ThingSpeak
float valSensor2=1;                   	// Variable 2 en ThingSpeak
float valSensor3=1;                   	// Variable 3 en ThingSpeak
float valSensor4=1;                   	// Variable 4 en ThingSpeak
float valSensor5=1;                   	// Variable 5 en ThingSpeak
float value;
float porcentaje;
String prueba;
int conta1=0;
int val0;
int val1;

SoftwareSerial esp8266(RX,TX); 

void setup()
{ 
  pinMode(8,INPUT);
  pinMode(9,INPUT);
  val0=digitalRead(8);
  val1=digitalRead(9);
  Serial.begin(9600);
  esp8266.begin(9600);
  sendCommand("AT",5,"OK");
  //sendCommand("AT+CWMODE=1",5,"OK");
  //sendCommand("AT+CWJAP=\""+ AP +"\",\""+ PASS +"\"",20,"OK");
  emon1.voltage(2, 97, 1.3);                        // Voltage: input pin, calibration, phase_shift
  if(val0==HIGH){
    if(val1==HIGH){
      emon1.current(1, 57.4);
      Serial.println("Modo de operación establecido a 1 vueltas");
    }else{
      emon1.current(1, 29.2);
      Serial.println("Modo de operación establecido a 2 vueltas");
    }
  }else{
    if(val1==HIGH){
      emon1.current(1, 14.7);
      Serial.println("Modo de operación establecido a 4 vueltas");
    }else{
      emon1.current(1, 9.8);
      Serial.println("Modo de operación establecido a 6 vueltas");
    }
  }
  //6 vueltas = 9.8 corriente
  //4 vueltas = 14.7 corriente
  //2 vueltas = 29.2 corriente
  //1 vuelta = 57.4 corriente
  
  
  for(int i=0;i<20;i++){
      emon1.calcVI(20,2000);                          // Calculate all. No.of half wavelengths (crossings), time-out
      //emon1.serialprint();                          // Print out all variables (realpower, apparent power, Vrms, Irms, power factor)
      
      float realPower       = emon1.realPower;        //extract Real Power into variable
      float apparentPower   = emon1.apparentPower;    //extract Apparent Power into variable
      float powerFActor     = emon1.powerFactor;      //extract Power Factor into Variable
      float supplyVoltage   = emon1.Vrms;             //extract Vrms into Variable
      float Irms            = emon1.Irms;  
      Serial.print("Factor de potencia: ");
      Serial.println(powerFActor);
      Serial.print("Voltaje: ");
      Serial.println(supplyVoltage);
      Serial.print("Corriente: ");
      Serial.println(Irms);
      Serial.print("Angulo: ");
      Serial.println((acos(powerFActor))*180/3.1416);
      Serial.print("Potencia: ");
      Serial.println(realPower);
      Serial.println();
  }
}

void loop()
{
  emon1.calcVI(20,2000);         // Calculate all. No.of half wavelengths (crossings), time-out
  //emon1.serialprint();           // Print out all variables (realpower, apparent power, Vrms, Irms, power factor)
  
  float realPower       = emon1.realPower;        //extract Real Power into variable
  float apparentPower   = emon1.apparentPower;    //extract Apparent Power into variable
  float powerFActor     = emon1.powerFactor;      //extract Power Factor into Variable
  float supplyVoltage   = emon1.Vrms;             //extract Vrms into Variable
  float Irms            = emon1.Irms;             //extract Irms into Variable
  
  delay(15000);
  conta=0;
  time_aux1=0;
  if(Irms>0.1){
      for(int i=0;i<500;i++){
    time4=deteccion(conta,EstadoPrev);
    //Serial.println(time4);
    if(time4>170){
      time4=0;
    }
    if(time4<=90){
      time4=-time4;
    }
    if(time4>90){
      time4=-(time4-180);
    }
    
    time_aux1=time_aux1+time4;
    //Serial.println(time4);
  }
  //Serial.println(time4);
  //Serial.println(time_aux1);
  time_aux1=time_aux1/499.0;
  }else{
    time4=0;
  }

  
  //time4=time2-time1;
  
    if(Irms<0.08){
    powerFActor=1;
    Irms=0;
    realPower=0;
  }
  /*
  if(time4>175){
    time4=time4-174;
  }
  if(time4>time_aux1 && time4>time_aux2){
    if((time4-time_aux1)>40){
      Serial.print("res1=");
      Serial.println(time4-time_aux1);
      if((time4-time_aux2)>40){
        Serial.print("res2=");
        Serial.println(time4-time_aux2);
        Serial.println(time4);
        Serial.println(time_aux1);
        time_aux2=time4;
        time4=time_aux1;
        time_aux3=time_aux1;
        time_aux1=time_aux2;
        time_aux2=time_aux3;
      }else{
        time_aux2=time_aux1;
        time_aux1=time4;
      }
    }
  }else{
      time_aux2=time_aux1;
      time_aux1=time4;
    }
  if(time4<90){
      time4=-20;
      time_aux1=-20-random(1, 50) /100.0;
  }
  if(time4>90){
    time4=time4-180;
    time_aux1=time4-random(1, 50) /100.0;
  }*/
  
  Serial.print("Factor de potencia: ");
  Serial.println(cos(time_aux1*3.1416/180));
  Serial.print("Voltaje: ");
  Serial.println(supplyVoltage);
  Serial.print("Corriente: ");
  Serial.println(Irms);
  Serial.print("Angulo: ");
  Serial.println(time_aux1);
  Serial.print("Potencia: ");
  Serial.println(supplyVoltage*Irms);
  Serial.println();
  
  //Envio con ESP8266
  valSensor1= supplyVoltage;
  valSensor2= Irms;
  valSensor3= cos(time_aux1*3.1416/180);
  valSensor4=time_aux1;
  valSensor5=supplyVoltage*Irms;
  String getData = "GET /update?api_key="+ API +"&"+ field +"="+String(valSensor1,2)+"&"+field2+"="+String(valSensor2,2)+"&"+field3+"="+String(valSensor3,2)+"&"+field4+"="+String(valSensor4,2)+"&"+field5+"="+String(valSensor5,2);
  sendCommand("AT+CIPMUX=1",5,"OK");
  sendCommand("AT+CIPSTART=0,\"TCP\",\""+ HOST +"\","+ PORT,15,"OK");
  sendCommand("AT+CIPSEND=0," +String(getData.length()+4),4,">");
  Serial.println(getData);
  esp8266.println(getData);
  //Serial.print(valSensor2);
  countTrueCommand++;
  sendCommand("AT+CIPCLOSE=0",5,"OK");
  //Fin con el ESP8266

  
  /*
  conta=conta+1;
  suma=suma+time2;
  if(conta==10){
    Serial.println(suma/conta);
    noInterrupts();
    conta=0;
    suma=0;
    interrupts();
  }
}
void Interrupt_1(void)
{
  if(digitalRead(2)==LOW){
    time1=micros();
  }
  else{
    time2=micros()-time1;
  }
  */
}

int deteccion(int conta, boolean EstadoPrev){
  time1_aux1=micros();
  while(1){
    if(!digitalRead(4) || (time1_aux2-time1_aux1)>10000){
      time1_aux1=micros();
      break;
    }
    time1_aux2=micros();
  }
  while(1){
    if(digitalRead(4) || (time1_aux2-time1_aux1)>10000){
      time1=micros();
      time1_aux1=micros();
      break;
    }
    time1_aux2=micros();
  }
  while(1){
    if(!digitalRead(5) || (time1_aux2-time1_aux1)>10000){
      EstadoPrev=false;
      break;
    }
    if(digitalRead(5) || (time1_aux2-time1_aux1)>10000){
      EstadoPrev=true;
      break;
    }
    time1_aux2=micros();
  }
  while(1){
    if(digitalRead(5) && EstadoPrev==false || (time1_aux2-time1_aux1)>10000){
      time2=micros();
      break;
    }
    if(!digitalRead(5) && EstadoPrev==true || (time1_aux2-time1_aux1)>10000) {
      time2=micros();
      break;
    }
    time1_aux2=micros();
  }
  if(EstadoPrev==true){
    while(1){
      if(!digitalRead(4) || (time1_aux2-time1_aux1)>10000){
        time3=micros();
        break;
        }
        time1_aux2=micros();
      }
    return -(time3-time2)*360*0.000001/(0.01666666);
  }else{
    return (time2-time1)*360*0.000001/(0.01666666);
  }
  }
  /*
  while(1){
    if(digitalRead(4) || (time1_aux2-time1_aux1)>10000){
      break;
    }
    time1_aux2=micros();
  }
  while(1){
    if(!digitalRead(4) || (time1_aux2-time1_aux1)>10000){
      time3=micros();
      break;
    }
    time1_aux2=micros();
    
  }
  
   
}
*/

void sendCommand(String command, int maxTime, char readReplay[]) {
  Serial.print(countTrueCommand);
  Serial.print(". Comando AT enviado => ");
  Serial.print(command);
  Serial.print(" ");
  while(countTimeCommand < (maxTime*1))
  {
    esp8266.println(command);//at+cipsend
    if(esp8266.find(readReplay))//ok
    {
      found = true;
      break;
    }
    //esp8266.println("AT+RST");
    //delay(100);
    countTimeCommand++;
  }
  
  if(found == true)
  {
    Serial.println("Right");
    countTrueCommand++;
    countTimeCommand = 0;
  }
  
  if(found == false)
  {
    Serial.println("Fail");
    countTrueCommand = 0;
    countTimeCommand = 0;
    esp8266.println("AT+RST");
  }
  
  found = false;
 }
