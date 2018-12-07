
long Duration = 0;
int trigPin = 12;
int RFTrig = 8;

void setup() { 
     
  pinMode(trigPin, OUTPUT);
  pinMode(RFTrig, OUTPUT);
  //pinMode("A0", INPUT);
  
  //Serial.begin(9600); // Serial Output
}

void loop() {
  // put your main code here, to run repeatedly:
  
  // Sending the ultrasonic signal to the RPi
  digitalWrite(RFTrig, LOW);
  delayMicroseconds(5);
  digitalWrite(RFTrig, HIGH);
  delayMicroseconds(10);
  digitalWrite(RFTrig, LOW);
  
  delay(100);   
  digitalWrite(12, LOW);//0.1 second delay
  delayMicroseconds(5);
  digitalWrite(12, HIGH);           //set trig high (send signal) then wait and return to low
  delayMicroseconds(10);
  digitalWrite(12, LOW);

  // Sending the RF signal to the RPi
  
  //digitalWrite(RFTrig, LOW);


  /*
  //Ultrasonic Receiver code, just for testing
  Duration = pulseInLong(11,HIGH);  // Waits for the echo pin to get high
  distance = Duration/2.9/20;       //converts ms to cm
  Serial.print("Time (ms):");
  Serial.println(Duration);         //prints the delay between sending and receiving
  Serial.print("Distance (cm):");
  Serial.println(distance);         //prints the distance

  //RF Receiver code, jusr for testing
  data=analogRead("A0");    //listen for data on Analog pin 0
  
  if(data>upperThreshold){
     //digitalWrite(ledPin, LOW);   //If a LOW signal is received, turn LED OFF
     Serial.println("Low");
   }
   
   if(data<lowerThreshold){
     //digitalWrite(ledPin, HIGH);   //If a HIGH signal is received, turn LED ON
     Serial.println("High");
   }
  */
  
}
