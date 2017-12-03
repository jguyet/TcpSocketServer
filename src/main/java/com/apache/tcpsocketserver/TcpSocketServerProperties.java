package com.apache.tcpsocketserver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * TcpSocketServerProperties class
 * @author jguyet
 */
public class TcpSocketServerProperties {
	
	/**
	 * queue propeties
	 */
	public int		NumberOfThreadReader = 10;
	public int		NumberOfThreadTasker = 10;
	
	/**
	 * asynchronous mode
	 */
	public boolean	Asynchronous = false;
	
	/**
	 * Default contructor
	 */
	public TcpSocketServerProperties() {
	}
	
	/**
	 * Constructor by propeties fileName
	 * @param filename
	 */
	public TcpSocketServerProperties(String filename) {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(filename);

			// load a properties file
			prop.load(input);
			
			for (Object okey : prop.keySet()) {
				String key = (String)okey;
				
				switch (key.toUpperCase()) {
					case "NUMBEROFTHREADREADER":
						this.NumberOfThreadReader = Integer.parseInt(prop.getProperty(key, this.NumberOfThreadReader + ""));
						break ;
					case "NUMBEROFTHREADTASKER":
						this.NumberOfThreadTasker = Integer.parseInt(prop.getProperty(key, this.NumberOfThreadTasker + ""));
						break ;
					case "ASYNCHRONOUS":
						if (prop.getProperty(key, "false").toUpperCase().equalsIgnoreCase("TRUE"))
							this.Asynchronous = true;
						else
							this.Asynchronous = false;
						break ;
				}
				
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * method for save properties on hardware
	 * @param filename
	 * @return
	 */
	public TcpSocketServerProperties save(String filename) {
		Properties prop = new Properties();
		OutputStream output = null;

		try {

			output = new FileOutputStream(filename);

			// set the properties value
			prop.setProperty("NumberOfThreadReader", this.NumberOfThreadReader + "");
			prop.setProperty("NumberOfThreadTasker", this.NumberOfThreadTasker + "");
			prop.setProperty("Asynchronous", this.Asynchronous ? "true" : "false");

			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return this;
	}
}
