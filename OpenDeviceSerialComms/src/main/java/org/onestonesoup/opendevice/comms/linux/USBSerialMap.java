package org.onestonesoup.opendevice.comms.linux;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.onestonesoup.core.process.ProcessWatch;
import org.onestonesoup.core.process.ProcessWatcher;

public class USBSerialMap implements ProcessWatcher {

	public static void main(String[] args) throws Exception {
		USBSerialMap map = new USBSerialMap();
		map.updateMap();
		map.waitForUpdate();
		
		if(map.portMap.size()==0) {
			System.out.println("No Ports Present");
		}
		for(String key: map.portMap.keySet()) {
			String value = map.portMap.get(key);
			
			System.out.println(key+" maps to "+value);
		}
	}
	
	private boolean ready = false;
	private Map<String,String> portMap = new HashMap<String,String>();
	private Map<String,String> addedPortMap = new HashMap<String,String>();
	private Map<String,String> removedPortMap = new HashMap<String,String>();
	private Map<String,String> tempPortMap = new HashMap<String,String>();
	private ProcessWatch watch;
	
	public USBSerialMap() throws Exception {
		watch = new ProcessWatch();
		
		//watch.addMatcher( RegExBuilder.regex().findAnyString().findTheString("now attached to ttyUSB").findAnyString().toRegEx(), this);
		//watch.addMatcher( RegExBuilder.regex().findAnyString().findTheString("now disconnected from ttyUSB").findAnyString().toRegEx(), this);
		watch.addMatcher( ".*(now attached to ttyUSB|now disconnected from ttyUSB).*",this );
	}

	public void updateMap() throws Exception {
		ready=false;
		tempPortMap = new HashMap<String,String>();
		watch.execute("dmesg");
	}

	public void waitForUpdate() throws InterruptedException {
		while(ready==false) {
			Thread.sleep(100);
		}
	}
	
	public void processMatch(String data) {
		
		System.out.println(data);
		if(data.contains("attached")) {
			String port = data.substring(data.indexOf("tty"));
			String path = data.substring(data.indexOf("usb "),data.indexOf(":")).substring(1);
			path = path.substring(path.indexOf(" ")+1);
			registerAttachedPort(port,path);
		} else {
			String port = data.substring(data.indexOf("tty"),data.indexOf(":"));
			removeAttachedPort(port);
		}
	}

	private void registerAttachedPort(String port, String path) {
		tempPortMap.put(port, path);
		//System.out.println("adding "+port);
	}

	private void removeAttachedPort(String port) {
		tempPortMap.remove(port);
		//System.out.println("removing "+port);
	}

	public void processEnd() {
		reset();
		//test for new
		for(String key: tempPortMap.keySet()) {
			if(portMap.get(key)==null || !portMap.get(key).equals(tempPortMap.get(key))) {
				addedPortMap.put(key, tempPortMap.get(key));
			}
		}
		//test for old
		for(String key: portMap.keySet()) {
			if(tempPortMap.get(key)==null || !tempPortMap.get(key).equals(portMap.get(key))) {
				removedPortMap.put(key, portMap.get(key));
			}
		}
		//transfer
		portMap = tempPortMap;
		tempPortMap = new HashMap<String,String>();
		ready=true;
	}
	
	public void reset() {
		removedPortMap = new HashMap<String,String>();
		addedPortMap = new HashMap<String,String>();
		ready=false;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public String getPathToPort(String port) {
		return portMap.get(port);
	}
	
	public String getPortAtPath(String path) {
		for(Entry<String, String> testPath: portMap.entrySet()) {
			if(testPath.getValue().equals(path)) {
				return testPath.getKey();
			}
		}
		return null;
	}
	
	public String[] getPorts() {
		return portMap.keySet().toArray(new String[]{});
	}
	
	public String[] getAddedPorts() {
		return addedPortMap.keySet().toArray(new String[]{});
	}
	
	public String[] getRemovedPorts() {
		return removedPortMap.keySet().toArray(new String[]{});
	}
}
