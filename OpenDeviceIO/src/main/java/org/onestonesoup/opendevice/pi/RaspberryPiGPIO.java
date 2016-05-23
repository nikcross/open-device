package org.onestonesoup.opendevice.pi;

import java.util.List;

import org.onestonesoup.core.data.EntityTree;
import org.onestonesoup.opendevice.Device;
import org.onestonesoup.opendevice.Logger;
import org.onestonesoup.opendevice.Switch;
import org.onestonesoup.opendevice.SwitchControl;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigital;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RaspberryPiGPIO  implements Device, Logger,
SwitchControl {
	private static final String DEFAULT_ALIAS="Raspberry Pi GPIO";

	private static final String INPUT_PIN = "input";
	private static final String OUTPUT_PIN = "output";
	
	private Pin[] PINS = new Pin[] {
			RaspiPin.GPIO_00,RaspiPin.GPIO_01,RaspiPin.GPIO_02,RaspiPin.GPIO_03,RaspiPin.GPIO_04,
			RaspiPin.GPIO_05,RaspiPin.GPIO_06,RaspiPin.GPIO_07,RaspiPin.GPIO_08,RaspiPin.GPIO_09,
			RaspiPin.GPIO_10,RaspiPin.GPIO_00,RaspiPin.GPIO_00,RaspiPin.GPIO_00,RaspiPin.GPIO_00,
			RaspiPin.GPIO_10,RaspiPin.GPIO_11,RaspiPin.GPIO_12,RaspiPin.GPIO_13,RaspiPin.GPIO_14,
			RaspiPin.GPIO_15,RaspiPin.GPIO_16,RaspiPin.GPIO_17,RaspiPin.GPIO_18,RaspiPin.GPIO_19,
			RaspiPin.GPIO_20,RaspiPin.GPIO_21,RaspiPin.GPIO_22,RaspiPin.GPIO_23,RaspiPin.GPIO_24,
			RaspiPin.GPIO_25,RaspiPin.GPIO_26,RaspiPin.GPIO_27,RaspiPin.GPIO_28,RaspiPin.GPIO_29
	};
	
	public static void main(String[] args) throws InterruptedException {
		RaspberryPiGPIO pi = new RaspberryPiGPIO();
		
		for(int x=0;x<30;x++) {
			
			System.out.println("0 -> TRUE");
			pi.setOutput(0, true);
			Thread.sleep(2000);

			System.out.println("0 -> FALSE");
			pi.setOutput(0, false);
			Thread.sleep(2000);
		}
		
		System.out.println("Finished");
	}
	
	private GpioPinDigital pin[];
	
	private GpioController gpio;
	private String alias = DEFAULT_ALIAS;
	
	public RaspberryPiGPIO() {
		gpio = GpioFactory.getInstance();
		
		pin = new GpioPinDigital[PINS.length];
	}
	
	public String getAlias() {
		return alias;
	}

	public String getDefaultAlias() {
		return DEFAULT_ALIAS;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean getInput(int index) {
		setDirection(index, INPUT_PIN);
		return pin[index].getState().isHigh();
	}
	
	public void setOutput(int index,boolean state) {
		setDirection(index, OUTPUT_PIN);
		((GpioPinDigitalOutput)pin[index]).setState(state);
	}
	
	private void setDirection(int index,String direction) {
		if(pin[index]!=null) {
			if((pin[index] instanceof GpioPinDigitalInput) && direction.equals(OUTPUT_PIN)){
				throw new IllegalStateException("Cannot set pin "+index+" direction as "+direction+". Pin set as input");
			}
			if((pin[index] instanceof GpioPinDigitalOutput) && direction.equals(INPUT_PIN)){
				throw new IllegalStateException("Cannot set pin "+index+" direction as "+direction+". Pin set as output");
			}
		} else {
			if(direction.equals(INPUT_PIN)) {
				
				pin[index] = gpio.provisionDigitalInputPin(PINS[index],"Pin "+index,PinPullResistance.PULL_DOWN);
				
			} else if(direction.equals(OUTPUT_PIN)) {
			
				pin[index] = gpio.provisionDigitalOutputPin(PINS[index],"Pin "+index,PinState.LOW);
				
			}
		}
	}
	
	public List<Switch> getSwitches() {
		return null;
	}

	public boolean switchOff(int arg0) {
		return false;
	}

	public boolean switchOn(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clearDataLog() {
	}

	public boolean dataAvailable() {
		return false;
	}

	public EntityTree getDataLog() {
		return null;
	}

	public void setLogPeriod(long arg0) {
		
	}

	public String getParameter(String arg0) {
		return null;
	}

	public Device getParent() {
		return null;
	}

	public boolean hasParent() {
		return false;
	}

	public void kill() {
		
	}

	public void setDebug(boolean arg0) {
	}

	public void setParameter(String arg0, String arg1) {
	}

}
