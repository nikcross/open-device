package org.onestonesoup.opendevice.speechtotext;

import java.io.IOException;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.Microphone;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.recognizer.Recognizer;

public class SpeechToText {

	private LiveSpeechRecognizer recognizer;
	private Configuration configuration;
	
	public SpeechToText() throws IOException {
		configuration = new Configuration();
		 
		// Set path to acoustic model.
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/acoustic/wsj");
		// Set path to dictionary.
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/acoustic/wsj/dict/cmudict.0.6d");
		// Set language model.
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/language/en-us.lm.dmp");
		
		recognizer = new LiveSpeechRecognizer(configuration);
	}
	
	public void start() {
		start(true);
	}
	
	public void start(boolean clear) {
		// Start recognition process pruning previously cached data.
		recognizer.startRecognition(clear);
	}
	
	public String getText() {
		SpeechResult result = recognizer.getResult();
		return result.getHypothesis();
	}
	
	public void stop() {
		// Pause recognition process. It can be resumed then with startRecognition(false).
		recognizer.stopRecognition();
	}
}
