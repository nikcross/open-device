package org.onestonesoup.opendevice.midi;

import org.onestonesoup.opendevice.Device;

public interface MidiInstrument extends Device{

	void playNote(int channelNumber, int note, int velocity, int instrument);
	void playNote(int channelNumber, int note, int velocity, String instrument);
	void stopNote(int chanelNumber, int note);
	String[] listInstruments();
}
