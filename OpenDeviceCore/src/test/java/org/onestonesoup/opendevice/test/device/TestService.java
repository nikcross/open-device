package org.onestonesoup.opendevice.test.device;

import java.util.HashMap;
import java.util.Map;

import org.onestonesoup.core.data.EntityTree;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Logger;

public class TestService implements Runnable, Logger {

	private String alias = "Test Service";
	private Map<String,String> parameters = new HashMap<String,String>();
	public String getAlias() {
		return alias;
	}
	public String getParameter(String key) {
		return parameters.get(key);
	}
	public void setAlias(String alias) {
		this.alias=alias;
	}
	public void setParameter(String key, String value) {
		parameters.put(key,value);
	}

	
	public void run() {
	}

	public void clearDataLog() {
	}

	public boolean dataAvailable() {
		return false;
	}

	public EntityTree getDataLog() {
		return null;
	}

	public void setDebug(boolean state) {
	}

	public void setLogPeriod(long timeSeconds) {
	}

	public void kill(){}
	public Device getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasParent() {
		// TODO Auto-generated method stub
		return false;
	}
	public String getDefaultAlias() {
		// TODO Auto-generated method stub
		return null;
	}
}
