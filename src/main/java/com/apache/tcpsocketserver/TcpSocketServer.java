package com.apache.tcpsocketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * TcpSocketServer class
 * @author jguyet
 */
public class TcpSocketServer implements Runnable {
	
	/**
	 * Vars
	 */
	private int							port;
	private ITcpSocketServerHandler		handler;
	private Thread				 		thread;
	private ServerSocket				server;
	private boolean						running;
	private ArrayList<TcpClient>		clients = new ArrayList<TcpClient>();
	/**
	 * Queues
	 */
	private ThreadReaderQueue			readerQueue;
	private ThreadTaskerQueue			taskerQueue;
	/**
	 * Properties
	 */
	private TcpSocketServerProperties	properties;

	/**
	 * TcpSocketServer contructor
	 * @param port
	 * @param handler
	 * @param properties
	 * @throws IOException
	 */
	public TcpSocketServer(int port, ITcpSocketServerHandler handler, TcpSocketServerProperties properties) throws IOException {
		this.port = port;
		this.handler = handler;
		this.properties = properties == null ? new TcpSocketServerProperties() : properties;
		this.running = true;
		
		this.readerQueue = new ThreadReaderQueue(this);
		this.taskerQueue = new ThreadTaskerQueue(this);
		
		this.server = new ServerSocket(this.port);
		
		if (this.properties.Asynchronous == true) {
			this.thread = new Thread(this);
			this.thread.setPriority(Thread.MAX_PRIORITY);
			this.thread.start();
		} else {
			run();
		}
	}
	
	/**
	 * return boolean true if server is running state
	 * @return
	 */
	public boolean serverIsRunning() {
		return this.running;
	}
	
	/**
	 * stop server if this is in running state else start on asynchronized mode
	 * @param running
	 */
	@SuppressWarnings("deprecation")
	public void setServerRunning(boolean running) {
		if (this.running == false) {
			this.thread.start();
		} else {
			this.thread.stop();
		}
		this.running = running;
	}
	
	/**
	 * restart this
	 */
	public void restart() {
		setServerRunning(false);
		setServerRunning(true);
	}
	
	/**
	 * getter port
	 * @return int
	 */
	public int getPort() {
		return port;
	}

	/**
	 * runnable method
	 */
	public void run() {
		while (this.running) {
			acceptClient();
		}
	}
	
	/**
	 * while on server.accept();
	 */
	private void acceptClient() {
		try {
			while (this.running) {
				TcpClient client = new TcpClient(server.accept());
				
				/**
				 * Add new client to list
				 */
				this.addClient(client);
				/**
				 * Call onAccept method on handler
				 */
				this.onAcceptClient(client);
				/**
				 * Add to readerQueue
				 */
				this.addClientToReaderQueue(client);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * getter of arrayList Tcp clients
	 * @return ArrayList<TcpClient>
	 */
	public ArrayList<TcpClient> getClients() {
		return clients;
	}

	/**
	 * setter of arrayList Tcp clients
	 * @param clients ArrayList<TcpClient>
	 */
	public void setClients(ArrayList<TcpClient> clients) {
		this.clients = clients;
	}

	/**
	 * Add on client on arraylist<TcpClient>
	 * @param client TcpClient
	 */
	public void addClient(TcpClient client) {
		this.clients.add(client);
	}
	
	/**
	 * remove once TcpClient
	 * @param client TcpClient
	 */
	@SuppressWarnings("deprecation")
	public void removeClient(TcpClient client) {
		this.clients.remove(client);
		if (client.onReaderId() != -1L)
			this.readerQueue.removeClient(client);
		if (client.onTaskerId() != -1L)
			this.taskerQueue.removeClient(client);
	}
	
	/**
	 * call handler onAccept method
	 * @param client
	 */
	public void onAcceptClient(TcpClient client) {
		this.handler.onAccept(client);
	}
	
	/**
	 * Call handler onMessage method
	 * @param client
	 * @param message
	 */
	public void onMessageReceiveClient(TcpClient client, ByteBuffer message) {
		this.handler.onMessage(client, message);
	}
	
	/**
	 * Call handler onDisconnect method
	 * @param client
	 */
	public void onDisconnectClient(TcpClient client) {
		this.handler.onDisconnect(client);
	}
	
	/**
	 * Add one client on reader queue
	 * @param client
	 */
	public void addClientToReaderQueue(TcpClient client) {
		this.readerQueue.addClient(client);
	}
	
	/**
	 * Add one client on tasker queue
	 * @param client
	 */
	public void addClientToTaskQueue(TcpClient client) {
		this.taskerQueue.addClient(client);
	}
	
	/**
	 * getter of server properties
	 * @return
	 */
	public TcpSocketServerProperties getProperties() {
		return this.properties;
	}
	
	/**
	 * To string method for watch all content of queues
	 * @return
	 */
	public String getInformations() {
		StringBuilder str = new StringBuilder();
		
		str.append(this.readerQueue.getInformation());
		return str.toString();
	}
}
