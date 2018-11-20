/* 
This is the code for testing the individual pieces of software using
a hardware stub to provide good and bad values to the software.
*/
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>
#include <inttypes.h>
#include "sensorRFRecStub.h"
#define __STDC_FORMAT_MACROS

struct timespec ts;
clock_gettime( CLOCK_MONOTONIC, &ts );

bool isEcho = false;
bool isRadio = false;
uint64_t timeout_Start = 0;
uint64_t timeout_End = 0;
uint64_t endTime = 0;


// Normal case where RF receiver receives a high value (1)
// Also prints beginTime to make sure time is being stamped at a certain point
void gotRFDataCase() {
	
	uint64_t beginTime = 0;
	
	while (1) {
		if(getRFData()) {
			uint64_t beginTime = (uint64_t)ts.tv_sec * 1000000000U + (uint64_t)ts.tv_nsec;
			printf("RPi got the data from RF sensor and recorded a start time for Ultrasonic Transmition, Test Passed\n");
			printf("%"PRIu64" is the beginning timestamp\n", beginTime);
			break;
		} else {
			printf("No Data Received");
			break;
		}
	}
}

//No data case where the RF Transmission wasn't sent or was failed to be received
void noRFDataCase() {
	
	uint64_t beginTime = 0;
	
	while (1) {
		if(getNoRFData()) {
			beginTime = (uint64_t)ts.tv_sec * 1000000000U + (uint64_t)ts.tv_nsec;
			printf("RPi got the data from RF sensor and recorded a start time for Ultrasonic Transmition");
			printf("%"PRIu64" is the beginning timestamp\n", beginTime);
			break;
		} else {
			printf("No Data Received");
			break;
		}
	}	
}

// Testing to see if logic for leaving while loop after RF transmission receive works
// This is dependent on isRadio being changed within the while loop
void RF_LeaveCase() {

	uint64_t beginTime = 0;

	while(!(isRadio)) {
			if(getRFData()) {
				beginTime = (uint64_t)ts.tv_sec * 1000000000U + (uint64_t)ts.tv_nsec;
				isRadio = true;
			}
		}
		printf("Successfully left the while");
}

// Testing to ensure there is no break from the while loop if we
// haven't timed out (polled too long) yet
void valid_timeout_End() {
	timeout_Start = (uint64_t)ts.tv_sec * 1000000000U + (uint64_t)ts.tv_nsec;
	timeout_End = 0;
	
		while(!(isEcho)) {
			timeout_End = getValidTimeoutEnd();
			if(getUltraData()) {
				endTime = (uint64_t)ts.tv_sec * 1000000000U + (uint64_t)ts.tv_nsec;
				isEcho = true;
			}
		
			if((timeout_End - timeout_Start) >= 15000) {
				break;
			} else {
				printf("timeout_End hasn't timed out yet, test passed");
			}
		
		}
	
}

// Testing to make sure we break from the while loop if 
// we timeout (polled too long)
void invalid_timeout_End() {
	timeout_Start = (uint64_t)ts.tv_sec * 1000000000U + (uint64_t)ts.tv_nsec;
	timeout_End = 0;
	
		while(!(isEcho)) {
			timeout_End = getFailedTimeoutEnd();
			
			if(getUltraData()) {
				endTime = (uint64_t)ts.tv_sec * 1000000000U + (uint64_t)ts.tv_nsec;
				isEcho = true;
			}
		
			if((timeout_End - timeout_Start) >= 15000) {
				break;
			}
		
		}	
		
}

// A main function to place and run all test functions in
int main(void) {
	
	gotRFDataCase();
	
	noRFDataCase();
	
	RF_LeaveCase();
	
	valid_timeout_End();
	
	invalid_timeout_End();
	
}
