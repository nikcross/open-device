package org.one.stone.soup.open.device;

public interface Device extends AliasedInstance{
	public abstract Device getParent();
	public abstract boolean hasParent();
	public void setParameter(String key,String value);
	public String getParameter(String key);
	public abstract void kill();
	public abstract void setDebug(boolean state);
}
