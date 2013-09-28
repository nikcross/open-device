package org.one.stone.soup.open.device.audon.data.aquisition;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.one.stone.soup.core.data.EntityTree;
import org.one.stone.soup.open.device.Connection;
import org.one.stone.soup.open.device.Device;
import org.one.stone.soup.open.device.Logger;
import org.one.stone.soup.open.device.Switch;
import org.one.stone.soup.open.device.SwitchControl;
import org.one.stone.soup.open.device.comms.RS232Driver;

public class DataAcquasitionModule20Channel implements Device, Logger,
		SwitchControl {
	
	// http://www.dlpdesign.com/usb/dlp-io20-ds-v10.pdf
	
	//Port list
	public static final byte AN0 = 0x00;
	public static final byte AN1 = 0x01;
	public static final byte AN2 = 0x02;
	public static final byte AN3 = 0x03;
	public static final byte AN4 = 0x04;
	public static final byte AN5 = 0x05;
	public static final byte AN6 = 0x06;
	public static final byte AN7 = 0x07;
	public static final byte AN8 = 0x08;
	public static final byte AN9 = 0x09;
	public static final byte AN10 = 0x0A;
	public static final byte AN11 = 0x0B;
	public static final byte AN12 = 0x0C;
	public static final byte AN13 = 0x0D;
	public static final byte RA4 = 0x0E;
	public static final byte P5 = 0x0F;
	public static final byte P6 = 0x10;
	public static final byte P7 = 0x11;
	public static final byte RB7 = 0x12;
	public static final byte RB6 = 0x13;
	
	public static final byte[] PORTS = {
		AN0,AN1,AN2,AN3,AN4,AN5,AN6,AN7,
		AN8,AN9,AN10,AN11,AN12,AN13,
		RA4,
		P5,P6,P7,
		RB7,RB6
	};
	
	private static final String DEFAULT_ALIAS="Audon Data Acquasition Module 20 Channel";
	public static final void main(String[] args) throws Exception {
		String[] ports = RS232Driver.listPorts();
		for(String port: ports) {
			System.out.println("Port available: "+port);
		}
		
		RS232Driver connection = new RS232Driver("/dev/ttyUSB0","DEFAULT_ALIAS");
		DataAcquasitionModule20Channel device = new DataAcquasitionModule20Channel(connection);
		for(int i=0;i<20;i++) {
			device.flashLed();
			Thread.sleep(1000);
		}
	}
	
	private String alias = DEFAULT_ALIAS;
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}
	
	private Connection connection;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private Map<String,String> parameters = new HashMap<String,String>();
	
	public DataAcquasitionModule20Channel(Connection connection) throws Exception
	{
		this.connection = connection;
		connection.connect();
		
		inputStream = connection.getInputStream();
		outputStream = connection.getOutputStream();
	}
	
	public void setAlias(String alias) {
		this.alias=alias;
	}

	public String getAlias() {
		return alias;
	}

	private void writeBytes(byte[] data) throws IOException	{
		outputStream.write(data);
	}
	
	public boolean switchOn(int port) {
		setRelay(true, port);
		return true;
	}

	public boolean switchOff(int port) {
		setRelay(false, port);
		return true;
	}

	public List<Switch> getSwitches() {
		// Not implemented yet
		return null;
	}

	public void clearDataLog() {
		// Not implemented yet
	}

	public boolean dataAvailable() {
		// Not implemented yet
		return false;
	}

	public EntityTree getDataLog() {
		// Not implemented yet
		return null;
	}

	public void setLogPeriod(long timeSeconds) {
		// Not implemented yet
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
		// Not implemented yet
	}

	public void setDebug(boolean state) {
		// Not implemented yet
	}

	public boolean ping() {
		try{
			byte[] frame = new byte[2];
		frame[0] = (byte)frame.length;
			frame[1] = 0x27;
			writeBytes(frame);
			byte in = (byte)inputStream.read();
			if(in==0x59) {
				return true;
			} else {
				return false;
			}
		} catch(Exception e){
		}
		return false;
	}
	
	public void flashLed() {
		try{
			byte[] frame = new byte[2];
		frame[0] = (byte)frame.length;
			frame[1] = 0x28;
			writeBytes(frame);
		} catch(Exception e){
		}
	}
	
	public void ledOn() {
		try{
			byte[] frame = new byte[3];
		frame[0] = (byte)frame.length;
			frame[1] = 0x29;
			frame[2] = 0x01;
			writeBytes(frame);
		} catch(Exception e){
		}		
	}
	
	public void ledOff() {
		try{
			byte[] frame = new byte[3];
		frame[0] = (byte)frame.length;
			frame[1] = 0x29;
			frame[2] = 0x00;
			writeBytes(frame);
		} catch(Exception e){
		}
	}
	
	public void setRelay(boolean state,int port) {
		try{
			byte[] frame = new byte[4];
		frame[0] = (byte)frame.length;
			frame[1] = 0x30;
			frame[2] = (byte)port; //1 or 2
			if(state==true) {
				frame[3] =0x01;
			} else {
				frame[3] =0x00;
			}
			writeBytes(frame);
		} catch(Exception e){
		}
	}
	
	public int getInput(int port) {
		try{
			byte[] frame = new byte[5];
		frame[0] = (byte)frame.length;
			frame[1] = 0x35;
			frame[2] = (byte)port; //Select from port list
			frame[3] = 0x01; //Input mode 
			frame[4] = 0x00; // dummy byte
			
			writeBytes(frame);
			int result = inputStream.read();
			return result;
		} catch(Exception e){
		}
		return -1;
	}
	
	public void setOutput(int port,boolean state) {
		try{
			byte[] frame = new byte[5];
		frame[0] = (byte)frame.length;
			frame[1] = 0x35;
			frame[2] = (byte)port; //Select from port list
			frame[3] = 0x00; //Output mode 
			if(state==true) {
				frame[4] =0x01;
			} else {
				frame[4] =0x00;
			}
			
			writeBytes(frame);
		} catch(Exception e){
		}
	}
	
	public void enableAndClearEventCounter(int port,boolean trailingEdge) {
		try{
			byte[] frame = new byte[4];
		frame[0] = (byte)frame.length;
			frame[1] = 0x36;
			frame[2] = (byte)port; //Select from RB6 or RB7
			if(trailingEdge==true) {
				frame[3] =0x01;
			} else {
				frame[3] =0x00;
			}
			
			writeBytes(frame);
		} catch(Exception e){
		}
	}
	
	public int readEventCounter(int port) {
		try{
			byte[] frame = new byte[3];
		frame[0] = (byte)frame.length;
			frame[1] = 0x37;
			frame[2] = (byte)port; //Select from RB6 or RB7
			
			writeBytes(frame);
			
			int a = inputStream.read();
			int b = inputStream.read();
			int c = inputStream.read();
			int d = inputStream.read();
			
			int result = 
					(d>>24)+
					(c>>16)+
					(b>>8)+
					a;
			
			return result;
		} catch(Exception e){
		}
		return -1;
	}
	
	public Integer[] detectTemperatureSensors() {

		List<Integer> temperaturePorts = new ArrayList<Integer>();
		for(int port: PORTS) {
			if(detectTemperatureSensor(port)==true) {
				temperaturePorts.add(port);
			}
		}
		return temperaturePorts.toArray(new Integer[]{});
	}
	
	public boolean detectTemperatureSensor(int port) {
		try{
			byte[] frame = new byte[3];
		frame[0] = (byte)frame.length;
			frame[1] = 0x39;
			frame[2] = (byte)port; //Select from port list
			
			writeBytes(frame);
			
			int a = inputStream.read();
			int b = inputStream.read();
			int c = inputStream.read();
			int d = inputStream.read();
			int e = inputStream.read();
			int f = inputStream.read();
			int g = inputStream.read();
			int h = inputStream.read();
			
			if(a==1 || a==0) {
				return false;
			} else {
				return true;
			}
		} catch(Exception e){
		}
		return false;
	}
	
	public int readTemperature(int port) {
		try{
			byte[] frame = new byte[3];
			frame[0] = (byte)frame.length;
			frame[1] = 0x39;
			frame[2] = (byte)port; //Select from port list
			
			writeBytes(frame);
			
			int a = inputStream.read();
			int b = inputStream.read();

			int result = 
					(b>>8)+
					a;
			return result;
		} catch(Exception e){
		}
		return 0;
	}
	
	public void setTemperatureResolution(int resolution,int port) {
		try{
			byte[] frame = new byte[4];
			frame[0] = (byte)frame.length;
			frame[1] = 0x42;
			frame[2] = (byte)port; //Select from port list
			frame[3] = (byte)resolution;
					
			writeBytes(frame);
		} catch(Exception e){
		}
	}
	
	public int readVoltageOnce(int port) {
		try{
			byte[] frame = new byte[5];
			frame[0] = (byte)frame.length;
			frame[1] = 0x50;
			frame[2] = (byte)port; //Select from port list
			
			writeBytes(frame);
			
			int a = inputStream.read();
			int b = inputStream.read();

			int result = 
					(b>>8)+
					a;
			return result;
		} catch(Exception e){
		}
		return 0;
	}
	
	// multi voltage reads not implemented yet
	
	public void setExternalADReference() {
		try{
			byte[] frame = new byte[2];
			frame[0] = (byte)frame.length;
			frame[1] = 0x53;
			
			writeBytes(frame);
		} catch(Exception e){
		}
	}
	
	public void setInternalADReference() {
		try{
			byte[] frame = new byte[2];
			frame[0] = (byte)frame.length;
			frame[1] = 0x54;
			
			writeBytes(frame);
		} catch(Exception e){
		}	
	}
}
