package org.onestonesoup.opendevice.dataaquisition;

import org.onestonesoup.opendevice.comms.RS232Driver;

public class DataAquasitionHelper {

	public AudonDataAcquasitionModule20Channel getADC(String portName,String alias) throws Exception {
		String[] ports = RS232Driver.listPorts();
		for(String port: ports) {
			System.out.println("Port available: "+port);
		}
		
		RS232Driver connection = new RS232Driver(portName,alias);
		AudonDataAcquasitionModule20Channel device = new AudonDataAcquasitionModule20Channel(connection);
		for(int i=0;i<5;i++) {
			device.flashLed();
			Thread.sleep(1000);
		}
		
		return device;
	}
}
