package org.onestonesoup.opendevice.tapemeasure;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.one.stone.soup.core.data.EntityTree;
import org.one.stone.soup.core.data.EntityTree.TreeEntity;
import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Logger;

public class MaxSonar implements Logger,Runnable {
	private static final String DEFAULT_ALIAS="Max Sonar";
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
	
	private EntityTree dataLog;
	private Connection connection;
	private InputStream iStream;
	private long logPeriod;
	private boolean running=false;
	
	public MaxSonar(Connection connection) throws Exception
	{
		this.connection=connection;
		connection.connect();
		iStream = connection.getInputStream();
		new Thread(this,"MaxSonar Logger").start();
	}
	
	public void clearDataLog()
	{
		dataLog = new EntityTree("maxSonar");
		dataLog.setAttribute("device", "Max Sonar Distance Sensor");
	}
	
	public boolean dataAvailable()
	{
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
		int distance=0;
		
		List<TreeEntity> entries = log.getChildren("entry");
		for(TreeEntity entry: entries)
		{
			distance += Integer.parseInt( entry.getChild("distance").getValue() );
		}
		distance = distance/entries.size();
		
		EntityTree tempData = new EntityTree("log");
		tempData.addChild("distance").setValue(""+distance);

		return tempData;
	}

	public void kill()
	{
		try{
			connection.disconnect();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		running = false;
	}

	public void setDebug(boolean state) {
		// TODO Auto-generated method stub
		
	}

	public void setLogPeriod(long timeSeconds) {
		this.logPeriod = timeSeconds*1000;
	}

	public void run()
	{
		if(running==true){ return; }
		running = true;
		clearDataLog();
		while(running) {
			try {
				readData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try{ Thread.sleep(logPeriod); }catch(Exception e){}
		}
		running = false;
	}
	
	private void readData() throws Exception
	{
		int i=iStream.read();
		while(i!='R')
		{
			i=iStream.read();
			if(i==-1) return;
		}
		i=iStream.read();
		if(i==-1) return;
		
		StringBuffer data = new StringBuffer();
		while(i!=13)
		{
			data.append((char)i);
			
			i=iStream.read();
			if(i==-1) return;
		}
		int distance = Integer.parseInt(data.toString());
		
		logData(distance);
	}
	
	private void logData(int distance)
	{
		TreeEntity entry = dataLog.addChild("entry");
		entry.addChild("distance").setValue(""+distance);
	}
	public Device getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}
}
