package org.onestonesoup.opendevice.dataaquisition;

import org.onestonesoup.opendevice.comms.RS232Driver;

public class DataAquasitionHelper {

	public AudonDataAcquasitionModule20Channel getADC(RS232Driver connection) throws Exception {
		AudonDataAcquasitionModule20Channel device = new AudonDataAcquasitionModule20Channel(connection);
		for(int i=0;i<5;i++) {
			device.flashLed();
			Thread.sleep(1000);
		}
		
		return device;
	}
}
