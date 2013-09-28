package org.one.stone.soup.open.device.gps.garmin;

public class PVTPacket {
	public static final int FIX_UNUSABLE	= 0;
	public static final int FIX_INVALID		= 1;
	public static final int FIX_2D			= 2;
	public static final int FIX_3D			= 3;
	public static final int FIX_2D_DIFF		= 4;
	public static final int FIX_3D_DIFF		= 5;
	
	// int 16bit
	// long 32bit
	// float 32bit
	// double 64bit
	
	protected float alt; // altitude above WGS 84 ellipsoid (meters) 				0-3
	protected float epe; // estimated position error, 2 sigma (meters) 			4-7
	protected float eph; // epe, but horizontal only (meters) 					8-15
	protected float epv; // epe, but vertical only (meters) 						16-23
	protected int fix; // type of position fix 									24-25
	protected double tow; // time of week (seconds)								26-33
	
//	Radian_Type posn; // latitude and longitude (radians)
	protected double lat; //														34-41
	protected double lon; //														42-49
	
	protected float east; // velocity east (meters/second) 						50-53
	protected float north; // velocity north (meters/second) 						54-57
	protected float up; // velocity up (meters/second) 							58-65
	protected float msl_hght; // height of WGS 84 ellipsoid above MSL (meters) 	66-73
	protected int leap_scnds; // difference between GPS and UTC (seconds) 		74-75
	protected long wn_days; // week number days 									76-79
/**
 * PVTPacket constructor comment.
 */
protected PVTPacket() {
	super();
}
/**
 * 
 * @return double
 */
public double getAltitude() {
	return lat;
}
/**
 * 
 * @return long
 */
public long getDayOfWeek() {
	return wn_days;
}
/**
 * 
 * @return float
 */
public float getEstimatedPositionError() {
	return epe;
}
/**
 * 
 * @return float
 */
public float getEstimatedPositionErrorVertical() {
	return epv;
}
/**
 * 
 * @return float
 */
public float getEstimatedPostionErrorHorizontal() {
	return eph;
}
/**
 * 
 * @return int
 */
public int getFixType() {
	return fix;
}
/**
 * 
 * @return float
 */
public float getHeightboveMSL() {
	return msl_hght;
}
/**
 * 
 * @return double
 */
public double getLatitude() {
	return lat;
}
/**
 * 
 * @return double
 */
public double getLongitude() {
	return lon;
}
/**
 * 
 * @return double
 */
public double getTimeOfWeek() {
	return tow;
}
/**
 * 
 * @return int
 */
public int getUTCDifference() {
	return leap_scnds;
}
/**
 * 
 * @return float
 */
public float getVelocityEast() {
	return east;
}
/**
 * 
 * @return float
 */
public float getVelocityNorth() {
	return north;
}
/**
 * 
 * @return float
 */
public float getVelocityUp() {
	return up;
}
}
