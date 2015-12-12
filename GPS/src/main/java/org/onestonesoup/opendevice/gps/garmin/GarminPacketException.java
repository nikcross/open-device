package org.onestonesoup.opendevice.gps.garmin;

public class GarminPacketException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int UNKNOWN_CODE = 0;
	public static final int ERROR_BAD_START = 1;
	public static final int ERROR_BAD_PID = 2;
	public static final int ERROR_BAD_PVT = 3;
	
	public static final int WARNING_ = 4;
	public static final String[] message;
				
	static{
		message = new String[5];

		message[UNKNOWN_CODE]="Unknown exception code #.";
		message[ERROR_BAD_START]="ERROR: Byte 0 must be DLE";
		message[ERROR_BAD_PID]="ERROR: Packet byte 1 must be ACK , NAK or PVT_DATA";
		message[ERROR_BAD_PVT]="ERROR: Bad PVT Packet";
		message[WARNING_]="WARNING: ";
	}	
	
/**
 * 
 */
public GarminPacketException(int code) {
	super( message[code] );
}
/**
 * 
 */
public GarminPacketException(int code,Object[] data) {
	super( message[code]+":"+data );
}
}
