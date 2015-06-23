package org.onestonesoup.opendevice.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class MidiReader implements KeyboardListener {
	public static void main(String[] args) {
		new MidiReader();
	}

	private List<KeyboardListener> listeners = new ArrayList<KeyboardListener>();

	public MidiReader() {
		
		MidiDevice device;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			try {
				device = MidiSystem.getMidiDevice(infos[i]);
				// does the device have any transmitters?
				// if it does, add it to the device list
				System.out.println(infos[i]);

				// get all transmitters
				List<Transmitter> transmitters = device.getTransmitters();
				// and for each transmitter

				for (int j = 0; j < transmitters.size(); j++) {
					// create a new receiver
					transmitters.get(j).setReceiver(
					// using my own MidiInputReceiver
							new MidiInputReceiver(device.getDeviceInfo()
									.toString()));
				}

				Transmitter trans = device.getTransmitter();
				trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo()
						.toString()));

				// open each device
				device.open();
				// if code gets this far without throwing an exception
				// print a success message
				System.out.println(device.getDeviceInfo() + " Was Opened");

			} catch (MidiUnavailableException e) {
			}
		}

	}

	public void addKeyboardListener(KeyboardListener listener) {
		this.listeners.add(listener);
	}
	
	public class MidiInputReceiver implements Receiver {
		private static final int INSTRUMENT = -64;
		private static final int KEY = -112;
		private static final int ALIVE = -2;
		public String name;

		public MidiInputReceiver(String name) {
			this.name = name;
		}

		public void send(MidiMessage msg, long timeStamp) {
			int command = msg.getMessage()[0];
			if (command == ALIVE) {
				//System.out.println("Alive");
			} else if (command == KEY) {
				if (msg.getMessage()[2] == 0) {
					//System.out.println("Key " + msg.getMessage()[1] + " UP");
					keyUp(msg.getMessage()[1]);
				} else {
					//System.out.println("Key " + msg.getMessage()[1]	+ " DOWN Velocity:" + msg.getMessage()[2]);
					keyDown(msg.getMessage()[1],msg.getMessage()[2]);
				}
			} else if (command == INSTRUMENT) {
				//System.out.println("Change to instrument to " + msg.getMessage()[1]);
				setInstrument(msg.getMessage()[1]);
			} else {
				//System.out.println("midi received " + bytesToHex(msg.getMessage()) + " > " + msg.getMessage()[0]);
			}
		}

		public void close() {
		}
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static Class getListenerClass() {
		return KeyboardListener.class;
	}

	@Override
	public void keyUp(int key) {
		for(KeyboardListener keyListener: listeners) {
			keyListener.keyUp(key);
		}
	}

	@Override
	public void keyDown(int key, int velocity) {
		for(KeyboardListener keyListener: listeners) {
			keyListener.keyDown(key, velocity);
		}
	}

	@Override
	public void setInstrument(int instrument) {
		for(KeyboardListener keyListener: listeners) {
			keyListener.setInstrument(instrument);
		}
	}
}
