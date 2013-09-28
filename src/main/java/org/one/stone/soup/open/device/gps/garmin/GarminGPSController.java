package org.one.stone.soup.open.device.gps.garmin;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import org.one.stone.soup.open.device.Connection;

public class GarminGPSController implements Runnable{

	private Connection connection;	
	
// Thread data
	private int waitTime = 20;

// Thread state
	private boolean running = false;
	private boolean stopped = true;
	private boolean waitingResponse = false;
	
//Data out
	private BufferedInputStream iStream;
	private Vector packetOut = new Vector();

//Data in
	private OutputStream oStream;
protected GarminGPSController(Connection connection) {
	super();
	this.connection=connection;
}
public void run()
{
	running=true;
	stopped=false;
	GarminPacket currentPacket = null;

	try{	
		while(running)
		{
try{
// Wait between polling
			try{Thread.sleep(waitTime);}catch(Exception e){}

// If packet(s) waiting to be sent
			if(packetOut.size()>0)
			{
// Generate new Garmin packet for request
				GarminPacket newPacket = (GarminPacket)( packetOut.elementAt(0) );

// If the request is 'forced not to wait' or no packet is waiting to be sent
				if(
					newPacket.overidesWaiting() ||
					(
						currentPacket==null || 
						(currentPacket!=null && currentPacket.isWaiting()==false)
					)
				)
				{
// Send the request and remove it from the queue
					currentPacket = newPacket;
					oStream.write(currentPacket.getBytes());
					oStream.flush();

/*	byte[] data = currentPacket.getBytes();
	for(int loop=0;loop<data.length;loop++)
	{
		System.out.println(">B["+loop+"]="+data[loop]);
	}*/
					
					packetOut.removeElementAt(0);
				}
			}

// Wait to give GPS time to respond
			try{Thread.sleep(waitTime);}catch(Exception e){}

// If a packet is waiting to be received
			if(iStream.available()>0)
			{
// Wait for the buffer to fill
				try{Thread.sleep(waitTime);}catch(Exception e){}
// Read the packet data
				/*byte[] resp = new byte[iStream.available()];
				iStream.read(resp);*/

				byte[] data = new byte[1000];
				data[0] = (byte)iStream.read();
				int pos=1;
				while(
					pos<2 ||
					!(data[pos-2]==GarminPacketFactory.DLE && data[pos-1]==GarminPacketFactory.ETX)
				)
				{
					data[pos] = (byte)iStream.read();
					pos++;
				}
				byte[] resp = new byte[pos];
				System.arraycopy(data,0,resp,0,pos);
				
// Create a Garmin packet
				GarminPacketFactory.setPacketResponse(currentPacket,resp);
// Notify packet listeners
				currentPacket.notifyListeners();
			}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally
	{
		stopped=true;
		running=false;
	}
}
/**
 * 
 * @param packet wet.wired.gps.garmin.GarminPacket
 */
public void sendPacket(GarminPacket packet)
{
	// add a packet to the send queue
	packetOut.addElement(packet);
}
public void startComms()
{
// Check if controller already running
	if(running)
	{
		System.out.println("GPS Controller already started");
		return;
	}
	
	try{
		oStream=connection.getOutputStream();
		iStream= new BufferedInputStream( connection.getInputStream() );	
	}
	catch(Exception e)
	{
		System.out.println("Exception:"+e);
		return;
	}

	System.out.println("Comm Port open");
	
/*
	controller.sendPacket( GPSPacketFactory.getStopPVT() );
*/

// Start monitor thread
	Thread thread = new Thread(this,"Garmin GPS monitor thread");
	thread.start();
}
public void stopComms() throws Exception
{
	running = false;

// wait for thread to stop
	/*while(stopped == false)
	{
		try{ Thread.sleep(200); }catch(Exception e){}
	}*/

// close comm port
	connection.disconnect();
}
/**
 * Insert the method's description here.
 * Creation date: (08/08/03 20:34:22)
 */
public void turnOff()
{
	sendPacket( GarminPacketFactory.getTurnOffPacket() );
}
}
