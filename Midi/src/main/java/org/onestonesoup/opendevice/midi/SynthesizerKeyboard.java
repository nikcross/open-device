package org.onestonesoup.opendevice.midi;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public class SynthesizerKeyboard {


	public static final void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException {
		new SynthesizerKeyboard();
	}

	private MidiReader midiReader;
	private Synthesizer synthesizer;
	
	public SynthesizerKeyboard() throws MidiUnavailableException, InvalidMidiDataException, IOException {
		midiReader = new MidiReader();
		synthesizer = new Synthesizer();
		
		String[]  instruments = synthesizer.listInstruments();
		for(int i=0;i<instruments.length;i++) {
			System.out.println("Bank:"+i+" = "+instruments[i]);
		}
		
		midiReader.addKeyboardListener( synthesizer.getKeyboardListener() );
	}
}
