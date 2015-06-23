package org.onestonesoup.opendevice;

import org.onestonesoup.core.data.EntityTree;

public interface Logger extends Device{
	public abstract void clearDataLog();
	public abstract boolean dataAvailable();
	public abstract EntityTree getDataLog();
	public abstract void setLogPeriod(long timeSeconds);
	
}
