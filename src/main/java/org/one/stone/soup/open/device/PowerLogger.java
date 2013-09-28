package org.one.stone.soup.open.device;

public interface PowerLogger extends AliasedInstance{
	public int getPower();
	public double getUnits();
	public long getLastLogTimeStamp();
}
