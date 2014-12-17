package org.onestonesoup.opendevice.smartmeter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.one.stone.soup.core.constants.TimeConstants;
import org.one.stone.soup.core.data.EntityTree;
import org.one.stone.soup.core.data.EntityTree.TreeEntity;
import org.one.stone.soup.core.data.XmlHelper;
import org.onestonesoup.opendevice.ArchiveListener;
import org.onestonesoup.opendevice.Archivist;
import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Logger;
import org.onestonesoup.opendevice.PowerLogger;
import org.onestonesoup.opendevice.Thermometer;
import org.onestonesoup.opendevice.comms.RS232Driver;

public class CurrentCostPowerMonitor implements Runnable,Logger,Archivist,PowerLogger,Thermometer{
	private static final String DEFAULT_ALIAS="Current Cost Power Monitor";
	
	public static void main(String[] args) throws Exception {
		
		String[] ports = RS232Driver.listPorts();
		for(String port: ports) {
			System.out.println("port:"+port);
		}
		

		System.out.println("Using port:"+args[0]);
		RS232Driver connection = new RS232Driver(args[0],DEFAULT_ALIAS);
		connection.setBaud(57600);
		CurrentCostPowerMonitor monitor = new CurrentCostPowerMonitor(connection);
		
		int count = 60;
		while(monitor.dataAvailable()==false && count>0) {
			Thread.sleep(1000);
			count--;
		}
	}
	
	private String alias = DEFAULT_ALIAS;
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}
	public class CurrentCostSensor implements Logger,PowerLogger,Thermometer {
		private String DEFAULT_ALIAS=null;
		private String alias = DEFAULT_ALIAS;
		public String getDefaultAlias() {
			return DEFAULT_ALIAS;
		}
		
		private CurrentCostPowerMonitor parent;
		private int sensor;
		private int power;
		private double units;
		private double temperature;
		private long lastLogTimeStamp;
		private EntityTree dataLog;
		
		public class LogData {
			public int channelA=0;
			public int channelB=0;
			public int channelC=0;
			public double temperature=0;
		}
		private LogData logData = new LogData();
		
		CurrentCostSensor(CurrentCostPowerMonitor parent,int sensor) {
			this.parent = parent;
			this.sensor = sensor;
			DEFAULT_ALIAS = CurrentCostPowerMonitor.DEFAULT_ALIAS+".sensor."+sensor;
			this.alias = DEFAULT_ALIAS;
			clearDataLog();
		}

		public void clearDataLog() {
			dataLog = new EntityTree("currentCost");
			dataLog.setAttribute("device",getAlias());
		}

		private void logData(int channelA,int channelB,int channelC,double temperature) {
			TreeEntity entry = dataLog.addChild("entry");
			entry.addChild("timestamp").setValue(""+System.currentTimeMillis());
			entry.addChild("sensor").setValue(""+sensor);
			entry.addChild("channelA").setValue(""+channelA);
			entry.addChild("channelB").setValue(""+channelB);
			entry.addChild("channelC").setValue(""+channelC);
			entry.addChild("temperature").setValue(""+temperature);
			
			logData.channelA = channelA;
			logData.channelB = channelB;
			logData.channelC = channelC;
			logData.temperature = temperature;
		}
		
		public LogData getLiveData() {
			return logData;
		}
		
		public boolean dataAvailable() {
			if(dataLog.getChildren().size()<2)
			{
				return false;
			}
			else
			{
				return true;
			}
		}

		public EntityTree getDataLog() {
			if(dataAvailable()==false) {
				return null;
			}
			
			EntityTree logIn = dataLog;
			clearDataLog();
			
			List<TreeEntity> entries = logIn.getChildren("entry");
			int watts = 0;
			int watts2 = 0;
			int watts3 = 0;
			double temp = 0;
			int entriesCount = 0;
			int tempEntriesCount = 0;
			long startTimeStamp = 0;
			long endTimeStamp = 0;
			boolean isFirst = true;
			for(TreeEntity entry: entries)
			{
				endTimeStamp = Long.parseLong( entry.getChild("timestamp").getValue() );
				if(isFirst) {
					startTimeStamp = endTimeStamp;
					isFirst=false;
				}
				int logSensor = Integer.parseInt(entry.getChild("sensor").getValue());
				if(logSensor!=sensor) continue;
				
				watts += Integer.parseInt( entry.getChild("channelA").getValue() );
				watts2 += Integer.parseInt( entry.getChild("channelB").getValue() );
				watts3 += Integer.parseInt( entry.getChild("channelC").getValue() );
				
				if(watts!=0 || watts2!=0 || watts3!=0)
				{
					entriesCount++;
				}
				
				if(sensor==0) {
					temp += Double.parseDouble( entry.getChild("temperature").getValue() );
					tempEntriesCount++;
				}
			}

			EntityTree log = new EntityTree("log");
			if(entriesCount>0)
			{
				watts = watts/entriesCount;
				watts2 = watts2/entriesCount;
				watts3 = watts3/entriesCount;
			}
			if(tempEntriesCount>0) {
				temp = temp/tempEntriesCount;
			}
			log.addChild("watts").setValue(""+watts);
			log.addChild("watts2").setValue(""+watts2);
			log.addChild("watts3").setValue(""+watts3);
			log.addChild("power").setValue(""+(watts+watts2+watts3));
			log.addChild("startTimeStamp").setValue(""+startTimeStamp);
			log.addChild("endTimeStamp").setValue(""+endTimeStamp);
			power = watts+watts2+watts3;
			if(endTimeStamp==startTimeStamp) {
				units=0;
			} else {
				units = (double)power/((endTimeStamp-startTimeStamp)*TimeConstants.HOUR);
			}
			lastLogTimeStamp = endTimeStamp;
			temperature = temp;
			
			log.addChild("temperature").setValue(""+temperature);
			
			showDebug(XmlHelper.toXml(log));
			
			return log;
		}
		
		public void setLogPeriod(long timeSeconds) {
			parent.setLogPeriod(timeSeconds);
		}

		public void kill() {
			parent.kill();
		}

		public void setDebug(boolean state) {
			parent.setDebug(state);
		}

		public void setParameter(String key, String value) {
			parent.setParameter(key, value);
		}

		public String getAlias() {
			return alias;
		}

		public String getParameter(String key) {
			return parent.getParameter(key);
		}

		public void setAlias(String alias) {
			this.alias=alias;
		}

		public int getPower() {
			return power;
		}

		public double getUnits() {
			return units;
		}
		
		public double getTemperature() {
			return temperature;
		}

		public Device getParent() {
			return parent;
		}

		public boolean hasParent() {
			return true;
		}

		public long getLastLogTimeStamp() {
			return lastLogTimeStamp;
		}
		
	}
	private List<CurrentCostSensor> sensors = new ArrayList<CurrentCostSensor>();
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
	
//	public static boolean testPort(RS232Driver connection)
	public static boolean testPort(Connection connection)
	{
		try{
			connection.connect();	
			
			//create in and out streams
			InputStream in = connection.getInputStream();
			OutputStream out = connection.getOutputStream();
			
			for(int loop=0;loop<5;loop++)
			{
				EntityTree data = XmlHelper.loadXml(in, true);
				if(data!=null && data.getName().equals("msg"))
				{
					return true;
				}
				//clear in stream
				long time = System.currentTimeMillis()+1000;
				
				out.write( 0x06 );
				out.flush();
				try{Thread.sleep(50);}catch(Exception e){}
				
				int i = in.read();
				System.out.println("Clearing input stream");
				while( i!=-1 && System.currentTimeMillis()<time )
				{
					i = in.read();
					System.out.println("Dumped:"+i);
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}		
		return false;
	}
	
	private boolean running=false;
	private Connection connection;
	private boolean debug;
	
	public CurrentCostPowerMonitor( Connection connection ) throws Exception
	{
		for(int i=0;i<10;i++) {
			sensors.add(new CurrentCostSensor(this,i));
		}
		
		this.connection = connection;
		connection.connect();
		
		new Thread(this,"CurrentCost Power Monitor").start();
	}
	
	public List<CurrentCostSensor> getSensors() {
		return sensors;
	}
	
	public EntityTree getDataLog()
	{
		return sensors.get(0).getDataLog();
	}
	
	public void clearDataLog()
	{
		sensors.get(0).clearDataLog();
	}
	
	public boolean dataAvailable()
	{
		if(sensors.get(0).dataLog.getChildren().size()<2)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void run()
	{
		if(running==true){ return; }
		running = true;
		clearDataLog();
		while(running)
		{
			try{
				readData();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		running = false;
	}
	
	private void readData() throws Exception
	{
		EntityTree data = XmlHelper.loadXml( connection.getInputStream(),false );
		if(data==null)
		{
			return;
		}
		
		if(data.getChild("hist")!=null)
		{
			for(ArchiveListener listener: listeners) {
				listener.receiveArchive(this, data);
			}
			return;
		}
		if(data.getChild("ch1")==null)
		{
			data.addChild("ch1").addChild("watts").setValue("0");
		}
		if(data.getChild("ch2")==null)
		{
			data.addChild("ch2").addChild("watts").setValue("0");
		}
		if(data.getChild("ch3")==null)
		{
			data.addChild("ch3").addChild("watts").setValue("0");
		}
		if(data.getChild("tmpr")==null)
		{
			data.addChild("tmpr").setValue("0");
		}
		
		showDebug( XmlHelper.toXml(data) );
		
		int sensor = Integer.parseInt( data.getChild("sensor").getValue() );
		int channelA = Integer.parseInt( data.getChild("ch1").getChild("watts").getValue() );
		int channelB = Integer.parseInt( data.getChild("ch2").getChild("watts").getValue() );
		int channelC = Integer.parseInt( data.getChild("ch3").getChild("watts").getValue() );
		double temperature = Double.parseDouble( data.getChild("tmpr").getValue() );
		

		this.sensors.get(sensor).logData(channelA,channelB,channelC,temperature);
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
	
	private List<ArchiveListener> listeners = new ArrayList<ArchiveListener>();
	
	public void addListener(ArchiveListener listener) {
		listeners.add(listener);
	}
	public int getPower() {
		return sensors.get(0).getPower();
	}
	public double getTemperature() {
		return sensors.get(0).getTemperature();
	}
	public Device getParent() {
		return null;
	}
	public boolean hasParent() {
		return false;
	}
	public long getLastLogTimeStamp() {
		return sensors.get(0).getLastLogTimeStamp();
	}
	public double getUnits() {
		return sensors.get(0).getUnits();
	}
}
