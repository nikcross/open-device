package org.onestonesoup.opendevice.gps;

import java.io.*;
public class PVTPacketFactory {
private PVTPacketFactory() {
	super();
}
/**
 * 
 * @return wet.wired.gps.garmin.PVTPacket
 * @param data byte[]
 */
public final static PVTPacket buildPacket(byte[] data) throws java.io.IOException {
	
	PVTPacket packet = new PVTPacket();
	
	ByteArrayInputStream iStream = new ByteArrayInputStream(data,3,data.length-5);
	GarminGPSInputStream dStream = new GarminGPSInputStream(iStream);
		
	packet.alt = dStream.readFloat(); 
//	System.out.println("altitude above WGS 84 ellipsoid (meters) ="+alt);
	packet.epe = dStream.readFloat();
//	System.out.println("estimated position error, 2 sigma (meters) ="+epe);
	packet.eph = dStream.readFloat();
//	System.out.println("epe, but horizontal only (meters) ="+eph);
	packet.epv = dStream.readFloat(); 
//	System.out.println("epe, but vertical only (meters) ="+epv);
	packet.fix = dStream.readInt(); 
//	System.out.println("type of position fix ="+fix);
	packet.tow = dStream.readDouble(); 
//	System.out.println("time of week (seconds) ="+tow);
	
//	Radian_Type posn; // latitude and longitude (radians)
	packet.lat = dStream.readDouble();
//	System.out.println("Latitude = "+lat);
	packet.lon = dStream.readDouble();
//	System.out.println("Longitude = "+lon);
	
	packet.east = dStream.readFloat();
//	System.out.println("velocity east (meters/second) ="+east);
	packet.north = dStream.readFloat();
//	System.out.println("velocity north (meters/second) ="+north);
	packet.up = dStream.readFloat(); 
//	System.out.println("velocity up (meters/second) ="+up);
	packet.msl_hght = dStream.readFloat(); 
//	System.out.println("height of WGS 84 ellipsoid above MSL (meters) ="+msl_hght);
	packet.leap_scnds = dStream.readInt(); 
//	System.out.println("difference between GPS and UTC (seconds) ="+leap_scnds);
	packet.wn_days = dStream.readLong(); 
//	System.out.println("week number days ="+wn_days);

	return packet;
}
}
