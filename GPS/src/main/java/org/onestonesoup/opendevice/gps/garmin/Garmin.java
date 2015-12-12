package org.onestonesoup.opendevice.gps.garmin;

import org.onestonesoup.opendevice.comms.RS232Driver;

public class Garmin {
	public static void main(String[] args)
	{
		RS232Driver connection = new RS232Driver("COM4","com4");
		connection.connect();
		GarminGPS gps = new GarminGPS(connection);
		gps.start();
		downloadTrack(gps);
	}
	
	public static void downloadTrack(GarminGPS gps)
	{
		final class TrackProcessor implements GarminPacketListener,GarminProcessListener 
		{
			boolean processing = true;
			public void packetResponse(GarminPacket packet) {
				byte[] data = packet.getResponseData();
				
				long latSemi = GPSDataConverter.getLong(data,0);
				long lonSemi = GPSDataConverter.getLong(data,4);

				double lat = GPSDataConverter.getDegForSemi(latSemi);
				double lon = GPSDataConverter.getDegForSemi(lonSemi);

				System.out.println("Lat semi:"+latSemi+" deg:"+lat);
				System.out.println("Lon semi:"+lonSemi+" deg:"+lon);
			}
			public void processComplete() {
				processing = false;
			}
			public void processFailed() {
				// TODO Auto-generated method stub	
			}			
		}
		
		TrackProcessor tp = new TrackProcessor();
		
		gps.transferTracks(tp, tp);
		
		try{while(tp.processing==true){Thread.sleep(500);}}catch(Exception e){}
	}
}
