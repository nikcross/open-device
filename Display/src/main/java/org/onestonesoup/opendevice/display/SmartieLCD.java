package org.onestonesoup.opendevice.display;

import java.io.IOException;
import java.io.OutputStream;

import org.onestonesoup.core.StringHelper;
import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;

public class SmartieLCD implements Device {
	private static String DEFAULT_ALIAS = "SmartieLCD";
	
	private String alias = DEFAULT_ALIAS;
	private Connection connection;

	private boolean debug;
	private OutputStream outputStream;
	
	public SmartieLCD(Connection connection) throws Exception {
		this.connection = connection;
		connection.connect();
		outputStream = connection.getOutputStream();
		
		System.out.println("os:"+outputStream);
	}
	
	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getParameter(String name) {
		return null;
	}

	@Override
	public Device getParent() {
		return null;
	}

	@Override
	public boolean hasParent() {
		return false;
	}

	@Override
	public void kill() {
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void setParameter(String name, String value) {
	}

	public void displayMessage(String messageLines) throws IOException {
		byte[] line1 = new byte[] { (byte)0xfe, 0x47, 0x01, 0x01 };
		byte[] line2 = new byte[] { (byte)0xfe, 0x47, 0x01, 0x02 };

		String[] message = messageLines.split("\n");
		
		System.out.println("Display message: "+messageLines);
		
		if(message.length>0) {
			message[0] = StringHelper.padRightToFitSize(message[0], ' ', 16);
			outputStream.write(line1);
			outputStream.write(message[0].getBytes());
		}
		if(message.length>1) {
			message[1] = StringHelper.padRightToFitSize(message[1], ' ', 16);
			outputStream.write(line2);
			outputStream.write(message[1].getBytes());
		}
	}
	
	public void backLightOff() throws IOException {

		outputStream.write((byte)0xfe);
		outputStream.write((byte)0x46);
	}
	public void backLightOn() throws IOException {

		outputStream.write((byte)0xfe);
		outputStream.write((byte)0x42);
		outputStream.write((byte)0x00);
	}
	
	public void setContrast(int contrast) throws IOException {

		outputStream.write((byte)0xfe);
		outputStream.write((byte)0x50);
		outputStream.write((byte)contrast);
	}
	
	public void setBackLight(int contrast) throws IOException {

		outputStream.write((byte)0xfe);
		outputStream.write((byte)0x98);
		outputStream.write((byte)contrast);
	}
}
