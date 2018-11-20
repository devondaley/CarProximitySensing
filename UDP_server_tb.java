import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.junit.Assert.assertThat;
//import static org.hamcrest.CoreMatchers.any;

//import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.nio.ByteBuffer;

//import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.*;

public class UDP_server_tb {
	/*
    public static void main(String args[]) throws Exception{
        //Initialize
        DatagramSocket socketMock = mock(DatagramSocket.class);    //MOCK OBJECT
        byte[] blank = new byte[256];
        DatagramPacket message = new DatagramPacket(blank, blank.length);
        UDP_server server =  new UDP_server(socketMock);
        
        //Different IPs
        InetAddress addressCarpi = InetAddress.getByName( "192.168.0.11" );
        InetAddress addressAndroid = InetAddress.getByName( "192.168.0.12" );
        InetAddress addressArduino = InetAddress.getByName( "192.168.0.13" );
        
        
        //CASE 1b
        String string = new String("1000000");
        byte[] tmp = string.getBytes();
        
        message = new DatagramPacket(tmp,tmp.length,addressCarpi,3443);    
        //the server should interpret this as a message from carPi when run. 
        server.run();
        
        verify(socketMock).receive(message);    //verify it, will get error if failed.
		System.out.println("Case 1b successful");
        
        //CASE 2d continues from previous simulation path, since the time sent returns  a distance that is less than minDistance, stopping the motors
        byte[] tmp2 = new byte[1];
        tmp2[0] = 1;        //tell them to shut off
        message = new DatagramPacket(tmp2,tmp2.length,addressArduino,3443);
        
        verify(socketMock).send(message);    //verify it, will get error if failed.
		System.out.println("Case 2d successful");
        
        //CASE 3d continues from previous simulation path
        message = new DatagramPacket(tmp2,tmp2.length,addressCarpi,3443);
        
        verify(socketMock).send(message);        //verify it, will get error if failed.
		System.out.println("Case 3d successful");
        
        //CASE 6a continues from previous simulation path
        message = new DatagramPacket(tmp2,tmp2.length,addressArduino,3443);
        
        verify(socketMock).send(message);
		System.out.println("Case 6a successful");
        
        //CASE 4a does NOT continue simulation path
        message = new DatagramPacket(tmp2,tmp2.length,addressAndroid,3443);        
                //reuse tmp2 since it just says to turn off motors
        
        verify(socketMock).receive(message); //verify it, will get error if failed.
		System.out.println("Case 4a successful");
        
        //CASE 5a does NOT continue simulation path
        string = new String("1");        //change minDistance to 1
        tmp = string.getBytes();
        message = new DatagramPacket(tmp,tmp.length,addressAndroid,3443);
        
        verify(socketMock).receive(message); //verify it, will get error if failed.
		System.out.println("Case 5a successful");
    }
    */
	private byte[] tmp = new byte[256];
	private DatagramPacket defaultpacket;
	private DatagramPacket sentPacket;
	
	//Mocked
	private DatagramSocket socketMock;
	
	//Under Test
	private UDP_server server;
	
	@Before
	public void setUp() throws Exception{
		socketMock = mock(DatagramSocket.class);
		server= new UDP_server(socketMock);
		defaultpacket = new DatagramPacket(tmp, tmp.length);
	}
	
	@Test
	public void test1a() throws Throwable {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.11" ); //Carpi address
				String tmp = new String("1000000");	// more than minDistance
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
		
		server.run();
		assert(server.getDistance()>0.35);
		System.out.println(server.getDistance());
	}
	
	@Test
	public void test1b() throws Throwable{
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.11" ); //Carpi address
				String tmp = new String("100000");	// less than minDistance
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
	
		server.run();
		assert(server.getDistance()<0.35);
		System.out.println("Case 1b successful");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test1c() throws Throwable{
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.11" ); //Carpi address
				String tmp = new String("1btasdf");	// less than mindistance
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
		
		server.run();
		System.out.println("Case 1c successful");
	}
	
	@Test
	public void test2ab() throws Throwable{	
		
		//4b
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
				String tmp = new String("0");	// turn on motors
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
		
		//new stuff
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				sentPacket = invocation.getArgument(0);
				return null;
			}
		}).when(socketMock).send(any(DatagramPacket.class));
		
		InetAddress address = InetAddress.getByName( "192.168.0.13" ); //Arduino address
		server.run();
		assert(sentPacket.getData()[0] == 0);
		assert(sentPacket.getAddress() == address);
		System.out.println("Case 2ab successful");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test2cd()  throws Throwable{
		//4a
				doAnswer(new Answer<Void>() {
					@Override
					public Void answer(InvocationOnMock invocation) throws Throwable {
						DatagramPacket packet = invocation.getArgument(0);
						InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
						String tmp = new String("1");	// turn off motors
						byte[] message = tmp.getBytes();
						packet.setAddress(address);
						packet.setPort(3443);
						packet.setData(message);
						packet.setLength(message.length);
						socketMock = (DatagramSocket) invocation.getMock();
						return null;
					}
				}).when(socketMock).receive(defaultpacket);
				
		//new stuff
				doAnswer(new Answer<Void>() {
					@Override
					public Void answer(InvocationOnMock invocation) throws Throwable {
						sentPacket = invocation.getArgument(0);
						return null;
					}
				}).when(socketMock).send(any(DatagramPacket.class));
				
				InetAddress address = InetAddress.getByName( "192.168.0.13" ); //Arduino address
				server.run();
				assert(sentPacket.getData()[0] == 1);
				assert(sentPacket.getAddress() == address);
				System.out.println("Case 2cd successful");
	}
	
	@Test
	public void test3ab()  throws Throwable{
		//4b
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
				String tmp = new String("0");	// turn on motors
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
		
		//new stuff
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				sentPacket = invocation.getArgument(0);
				return null;
			}
		}).when(socketMock).send(any(DatagramPacket.class));
		
		InetAddress address = InetAddress.getByName( "192.168.0.11" ); //Carpi address
		server.run();
		assert(sentPacket.getData()[0] == 0);
		assert(sentPacket.getAddress() == address);
		System.out.println("Case 3ab successful");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test3cd()  throws Throwable{
		//4a
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
				String tmp = new String("1");	// turn off motors
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
		
		//new stuff
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				sentPacket = invocation.getArgument(0);
				return null;
			}
		}).when(socketMock).send(any(DatagramPacket.class));
		
		InetAddress address = InetAddress.getByName( "192.168.0.11" ); //Carpi address
		server.run();
		assert(sentPacket.getData()[0] == 1);
		assert(sentPacket.getAddress() == address);
		System.out.println("Case 3cd successful");
	}
	
	@Test
	public void test4a() throws Throwable{
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
				String tmp = new String("1");	// turn off motors
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
	
		server.run();
		assert(server.getMotors() == true);
		System.out.println("Case 4a successful");
		
	}
	
	@Test
	public void test4b()  throws Throwable{
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
				String tmp = new String("0");	// turn on motors
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
	
		server.run();
		assert(server.getMotors() == false);
		System.out.println("Case 4b successful");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test4c()  throws Throwable{
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
				String tmp = new String("a");			//invalid input
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
	
		server.run();
		System.out.println("Case 4c successful");
	}
	
	@Test
	public void test5a()  throws Throwable{
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
				String tmp = new String("1000000");			//new minDistance
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
	
		server.run();
		assert(server.getMinDistance() == 0.347);
		System.out.println("Case 5a successful");	
	}
	
	@Test
	public void test6a()  throws Throwable{
		//1b
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				DatagramPacket packet = invocation.getArgument(0);
				InetAddress address = InetAddress.getByName( "192.168.0.11" ); //Carpi address
				String tmp = new String("100000");	// less than minDistance
				byte[] message = tmp.getBytes();
				packet.setAddress(address);
				packet.setPort(3443);
				packet.setData(message);
				packet.setLength(message.length);
				socketMock = (DatagramSocket) invocation.getMock();
				return null;
			}
		}).when(socketMock).receive(defaultpacket);
		
		//new stuff
				doAnswer(new Answer<Void>() {
					@Override
					public Void answer(InvocationOnMock invocation) throws Throwable {
						sentPacket = invocation.getArgument(0);
						return null;
					}
				}).when(socketMock).send(any(DatagramPacket.class));
				
				InetAddress address = InetAddress.getByName( "192.168.0.12" ); //Android address
				server.run();
				assert(sentPacket.getData()[0] == 1);
				assert(sentPacket.getAddress() == address);
				System.out.println("Case 6a successful");
		
	}

}