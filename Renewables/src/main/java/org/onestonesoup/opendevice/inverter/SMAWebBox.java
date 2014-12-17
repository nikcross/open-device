package org.onestonesoup.opendevice.inverter;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.one.stone.soup.core.FileHelper;
import org.one.stone.soup.core.constants.TimeConstants;
import org.one.stone.soup.core.data.EntityTree;
import org.one.stone.soup.core.data.EntityTree.TreeEntity;
import org.one.stone.soup.core.data.XmlHelper;
import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Logger;
import org.onestonesoup.opendevice.comms.HttpConnection;

public class SMAWebBox implements Runnable, Logger {
	private static final String DEFAULT_ALIAS="SMA Web Box";
	private String alias = DEFAULT_ALIAS;
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}
	private boolean debug = false;
	private boolean running = false;
	private Connection connection;
	private EntityTree dataLog;
	private long power;
	private long logPeriod = TimeConstants.SECOND*10;

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
	
	public SMAWebBox(Connection connection) throws Exception
	{
		this.connection = connection;
		
		new Thread(this,"SMA Web Box").start();
	}
	
	public void run() {
		if(running==true){ return; }
		running = true;
		clearDataLog();
		while(running)
		{
			try{
				Thread.sleep(logPeriod);
				readData();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		running = false;
	}

	private void readData()
	{
		try{
			connection.connect();
			String script = "{\"version\": \"1.0\",\"proc\": \"GetPlantOverview\",\"id\": \"1\",\"format\": \"JSON\"}";
			String dataOut = "RPC="+URLEncoder.encode( script,"UTF-8" );
			
			PrintWriter out = new PrintWriter( connection.getOutputStream() );
			out.print( dataOut );
			out.flush();			
	
			/*JavascriptEngine jsEngine = new JavascriptEngine();
			Map<String,Object> jsObjects = new HashMap<String,Object>();
			jsObjects.put("SMAWebBox",this);
			String dataIn = FileHelper.loadFileAsString( connection.getInputStream() );
			
			script = "data=eval("+dataIn+").result.overview;"+
			"pwr=0;energyToday=0;energyTotal=0;"+
			"for(i=0;i<data.length;i++)" +
			"{" +
			"if(data[i].meta==\"GriPwr\") pwr=parseFloat(data[i].value);" +
			"if(data[i].meta==\"GriEgyTdy\") energyToday=parseFloat(data[i].value);" +
			"if(data[i].meta==\"GriEgyTot\") energyTotal=parseFloat(data[i].value);" +
			"}" +
			"SMAWebBox.log(pwr,energyToday,energyTotal);";
			jsEngine.runScript("SMA Log Processor",script,jsObjects);*/
			try{ connection.disconnect(); }catch(Exception e2){}
		}
		catch(Throwable e)
		{
			try{ connection.disconnect(); }catch(Exception e2){}
			e.printStackTrace();
		}
	}
	
	public void log(double power,double energyToday,double energyTotal)
	{
		TreeEntity entry = dataLog.addChild("entry");
		entry.addChild("timestamp").setValue(""+System.currentTimeMillis());
		entry.addChild("power").setValue(""+power);
		entry.addChild("energyToday").setValue(""+energyToday);
		entry.addChild("energyTotal").setValue(""+energyTotal);
		
		showDebug("Read SMA WebBox Power:"+power+" Energy Today:"+energyToday+" EnergyTotal:"+energyTotal);
	}
	
	public void clearDataLog() {
		dataLog = new EntityTree("smaWebBox");
		dataLog.setAttribute("device", "SMA WebBox");
	}

	public boolean dataAvailable() {
		if(dataLog.getChildren().size()==0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public EntityTree getDataLog() {
		EntityTree log = dataLog;
		clearDataLog();
		
		double power = 0;
		int entriesCount=0;
		double energyToday = 0;
		double energyTotal = 0;
		for(TreeEntity entry: log.getChildren("entry"))
		{
			power += Double.parseDouble( entry.getChild("power").getValue() );
			
			if(power!=0)
			{
				entriesCount++;
			}
			
			energyToday = Double.parseDouble( entry.getChild("energyToday").getValue() );
			energyTotal = Double.parseDouble( entry.getChild("energyTotal").getValue() );
		}
		if(entriesCount>2)
		{
			power = power/entriesCount;
		}
		
		EntityTree tempData = new EntityTree("log");
		tempData.addChild("power").setValue(""+power);
		tempData.addChild("energyToday").setValue(""+energyToday);
		tempData.addChild("energyTotal").setValue(""+energyTotal);
		
		showDebug(XmlHelper.toXml(tempData));
		
		return tempData;
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
		logPeriod = timeSeconds*TimeConstants.SECOND;
	}
	
	public void kill()
	{
		running=false;
	}
	public Device getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}
	public String getHost() {
		if(connection instanceof HttpConnection) {
			return ((HttpConnection)connection).getHost();
		} else {
			return null;
		}
	}
	
	public long getPower() {
		return power;
	}
}
