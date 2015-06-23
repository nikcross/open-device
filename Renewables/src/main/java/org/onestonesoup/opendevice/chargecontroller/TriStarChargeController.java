package org.onestonesoup.opendevice.chargecontroller;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.onestonesoup.core.data.EntityTree;
import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Logger;
import org.onestonesoup.opendevice.comms.RS232Driver;
import org.onestonesoup.opendevice.modbus.ModbusDevice;
public class TriStarChargeController extends ModbusDevice implements Logger{

	private static final String DEFAULT_ALIAS="Tri Star Charge Controller";
	private String alias = DEFAULT_ALIAS;
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}
	private Map<String,String> parameters = new HashMap<String,String>();
	public String getAlias() {
		return alias;
	}
	public String getParameter(String key) {
		return parameters.get(key);
	}
	public void setAlias(String alias) {
		this.alias=alias;
	}
	public void setParameter(String key, String value) {
		parameters.put(key,value);
	}	
	
	public static void main(String[] args)
	{	
		try{
		RS232Driver driver = new RS232Driver("COM8","TriStar");
		TriStarChargeController controller = new  TriStarChargeController(driver);
		
		System.out.println("Amp Hours:"+controller.getAmpHours());
		System.out.println("Heatsink Temp:"+controller.getHeatsinkTemperature());
		System.out.println("Battery Voltage:"+controller.getBatteryVoltage());
		System.out.println("Panel Voltage:"+controller.getPanelVoltage());
		System.out.println("Charging Current:"+controller.getChargingCurrent());

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private boolean debug = true;
	private Connection connection;

	public TriStarChargeController(Connection connection) throws Exception
	{
		this.connection = connection;
		connection.connect();
	}
	
      private int id = 0x01;
      
      public double getPanelVoltage() throws Exception
      {
    	  try{
            byte[] frame = createReadHoldingRegistersFrame(id, 11, 1);
            frame = processFrame(connection.getOutputStream(),connection.getInputStream(),frame);
            int value = getFrameWordField(frame,0);
          
            double voltage = (value*139.15)/32768;
            showDebug("TriStar Panel Voltage:"+voltage);    
            return voltage;
    	  }
    	  catch(IOException e)
    	  {
    		  e.printStackTrace();
    		  throw e;
    	  }
      }
      
      public double getChargingCurrent() throws Exception
      {
    	  try{
            byte[] frame = createReadInputRegistersFrame(id, 12, 1);
            frame = processFrame(connection.getOutputStream(),connection.getInputStream(),frame);
            int value = getFrameWordField(frame,0);
            
            double current = (value*66.667)/32768;
            showDebug("TriStar Charging Current:"+current);            
           return current;
    	  }
    	  catch(IOException e)
    	  {
    		  e.printStackTrace();
    		  throw e;
    	  }
      }
      
      public double getBatteryVoltage() throws Exception
      {
    	  try{
            byte[] frame = createReadHoldingRegistersFrame(id, 0x08, 1);
            frame = processFrame(connection.getOutputStream(),connection.getInputStream(),frame);
            int value = getFrameWordField(frame,0);
            
            double voltage = (value*96.667)/32768; // Checked an OK
            showDebug("TriStar Battery Voltage:"+voltage);            

            return voltage;
    	  }
    	  catch(IOException e)
    	  {
    		  e.printStackTrace();
    		  throw e;
    	  }
      }
    
      public double getHeatsinkTemperature() throws Exception
      {
    	  try{
            byte[] frame = createReadHoldingRegistersFrame(id, 15, 1);
            frame = processFrame(connection.getOutputStream(),connection.getInputStream(),frame);
            int value = getFrameWordField(frame,0);
           
            double temperature = value;
            if(value>127)
            {
            	temperature = value-256;
            }

            showDebug("TriStar Heatsink Temperature:"+temperature);            
            
            return temperature;
    	  }
    	  catch(IOException e)
    	  {
    		  e.printStackTrace();
    		  throw e;
    	  }
      }    
      
      public double getAmpHours() throws Exception
      {
    	  try{
            byte[] frame = createReadHoldingRegistersFrame(id, 20, 2);
            frame = processFrame(connection.getOutputStream(),connection.getInputStream(),frame);
            int value = getFrameWordField(frame,1);
            value = value<<16;
            value += getFrameWordField(frame,0);
            
            showDebug("TriStar Amp Hours:"+value);            
            
            return ((double)value)/10.0;
    	  }
    	  catch(IOException e)
    	  {
    		  e.printStackTrace();
    		  throw e;
    	  }
      }

	public void clearDataLog() {
		// No Action
	}

	public boolean dataAvailable() {
		return true;
	}

	public EntityTree getDataLog() {
		EntityTree triStar = new EntityTree("log");
		try{
			triStar.addChild("ampHours").setValue(""+getAmpHours());
			triStar.addChild("batteryVoltage").setValue(""+getBatteryVoltage());
			triStar.addChild("chargingCurrent").setValue(""+getChargingCurrent());
			triStar.addChild("heatsinkTemperature").setValue(""+getHeatsinkTemperature());
			triStar.addChild("panelVoltage").setValue(""+getPanelVoltage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return triStar;
	}

	public void setDebug(boolean state) {
		debug = state;
	}  
	
	public void showDebug(String message)
	{
		if(debug==false)
		{
			return;
		}
		System.out.println(message);
	}

	public void setLogPeriod(long timeSeconds) {
		// TODO Auto-generated method stub
		
	}
	
	public void kill(){}
	public Device getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}
}