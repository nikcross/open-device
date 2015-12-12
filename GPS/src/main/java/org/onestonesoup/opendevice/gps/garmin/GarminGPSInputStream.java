package org.onestonesoup.opendevice.gps.garmin;

import java.io.*;

public class GarminGPSInputStream {
	private InputStream iStream;
public GarminGPSInputStream(InputStream iStream) {
	super();

	this.iStream = iStream;
}
public double readDouble() throws IOException{

	long a = iStream.read();
	long b = iStream.read();
	long c = iStream.read();
	long d = iStream.read();

	long e = iStream.read();
	long f = iStream.read();
	long g = iStream.read();
	long h = iStream.read();

	if(e==-1 || f==-1 || g==-1 || h==-1)
		throw new IOException("Early end of stream found");
	
	if(a==-1 || b==-1 || c==-1 || d==-1)
		throw new IOException("Early end of stream found");
	
	long v = h<<56 | g<<48 | f<<40 | e<<32 |d<<24 | c<<16 | b<<8 | a;


	
	return Double.longBitsToDouble(v);
}
public float readFloat() throws IOException{

	int a = iStream.read();
	int b = iStream.read();
	int c = iStream.read();
	int d = iStream.read();

	if(a==-1 || b==-1 || c==-1 || d==-1)
		throw new IOException("Early end of stream found");
	
	int v = d<<24 | c<<16 | b<<8 | a;
	
	return Float.intBitsToFloat(v);
}
public int readInt() throws IOException{

	int a = iStream.read();
	int b = iStream.read();

	if(a==-1 || b==-1)
		throw new IOException("Early end of stream found");
	
	int v = b<<8 | a;
	
	return v;
}
public long readLong() throws IOException{

	int a = iStream.read();
	int b = iStream.read();
	int c = iStream.read();
	int d = iStream.read();

	if(a==-1 || b==-1 || c==-1 || d==-1)
		throw new IOException("Early end of stream found");
	
	int v = d<<24 | c<<16 | b<<8 | a;
	
	return (long)v;
}
}
