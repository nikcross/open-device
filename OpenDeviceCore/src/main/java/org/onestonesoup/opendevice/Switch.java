package org.onestonesoup.opendevice;

public interface Switch extends Device{
	public void switchOn();
	public void switchOff();
	public boolean isOn();
}
