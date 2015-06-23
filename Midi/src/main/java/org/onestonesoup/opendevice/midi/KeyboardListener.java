package org.onestonesoup.opendevice.midi;

public interface KeyboardListener {
	void keyUp(int key);
	void keyDown(int key, int velocity);
	void setInstrument(int instrument);
}