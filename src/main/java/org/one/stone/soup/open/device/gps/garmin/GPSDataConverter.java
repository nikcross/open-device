package org.one.stone.soup.open.device.gps.garmin;

public final class GPSDataConverter {
public static String formatDegForRad(float rad) {

	float degF = getDegForRad(rad);

	int deg=(int)degF;
	degF-=deg;
	int min=(int)(degF*60);
	degF-=((float)min)/60;
	int sec=(int)(degF*360);
	
	return deg+":"+min+"':"+sec+"\"";
}
public static float getDegForRad(float rad) {

	double degF = (rad*180/3.14159);
		
	return (float)degF;
}
public static long getMetersForLatitude(float lat) {
	return -(long)(lat*111242);
}
public static long getMetersForLongitude(float lon) {
	return (long)(lon*71698);
}
public static float getMPHForMPS(float mps) {
	return (float)(mps*2.24);
}
/**
 * 
 * @return float
 * @param east float
 * @param north float
 */
public static float getMPS(float east, float north) {
	return (float)Math.pow( (east*east)+(north*north) ,0.5);
}

private static int byte2int(byte data)
{
	int returnValue=(int) data;
		
	if(returnValue < 0)
	{
		returnValue = (returnValue & 127) + 128;
	}

	return(returnValue);
}

/**
 * Insert the method's description here.
 * Creation date: (09/08/03 13:00:25)
 * @return double
 * @param semicircles long
 */
public static double getDegForSemi(long semicircles) {
	
	double degs = (double)(semicircles * 180L) / (double)2147483648L;
	
	return degs;
}

public static double getDouble(byte[] data,int offset){

	long a = byte2int(data[offset]);
	long b = byte2int(data[offset+1]);
	long c = byte2int(data[offset+2]);
	long d = byte2int(data[offset+3]);

	long e = byte2int(data[offset+4]);
	long f = byte2int(data[offset+5]);
	long g = byte2int(data[offset+6]);
	long h = byte2int(data[offset+7]);
	
	long v = (h<<56) | (g<<48) | (f<<40) | (e<<32) | (d<<24) | (c<<16) | (b<<8) | a;
	
	return Double.longBitsToDouble(v);
}

public static float getFloat(byte[] data,int offset)
{
	int a = byte2int(data[offset]);
	int b = byte2int(data[offset+1]);
	int c = byte2int(data[offset+2]);
	int d = byte2int(data[offset+3]);
	
	int v = (d<<24) | (c<<16) | (b<<8) | a;
	
	return Float.intBitsToFloat(v);
}

public static int getInt(byte[] data,int offset)
{

	int a = byte2int(data[offset]);
	int b = byte2int(data[offset+1]);
	
	int v = (b<<8) | a;
	
	return v;
}

public static long getLong(byte[] data,int offset)
{
	int a = byte2int(data[offset]);
	int b = byte2int(data[offset+1]);
	int c = byte2int(data[offset+2]);
	int d = byte2int(data[offset+3]);
	
	int v = (d<<24) | (c<<16) | (b<<8) | a;
	
	return (long)v;
}
}
