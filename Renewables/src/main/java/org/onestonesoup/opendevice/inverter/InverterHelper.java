package org.onestonesoup.opendevice.inverter;

import org.onestonesoup.opendevice.comms.HttpConnection;

public class InverterHelper {

	public static SMAWebBox getSMAWebBox(String url) throws Exception {
		HttpConnection connection = new HttpConnection(url);
		SMAWebBox smaWebBox = new SMAWebBox(connection);
		
		return smaWebBox;
	}
}
