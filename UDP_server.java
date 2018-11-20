import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.io.IOException;

public class UDP_server {
	
	private DatagramSocket socket;
	private double distance = 0;
	private boolean motorsOff = false;
	private double minDistance = 0.35;
	
    public UDP_server() throws SocketException{
        socket = new DatagramSocket(3443);
    }
    
    public UDP_server(DatagramSocket socket){
        this.socket = socket;
    }
    
    public void run() throws Exception {
        

        
        //Initialize
        byte[] tmp = new byte[256];
        DatagramPacket packet = new DatagramPacket(tmp, tmp.length);
        
        //Different IPs
        InetAddress addressCarpi = InetAddress.getByName( "192.168.0.11" );
        InetAddress addressAndroid = InetAddress.getByName( "192.168.0.12" );
        InetAddress addressArduino = InetAddress.getByName( "192.168.0.13" );
                
        while(true) {
            socket.receive(packet);            //receive message
            InetAddress address = packet.getAddress();    //find address of message
            int port = packet.getPort();
            
            if(address == addressCarpi) { //If message is from Carpi                                        //CASE 1
                double time = ByteBuffer.wrap(packet.getData()).getDouble();
                
                //Calculate distance
                distance = calcDistance(time);                // d = time * Velocity
                
                if(distance <= minDistance) { //the cars are too close, turn off motors
                    byte[] message = new byte[1];
                    message[0] = 1;
                    toggleMotor(addressCarpi, addressArduino, message, packet, port, socket);
                    
                    // send message to android saying that the cars are off
                    packet = new DatagramPacket(message, message.length, addressAndroid, port);
                    socket.send(packet);    //CASE 6
                }
            }
            
            else if(address == addressAndroid){        //If message is from Android
                
                if(packet.getData().length == 1) {//If its to toggle the motors                                    //CASE 4
                    toggleMotor(addressCarpi, addressArduino, packet.getData(), packet, port, socket);                
                }
                else {        //If its a message to change the car distance                        //CASE 5
                    minDistance = ByteBuffer.wrap(packet.getData()).getDouble();
                }
            }
            else {
                break;
            }
            
        }
        System.out.println("An error occurred");
        socket.close();
    }
    
    public void toggleMotor(InetAddress first, InetAddress second, byte[] message, DatagramPacket packet, int port, DatagramSocket socket) throws IOException  {
        //Changes the state of the motors to whatever "message" contains
        
    	motorsOff = !(motorsOff);
        //Tell Carpi to toggle                                        //CASE 3
        InetAddress address = first;
        packet = new DatagramPacket(message, message.length,address,port);
        socket.send(packet);
        //Tell Arduino to toggle                                    //CASE 2
        address = second;
        packet = new DatagramPacket(message, message.length,address,port);
        socket.send(packet);
    }
    
    public double calcDistance(double time){
        return (time/1000000000) * 343;
    }
    
    public double getDistance() {
    	return distance;
    }
    
    public boolean getMotors() {
    	return motorsOff;
    }
    
    public double getMinDistance() {
    	return minDistance;
    }
}
