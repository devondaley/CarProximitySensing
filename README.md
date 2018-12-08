# CarProximitySensing
For SYSC 3600 Tuesday Team 1


JOZEF-------------------------------------------------
	The code for the android app had to compressed into a zipped folder due to github’s upload restrictions so within the repository there is a zip file that contains the android project as well as appCode.txt (which is a text file containing the main functions of the app), appManifest (the manifestAndroid file that was changed to allow UDP transfers) and ArduinoCode.ino (the code run on the arduino).
	ArduinoCode.ino runs on the arduino and just transmits signals. The app project included may not work as android studio stopped working on all the school computers sometime between the demos and writing this so I grabbed an old version of the app and changed the main and manifest file but all the files are there, mainly the two .txt within the zipped folder. Assuming the app file is correct, you would simply open it, change the destination IP as it would likely be different on a different network and push the app to your phone. After that you should be able to launch the app and properly communicate with the server. 




DEVON-------------------------------------------------
	The section that I was in charge of for this project was the UDP communication between the server and all the components. As a result, the server was almost wholly written by me. This includes message retrieval, message decoding, message sending, relay control, and server logic. The part that was not done by me is all the Database commands and logging.
	In the github repository, in the Individual Code branch you’ll find a folder called proj1. To run the server, this whole folder is needed, as well as an up to date version of eclipse. For ease of use, the code is to be run from within eclipse. If you download and unzip the proj1 folder from our git repository (UNDER BRANCH INDIVIDUAL CODE, NOT MASTER), then you can move it into your workspace. Then, create a new project named proj1. Now you should be able to access everything from within this new project. The database stuff may not work because of being on a new pi, but it can be commented out. If you get errors regarding any of the libraries, right click on proj1 in the Package Explorer in eclipse, then click Build Path ->Configure Build Path. From here, add any jar files that are needed. They should be in the src folder in our project. 




NIKHIL-------------------------------------------------
	I was in charge of creating and maintaining the database, as well as linking the mySQL code to the Java server code, and updating the JBDC drivers. The database project was named “database1” and the table name was “proj1”. These were embedded into the server code, where the distance and timestamp data were recorded into a table, automatically logging when the 2 cars were in close proximity to each other.
In the server code, the logging table was made as a function, so when the 2 cars were detected to be in close proximity, the logger function was called into the main code and started to record the distance between the 2 cars with the appropriate timestamp. The database can be viewed using “Putty”, entering the proper credentials to log onto the pi and the proper credantionals to login to mySQL, selecting the proper database to be used, which in this case is “Database1”, and then selecting which table to view, which in this case is “proj1”. At this point, the whole table can be seen in “YYYY-MM-DD HH:MM:SS” format.
"jdbc:mysql://localhost:3306/database1"




NIC-----------------------------------------------------
	The hardware code for the Raspberry Pi that used the receiving sensors was written by me in c code. This code also includes socket creation in c for sending time values to the server to determine distance between the two cars. This code can be found on our git repository under the branch called Individual-Code where it is named (sensorRFUltraRec.c). This code would need to be pulled from the github repository and placed into a folder or somewhere on an RPi. The RPi would then require the wiring of the receiving RF and ultrasonic sensors as shown in the circuit diagram in figure (INSERT FIGURE #). Assuming the code pulled from the repository was stored on the desktop, the user would then need to open the console and type the following to compile and run the code:

cd Desktop [hit enter] gcc -Wall -o sensorRFUltraRec sensorRFUltraRec.c [enter]
sudo ./sensorRFUltraRec

The RPi should now be running the code which polls for transmissions to its respective sensors and based on the receival of transmissions will send a udp signal to the server giving the server the time of the ultrasonic travel used to calculate distance. 

