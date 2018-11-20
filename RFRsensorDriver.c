#include <stdio.h>
#include <stdlib.h>
#include "RFRsensorDriver.h"
#include "wiringPi.h"


int main(void) {
	int x = 0;
	int y = 0;
	sensorGPIO_Init();
	
	while(x <= 10) {
		if(get_RFSignal()) {
			printf("RF Transmission Received\n");
			x += 1;
			delay(50);     // delays by 50 mS so getData() doesn't 
		}                  // accidentally receive the same signal twice
	}
	
	// Note: We could count the number of transmissions and compare to the number  
	// of receivals to verify we aren't receiving signals more than once
	printf("Number of transmissions was %d\n", x);   

	while(y <= 10) {
		if(get_UltrasonicSignal()) {
			printf("RF Transmission Received\n");
			y += 1;
			delay(50);     // delays by 50 mS so getData() doesn't 
		}                  // accidentally receive the same signal twice
	}
	
	// Note: We could count the number of transmissions and compare to the number  
	// of receivals to verify we aren't receiving signals more than once
	printf("Number of transmissions was %d\n", y);
	
}
