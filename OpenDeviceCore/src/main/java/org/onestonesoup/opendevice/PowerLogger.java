package org.onestonesoup.opendevice;

public interface PowerLogger extends AliasedInstance{
	public int getPower();
	public double getUnits();
	public long getLastLogTimeStamp();
}
