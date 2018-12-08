import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;


//GPIO
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class sendertest {
	public static void main(String args[]) throws Exception{
		/*
		DatagramSocket socket = new DatagramSocket(3444);
		InetAddress address = InetAddress.getByName("localhost");
		
        byte[] tmp = new byte[256];
        DatagramPacket packet = new DatagramPacket(tmp, tmp.length, address, 3443);
        socket.send(packet);
        socket.close();
        */
		
		//Setup for GPIO handling
    	final GpioController gpio = GpioFactory.getInstance();			//setup handle for gpio
    	final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01,"PinRELAY", PinState.LOW);
    	
    	
		
	}
}
