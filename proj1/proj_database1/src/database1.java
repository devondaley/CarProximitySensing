import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;

//SQL Database
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

//GPIO
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class database1 {
	
	private static DatagramSocket socket;				//Socket to send and receive stuff
	private static int distance = 0;					//Cars current distance from one another
	private static boolean motorsOff = false;			//Track the status of the motors. Kind of redundant because of pin.isHigh(), but it wasn't worth rewriting
	private static int minDistance = 15;				//The distance the cars are allowed to get from one another before stopping
    
    public static void main(String args[]) throws Exception {
    	
    	socket = new DatagramSocket(3443);
        
    	//Database setup
    	String url = "jdbc:mysql://localhost:3306/database1";			//URL for connecting to database
        String username = "username";									//Connecting with proper credentials
        String password = "password";
        //Connection con = DriverManager.getConnection(url, username, password);
        
        //Setup for GPIO handling
    	final GpioController gpio = GpioFactory.getInstance();			//setup handle for gpio
    	final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01,"PinRELAY", PinState.LOW);
    	
        //Initialize
        byte[] tmp = new byte[10];
        DatagramPacket packet = new DatagramPacket(tmp, tmp.length);
        
        //Different IPs to compare to
        InetAddress addressCarpi = InetAddress.getByName( "192.168.137.125" );
        InetAddress addressAndroid = InetAddress.getByName( "192.168.137.150" );
        
        double time;

        while(true) {
        	System.out.println("Receiving...");
            socket.receive(packet);            //wait to receive message        	
            
            System.out.println("Message Retrieved: " + byteArrayToInt(packet.getData()));		//FOR DEBUGGING
            
            InetAddress address = packet.getAddress();    //Find address of message
            int port = packet.getPort();
        	tmp = packet.getData();				//Store the message
        	//int count = 20;					//To count the number of times it tries to log
            
            if(address.toString().equals(addressCarpi.toString())) { //If message is from Carpi           
            	System.out.println("Packet received from Carpi");
                time = (double) byteArrayToInt(tmp);					// Convert byte array to int, then to double
                
                //Calculate distance in CENTIMETERS
                distance = (int) calcDistance(time);              // d = time * Velocity
                
                if(distance <= minDistance + (minDistance * 0.75)){				//Only logs if the cars are approaching minDistance
                	//if(count ==20){												//To avoid flooding the database, we'll only send every 1/20 messages
                		//log(con);			// Log the current distance
                		//count = 0;
                	//}
                	//count++;
                }
                
                
                if(distance <= minDistance) { //the cars are too close, turn off motors
                    byte[] message = new byte[1];
                    message[0] = 1;
                    toggleMotor(addressCarpi, pin, message, packet, port, socket);				//changes the motors depending on MESSAGE
                }
            }
            
            else if(address.toString().equals(addressAndroid.toString())){        	//If message is from Android
                
            	System.out.println("Packet received from Android");
            	
                if(byteArrayToInt(tmp) < 2) {									//If its to toggle the motors
                	port = 3450;
                    toggleMotor(addressCarpi, pin, packet.getData(), packet, port, socket);                
                }
                else {        //If its a message to change the car distance                
                    minDistance = byteArrayToInt(tmp);
                }
            }
            else {
                break;
            }
            
        	System.out.println("Minimum Distance: " + minDistance);
        	System.out.println("Motors Off: " + motorsOff);
        }
        System.out.println(packet.getAddress().toString());
        System.out.println("An error occurred");
        socket.close();
    	
    }
    
    public static void toggleMotor(InetAddress first, GpioPinDigitalOutput pin, byte[] message, DatagramPacket packet, int port, DatagramSocket socket) throws Exception  {
        //Changes the state of the motors to whatever "message" contains
        
        //Tell Carpi to toggle                                        //CASE 3
        InetAddress address = first;
        packet = new DatagramPacket(message, message.length,address,port);
        socket.send(packet);
        System.out.println("Packet sent to toggle carpi");
        //Tell Arduino to toggle                                    //CASE 2
        toggleCarduinoMotor(pin, message);
        System.out.println("Carduino toggled");

        updateMotorStatus(byteArrayToInt(message));
    }
    
    public static void updateMotorStatus(int i){
    	System.out.println(i); 
    	if(i >= 1){
    		motorsOff = true;
    	}
    	else{
    		motorsOff = false;
    	}
    }
    
    public static double calcDistance(double time){
        return (time/10000000) * 343; //speed of sound
    }
    
    public static void log(Connection con) throws SQLException{
    	
    	/*String query = " insert into proj1 (Time, Distance)"
                + " values (?, ?)";
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");		
        String time  = dateFormat.format(new Date());

        String sDistance = Double.toString(distance);
        
        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = con.prepareStatement(query);
        preparedStmt.setString (1, time );
        preparedStmt.setString (2,sDistance);

        // execute the preparedstatement
        preparedStmt.execute();*/
    }
    
    public static void toggleCarduinoMotor(GpioPinDigitalOutput pin, byte[] message) throws Exception{
    	if((byteArrayToInt(message) ==  1)){
    		if(motorsOff == false){
    	    	pin.toggle();
    		}
    	}
    	else {
    		if(motorsOff == true){
    			pin.toggle();
    		}
    	}
    }
    
    /*
    public static byte[] toByteArray(int value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putInt(value);
        return bytes;
    }

    public static int toInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt(0);
    }
    
    */
    public static int byteArrayToInt(byte[] b) throws Exception
    {
    	byte[] tmp = new byte[4];
    	if(b.length <4){
    		tmp[3] = 0;
    		tmp[2] = 0;
    		tmp[1] = 0;
    		tmp[0] = b[0];
    	}
    	else{
    		tmp[3] = b[3];
    		tmp[2] = b[2];
    		tmp[1] = b[1];
    		tmp[0] = b[0];
    	}
        return   tmp[3] & 0xFF |
                (tmp[2] & 0xFF) << 8 |
                (tmp[1] & 0xFF) << 16 |
                (tmp[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) throws Exception
    {
        return new byte[] {
            (byte) ((a >> 24) & 0xFF),
            (byte) ((a >> 16) & 0xFF),   
            (byte) ((a >> 8) & 0xFF),   
            (byte) (a & 0xFF)};
    }
}
