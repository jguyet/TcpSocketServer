package com.apache.tcpsocketserver.exceptions;

public class ClientDisconnectedException extends Exception {

	/**
	 * SERIAL ID
	 */
	private static final long serialVersionUID = 1L;

	public ClientDisconnectedException() {
		super("Client disconnected");
	}
}
