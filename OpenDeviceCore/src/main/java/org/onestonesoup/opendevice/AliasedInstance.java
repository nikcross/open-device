package org.onestonesoup.opendevice;

public interface AliasedInstance {
	public abstract void setAlias(String alias);
	public abstract String getAlias();
	public abstract String getDefaultAlias();
}
