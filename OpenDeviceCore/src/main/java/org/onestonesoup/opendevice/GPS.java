package org.onestonesoup.opendevice;

public interface GPS extends Device {

	public boolean hasFix();
	
	public long getTime();

	public double getAccuracy();
	
	public double getAltitude();
	
	public double getLatitude();

	public double getLongitude();
}
