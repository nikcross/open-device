package org.one.stone.soup.open.device.gps.garmin;

public class GarminCommsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int UNKNOWN_CODE = 0;
	public static final int ERROR_ = 1;
	public static final int WARNING_ = 2;

	public static String[] message = new String[3];
	
	static{

		message[UNKNOWN_CODE]="Unknown exception code #.";
		message[ERROR_]="ERROR: ";
		message[WARNING_]="WARNING: ";
	}	
	
/**
 * 
 */
public GarminCommsException(int code) {
	super( message[code] );
}
/**
 * 
 */
public GarminCommsException(int code,Object[] data) {
	super( message[code]+":"+data );
}
}
