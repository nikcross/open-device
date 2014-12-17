package org.onestonesoup.opendevice.wetwired;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.comms.StopWatch;
import org.onestonesoup.opendevice.comms.TimeoutException;

public class WetWiredConnection implements Connection {

	public boolean DEBUG = false;
	
	private static int BYTE_SAFE_OK	=	0;
	private static int BYTE_SAFE_ERROR	=	-1;
	private static long MAX_BYTE_TIME = 100;
	private static long BYTE_TICK_TIME = 10;	
	
	private String alias;
	private Connection connection;
	private InputStream iStream;
	private OutputStream oStream;
	
	public WetWiredConnection(Connection connection) throws Exception
	{
		this.connection = connection;
		iStream = connection.getInputStream();
		oStream = connection.getOutputStream();
	}
	
	public byte[] readBytesSafe(int size) throws IOException,TimeoutException
	{
		byte[] data = new byte[size];

		int loop=0;
		try{
			for(loop=0;loop<size;loop++)
			{
			    data[loop] = readByteSafe();
			}
		}
		catch(TimeoutException e)
		{
		    throw new TimeoutException("Timeout reading byte "+loop);
		}
		return data;
	}

	public byte[] readBytes(int size) throws IOException,TimeoutException
	{
	    StopWatch timer = new StopWatch();
		byte[] data = new byte[size];

		int loop=0;
		for(loop=0;loop<size;loop++)
		{
		    int in = iStream.read();
		    while(in==-1 && timer.elapsedTime()<MAX_BYTE_TIME)
		    {
		        in = iStream.read();
		    }
		    if(DEBUG) System.out.println("wwi -> "+in+" ("+(char)in+")");
	        
		    if(timer.elapsedTime()>MAX_BYTE_TIME)
		    {
		        break;
		    }
		    
		    data[loop] = (byte)in;
		}
		
		if( timer.elapsedTime()>MAX_BYTE_TIME )
		{
		    throw new TimeoutException("Timeout reading byte "+loop);
		}
		return data;
	}	
	
	public byte readByte() throws IOException,TimeoutException
	{
	    StopWatch timer = new StopWatch();

		    int in = iStream.read();
		    while(in==-1 && timer.elapsedTime()<MAX_BYTE_TIME)
		    {
		        in = iStream.read();
		    }
		    if(DEBUG) System.out.println("wwi -> "+in+" ("+(char)in+")");
	        
		    if(timer.elapsedTime()>MAX_BYTE_TIME)
		    {
		    	throw new TimeoutException("Timeout reading byte.");
		    }
		    
		    return (byte)in;
	}	
	
	public void writeByte(byte data) throws IOException
	{
		oStream.write(data);
	}	
	
	public void writeBytes(byte[] data) throws IOException
	{
		oStream.write(data);
	}
	
	public byte readByteSafe() throws IOException,TimeoutException
	{
	    if(DEBUG) System.out.println("Read byte (Safe)");
	    StopWatch timer = new StopWatch();
	    while(timer.elapsedTime()<MAX_BYTE_TIME)
	    {
	        int in = iStream.read();
	        if(in==-1)
	        {
	            continue;
	        }
	        if(DEBUG) System.out.println("wwi -> "+in+" ("+(char)in+")");
	        
	        int inComp = iStream.read();
	        if(DEBUG) System.out.println("wwi -> "+inComp);
	        
		    if(in == (~inComp&255))
		    {
		        oStream.write(BYTE_SAFE_OK);
		        if(DEBUG) System.out.println("wwi <- "+BYTE_SAFE_OK+" (OK)");
		        return (byte)in;
		    }
		    else
		    {
		        oStream.write(BYTE_SAFE_ERROR);
		        if(DEBUG) System.out.println("wwi <- "+BYTE_SAFE_OK+" (ERROR)");
		    }
	    }
	    throw new TimeoutException("Timeout reading byte");
	}
	
	public void writeBytesSafe(byte[] data) throws IOException,TimeoutException
	{
	    int loop=0;
	    try{
		    for(loop=0;loop<data.length;loop++)
		    {
		        writeByteSafe(data[loop]);
		    }
		}
		catch(TimeoutException e)
		{
		    throw new TimeoutException("Timeout writing byte "+loop);
		}
	}	
	
	public void writeByteSafe(byte data) throws IOException,TimeoutException
	{
	    StopWatch timer = new StopWatch();
	    int in = 0;
	    if(DEBUG) System.out.println("Write byte (safe) "+data);
	    
	    while(timer.elapsedTime()<MAX_BYTE_TIME)
	    {  
		    oStream.write(~data&255);
		    if(DEBUG) System.out.println("wwi <- "+(~data&255));
	        
		    oStream.write(data);
		    if(DEBUG) System.out.println("wwi <- "+data+" ("+(char)data+")");
		    
		    in = iStream.read();
		    if(DEBUG) System.out.println("wwi -> "+in+"\n");
		    if(in==BYTE_SAFE_OK)
		    {
		        return;
		    }
	    	try{ Thread.sleep(10); }catch(Exception e){}
	    }
	    throw new TimeoutException("Timeout reading byte");
	}

	public InputStream getInputStream() throws Exception {
		return connection.getInputStream();
	}

	public OutputStream getOutputStream() throws Exception {
		return connection.getOutputStream();
	}

	public void connect() throws Exception {
		connection.connect();
	}

	public void disconnect() throws Exception {
		connection.disconnect();
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias=alias;
	}

	public String getDefaultAlias() {
		return null;
	}
}
