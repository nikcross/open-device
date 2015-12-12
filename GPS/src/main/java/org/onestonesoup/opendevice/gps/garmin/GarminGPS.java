package org.onestonesoup.opendevice.gps.garmin;

import org.onestonesoup.opendevice.Connection;

public class GarminGPS {
	// Position/Velocity/Time (Realtime data) mode status
	private boolean pvtMode = false;

	private GarminGPSController controller;
/**
 * GarminGPS constructor comment.
 */
public GarminGPS(Connection connection) {
	super();
	controller = new GarminGPSController( connection );
}
/**
 * 
 */
public void finalize() {
	try{
		controller.stopComms();
	}
	catch(Exception e){}
}
/**
 * 
 */
public void start()
{
// initialize the GPS controller
	controller.startComms();
}
/**
*	Set the GPS in realtime data mode and define a data listener
*/
public void startPVT(GarminPacketListener listener)
{
	GarminPacket packet = GarminPacketFactory.getStartPVTPacket();
	packet.addListener(listener);
	controller.sendPacket(packet);
	pvtMode=true;
}
/**
 * 
 */
public void stop()
{
// initialize the GPS controller
	try{
		controller.stopComms();
	}
	catch(Exception e){ e.printStackTrace(); }
}
/**
*	Take the GPS out of realtime data mode
*/
public void stopPVT()
{
	GarminPacket packet = GarminPacketFactory.getStopPVTPacket();
	controller.sendPacket(packet);
	pvtMode=false;
}

/**
*	Set the GPS in realtime data mode and define a data listener
*/
public void transferTracks(GarminPacketListener listener,GarminProcessListener processListener)
{
	TrackReceiver receiver = new TrackReceiver(controller,listener,processListener);
}

/**
*	Set the GPS in realtime data mode and define a data listener
*/
public void transferWayPoints(GarminPacketListener listener,GarminProcessListener processListener)
{
	WayPointReceiver receiver = new WayPointReceiver(controller,listener,processListener);
}

/**
 * 
 */
public void turnOff()
{
	controller.turnOff();
}
}
