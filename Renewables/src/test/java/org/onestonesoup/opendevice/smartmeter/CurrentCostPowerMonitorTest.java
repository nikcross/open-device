package org.onestonesoup.opendevice.smartmeter;

import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.comms.RS232Driver;
import org.onestonesoup.opendevice.smartmeter.CurrentCostPowerMonitor;

public class CurrentCostPowerMonitorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
		String address = "192.168.1.104";
		String portName="/dev/ttyUSB0";
		int baud=57600;
		
		/*RemoteRS232Clients rs232s = new RemoteRS232Clients();
		RemoteRS232Client rs232 = rs232s.getRemoteRS232Client(address, 232,233);
		RS232Connection port=null;
		
		port = rs232.getPort(portName, baud, 1);*/
		Connection port = new RS232Driver("COM7","testing");
		
		if( CurrentCostPowerMonitor.testPort(port)==true ) {
			CurrentCostPowerMonitor monitor = new CurrentCostPowerMonitor(port);
		} else {
			System.out.println("Connection to "+portName+" failed test.");
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
