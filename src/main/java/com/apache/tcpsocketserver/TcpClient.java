package com.apache.tcpsocketserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.apache.tcpsocketserver.exceptions.ClientDisconnectedException;

/**
 * TcpClient class
 * @author jguyet
 */
public class TcpClient {

	/**
	 * Unique User ID
	 */
	private static long				uid = 0L;
	
	/**
	 * Vars
	 */
	private long					id;
	private Socket					socket;
	
	/**
	 * Constructor
	 * @param socket
	 */
	public TcpClient(Socket socket) {
		this.socket = socket;
		this.id = TcpClient.uid++;
	}

	/**
	 * return unique ID
	 * @return long
	 */
	public long getId() {
		return id;
	}

	/**
	 * return inet Socket
	 * @return Socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Open an InputStream
	 * @return InputStream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		return this.socket.getInputStream();
	}
	
	/**
	 * Open an OutputStream
	 * @return OutputStream
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException {
		return this.socket.getOutputStream();
	}
	
	/**
	 * return InetAddress
	 * @return InetAddress
	 */
	public InetAddress getInetAddress() {
		return this.socket.getInetAddress();
	}
	
	private byte[]			splittedBuffer = new byte[0];
	
	/**
	 * Return available byte readable
	 * @return
	 * @throws IOException
	 * @throws ClientDisconnectedException
	 */
	public int available() throws IOException, ClientDisconnectedException {
		byte[] bufferChecker = new byte[1];
		int ret = this.socket.getInputStream().read(bufferChecker, 0, 1);
		
		if (ret == -1) {
			throw new ClientDisconnectedException();
		}
		/**
		 * Copy byte readed to splitter
		 */
		byte[] newsplittedBuffer = new byte[bufferChecker.length + splittedBuffer.length];
		System.arraycopy(splittedBuffer, 0, newsplittedBuffer, 0, splittedBuffer.length);
		System.arraycopy(bufferChecker, 0, newsplittedBuffer, splittedBuffer.length, 1);
		splittedBuffer = newsplittedBuffer;
		
		return this.socket.getInputStream().available();
	}
	
	/**
	 * Read byte of param size
	 * @param size
	 * @return
	 * @throws IOException
	 * @throws ClientDisconnectedException
	 */
	public ByteBuffer read(int size) throws IOException, ClientDisconnectedException {
		byte[] buffer = new byte[size + splittedBuffer.length];
		int offset = splittedBuffer.length;
		if (splittedBuffer.length > 0) {
			System.arraycopy(splittedBuffer, 0, buffer, 0, splittedBuffer.length);
			splittedBuffer = new byte[0];
		}
		
		int readed = this.socket.getInputStream().read(buffer, offset, size);
		
		if (readed == -1) {
			throw new ClientDisconnectedException();
		}
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, readed + offset);
		return byteBuffer;
	}
	
	//######################################################################
	//RESERVED BY LIBRARY TCPSOCKETSERVER --------------------------------->
	//######################################################################

	private long					onReaderId = -1;
	private long					onTaskerId = -1;
	
	@Deprecated()
	public long onReaderId() {
		return onReaderId;
	}
	
	@Deprecated()
	public void setonReader(long readerId) {
		this.onReaderId = readerId;
	}
	
	@Deprecated()
	public long onTaskerId() {
		return onTaskerId;
	}
	
	@Deprecated()
	public void setOnTaskerId(long taskerId) {
		this.onTaskerId = taskerId;
	}
	
	private ByteBuffer				lastMessage;
	
	@Deprecated()
	public void setLastMessage(ByteBuffer lastMessage) {
		this.lastMessage = lastMessage;
	}
	
	@Deprecated()
	public ByteBuffer getLastMessage() {
		return lastMessage;
	}
	
	//######################################################################
	//END RECERVED SECTION <------------------------------------------------
	//######################################################################
}
