package org.onestonesoup.opendevice.midi;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.onestonesoup.opendevice.midi.Synthesizer;

public class SynthesizerTest {
	
	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException {
		Synthesizer synthesizer = new Synthesizer();
		
		String[] instrucments = synthesizer.listInstruments();
		for(String instrument: instrucments) {
			System.out.println("i:"+instrument);
		}
		
		System.out.println("Start");
		synthesizer.playNote(0, 40, 100, 109);
		
		Thread.sleep(2000);
		
		synthesizer.stopNote(0, 60);
		System.out.println("Stop");
	}
}
