package org.onestonesoup.opendevice.rensmart.economygreen;

import java.util.ArrayList;
import java.util.List;

import org.onestonesoup.opendevice.Connection;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Switch;
import org.onestonesoup.opendevice.SwitchControl;
import org.onestonesoup.opendevice.relaycontroller.KmtronicRelayController;

public class EconomyGreenSwitchControl implements SwitchControl,Switch {
	private static final String DEFAULT_ALIAS="Economy Green Switch";
	private String alias = DEFAULT_ALIAS;
	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}

	private boolean economyGreenOn = false;
	public class EconomyGreenSwitch implements Switch {
		private String DEFAULT_ALIAS=null;
		private String alias = null;
		public String getDefaultAlias() {
			return DEFAULT_ALIAS;
		}
		private int port;
		private boolean on=false;
		private EconomyGreenSwitchControl control;
		
		private EconomyGreenSwitch(EconomyGreenSwitchControl control,String alias,int port) {
			this.DEFAULT_ALIAS = alias;
			this.alias = alias;
			this.control = control;
			this.port=port;
		}
		public String getAlias() {
			return alias;
		}

		public boolean isOn() {
			return on;
		}

		public void setAlias(String alias) {
			this.alias=alias;
		}

		public void switchOff() {
			if(control.switchOff(port)==true) {
				on=false;
			}
		}

		public void switchOn() {
			if(control.switchOn(port)==true) {
				on=true;
			}
		}
		public String getParameter(String key) {
			return null;
		}
		public Device getParent() {
			return control;
		}
		public boolean hasParent() {
			return true;
		}
		public void kill() {
			control.kill();
		}
		public void setDebug(boolean state) {
			control.setDebug(state);
		}
		public void setParameter(String key, String value) {
		}
		
	}
	private List<Switch> switches;
	
	private KmtronicRelayController relayController;
	public EconomyGreenSwitchControl(Connection connection) throws Exception {
		relayController = new KmtronicRelayController(connection);
		
		switches = new ArrayList<Switch>();
		switches.add(new EconomyGreenSwitch(this,this.getDefaultAlias()+".wireless.switch.1",1));
		switches.add(new EconomyGreenSwitch(this,this.getDefaultAlias()+".wireless.switch.2",2));
		switches.add(new EconomyGreenSwitch(this,this.getDefaultAlias()+".wireless.switch.3",3));
		switches.add(new EconomyGreenSwitch(this,this.getDefaultAlias()+".wireless.switch.4",4));
		switches.add(new EconomyGreenSwitch(this,this.getDefaultAlias()+".economyGREEN.light",5));
		switches.add(new EconomyGreenSwitch(this,this.getDefaultAlias()+".switch.5",6));
	}
	
	public boolean switchOff(int port) {
		if(port>0 && port<5) {
			switchOffControls();
			relayController.switchOn(port);
			relayController.switchOn(7);
			relayController.switchOn(8);
			try{ Thread.sleep(500); } catch(Exception e){}
			switchOffControls();
			try{ Thread.sleep(500); } catch(Exception e){}
			return true;
		} else if(port>4 && port<7) {
			relayController.switchOff(port);
			return true;
		}
		return false;
	}

	public void setEconomyGreen(boolean state) {
		economyGreenOn = state;
		if(economyGreenOn==true) {
			relayController.switchOn(5);
		} else {
			relayController.switchOff(5);
		}
	}
	
	public boolean switchOn(int port) {
		if(port>0 && port<5) {
			switchOffControls();
			relayController.switchOn(port);
			relayController.switchOn(8);
			try{ Thread.sleep(500); } catch(Exception e){}
			switchOffControls();
			try{ Thread.sleep(500); } catch(Exception e){}
			return true;
		} else if(port>4 && port<7) {
			relayController.switchOn(port);
			return true;
		}
		return false;
	}

	private void switchOffControls() {
		relayController.switchOff(1);
		relayController.switchOff(2);
		relayController.switchOff(3);
		relayController.switchOff(4);
		relayController.switchOff(6);
		relayController.switchOff(7);
		relayController.switchOff(8);
	}
	
	public String getAlias() {
		return alias;
	}

	public String getParameter(String key) {
		return null;
	}

	public void kill() {}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setDebug(boolean state) {}

	public void setParameter(String key, String value) {}

	public List<Switch> getSwitches() {
		return switches;
	}

	public Device getParent() {
		return null;
	}

	public boolean hasParent() {
		return false;
	}

	public static boolean testPort(Connection connection) {
		return false;
	}

	public boolean isOn() {
		return switches.get(1).isOn();
	}

	public void switchOff() {
		switches.get(1).switchOff();
	}

	public void switchOn() {
		switches.get(1).switchOn();
	}
}
