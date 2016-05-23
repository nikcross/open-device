package org.onestonesoup.opendevice.smartmeter;

import org.onestonesoup.opendevice.comms.RS232Driver;
import org.onestonesoup.opendevice.comms.linux.USBSerialMap;

public class SmartMeterHelper {

	private static CurrentCostPowerMonitor currentCostPowerMeter = null;
	private static USBSerialMap usbSerialMap;
	
	public static CurrentCostPowerMonitor getCurrentCostPowerMeter() {
		return currentCostPowerMeter;
	}
	
	public static CurrentCostPowerMonitor createCurrentCostPowerMeter(String portName,String alias) throws Exception {
		if(portName.startsWith("path:")) {
			String path = portName.substring(5);
			portName = getLinuxSerialMap().getPortAtPath(path);
		}
		
		RS232Driver connection = new RS232Driver(portName,alias);
		connection.setBaud(57600);
		
		currentCostPowerMeter = new CurrentCostPowerMonitor(connection);
		return currentCostPowerMeter;
	}
	
	public static String[] listSerialPorts() {
		return RS232Driver.listPorts();
	}
	
	public static USBSerialMap getLinuxSerialMap() throws Exception {
		if(usbSerialMap==null) {
			usbSerialMap = new USBSerialMap();
		}
		return usbSerialMap;
	}
}
