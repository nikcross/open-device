package org.onestonesoup.opendevice.weatherstation;

import org.onestonesoup.opendevice.comms.RS232Driver;

public class WeatherStationHelper {

	public static WS2350 getWS2350(String portName,String alias) throws Exception {
		RS232Driver connection = new RS232Driver(portName,alias);
		WS2350 device = new WS2350(connection);
		return device;
	}
}
