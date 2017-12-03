package com.apache.tcpsocketserver;

import java.nio.ByteBuffer;

/**
 * Interface Handler ITcpSocketServerHandler
 * @author jguyet
 */
public interface ITcpSocketServerHandler {

	/**
	 * Called on accept new connection
	 * @param client
	 */
	public void onAccept(TcpClient client);
	
	/**
	 * Called on new message readed
	 * @param client
	 * @param message
	 */
	public void onMessage(TcpClient client, ByteBuffer message);
	
	/**
	 * Called on Disconnect socket connection
	 * @param client
	 */
	public void onDisconnect(TcpClient client);
}
