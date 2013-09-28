package org.one.stone.soup.open.device.inverter.phoenix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.one.stone.soup.open.device.Connection;
import org.one.stone.soup.open.device.Device;
import org.one.stone.soup.open.device.Logger;
import org.one.stone.soup.core.StringHelper;
import org.one.stone.soup.core.data.EntityTree;

public class PhoenixInverter implements Logger{
	private static final String DEFAULT_ALIAS="Phoenix Inverter";
	private String alias = DEFAULT_ALIAS;
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}
	private static final byte COMMAND_SEND_LED_STATUS = 0x02;
	private static final byte COMMAND_SWITCH = 0x03;
	private static final byte COMMAND_SHORE_CURRENT_SET_POINT = 0x04;
	
	private static final byte COMMAND_SOFTWARE_VERSION_PART_0 = 0x05;
	private static final byte COMMAND_SOFTWARE_VERSION_PART_1 = 0x06;
	
	private static final byte COMMAND_SWITCH_AND_SHORE_AND_LEDS = 0x0C;
	private static final byte COMMAND_GET_SWITCH_AND_SHORE = 0x0D;
	private static final byte COMMAND_GET_SET_DEVICE_STATE = 0x0E;
	private static final byte COMMAND_READ_RAM_VAR = 0x30;
	private static final byte COMMAND_READ_SETTING = 0x31;
	private static final byte COMMAND_WRITE_RAM_VAR = 0x32;
	private static final byte COMMAND_WRITE_SETTING = 0x33;
	private static final byte COMMAND_WRITE_DATA = 0x34;
	private static final byte COMMAND_GET_SETTING_INFO = 0x35;
	private static final byte COMMAND_GET_RAM_VAR_INFO = 0x36;
	
	private static final byte RAM_UMAINS_RMS = 				0x00;
	private static final byte RAM_IMAINS_RMS = 				0x01;
	private static final byte RAM_UINVERTER_RMS = 			0x02;
	private static final byte RAM_IINVERTER_RMS = 			0x03;
	private static final byte RAM_UBAT = 					0x04;
	private static final byte RAM_IBAT = 					0x05;
	private static final byte RAM_UBAT_RMS = 				0x06;
	private static final byte RAM_INVERTER_PERIOD_TIME =	0x07;
	private static final byte RAM_MAINS_PERIOD_TIME = 		0x08;
	private static final byte RAM_SIGNED_AC_LOAD_CURRENT = 	0x09;
	private static final byte SETTING_ = 0x00;

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
	private Connection connection;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private boolean debug = true;
	private boolean running = false;
	private boolean processing = false;
	private boolean waiting = false;
	
	public PhoenixInverter(Connection connection) throws Exception
	{
		this.connection = connection;
		connection.connect();
		
		inputStream = connection.getInputStream();
		outputStream = connection.getOutputStream();
	}
	
	public void initialise(String arguments)
	{
	}
	
	private void readVersionNumberFrame(byte[] frame)
	{
		int version = 0;
		for(int loop=4;loop>0;loop--)
		{
			version = version << 8;
			version+=frame[loop+2];
		}
		 char mode = (char)frame[7];
		 
		 showDebug("Version "+version+" Mode "+mode);
	}
	
	private void setCheckSum( byte[]frame )
	{
		frame[frame.length-1] = calculateCheckSum( frame );
	}
	
	private byte calculateCheckSum( byte[] frame )
	{
		byte sum = 0;
		
		for(int loop=0;loop<frame.length-1;loop++)
		{
			sum += frame[loop];
		}
		
		return (byte)( 0xFF-sum+1 );
	}
	
	private boolean validateCheckSum(byte[] frame)
	{
		byte testSum = calculateCheckSum( frame );
		if(testSum==frame[frame.length-1])
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void printFrame( byte[] frame )
	{
		showDebug( StringHelper.asHex(frame) );
	}
	
	public void run()
	{
		if(running==true)
		{
			return;
		}
		
		try
		{
			running = true;
			
			boolean initialised = false;
			while(initialised == false)
			{
				try{
					byte[] frame = readFrame();
					readVersionNumberFrame(frame);
					initialised=true;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					Thread.sleep(200);
				}
			}
			
			//Switch on
			switchOn();
			Thread.sleep(200);
			
			/*while(running)
			{
				if(processing==false)
				{
					try{
						readFrame();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}

				waiting = true;
				Thread.sleep(200);
				waiting = false;
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			waiting = false;
			running = false;
		}
	}
	
	private void getLock()
	{
		/*while(waiting==false)
		{
			try{Thread.sleep(50);}catch(Exception e){}
		}*/
		processing = true;
	}
	
	private void releaseLock()
	{
		processing = false;
	}
	
	private byte readByte() throws IOException
	{
		return (byte)inputStream.read();
	}
	
	private void writeBytes(byte[] data) throws IOException
	{
		outputStream.write(data);
	}
	
	private byte[] readFrame() throws Exception
	{
		int in = readByte();
		while(in==0xff)
		{
			in = readByte();
		}
		
		//int in = port.getReceiveFramingByte();
		//in = iStream.read();
		
		if(in==0xff)
		{
			return new byte[0];
		}
		
		int length = in+2;
		byte[] frame = new byte[length];
		frame[0] = (byte)in;
		for(int loop=1;loop<length;loop++)
		{
			frame[loop] = readByte();
		}
		
		if( validateCheckSum(frame)==false )
		{
			throw new IOException( "Checksum failed. Frame:"+StringHelper.asHex(frame) );
		}
		
		return frame;
	}
	
	private byte[] sendCommand(byte command,byte arg0,byte arg1) throws Exception
	{
		try{
			getLock();
			
			byte[] frame = new byte[8];
			
			frame[0] = 0x06;
			frame[1] = (byte)0xff;
			frame[2] = 0x57;
			
			frame[3] = command;
			frame[4] = arg0;
			frame[5] = arg1;
	
			frame[6] = 0x00;
			frame[7] = calculateCheckSum(frame);
			
			System.out.print("SEND: ");
			printFrame(frame);
			
			frame = readFrame();
			System.out.print("REC1: ");
			printFrame(frame);
			
			frame = readFrame();
			System.out.print("REC2: ");
			printFrame(frame);
			
			frame = readFrame();
			System.out.print("REC3: ");
			printFrame(frame);
			
			return frame;
		}
		finally
		{
			releaseLock();
		}
	}
	
	public void switchChargerOnly() throws IOException
	{
		byte[] frame = new byte[8];
		
		frame[0] = 0x06;
		frame[1] = (byte)0xff;
		frame[2] = 0x57;
		
		frame[3] = COMMAND_SWITCH;
		frame[4] = 0x01;
		frame[5] = 0x00;
		
		frame[6] = 0x00;
		frame[7] = calculateCheckSum(frame);
		
		writeBytes(frame);
		
		showDebug( "Switched to Charger Only" );
	}
	
	public void switchInverterOnly() throws IOException
	{
		byte[] frame = new byte[8];
		
		frame[0] = 0x06;
		frame[1] = (byte)0xff;
		frame[2] = 0x57;
		
		frame[3] = COMMAND_SWITCH;
		frame[4] = 0x02;
		frame[5] = 0x00;
		
		frame[6] = 0x00;
		frame[7] = calculateCheckSum(frame);
		
		writeBytes(frame);
		
		showDebug( "Switched to Inverter Only" );
	}
	
	public void switchOn() throws IOException
	{
		byte[] frame = new byte[8];
		
		frame[0] = 0x06;
		frame[1] = (byte)0xff;
		frame[2] = 0x57;
		
		frame[3] = COMMAND_SWITCH;
		frame[4] = 0x03;
		frame[5] = 0x00;
		
		frame[6] = 0x00;
		frame[7] = calculateCheckSum(frame);
		
		writeBytes(frame);
		
		showDebug( "Switched ON" );
	}	
	
	public void switchOff() throws Exception
	{
		byte[] frame = new byte[8];
		
		frame[0] = 0x06;
		frame[1] = (byte)0xff;
		frame[2] = 0x57;
		
		frame[3] = COMMAND_SWITCH;
		frame[4] = 0x04;
		frame[5] = 0x00;
		
		frame[6] = 0x00;
		frame[7] = calculateCheckSum(frame);
		
		writeBytes(frame);
		
		showDebug( "Switched ON" );		
	}
	
	private int readRam(byte address) throws Exception
	{
		byte[] frame = sendCommand(COMMAND_READ_RAM_VAR,address,(byte)0x00);
		
		if( frame[3]!=0x85 )
		{
			throw new IOException("Variable Not Supported");
		}
		
		int value = frame[5];
		value = value << 8;
		value += frame[4];
		
		return value;
	}	

	public String getDeviceState() throws Exception
	{
		byte[] frame = new byte[8];
		
		frame[0] = 0x06;
		frame[1] = (byte)0xff;
		frame[2] = 0x57;
		
		frame[3] = COMMAND_GET_SET_DEVICE_STATE;
		frame[4] = 0x00;
		frame[5] = 0x00;
		
		frame[6] = 0x00;
		frame[7] = calculateCheckSum(frame);
		
		writeBytes(frame);
	
		System.out.print("SEND: ");
		printFrame(frame);
		
		frame = readFrame();
		System.out.print("REC: ");
		printFrame(frame);
		
		return StringHelper.asHex(frame);
	}	
	
	public int getBatteryVoltage() throws Exception
	{
		return readRam(RAM_IBAT);
	}
	
	public int getMainsRMS() throws Exception
	{
		return readRam(RAM_UMAINS_RMS);
	}
	
	public int getMainsLoad() throws Exception
	{
		return readRam(RAM_SIGNED_AC_LOAD_CURRENT);
	}

	public void clearDataLog() {
		// TODO Auto-generated method stub
	}

	public boolean dataAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	public EntityTree getDataLog() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub	
	}
	
	public void kill(){}
	public Device getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}
}
