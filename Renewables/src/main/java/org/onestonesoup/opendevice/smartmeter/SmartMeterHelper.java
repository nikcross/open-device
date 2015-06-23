package org.onestonesoup.opendevice.smartmeter;

import org.onestonesoup.opendevice.comms.RS232Driver;

public class SmartMeterHelper {

	public static CurrentCostPowerMonitor getCurrentCostPowerMeter(String portName,String alias) throws Exception {
		String[] ports = RS232Driver.listPorts();
		for(String port: ports) {
			System.out.println("Port available: "+port);
		}
		
		RS232Driver connection = new RS232Driver(portName,alias);
		connection.setBaud(57600);
		
		return new CurrentCostPowerMonitor(connection);
	}
}
