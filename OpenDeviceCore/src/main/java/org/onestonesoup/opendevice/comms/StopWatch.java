package org.onestonesoup.opendevice.comms;

public class StopWatch {

	private long startTime;
	
	public StopWatch()
	{
		startTime = System.currentTimeMillis();
	}
	
	public long elapsedTime()
	{
		return System.currentTimeMillis()-startTime;
	}
	
	public void pause(long time)
	{
		try{ Thread.sleep(time); }catch(Exception e){}
	}
}
