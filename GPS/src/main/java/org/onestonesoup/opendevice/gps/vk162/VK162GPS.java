package org.onestonesoup.opendevice.gps.vk162;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.GPS;

public class VK162GPS implements GPS, Runnable {

	private static final String DEFAULT_ALIAS = "VK162GPS";
	private String alias = DEFAULT_ALIAS;

	private Connection connection;
	private InputStream inputStream;
	private boolean running = false;
	private long time;

	private double latitude;
	private double longitude;
	private boolean fix = false;
	private double accuracy;
	private boolean fix3D;
	private int satellites;
	private double hdop;
	private double altitude;
	private int quality;
	private boolean debug=false;


	public long getTime() {
		return time;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public VK162GPS(Connection connection) throws Exception {
		this.connection = connection;
		System.out.println("GPS connecting to "+connection);
		connection.connect();


		System.out.println("GPS getting input stream");
		inputStream = connection.getInputStream();


		System.out.println("GPS starting thread");
		new Thread(this,alias).start();
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}

	public void run() {
		if (running) {
			System.out.println("GPS thread already running");
			return;
		}
		running = true;

		System.out.println("Started GPS thread");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		try {
			String line = reader.readLine(); // Dump first line as may not be complete
			System.out.println("GPS dumped first line "+line);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (running) {
			try {
				// http://aprs.gids.nl/nmea/#rmc
				String record = reader.readLine();
				/*if(debug) {
					System.out.println("GPS record :"+record);
				}*/
				if (record.startsWith("$GPRMC")) {
					// parseRMC();
				} else if (record.startsWith("$GPRVTG")) {
					// parseVTG();
				} else if (record.startsWith("$GPGGA")) {
					parseGGA(record);
				} else if (record.startsWith("$GPGSA")) {
					// parseGPGSA();
				} else if (record.startsWith("$GPGSV")) {
					// parseGSV();
				} else if (record.startsWith("$GPGLL")) {
					//parseGLL(record);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("GPS thread stopped");
	}

	private void parseGLL(String record) {
		record = record.substring(0, record.lastIndexOf("*"));
		String[] parts = record.split(",");
		
		time = System.currentTimeMillis();
		if(parts.length<3 || parts[1].length()==0 || parts[3].length()==0) {
			fix = false;
		}

		latitude = Long.parseLong(parts[1]);
		longitude = Long.parseLong(parts[3]);
		fix = true;
	}
	
	private void parseGGA(String record) {
		/**
		 * Name	Example Data	Description
			Sentence Identifier	$GPGGA	Global Positioning System Fix Data
			Time	170834	17:08:34 Z
			Latitude	4124.8963, N	41d 24.8963' N or 41d 24' 54" N
			Longitude	08151.6838, W	81d 51.6838' W or 81d 51' 41" W
			Fix Quality:
			- 0 = Invalid
			- 1 = GPS fix
			- 2 = DGPS fix	1	Data is from a GPS fix
			Number of Satellites	05	5 Satellites are in view
			Horizontal Dilution of Precision (HDOP)	1.5	Relative accuracy of horizontal position
			Altitude	280.2, M	280.2 meters above mean sea level
			Height of geoid above WGS84 ellipsoid	-34.0, M	-34.0 meters
			Time since last DGPS update	blank	No last update
			DGPS reference station id	blank	No station id
			Checksum	*75	Used by program to check for transmission errors
		 */
		/*
		 * 0 GPGGA
		 * 1 131929.00
		 * 2 5115.31222
		 * 3 N
		 * 4 00123.51558
		 * 5 W
		 * 6 1
		 * 7 04
		 * 8 2.12
		 * 9 136.1
		 * 10 M
		 * 11 47.1
		 * 12 M
		 * 13
		 * 14 *41
		 */

		System.out.println("GPS GGA record :"+record);
		
		record = record.substring(0, record.lastIndexOf("*"));
		String[] parts = record.split(",");
		
		time = System.currentTimeMillis();
		if(parts.length<3 || parts[1].length()==0 || parts[3].length()==0) {
			fix = false;
			return;
		}

		//parts[1]; //time
		latitude = parseLatitudeDouble(parts[2],parts[3]);
		longitude = parseLongitudeDouble(parts[4],parts[5]);
		quality = Integer.parseInt(parts[6]); // quality
		satellites = Integer.parseInt(parts[7]); // satelites
		hdop = Double.parseDouble(parts[8]); // hdop
		altitude = Double.parseDouble(parts[9]); // altitude
		//height = parts[11]; // height
		
		System.out.println("Quality:"+quality);
		if(quality>0) {
			fix = true;
		} else {
			fix = false;
		}
	}

	private double parseLatitudeDouble(String record,String direction) {
		double degrees = Double.parseDouble(record.substring(0,2));
		double minutes = Double.parseDouble(record.substring(2));
		degrees += (minutes/60);
		
		if(direction.equals("S")) {
			degrees = -degrees;
		}
		
		return degrees;
	}
	
	private double parseLongitudeDouble(String record,String direction) {
		double degrees = Double.parseDouble(record.substring(0,3));
		double minutes = Double.parseDouble(record.substring(3));
		degrees += (minutes/60);
		
		if(direction.equals("W")) {
			degrees = -degrees;
		}
		
		return degrees;
	}
	
	private double parseLongitudeDouble(String record) {
		//Only works for UK
		double degrees = Double.parseDouble(record.substring(0,2));
		double minutes = Double.parseDouble(record.substring(2));
		degrees += (minutes/60);
		
		return degrees;
	}
	
	public int getSatellites() {
		return satellites;
	}

	public int getQuality() {
		return quality;
	}

	public boolean hasFix() {
		return fix;
	}
	
	public boolean has3DFix() {
		return fix3D;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public double getAltitude() {
		return altitude;
	}

	public Device getParent() {
		return null;
	}

	public boolean hasParent() {
		return false;
	}

	public void setParameter(String key, String value) {
		
	}

	public String getParameter(String key) {
		return null;
	}

	public void kill() {
		
	}

	public void setDebug(boolean state) {
		debug = state;
	}

}
