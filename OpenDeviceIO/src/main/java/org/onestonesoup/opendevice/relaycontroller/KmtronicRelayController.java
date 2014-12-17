package org.onestonesoup.opendevice.relaycontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Switch;
import org.onestonesoup.opendevice.SwitchControl;

public class KmtronicRelayController implements SwitchControl {
	private static final String DEFAULT_ALIAS="KMTronic Relay Controller";
	private String alias = DEFAULT_ALIAS;
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}
	
	private Connection connection;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private Map<String,String> parameters = new HashMap<String,String>();
	
	public KmtronicRelayController(Connection connection) throws Exception
	{
		this.connection = connection;
		connection.connect();
		
		inputStream = connection.getInputStream();
		outputStream = connection.getOutputStream();
	}
	
	public boolean switchOff(int port) {
		try{
		//OFF command : FF 01 00 (HEX) or 255 1 0 (DEC)
			byte[] frame = new byte[3];
		frame[0] = (byte)0xFF;
			frame[1] = (byte)port;
			frame[2] = 0x00;
			writeBytes(frame);
		} catch(Exception e){
			return false;
		}
		return true;
	}

	public boolean switchOn(int port) {
		try{
			//ON command : FF 01 01 (HEX) or 255 1 1 (DEC)		
			byte[] frame = new byte[3];
			frame[0] = (byte)0xFF;
			frame[1] = (byte)port;
			frame[2] = 0x01;
			writeBytes(frame);
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	public void switchOffAll() {
		for(int i=1;i<9;i++) {
			switchOff(i);
		}
	}
	
	public void kill() {}

	public void setDebug(boolean state) {}

	private void writeBytes(byte[] data) throws IOException	{
		outputStream.write(data);
	}
	
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

	public List<Switch> getSwitches() {
		// TODO Auto-generated method stub
		return null;
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
