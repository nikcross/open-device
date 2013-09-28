package org.one.stone.soup.open.device.gps.garmin;

import org.one.stone.soup.open.device.comms.RS232Driver;

public class GarminTest implements GarminPacketListener,GarminProcessListener{
	private GarminGPS gps;
/**
 * GarminTest constructor comment.
 */
public GarminTest() {
	super();

	RS232Driver driver = new RS232Driver("COMM1", "Com1 -> Garmin GPS");
	gps = new GarminGPS(driver);

	gps.start();

//	gps.startPVT(this);

	gps.transferTracks(this,this);

	loop();
}
/**
 * Insert the method's description here.
 * Creation date: (09/08/03 11:20:57)
 * @param packet wet.wired.gps.garmin.GarminPacket
 */
public void displayTrackData(GarminPacket packet)
{
	byte[] data = packet.getResponseData();

	for(int loop=0;loop<data.length;loop++)
	{
		System.out.println("  T["+loop+"]="+data[loop]);
	}

	long latSemi = GPSDataConverter.getLong(data,0);
	long lonSemi = GPSDataConverter.getLong(data,4);

	double lat = GPSDataConverter.getDegForSemi(latSemi);
	double lon = GPSDataConverter.getDegForSemi(lonSemi);

	System.out.println("Lat semi:"+latSemi+" deg:"+lat);
	System.out.println("Lon semi:"+lonSemi+" deg:"+lon);
}
/**
 * 
 */
public void loop()
{
	int loopIt = 0;

	while( loopIt<60 )
	{
		try{ Thread.sleep(1000); }catch(Exception e){}
		loopIt++;
		//System.out.println("\nTime = "+loopIt+" seconds.");

		if(loopIt>60)
		{
			gps.stop();
			System.exit(0);
		}
	}
}
/**
 * 
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	GarminTest test = new GarminTest();
}
/**
 * packetResponse method comment.
 */
public void packetResponse(GarminPacket packet)
{
	System.out.println(" Response:");

	System.out.println("Id:"+packet.getResponseId());
//	System.out.println("\tLongitude"+packet.getPvt().getLongitude());
//	System.out.println("\tLatitude"+packet.getPvt().getLatitude());

	//gps.turnOff();
	//gps.stop();

	switch(packet.getResponseId())
	{
		case GarminPacketFactory.TRK_DATA_PID:
			displayTrackData(packet);
			break;
	}
}
public void processComplete() {
	// TODO Auto-generated method stub
	
}
public void processFailed() {
	// TODO Auto-generated method stub
	
}
}
