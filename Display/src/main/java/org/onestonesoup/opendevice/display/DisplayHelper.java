package org.onestonesoup.opendevice.display;

import org.onestonesoup.opendevice.comms.RS232Driver;


public class DisplayHelper {
	
	public static SmartieLCD getSmartieLCD(RS232Driver connection) throws Exception {
		SmartieLCD display = new SmartieLCD(connection);
		return display;
	}
}
