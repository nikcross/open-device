package org.onestonesoup.opendevice.weatherstation;

import gnu.io.RXTXPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.onestonesoup.core.constants.TimeConstants;
import org.onestonesoup.core.data.EntityTree;
import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Logger;
import org.onestonesoup.opendevice.comms.RS232Driver;

// Notes
// http://www.lavrsen.dk/foswiki/bin/view/Open2300/OpenWSAPI

public class WS2350 implements Runnable,Logger {
	private static final String DEFAULT_ALIAS="WS2350 Weather Station";
	private String alias = DEFAULT_ALIAS;
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}
	private Map<String,String> parameters = new HashMap<String,String>();
	public String getAlias() {
		return alias;
	}
	public String getParameter(String key) {
		return parameters.get(key);
	}
	public void setAlias(String alias) {
		this.alias=alias;
	}
	public void setParameter(String key, String value) {
		parameters.put(key,value);
	}
	
	public static boolean testPort(RS232Driver connection)
	{
		try{
	//create connection
			connection.setBaud(2400);
			connection.connect();
			RXTXPort port = ((RS232Driver)connection).getPort();
			port.setRTS(true);
			port.setDTR(false);
	
	//create in and out streams
			InputStream in = connection.getInputStream();
			OutputStream out = connection.getOutputStream();		
		for(int loop=0;loop<5;loop++)
		{
			System.out.println("Connect attempt "+loop);
	//clear in stream
				long time = System.currentTimeMillis()+1000;
				
				out.write( 0x06 );
				out.flush();
				try{Thread.sleep(50);}catch(Exception e){}
				
				System.out.println("Clearing input stream");
				while( in.available()>0 && System.currentTimeMillis()<time )
				{
					int i = in.read();
					System.out.println("Dumped:"+i);
				}
				System.out.println("Cleared input stream");
				
		//Test for initialise response
	
				System.out.println("Initialising Data");
				
				out.write( 0x06 );
				out.flush();
				try{Thread.sleep(50);}catch(Exception e){}
				
				int testValue = -1;
				
				if(in.available()>0)
				{	
					testValue = in.read();
					System.out.println("Reset response:"+testValue);
				}
				else
				{
					System.out.println("No Reset Response Sent");
				}
				if(testValue==0x02)
				{
					System.out.println("Initialised Data");
					return true;
				}
				else
				{
					System.out.println("Failed To Initialise Data");
					
					try{Thread.sleep(100);}catch(Exception e){}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public class WeatherLogEntry
	{
		private double indoorHumidity;
		private double insideTemperature;
		private double outdoorHumidity;
		private double outsideTemperature;
		private double rain1Hour;
		private double rain24Hours;
		private double relativeAirPressure;
		private double windDirection;
		private double windSpeed;
		public double getIndoorHumidity() {
			return indoorHumidity;
		}
		public void setIndoorHumidity(double indoorHumidity) {
			this.indoorHumidity = indoorHumidity;
		}
		public double getInsideTemperature() {
			return insideTemperature;
		}
		public void setInsideTemperature(double insideTemperature) {
			this.insideTemperature = insideTemperature;
		}
		public double getOutdoorHumidity() {
			return outdoorHumidity;
		}
		public void setOutdoorHumidity(double outdoorHumidity) {
			this.outdoorHumidity = outdoorHumidity;
		}
		public double getOutsideTemperature() {
			return outsideTemperature;
		}
		public void setOutsideTemperature(double outsideTemperature) {
			this.outsideTemperature = outsideTemperature;
		}
		public double getRain1Hour() {
			return rain1Hour;
		}
		public void setRain1Hour(double rain1Hour) {
			this.rain1Hour = rain1Hour;
		}
		public double getRain24Hours() {
			return rain24Hours;
		}
		public void setRain24Hours(double rain24Hours) {
			this.rain24Hours = rain24Hours;
		}
		public double getRelativeAirPressure() {
			return relativeAirPressure;
		}
		public void setRelativeAirPressure(double relativeAirPressure) {
			this.relativeAirPressure = relativeAirPressure;
		}
		public double getWindDirection() {
			return windDirection;
		}
		public void setWindDirection(double windDirection) {
			this.windDirection = windDirection;
		}
		public double getWindSpeed() {
			return windSpeed;
		}
		public void setWindSpeed(double windSpeed) {
			this.windSpeed = windSpeed;
		}
		
		
	}
	
	private long pollTime = 30000; // 30 seconds
	private boolean debug = true;
	private boolean running=false;
	private Connection connection;
	private InputStream in = null;
	private OutputStream out = null;
	
	public WS2350(Connection connection) throws Exception
	{
		this.connection = connection;
		
		if(connection instanceof RS232Driver)
		{
			((RS232Driver)connection).setBaud(2400);
			connection.connect();
			RXTXPort port = ((RS232Driver)connection).getPort();
			port.setRTS(true);
			port.setDTR(false);
		}
		in = connection.getInputStream();
		out = connection.getOutputStream();
		
		start();
	}
	
	public void start()
	{
		new Thread(this,"WS2350 Weather Station").start();
	}
	
	private boolean initialiseData() throws Exception
	{
		clearInputStream();

		showDebug("Initialising Data");
		writeByte(0x06);
		int testValue = readByte();
		showDebug("Reset response:"+testValue);
		if(testValue==0x02)
		{
			showDebug("Initialised Data");
			return true;
		}
		else
		{
			showDebug("Failed To Initialise Data");
			clearInputStream();
			
			try{Thread.sleep(100);}catch(Exception e){}
			return false;
		}
	}
	
	private void clearInputStream() throws Exception
	{
		long time = System.currentTimeMillis()+1000;
		writeByte(0x06);
		
		showDebug("Clearing input stream");
		while( in.available()>0 && System.currentTimeMillis()<time )
		{
			int i = in.read();
			showDebug("Dumped:"+i);
		}
		showDebug("Cleared input stream");
	}
	
	private static final int[] READ_OUTSIDE_TEMPERATURE = 
	  new int[]{0x82,0x8E,0x9E,0x8E};
	
	private void readOutsideTemperature() throws Exception
	{
		//Address 0373
		
		if(
			sendCommand( READ_OUTSIDE_TEMPERATURE )==false
		)
		{
			throw new IOException("READ_OUTSIDE_TEMPERATURE Command Failed");

		}
			
		//Four BCD Nibbles
		// divide by 100 subtract 30
		
		writeByte( 0xCA );
		
		int header = readByte();
		resetChecksum();
		String partB = Integer.toHexString(readByte());
		String partA = Integer.toHexString(readByte());
		checkSum(header);
		
		if(partA.length()==1){ partA = "0"+partA; }
		if(partB.length()==1){ partB = "0"+partB; }
		
		String decString = partA+partB;
		
		showDebug("Dec:"+decString);
		
		int rawData = Integer.parseInt(decString);
		
		logEntry.outsideTemperature = ((double)rawData/100.0)-30;
		showDebug("Outside Temperature:"+logEntry.outsideTemperature);
	}

	private static final int[] READ_INSIDE_TEMPERATURE = 
		  new int[]{0x82,0x8E,0x92,0x9A};
		
		private void readInsideTemperature() throws Exception
		{
			//Address 0346
			
			if(
				sendCommand( READ_INSIDE_TEMPERATURE )==false
			)
			{
				throw new IOException("READ_INSIDE_TEMPERATURE Command Failed");
			}
				
			//Four BCD Nibbles
			// divide by 100 subtract 30
			
			writeByte( 0xCA );
			
			int header = readByte();
			resetChecksum();
			String partB = Integer.toHexString(readByte());
			String partA = Integer.toHexString(readByte());
			checkSum(header);
			
			if(partA.length()==1){ partA = "0"+partA; }
			if(partB.length()==1){ partB = "0"+partB; }
			
			String decString = partA+partB;
			
			showDebug("Dec:"+decString);
			
			int rawData = Integer.parseInt(decString);
			
			logEntry.insideTemperature = ((double)rawData/100.0)-30;
			showDebug("Inside Temperature:"+logEntry.insideTemperature);
		}	
	
	private static final int[] READ_RAIN_1_HOUR = 
		  new int[]{0x82,0x92,0xAE,0x92};	
	private void readRainInLastHour() throws Exception
	{
		//Address 04B4
		if(
				sendCommand( READ_RAIN_1_HOUR )==false
		)
		{
			throw new IOException("READ_RAIN_1_HOUR Command Failed");
		}
		
		//6 Nibbles bcd
		writeByte( 0xCE );
		
		int header = readByte();
		resetChecksum();
		String partC = Integer.toHexString(readByte());
		String partB = Integer.toHexString(readByte());
		String partA = Integer.toHexString(readByte());
		checkSum(header);
		
		if(partA.length()==1){ partA = "0"+partA; }
		if(partB.length()==1){ partB = "0"+partB; }
		if(partC.length()==1){ partC = "0"+partC; }
		
		String decString = (partA+partB+partC);
		
		showDebug("Dec:"+decString);
		
		int rawData = Integer.parseInt(decString);
		
		// divide by 100
		logEntry.rain1Hour = ((double)rawData/100);
		showDebug("Rain in last hour:"+logEntry.rain1Hour);
	}
	
	private static final int[] READ_RAIN_24_HOURS = 
		  new int[]{0x82,0x92,0xA6,0x9E};		
	private void readRainInLast24Hours() throws Exception
	{
		//Address 0497
		if(
				sendCommand( READ_RAIN_24_HOURS )==false
		)
		{
			throw new IOException("READ_RAIN_24_HOURS Command Failed");

		}
		
		//6 Nibbles bcd
		writeByte( 0xCE );
		
		int header = readByte();
		resetChecksum();
		String partC = Integer.toHexString(readByte());
		String partB = Integer.toHexString(readByte());
		String partA = Integer.toHexString(readByte());
		checkSum(header);
		
		if(partA.length()==1){ partA = "0"+partA; }
		if(partB.length()==1){ partB = "0"+partB; }
		if(partC.length()==1){ partC = "0"+partC; }
		
		String decString = (partA+partB+partC);
		
		showDebug("Dec:"+decString);
		
		int rawData = Integer.parseInt(decString);
		
		// divide by 100
		logEntry.rain24Hours = ((double)rawData/100);
		showDebug("Rain in last 24 hours:"+logEntry.rain24Hours);
	}
	
	private static final int[] READ_INDOOR_HUMIDITY = 
		  new int[]{0x82,0x8E,0xBE,0xAE};
	private void readInsideHumidity() throws Exception
	{
		//Address 03FB
		if(
				sendCommand( READ_INDOOR_HUMIDITY )==false
		)
		{
			throw new IOException("READ_INDOOR_HUMIDITY Command Failed");
		}			
		
		//Nibbles 2
		writeByte( 0xC6 );
		
		int header = readByte();
		resetChecksum();
		String decString = Integer.toHexString(readByte());
		checkSum(header);
		
		if(decString.length()==1){ decString = "0"+decString; }
		
		showDebug("Dec:"+decString);
		
		int rawData = Integer.parseInt(decString);
		
		logEntry.indoorHumidity = ((double)rawData);
		showDebug("Indoor Humidity:"+logEntry.indoorHumidity);
	}
	
	private static final int[] READ_OUTDOOR_HUMIDITY = 
		  new int[]{0x82,0x92,0x86,0xA6};
	private void readOutsideHumidity() throws Exception
	{
		//Address 0419
		if(
				sendCommand( READ_OUTDOOR_HUMIDITY )==false
		)
		{
			throw new IOException("READ_OUTDOOR_HUMIDITY Command Failed");
		}			
		
		//Nibbles 2
		writeByte( 0xC6 );
		
		int header = readByte();
		resetChecksum();
		String decString = Integer.toHexString(readByte());
		checkSum(header);
		
		if(decString.length()==1){ decString = "0"+decString; }
		
		showDebug("Dec:"+decString);
		
		int rawData = Integer.parseInt(decString);
		
		logEntry.outdoorHumidity = ((double)rawData);
		showDebug("Outdoor Humidity:"+logEntry.outdoorHumidity);		
	}	
	
	private static final int[] READ_RELATIVE_AIR_PRESSURE = 
		  new int[]{0x82,0x96,0xBA,0x8A};
	private void readRelativeAirPresure() throws Exception
	{
		//Address 05D8
		//Address 05E2 (relative)
		
		if(
				sendCommand( READ_RELATIVE_AIR_PRESSURE )==false
		)
		{
			throw new IOException("READ_RELATIVE_AIR_PRESSURE Command Failed");
		}	
		
		//Five BCD Nibbles
		
		writeByte( 0xCE );
		
		int header = readByte();
		resetChecksum();
		String partC = Integer.toHexString(readByte());
		String partB = Integer.toHexString(readByte());
		String partA = Integer.toHexString(readByte());
		checkSum(header);
		
		if(partA.length()==1){ partA = "0"+partA; }
		if(partB.length()==1){ partB = "0"+partB; }
		if(partC.length()==1){ partC = "0"+partC; }
		
		String decString = (partA+partB+partC).substring(1);
		
		showDebug("Dec:"+decString);
		
		int rawData = Integer.parseInt(decString);
		
		// divide by 10
		logEntry.relativeAirPressure = ((double)rawData/10.0);
		showDebug("Relative Air Pressure:"+logEntry.relativeAirPressure);
	}
	
	private static final int[] READ_WIND_SPEED = 
		  new int[]{0x82,0x96,0x8A,0x9E};
	
	private void readWindSpeedAndDirection() throws Exception
	{
		//Address 0527
		
		if(
				sendCommand( READ_WIND_SPEED )==false
		)
		{
			throw new IOException("READ_WIND_SPEED Command Failed");
		}		
		
		//Six Nibbles
		writeByte( 0xCE );
		
		int header = readByte();
		resetChecksum();
		String partC = Integer.toHexString(readByte());
		String partB = Integer.toHexString(readByte());
		String partA = Integer.toHexString(readByte());
		checkSum(header);
		
		if(partA.length()==1){ partA = "0"+partA; }
		if(partB.length()==1){ partB = "0"+partB; }
		if(partC.length()==1){ partC = "0"+partC; }
		
		String hexString = partA+partB+partC;
		
		showDebug("Hex:"+hexString);
		
		int rawData = Integer.parseInt(hexString.substring(1,4),16);		
		
		//Nibbles 2-5 =  speed
		// divide by 10
		logEntry.windSpeed = (double)rawData/10;
		showDebug("Wind Speed:"+logEntry.windSpeed);
		
		//Nibble 6 = direction
		// multiply by 22.5
		logEntry.windDirection = ((double)Integer.parseInt(hexString.substring(0,1),16))*22.5;
		showDebug("Wind Direction:"+logEntry.windDirection);
	}
	
	public void run()
	{		
		if(running==true){ return; }
		running = true;
		clearDataLog();
		while(running)
		{
			try{
				while(initialiseData()==false)
				{
					try{Thread.sleep(10000);}catch(Exception e){}
				}
				
				try{
					readData();
					
					try{Thread.sleep(pollTime);}catch(Exception e){}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				try{Thread.sleep(pollTime);}catch(Exception e2){}
			}
		}
		running = false;
	}
	

	private WeatherLogEntry accLogEntry = new WeatherLogEntry();
	private WeatherLogEntry logEntry = new WeatherLogEntry();
	private int readings = 0;
	
	private WeatherLogEntry getLogEntry()
	{
		WeatherLogEntry oldLogEntry = accLogEntry;
		int oldReadings = readings;
		accLogEntry = new WeatherLogEntry();
		readings = 0;		
		
		oldLogEntry.indoorHumidity=oldLogEntry.indoorHumidity/oldReadings;
		oldLogEntry.insideTemperature=oldLogEntry.insideTemperature/oldReadings;
		oldLogEntry.outdoorHumidity=oldLogEntry.outdoorHumidity/oldReadings;
		oldLogEntry.outsideTemperature=oldLogEntry.outsideTemperature/oldReadings;
		oldLogEntry.relativeAirPressure=oldLogEntry.relativeAirPressure/oldReadings;
		oldLogEntry.windDirection=(oldLogEntry.windDirection/oldReadings)%360;
		oldLogEntry.windSpeed=oldLogEntry.windSpeed/oldReadings;

		return oldLogEntry;
	}
	
	private void readData() throws Exception
	{
		logEntry = new WeatherLogEntry();
	
		initialiseData();
		
		try{
			readWindSpeedAndDirection();
		}
		catch(Exception e)
		{
			initialiseData();
		}
		
		try{
			readOutsideHumidity();
		}
		catch(Exception e)
		{
			initialiseData();
		}
	
		try{
			readOutsideTemperature();
		}
		catch(Exception e)
		{
			initialiseData();
		}
		
		try{
			readRainInLastHour();
		}
		catch(Exception e)
		{
			initialiseData();
		}
		
		try{
			readRainInLast24Hours();
		}
		catch(Exception e)
		{
			initialiseData();
		}
		
		try{	
			readRelativeAirPresure();
		}
		catch(Exception e)
		{
			initialiseData();
		}
		
		try{
			readInsideHumidity();
		}
		catch(Exception e)
		{
			initialiseData();
		}
		
		try{		
			readInsideTemperature();
		}
		catch(Exception e)
		{
			initialiseData();
		}
		
		readings++;
		accLogEntry.indoorHumidity+=logEntry.indoorHumidity;
		accLogEntry.insideTemperature+=logEntry.insideTemperature;
		accLogEntry.outdoorHumidity+=logEntry.outdoorHumidity;
		accLogEntry.outsideTemperature+=logEntry.outsideTemperature;
		accLogEntry.rain1Hour=logEntry.rain1Hour;
		accLogEntry.rain24Hours=logEntry.rain24Hours;
		accLogEntry.relativeAirPressure+=logEntry.relativeAirPressure;
		accLogEntry.windDirection+=logEntry.windDirection;
		accLogEntry.windSpeed+=logEntry.windSpeed;
	}
	
	private boolean sendCommand(int[] data) throws Exception
	{
		int checkSum = 0;
		int testValue = 0;
		for(int loop=0;loop<data.length;loop++)
		{
			checkSum = (16*loop)+((data[loop]-0x82)/4);
			
			writeByte( data[loop] );
			testValue = readByte();
			
			//showDebug("TestValue:"+testValue+" CheckSum:"+checkSum);
			
			if( testValue!=checkSum )
			{
				showDebug("Recieved Value:"+testValue+" Expected Value:"+checkSum);
				return false;
			}
		}
		
		return true;
	}
	
	private int checksum = 0;
	private int packetSize = 0;
	public double getIndoorHumidity() {
		return indoorHumidity;
	}
	public double getIndoorTemperature() {
		return indoorTemperature;
	}
	public double getOutdoorHumidity() {
		return outdoorHumidity;
	}
	public double getOutdoorTemperature() {
		return outdoorTemperature;
	}
	public double getRain1Hour() {
		return rain1Hour;
	}
	public double getRain24Hours() {
		return rain24Hours;
	}
	public double getRelativeAirPressure() {
		return relativeAirPressure;
	}
	public double getWindDirection() {
		return windDirection;
	}
	public double getWindSpeed() {
		return windSpeed;
	}
	private double indoorHumidity;
	private double indoorTemperature;
	private double outdoorHumidity;
	private double outdoorTemperature;
	private double rain1Hour;
	private double rain24Hours;
	private double relativeAirPressure;
	private double windDirection;
	private double windSpeed;
	
	private void resetChecksum()
	{
		checksum = 0;
		packetSize = 0;
	}
	
	private void checkSum(int testSize) throws Exception
	{
		try{Thread.sleep(20);}catch(Exception e){}
		
		if(in.available()==0)
		{

			throw new IOException("Packet Size Not Given");
		}
		
		int testChecksum = in.read();
		
		if(testSize-0x30!=packetSize)
		{
			showDebug("Packet Size Wrong:"+packetSize+" value recieved:"+(testSize-0x30));
			checksum=0;
			packetSize=0;
			throw new IOException("Packet Size Wrong");
		}
		if(testChecksum!=(checksum&0xFF))
		{
			showDebug("Checksum Wrong:"+testChecksum+" should be:"+(checksum&0xFF));
			checksum=0;
			packetSize=0;
			throw new IOException("Checksum Wrong");
		}
		checksum=0;
		packetSize=0;
	}
	
	private int readByte() throws Exception
	{
		if(in.available()==0)
		{
			try{Thread.sleep(50);}catch(Exception e){}
		}
		int value = -1;
		int count=0;
		while(value==-1 && count<10)
		{
			if(in.available()==0)
			{
				value =  -1;
				try{Thread.sleep(10);}catch(Exception e){}
				count++;
			}
			else
			{
				value = in.read();
			}
		}
		try{Thread.sleep(50);}catch(Exception e){}
		checksum += value;
		packetSize++;
		
		return value;
	}
	
	private void writeByte( int data ) throws Exception
	{
		out.write( data );
		out.flush();
		try{Thread.sleep(50);}catch(Exception e){}
	}

	public void clearDataLog() {
		accLogEntry = new WeatherLogEntry();
		readings = 0;	
	}

	public boolean dataAvailable() {
		if(readings>0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public EntityTree getDataLog() {
		
		WeatherLogEntry logEntry = getLogEntry();
		
		indoorHumidity = logEntry.getIndoorHumidity();
		indoorTemperature = logEntry.getInsideTemperature();
		outdoorHumidity = logEntry.getOutdoorHumidity();
		outdoorTemperature = logEntry.getOutsideTemperature();
		rain1Hour = logEntry.getRain1Hour();
		rain24Hours = logEntry.getRain24Hours();
		relativeAirPressure = logEntry.getRelativeAirPressure();
		windDirection = logEntry.getWindDirection();
		windSpeed = logEntry.getWindSpeed();
		
		EntityTree weatherLog = new EntityTree("log");
		weatherLog.addChild("indoorHumidity").setValue(""+logEntry.getIndoorHumidity());
		weatherLog.addChild("indoorTemperature").setValue(""+logEntry.getInsideTemperature());
		weatherLog.addChild("outdoorHumidity").setValue(""+logEntry.getOutdoorHumidity());
		weatherLog.addChild("outdoorTemperature").setValue(""+logEntry.getOutsideTemperature());
		weatherLog.addChild("rain1Hour").setValue(""+logEntry.getRain1Hour());
		weatherLog.addChild("rain24Hours").setValue(""+logEntry.getRain24Hours());
		weatherLog.addChild("relativeAirPressure").setValue(""+logEntry.getRelativeAirPressure());
		weatherLog.addChild("windDirection").setValue(""+logEntry.getWindDirection());
		weatherLog.addChild("windSpeed").setValue(""+logEntry.getWindSpeed());
		
		return weatherLog;
	}
	
	public void setDebug(boolean state) {
		debug = state;
	}  
	
	public void showDebug(String message)
	{
		if(debug==false)
		{
			return;
		}
		System.out.println(message);
	}

	public void setLogPeriod(long timeSeconds) {
		pollTime = timeSeconds*TimeConstants.SECOND;
	}
	
	public void kill()
	{
		try{
			connection.disconnect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		running = false;
	}
	public Device getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}
}
