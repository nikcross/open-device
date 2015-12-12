package org.onestonesoup.opendevice.gps.vk162;

import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.comms.RS232Driver;

public class GPSHelper {
	
	public static VK162GPS getVK162GPS(RS232Driver connection) throws Exception {
		//System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
		
		connection.setBaud(9600);
		
		return new VK162GPS(connection);
	}
}
