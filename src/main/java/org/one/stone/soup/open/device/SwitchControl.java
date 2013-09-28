package org.one.stone.soup.open.device;

import java.util.List;

public interface SwitchControl extends Device {
	public boolean switchOn(int port);
	public boolean switchOff(int port);
	
	public List<Switch> getSwitches();
}
