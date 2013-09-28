package org.one.stone.soup.open.device.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.one.stone.soup.open.device.Connection;
import org.one.stone.soup.open.device.Device;
import org.one.stone.soup.open.device.comms.RS232Driver;
import org.one.stone.soup.open.device.jmf.WebCam;

public class OpenDeviceController {

	private static OpenDeviceController openDeviceController;
	
	public static OpenDeviceController getController() {
		if(openDeviceController==null) {
			openDeviceController=new OpenDeviceController();
		}
		return openDeviceController;
	}
	
	private Map<String,Device> devices;
	
	public OpenDeviceController() {
		devices = new HashMap<String,Device>();
	}
	
	public String[] getAvailableSerialPorts() {
		return RS232Driver.listAvailablePorts();
	}
	
	public String[] getAvailableWebCams() {
		return WebCam.getWebCamDevices();
	}
	
	public Device connectNewDevice(String deviceClassName,String deviceAlias,String portName) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		int baud = 9600;
		
		if(portName.indexOf(":")!=-1) {
			String[] parts = portName.split(":");
			portName = parts[0];
			if(parts.length>1) {
				baud=Integer.parseInt(parts[1]);
			}
		}
		
		System.out.println("Connecting "+deviceAlias+" to "+portName+" (baud:"+baud+")");
		
		RS232Driver connection = new RS232Driver(portName,deviceAlias);
		connection.setBaud(baud);
		Device device = (Device) Class.forName(deviceClassName).getConstructor(Connection.class).newInstance(connection);
		
		devices.put(deviceAlias,device);
		return device;
	}
	
	public Device getDevice(String deviceAlias) {
		return devices.get(deviceAlias);
	}
	
	public String[] getDevices() {
		return devices.keySet().toArray(new String[]{});
	}
}
