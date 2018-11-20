#include <stdio.h>
#include <stdlib.h>
#include "wiringPi.h"


void sensorGPIO_Init() {
	wiringPiSetup();        // Initializing wiringPi 
	
	pinMode(2, INPUT);      // Setting BCM_GPIO pin 27 as input for RF Receiver
	pinMode(5, INPUT);      // Setting BCM_GPIO pin 24 as input for Ultrasonic echo
	pinMode(4, OUTPUT);     // Setting BCM_GPIO pin 23 as output for Ultrasonic trig "May not need" 
}


int get_RFSignal() {
	return digitalRead(2);
	
}

int get_UltrasonicSignal() {
	return digitalRead(5);
}
