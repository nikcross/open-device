package org.onestonesoup.opendevice.comms;

public class ConnectionHelper {

	public static RS232Driver getRS232Connection(String port,String alias) {
		return new RS232Driver(port,alias);
	}
}
