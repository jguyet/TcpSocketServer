package com.apache.tcpsocketserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ThreadTaskerQueue class
 * @author jguyet
 */
public class ThreadTaskerQueue {
	/**
	 * Unique threadReader ID
	 */
	private static long				threadTaskerUID = 0L;
	
	/**
	 * Vars
	 */
	private TcpSocketServer	server;
	private Map<Long, ThreadTasker> taskers = new HashMap<Long, ThreadTasker>();
	
	/**
	 * ThreadTaskerQueue constructor
	 * @param server
	 */
	public ThreadTaskerQueue(TcpSocketServer server) {
		this.server = server;
		
		for (int i =0; i < server.getProperties().NumberOfThreadTasker; i++) {
			ThreadTasker tt = new ThreadTasker();
			tt.setRunning(true);
			
			this.taskers.put(tt.getId(), tt);
		}
	}
	
	/**
	 * Add one client on queue
	 * @param client
	 */
	public void addClient(TcpClient client) {
		ThreadTasker tr = this.taskers.get((long) client.getId() % (this.taskers.size() - 1));
		
		tr.addClient(client);
	}
	
	/**
	 * remove client if one tasker contain client
	 * @param client
	 */
	@SuppressWarnings("deprecation")
	public void removeClient(TcpClient client) {
		if (taskers.containsKey(client.onTaskerId()))
			taskers.get(client.onTaskerId()).removeClient(client);
	}
	
	public class ThreadTasker implements Runnable {

		private long					id;
		private Thread					_t;
		private boolean					isOver;
		private boolean					isEmpty;
		private boolean					running;
		private ArrayList<TcpClient>	clients = new ArrayList<TcpClient>();
		
		public ThreadTasker() {
			this._t = new Thread(this);
			this.isEmpty = true;
			this.isOver = false;
			
			this.id = ThreadTaskerQueue.threadTaskerUID++;
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
			client.setOnTaskerId(this.id);
			this.clients.add(client);
		}
		
		@SuppressWarnings("deprecation")
		public void removeClient(TcpClient client) {
			client.setOnTaskerId(-1L);
			this.clients.remove(client);
		}

		@SuppressWarnings("deprecation")
		public void run() {
			
			while (running) {
				ArrayList<TcpClient> lst = new ArrayList<TcpClient>(this.clients);
				
				for (TcpClient client : lst) {
					try {
						server.onMessageReceiveClient(client, client.getLastMessage());
						client.setLastMessage(null);
					} catch (Exception e) {
						server.onDisconnectClient(client);
						server.removeClient(client);
						continue ;
					}
					/**
					 * After finish Task
					 */
					this.removeClient(client);
					server.addClientToReaderQueue(client);
				}
				
				try {Thread.sleep(10); } catch (Exception e) {}
			}
			
		}
	}
}
