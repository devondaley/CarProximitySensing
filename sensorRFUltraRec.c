#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>
#include <stdint.h> 
#include <unistd.h> 
#include <string.h> 
#include <sys/types.h> 
#include <sys/socket.h> 
#include <arpa/inet.h> 
#include <netinet/in.h> 
#include "wiringPi.h"

// Defining value and port constants  
#define PORT     8080 
#define MAXLINE 1024
#define SERVADDR "192.168.137.1"
#define BILL 1000000000U

bool isEcho = 0;
bool isRadio = 0;
uint64_t beginTime = 0;
uint64_t endTime = 0;
uint64_t prev_beginTime = 0;
uint64_t prev_endTime = 0;
uint64_t timeout_Start = 0;
uint64_t timeout_End = 0;
int distTime = 0;

struct timespec ts;

void setup_GPIOInit() {
	wiringPiSetup ();
	
	pinMode(2, INPUT);      // Setting BCM_GPIO pin 27 as input for RF Receiver
	pinMode(5, INPUT);      // Setting BCM_GPIO pin 24 as input for Ultrasonic echo
	pinMode(4, OUTPUT);     // Setting BCM_GPIO pin 23 as output for Ultrasonic trig "May not need" 
	pullUpDnControl(2, PUD_DOWN);
	pullUpDnControl(5, PUD_DOWN);
}
/*
char intToChar(int i) {
	int i = time;
	int count = 0;
	
	while(i != 0) {
		i /= 10;
		count++;
	}
	
	char str[count];
	sprintf(str, "%d", i);
	return *str;
}
*/

void loop() {
	prev_beginTime = beginTime;
	prev_endTime = endTime;
	
	// wait until RF Transmission received, take time when received and move on
	while(isRadio != 1) {
		printf("Entered loop");
		if(digitalRead(2)) {
			beginTime = (uint64_t)ts.tv_sec * BILL + (uint64_t)ts.tv_nsec;
			isRadio = 1;
			printf("GOT THE RF TRANS");
		}
	}

	// Reset above while loop variable check
	isRadio = 0;    
	
	// Set Start timeout time variable and set/reset end before updating in loop
	timeout_Start = (uint64_t)ts.tv_sec * BILL + (uint64_t)ts.tv_nsec;
	timeout_End = 0;
	
	// wait until an echo is received as an input and record the input time stamp, timeout if it takes too long
	while(isEcho != 1) {
		timeout_End = (uint64_t)ts.tv_sec * BILL + (uint64_t)ts.tv_nsec;
		if(digitalRead(5)) {
			endTime = (uint64_t)ts.tv_sec * BILL + (uint64_t)ts.tv_nsec;
			isEcho = 1;
		}
		
		// timeout check so the system doesn't hang
		if((timeout_End - timeout_Start) >= 15000) {						//since we don't care about distances greater than 1m, delay =1/(343/1) = approximately 2.9 ms = 2.9M ns
			break;
		}
		
	}
	
	// values to check if we have updated our time values, if not then don't send (repeated time values) 
	
	// Reset above while loop variable check
	isEcho = 0;
}

void sendingData(int time) {
	// Client side implementation of UDP client-server model 
   
	int i = time;
	int count = 0;
	
	while(i != 0) {
		i /= 10;
		count++;
	}
	
	//char str[count];
	//sprintf(str, "%d", i);
   
	// Driver code  
    int sockfd; 
    char buffer[MAXLINE]; 
    char *data[count];
    struct sockaddr_in     servaddr; 
    sprintf(*data, "%d", i);
  
    // Creating socket file descriptor 
    if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
        perror("socket creation failed"); 
        exit(EXIT_FAILURE); 
    } 
  
    memset(&servaddr, 0, sizeof(servaddr)); 
      
    // Filling server information 
    servaddr.sin_family = AF_INET; 
    servaddr.sin_port = htons(PORT); 
    servaddr.sin_addr.s_addr = inet_addr(SERVADDR); 
      
    int n; 
    socklen_t len = sizeof(servaddr);
      
    sendto(sockfd, (const char *)data, strlen(*data), 
        MSG_CONFIRM, (const struct sockaddr *) &servaddr,  
            sizeof(servaddr)); 
    printf("Hello message sent.\n"); 
          
    n = recvfrom(sockfd, (char *)buffer, MAXLINE,  
                MSG_WAITALL, (struct sockaddr *) &servaddr, &len); 
    buffer[n] = '\0'; 
    printf("Server : %s\n", buffer); 
  
    close(sockfd);  
}

int main(void) {
	clock_gettime(CLOCK_MONOTONIC, &ts);
	
	// Initialize GPIO
	setup_GPIOInit();
	
	// Run the sensor polling loop and retrieve two times
	while(1) {
		/*loop();
		// note will be sending beginTime and endTime to the server for calculations and display on android
		if((endTime - beginTime) <= 0) {
			// send bad values message?
		} else if(endTime == prev_endTime || beginTime == prev_beginTime) {
			// time values not changed, 
		}
		*/
		
		sendingData(5000);
		
		// delaying the polling
		delay(5);
	}
}


