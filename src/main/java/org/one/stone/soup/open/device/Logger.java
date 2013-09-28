package org.one.stone.soup.open.device;

import org.one.stone.soup.core.data.EntityTree;

public interface Logger extends Device{
	public abstract void clearDataLog();
	public abstract boolean dataAvailable();
	public abstract EntityTree getDataLog();
	public abstract void setLogPeriod(long timeSeconds);
	
}
