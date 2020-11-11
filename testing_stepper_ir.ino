#include <MultiStepper.h>
#include <AccelStepper.h>
#include <IRremote.h>

const int dirPin = 8;
const int stepPin = 9;
const int motorInterfaceType 1
const int RECV_PIN = 7;
const long KEY_1 = 0xFFA25D;
const long KEY_0 = 0xFF9867;
const long KEY_2 = 0XFF629D;
const long KEY_3 = 0XFFE21D;
 
const int steps = 200;
IRrecv irrecv(RECV_PIN);
decode_results results;
unsigned long key_value = 0;
AccelStepper stepper = AccelStepper(motorInterfaceType, stepPin, dirPin);
 
void setup() {
 Serial.begin(9600);
 irrecv.enableIRIn();
  stepper.setMaxSpeed(steps);
  stepper.setSpeed(100);
  key_value = KEY_0;
}
 
void loop() {
   if (irrecv.decode(&results)) { // decodifica la senal y la almacena en results
    if (results.value == 0xFFFFFFFF) { // fix para cuando lee fffffff
      results.value = key_value;
    }
    Serial.println(results.value, HEX);
    if ( key_value != results.value) {
       key_value = results.value; // alamcenar el valor en key_value
    }
    irrecv.resume(); // reset para leer proximo valor
  }

  switch (key_value) { 
      case KEY_1: 
        Serial.println("KEY_1"); 
         stepper.setSpeed(50);
        stepper.runSpeed();
        break;
      case KEY_2:
        Serial.println("KEY_2");
        stepper.setSpeed(100);
        stepper.runSpeed();
        break;
       case KEY_3:
        Serial.println("KEY_3");
        stepper.setSpeed(150);
        stepper.runSpeed();
        break;
      case KEY_0:
        Serial.println("KEY_0");
        stepper.stop();
        break;
    }
    
}
