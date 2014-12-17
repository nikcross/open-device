package org.onestonesoup.opendevice.wetwired;

/**
 * @author Nicholas Cross
 *
 */
public class Packet {

    private Exception exception = null;
    private boolean complete = false;

    public byte[] id;
    public byte[] request;
    public byte[] response;
    
    public void setCompleted()
    {
        complete = true;
    }
    public void throwException(Exception e)
    {
        exception = e;
        complete = true;
    }
    public boolean isCompleted() throws Exception
    {
        if(exception!=null)
        {
            throw exception;
        }
        return complete;
    }
    
    public Packet(int id,String command,int responseLength)
    {
        this.id = new byte[2];
        this.id[0] = (byte)(id&255);
        this.id[1] = (byte)((id>>8)&255); 
        
        request = command.getBytes();
        response = new byte[responseLength];
    }

    public Packet(int id,byte data,int responseLength)
    {
        this.id = new byte[2];
        this.id[0] = (byte)(id&255);
        this.id[1] = (byte)((id>>8)&255); 
        
        request = new byte[1];
        request[0] = data;
        
        response = new byte[responseLength];
    }    
    
    public Packet(int id,int data,int responseLength)
    {
        this.id = new byte[2];
        this.id[0] = (byte)(id&255);
        this.id[1] = (byte)((id>>8)&255);    	
    	
        request = new byte[2];
        request[0] = (byte)(data&255);
        request[1] = (byte)((data>>8)&255);
        
        response = new byte[responseLength];
    }
    
    public Packet(int id,byte[] data,int responseLength)
    {
        this.id = new byte[2];
        this.id[0] = (byte)(id&255);
        this.id[1] = (byte)((id>>8)&255);  
        
        request = data;
        response = new byte[responseLength];
    }
}
