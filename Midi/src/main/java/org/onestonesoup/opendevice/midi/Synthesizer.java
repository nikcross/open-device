package org.onestonesoup.opendevice.midi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;

import org.onestonesoup.opendevice.Device;

public class Synthesizer implements MidiInstrument{
	private String alias = "Synthesizer";
	private javax.sound.midi.Synthesizer synth  = null;
	private MidiChannel[] channels = null;
	private Soundbank soundbank = null;
	private Map<String,Integer> instruments = null;
	
	public Synthesizer() throws MidiUnavailableException, InvalidMidiDataException, IOException {
		synth = MidiSystem.getSynthesizer();
		
		/*File soundbankFile = new File("src/main/java/org/one/stone/soup/open/device/midi/soundbank-deluxe.gm"); 
		System.out.println("Loading "+soundbankFile.getAbsolutePath());
		Soundbank soundbank = MidiSystem.getSoundbank(soundbankFile);
		synth.loadAllInstruments(soundbank);*/
		
		soundbank = synth.getDefaultSoundbank();
		populateIntrumentsList();
		channels = synth.getChannels();
		
		System.out.println("Soundbank: " + soundbank);
		System.out.println("Name: " + soundbank.getName());
		System.out.println("Description: " + soundbank.getDescription());
		System.out.println("Vendor: " + soundbank.getVendor());
		System.out.println("Version: " + soundbank.getVersion());
		
		synth.open();
	}
	
	public String[] listInstruments() {
		return instruments.keySet().toArray(new String[]{});
	}
	
	public void playNote(int channelNumber, int note, int velocity, String instrument) {
		playNote(channelNumber, note, velocity,getInstrumentNumber(instrument) );
	}
	
	private void populateIntrumentsList() {
		Instrument[] instrumentList = soundbank.getInstruments();
		instruments = new HashMap<String,Integer>();
		for (Instrument instrument: instrumentList) {
			instruments.put(instrument.getName(), instrument.getPatch().getProgram());
		}
	}
	
	private int getInstrumentNumber(String instrument) {
		Integer instrumentNumber = instruments.get(instrument);
		if(instrumentNumber==null) {
			instrumentNumber = 0;
		}
		return instrumentNumber;
	}

	public void playNote(int channelNumber, int note, int velocity, int instrument) {
		MidiChannel	channel = channels[channelNumber];
		
		if(channel.getProgram()!=instrument) {
			channel.programChange(instrument);
		}
		
		channel.noteOn(note, velocity);
	}
	
	public void stopNote(int channelNumber, int note) {
		MidiChannel[]	channels = synth.getChannels();
		MidiChannel	channel = channels[channelNumber];
		channel.noteOff(note);
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public String getDefaultAlias() {
		return "Synthesizer";
	}

	public Device getParent() {
		return null;
	}

	public boolean hasParent() {
		return false;
	}

	public void setParameter(String key, String value) {
	}

	public String getParameter(String key) {
		return null;
	}

	public void kill() {
		synth.close();
	}

	public void setDebug(boolean state) {}
}
