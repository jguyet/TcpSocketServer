package com.apache.tcpsocketserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.apache.tcpsocketserver.exceptions.ClientDisconnectedException;

/**
 * ThreadReaderQueue class
 * @author jguyet
 */
public class ThreadReaderQueue {
	/**
	 * Unique threadReader ID
	 */
	private static long				threadReaderUID = 0L;

	/**
	 * Vars
	 */
	private TcpSocketServer			server;
	private Map<Long, ThreadReader>	readers = new HashMap<Long, ThreadReader>();
	
	/**
	 * ThreadReaderQueue constructor
	 * @param server
	 */
	public ThreadReaderQueue(TcpSocketServer server) {
		this.server = server;
		
		for (int i = 0; i < server.getProperties().NumberOfThreadReader; i++) {
			ThreadReader tr = new ThreadReader();
			tr.setRunning(true);
			
			this.readers.put(tr.getId(), tr);
		}
	}
	
	/**
	 * Add one client on queue
	 * @param client
	 */
	public void addClient(TcpClient client) {
		ThreadReader tr = this.readers.get((long) client.getId() % (this.readers.size() - 1));
		
		tr.addClient(client);
	}
	
	/**
	 * remove client if one reader contain client
	 * @param client
	 */
	@SuppressWarnings("deprecation")
	public void removeClient(TcpClient client) {
		if (this.readers.containsKey(client.onReaderId()))
			this.readers.get(client.onReaderId()).removeClient(client);
	}
	
	/**
	 * information to string
	 * @return
	 */
	public String getInformation() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("ThreadReader :\n");
		
		for (ThreadReader tr : this.readers.values()) {
			builder.append("ThreadReader(").append(tr.getId()).append(")")
			.append(" size(").append(tr.getClients().size()).append(")\n");
		}
		return builder.toString();
	}
	
	public class ThreadReader implements Runnable {
		
		private long					id;
		private Thread					_t;
		private boolean					isOver;
		private boolean					isEmpty;
		private boolean					running;
		private ArrayList<TcpClient>	clients = new ArrayList<TcpClient>();
		
		public ThreadReader() {
			this._t = new Thread(this);
			this.isEmpty = true;
			this.isOver = false;
			
			this.id = ThreadReaderQueue.threadReaderUID++;
		}
		
		public long getId() {
			return id;
		}

		public boolean isOver() {
			return isOver;
		}

		public void setOver(boolean isOver) {
			this.isOver = isOver;
		}

		public boolean isEmpty() {
			return isEmpty;
		}

		public void setEmpty(boolean isEmpty) {
			this.isEmpty = isEmpty;
		}
		
		public boolean isRunning() {
			return running;
		}

		public void setRunning(boolean running) {
			this.running = running;
			if (this.running == false)
				return ;
			this._t.setPriority(Thread.NORM_PRIORITY);
			this._t.start();
		}

		public ArrayList<TcpClient> getClients() {
			return clients;
		}

		public void setClients(ArrayList<TcpClient> clients) {
			this.clients = clients;
		}
		
		@SuppressWarnings("deprecation")
		public void addClient(TcpClient client) {
			client.setonReader(this.id);
			this.clients.add(client);
		}
		
		@SuppressWarnings("deprecation")
		public void removeClient(TcpClient client) {
			client.setonReader(-1L);
			this.clients.remove(client);
		}

		@SuppressWarnings("deprecation")
		public void run() {
			
			while (running) {
				ArrayList<TcpClient> lst = new ArrayList<TcpClient>(this.clients);
				
				for (TcpClient client : lst) {
					try {
						int available = 0;
						
						if ((available = client.available()) >= 0) {
							ByteBuffer buffer = client.read(available);
							
							if (buffer.capacity() > 0) {
								client.setLastMessage(buffer);
								server.addClientToTaskQueue(client);
								/**
								 * After finish Read
								 */
								this.removeClient(client);
							}
						}
					} catch (IOException | ClientDisconnectedException e) {
						server.onDisconnectClient(client);
						server.removeClient(client);
						continue ;
					}
				}
				
				try {Thread.sleep(10); } catch (Exception e) {}
			}
			
		}
	}
}
