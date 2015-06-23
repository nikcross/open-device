/*
 * Created on 06-Dec-2005
 */
package org.onestonesoup.opendevice.comms;

import gnu.io.CommDriver;
import gnu.io.CommPortIdentifier;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXPort;
import gnu.io.RXTXVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import org.onestonesoup.opendevice.Connection;

/**
 * @author Nicholas Cross
 */
public class RS232Driver implements Connection{

    public boolean DEBUG = true;
    
	private int BAUD = 9600;
	private static final int DATA_BITS = RXTXPort.DATABITS_8;
	private int STOP_BITS = RXTXPort.STOPBITS_1;
	private static final char FLOW_CONTROL = RXTXPort.FLOWCONTROL_NONE;

	private String commPortName;
	private String alias;
	private RXTXPort port;
	private InputStream iStream;
	private OutputStream oStream;
	private boolean connected;
	
	public static void main(String[] args)
	{
		try{
			String[] ports = RS232Driver.listPorts();
			if(ports.length==0){
				System.out.println("No serial ports available");
			}
			for(String port: ports) {
				System.out.println("port:"+port);
			}
		}
		catch(Exception e){e.printStackTrace();}
	}

	public static String[] listPorts(boolean availableOnly)
	{
		
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier	portId;
		ArrayList<String> list = new ArrayList<String>();
		
		while(ports.hasMoreElements())
		{
			portId = (CommPortIdentifier)ports.nextElement();
			if(portId.getPortType()!=CommPortIdentifier.PORT_SERIAL) {
				continue;
			}
			if( availableOnly && portId.isCurrentlyOwned() )
			{
				continue;
			}
			list.add(portId.getName());
		}
		
		return list.toArray(new String[]{});
	}	
	
	public static String[] listPorts()
	{
		return listPorts(false);
	}	
	
	public static String[] listAvailablePorts()
	{
		return listPorts(true);
	}
	
	public RS232Driver(String commPort,String alias)
	{
	    this.commPortName = commPort;
	    this.alias = alias;
	}
	
	public void connect()
	{
		String driverName = "gnu.io.RXTXCommDriver";
		try {
		CommDriver commdriver = (CommDriver)Class.forName(driverName).newInstance();
		commdriver.initialize();
		} catch (Exception e2) {
		e2.printStackTrace();
		}
//
		Enumeration ids = CommPortIdentifier.getPortIdentifiers();
		while(ids.hasMoreElements())
		{
			CommPortIdentifier id = (CommPortIdentifier)ids.nextElement();
			System.out.println( id.getName()+" - "+id.getPortType()+" - "+id.getCurrentOwner() );
		}
		
//	 Get reference for port
		CommPortIdentifier	portId;
		try{
			portId=CommPortIdentifier.getPortIdentifier(commPortName);
		}
		catch(Exception e)
		{
			System.out.println("Exception:"+e);
			return;
		}

//	 If already in use throw Exception
		if (portId.isCurrentlyOwned())
		{
			System.out.println("Detected "
				   + portId.getName()
				   + " in use by "
				   + portId.getCurrentOwner());
			return;
		}

		if(DEBUG) System.out.println("Comm Port reserved");

//	 Open the port at 9600 baud (8,1,N)
		try{
			port = (RXTXPort)portId.open(alias,10);
			port.setSerialPortParams(
				BAUD,
				DATA_BITS,
				STOP_BITS,
				FLOW_CONTROL
			);

			oStream=port.getOutputStream();
			iStream= port.getInputStream();

		}
		catch(Exception e)
		{
			System.out.println("Exception:"+e);
			return;
		}

		try{		 
		 if(DEBUG) System.out.print("Flushing Comm Port. Data:");
		 if(iStream.available()>0)
		 {
			 int in = iStream.read();
		     while(iStream.available()>0)
		     {
		         System.out.print((char)in);
		         in = iStream.read();
		     }
			}
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
		if(DEBUG) System.out.println("\n Comm Port open");
		connected = true;
	}
	public boolean isConnected()
	{
	    return connected;
	}
	public void disconnect()
	{
	    port.close();
	    connected = false;
	}

	public void setStopBits(int bits)
	{
		if(bits==1)
		{
			STOP_BITS = RXTXPort.STOPBITS_1;
		}
		else if(bits==2)
		{
			STOP_BITS = RXTXPort.STOPBITS_2;
		}	
	}
	
	public RXTXPort getPort()
	{
		return port;
	}
	
	public void setBaud(int baud) {
/*	    try{
	        port.setSerialPortParams(
				baud,
				DATA_BITS,
				STOP_BITS,
				FLOW_CONTROL
			);
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	        disconnect();
	    }*/
		BAUD = baud;
	}
	    
	public void finalize()
	{
		disconnect();
	}

	public InputStream getInputStream() {
		return iStream;
	}

	public OutputStream getOutputStream() {
		return oStream;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
	}

	public String getDefaultAlias() {
		return null;
	}
}
