package org.onestonesoup.opendevice.inverter;

import java.lang.reflect.Constructor;

import org.one.stone.soup.core.data.EntityTree;
import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Logger;
import org.onestonesoup.opendevice.comms.HttpConnection;

public class TestSMAWebBox {

	public static final void main(String[] args)
	{
		try{
		TestSMAWebBox boxTest = new TestSMAWebBox();
		DataLogger logger = boxTest.createLogger(
				null,
				"name",
				"192.168.1.200",
				"id",
				"org.one.stone.soup.building.control.sma.webbox.SMAWebBox",
				"true",
				"10"
				);
		
		logger.logger.getDataLog();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private class DataLogger
	{
		public EntityTree config;
		public Logger logger;
	}
	
	private DataLogger createLogger(
			EntityTree loggerConfig,
			String name,
			String host,
			String id,
			String clazz,
			String debug,
			String logPeriod
		) throws Exception
	{
		System.out.println("Createing logger "+name+" for host "+host+" id:"+id+" class:"+clazz);
		Logger logger = null;
		
		HttpConnection connection = new HttpConnection(host);
		try
		{
			Constructor constructor = Class.forName(clazz).getConstructor(Connection.class);
			DataLogger dataLogger = new DataLogger();
			dataLogger.logger = (Logger)constructor.newInstance( connection );
			dataLogger.config = loggerConfig;
			dataLogger.logger.setDebug( Boolean.parseBoolean(debug));
			dataLogger.logger.setLogPeriod( Long.parseLong(logPeriod));
			
			System.out.println("Created logger "+name+" class:"+clazz+" debug:"+debug);
			//logMessage("Created logger "+name+" class:"+clazz+" debug:"+debug);
			
			return dataLogger;
		}
		catch(Exception e)
		{
			System.out.println("Failed to create logger "+name+" class:"+clazz);
			
			//String alias = config.getAttributeValueByName("alias");
			//sendAlert("Data Logger Error","Data Logger "+alias+" Failed to create logger "+name+" class:"+clazz+" at "+new Date());
			
			e.printStackTrace();
			connection.disconnect();
			
			return null;
		}		
	}
}
