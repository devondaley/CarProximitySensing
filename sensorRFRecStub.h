#include <stdio.h>
#include <stdlib.h>

// This is the hardware stub that supplies the values that would
// be given by the hardware to the software being tested

// Simulating the normal case, RF Transmission is received.
int getRFData() {
	return 1;
}

// Simulating error case, RF Transmission isn't received.
int getNoRFData() {
	return 0;
}

// Simulating receival of Ultrasonic Transmission
int getUltraData() {
	return 1;
}

// Simulating failed receival of Ultrasonic Transmission
int getNoUltraData() {
	return 0;
}

// Simulating too large timeout value for polling
int getFailedTimeoutEnd() {
	return 20000;
}

// Simulating not large enough timeout value
// to break while loop
int getValidTimeoutEnd() {
	return 5000;
}
