
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
	
public class dummyCarduino {

		public static void main(String args[]) throws Exception{
			DatagramSocket socket = new DatagramSocket(3444);
			InetAddress address = InetAddress.getByName("192.168.137.7");
	        byte[] tmp = new byte[10];
	        DatagramPacket packet;
			
			for(int i = 5000000; i > 1000000; i = i -100000){
				tmp = toByteArray(i);
				packet = new DatagramPacket(tmp, tmp.length, address, 3443);
	        	socket.send(packet);
			}
        	socket.close();
		}

		public static byte[] toByteArray(int value) {
				byte[] bytes = new byte[8];
				ByteBuffer.wrap(bytes).putInt(value);
				return bytes;
		}
}