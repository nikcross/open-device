/*
 * Created on 06-Dec-2005
 */
package org.one.stone.soup.open.device.wet.wired;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.one.stone.soup.open.device.Connection;
import org.one.stone.soup.open.device.comms.RS232Driver;
import org.one.stone.soup.open.device.comms.TimeoutException;

/**
 * @author Nicholas Cross
 */
public class WetWiredInterfaceMaster {

    /*CMD_ECHO				EQU	'e'
    CMD_RESET				EQU	'R'
    CMD_SET_BAUD			EQU	'b'
    CMD_SEND_SYNC			EQU	'S'
    CMD_SEND_PACKET			EQU	's'
    CMD_WRITE_BYTES			EQU	'w'
    CMD_READ_BYTES			EQU	'r'
    CMD_ENABLE_SLAVE		EQU	'E'
    CMD_DISABLE_SLAVE		EQU	'D'*/
    
    private static final byte CMD_ECHO = 'e';
    private static final byte CMD_RESET = 'R';
    private static final byte CMD_SET_BAUD = 'b';
    private static final byte CMD_SEND_SYNC = 'S';
    private static final byte CMD_SEND_PACKET = 's';
    private static final byte CMD_WRITE_BYTES = 'w';
    private static final byte CMD_READ_BYTES = 'r';
    private static final byte CMD_ENABLE_SLAVE = 'E';
    private static final byte CMD_DISABLE_SLAVE = 'D';
    
    private static final byte UNRECOGNISED_COMMAND = '?';
    
    private static final byte[] SYNC = new byte[]{(byte)0x00,(byte)0x00};
    
	private static WetWiredInterfaceMaster master; // Singleton instance
	
	private boolean initialised = false;
	private WetWiredConnection driver;
	private int nextID = 0x02;
	
	public static void main(String[] args)
	{
		try{
			WetWiredInterfaceMaster master = new WetWiredInterfaceMaster("COM8",null);
			//master.reset();
			
			while(true)
			{
				Packet packet = new Packet(1,(byte)'G',2);
				master.sendPacket(packet);
				
				Thread.sleep(1000);
			}
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	private class PacketQueueMonitor implements Runnable 
	{
		private Queue<Packet> packetQ = new ArrayBlockingQueue<Packet>(100);
		private boolean running = false;
		
		private PacketQueueMonitor()
		{
		    new Thread(this,"Packet Q Monitor").start();;
		}
		
		public void stopMonitor()
		{
		    running = false;
		}
		
		public void post(Packet packet)
		{
		    packetQ.add(packet);
		}
		
		public void run()
	    {
	    	running = true;
	        
	        while(running)
	        {
	            while(!packetQ.isEmpty())
	            {
	                Packet packet = (Packet)packetQ.poll();
	                _sendPacket(packet);
	            }
	        }
	        
	        running = false;
	    }
	}
	private PacketQueueMonitor monitor;
	
	public WetWiredInterfaceMaster(String portName,String alias) throws Exception
	{
		Connection connection = new RS232Driver(portName,alias);
		driver = new WetWiredConnection( connection );
		
		monitor = new PacketQueueMonitor();
		
/*		try{
		 driver.writeByte((byte)0x00);
		 Thread.sleep(100);
		 driver.writeByte((byte)0x00);
		 Thread.sleep(100);
		 
		 driver.readByte();
		 driver.readByte();
		 driver.readByte();
		 
		 Thread.sleep(5000);
	     
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}*/
		
		try{
		    reset(0);
		}
		catch(TimeoutException te)
		{
		    throw new Exception("Failed to reset interface. "+te.getMessage());
		}
	}
	
	public boolean isConnected() throws Exception
	{
	    if( driver.getInputStream()!=null )
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
	    }
	}
	
	public void disconnect() throws Exception
	{
		driver.disconnect();
	}
	
	public void reset(int id) throws TimeoutException,Exception {
	    try{
	        Packet packet = new Packet(id,CMD_RESET,1);
	        sendPacket(packet);
	        String data = new String(packet.response);
	        
			if( data.charAt(0)==CMD_RESET )
			{
				System.out.println("Interface reset started");
				initialised=true;
			}
			else if( data.charAt(0)==UNRECOGNISED_COMMAND )
			{
			    byte unrecognised = driver.readBytes(1)[0];
				throw new Exception("Unrecognised command: "+unrecognised);
			}
			else
			{
				throw new Exception("Bad magic: "+data);
			}
			try{Thread.sleep(100);}catch(Exception e){}
			
			driver.writeByte((byte)0x00);
			Thread.sleep(100);
			driver.writeByte((byte)0x00);
			Thread.sleep(5000);
			 
			data = new String(driver.readBytes(3));
			if( data.equals("wwi") )
			{
				System.out.println("Interface reset");
			}
			else
			{
				throw new Exception("Bad magic: "+data);
			}
			
			
	    }
	    catch(IOException e)
	    {
	        throw new Exception("IOException");
	    }
	}

	public void sendPacket(Packet packet) throws IOException,TimeoutException
	{
	    monitor.post(packet);
	    
	    try{
		    while(packet.isCompleted()==false)
		    {
		        try{Thread.sleep(5);}catch(Exception e){}
		    }
	    }
	    catch(Exception e)
	    {
	        if(e instanceof IOException)
	        {
	            throw (IOException)e;
	        }
	        else if(e instanceof TimeoutException)
	        {
	            throw (TimeoutException)e;
	        }
	    }
	}
	
	private void _sendPacket(Packet packet)
	{
	    try{
	    	driver.writeByte(SYNC[0]);
	    	try{ Thread.sleep(20); }catch(Exception e){}
	    	driver.writeByte(SYNC[1]);
	    	try{ Thread.sleep(20); }catch(Exception e){}
	    	driver.writeByte(packet.id[0]);
	    	try{ Thread.sleep(20); }catch(Exception e){}
	    	driver.writeByte(packet.id[1]);
	    	try{ Thread.sleep(20); }catch(Exception e){}
	        driver.writeBytesSafe(packet.request);
	    	packet.response = driver.readBytesSafe(packet.response.length);
	    	packet.setCompleted();
	    }
	    catch(TimeoutException toe)
	    {
	        packet.throwException( toe );
	    }
	    catch(IOException ioe)
	    {
	        packet.throwException( ioe );
	    }
	}
}
