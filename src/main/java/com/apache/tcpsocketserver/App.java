package com.apache.tcpsocketserver;

import java.io.IOException;
import java.nio.ByteBuffer;

public class App implements ITcpSocketServerHandler
{
	
	@SuppressWarnings("unused")
	public App() {
	
		TcpSocketServer server = null;
		
		/**
		 * Properties
		 */
		TcpSocketServerProperties properties = new TcpSocketServerProperties();
		properties.Asynchronous = false;
		properties.NumberOfThreadReader = 10;
		properties.NumberOfThreadTasker = 10;
		properties.save("TcpSocketServerProperties.properties");
		
		/**
		 * Open socket server on port 5000 with properties conf.
		 * BindException if already address used
		 */
    	try {
    		server = new TcpSocketServer(5000, this, properties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static void main( String[] args )
    {
    	new App();
    }

    @Override
	public void onAccept(TcpClient client) {
		System.out.println("On Accept (" + client.getId() + ")");
	}

	@Override
	public void onMessage(TcpClient client, ByteBuffer message) {
		// TODO Auto-generated method stub
		System.out.println("On Message (" + client.getId() + ")");
		
		/*byte[] b = new byte[message.capacity()];
		message.get(b, 0, message.capacity());
		
		String s = new String(b);
		System.out.println(s);*/
	}

	@Override
	public void onDisconnect(TcpClient client) {
		// TODO Auto-generated method stub
		System.out.println("On Disconnect (" + client.getId() + ")");
	}
}
