package net.mtsoftware;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class GameSound {

	final int SAMPLE_RATE = 48000;
	final int SAMPLE_SIZE_IN_BITS = 16;
	final int CHANNELS = 2;
	final boolean DATA_IS_SIGNED = true;
	final boolean DATA_IS_BIGENDIAN = true;
	final int TONE_HZ = 261; // middle c tone
	
	SourceDataLine soundLine;
	long samplesPlayed;
	long samplesWritten;
	long writePointer;
	
	void init() throws LineUnavailableException {
		
		AudioFormat af = new AudioFormat(
			SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS,
	        DATA_IS_SIGNED, DATA_IS_BIGENDIAN
	        );
        
		// buffer size in bytes, to hold 1 sec of sound data
		int bufferSizeInBytes = CHANNELS*SAMPLE_RATE*SAMPLE_SIZE_IN_BITS/8;
		soundLine = (SourceDataLine) AudioSystem.getSourceDataLine(af);
        soundLine.open(af, bufferSizeInBytes);
        soundLine.start();
        
        samplesPlayed = 0;
        samplesWritten = 0;
    }
	
	void play() {
		// determine number of sound frames to be written ahead;
		// we want to keep the sound buffer full;
		// the buffer size in frames is equal to SAMPLE_RATE (1 sec worth of sound data)
		samplesPlayed = soundLine.getLongFramePosition();
		int samplesToWrite = (int) (samplesPlayed + SAMPLE_RATE - samplesWritten);
		play(samplesToWrite);
		System.out.println("sframes to write "+samplesToWrite);
	}
	
	void play(int samplesToWrite) {
		int bytesToWrite = samplesToWrite * CHANNELS * SAMPLE_SIZE_IN_BITS/8;
		byte[] buf = new byte[bytesToWrite];
		int samplingInterval = (int)(CHANNELS * SAMPLE_RATE / TONE_HZ);
		for(int i=0; i<buf.length; i++) {
			boolean isLow = (writePointer % samplingInterval) < (samplingInterval/2);
			buf[i] = isLow ? (byte)-127 : (byte)127;
			writePointer++;
		}
		soundLine.write(buf, 0, buf.length);
		samplesWritten += samplesToWrite;
	}

	void playSin(int ms) {
		byte[] sin = new byte[ms * SAMPLE_RATE / 1000];
		double samplingInterval = (double)(CHANNELS * SAMPLE_RATE / TONE_HZ);
		for(int i=0; i<sin.length; i++) {
			double angle = (2.0 * Math.PI * writePointer) / samplingInterval;
			sin[i] = (byte) (Math.sin(angle) * 127);
			writePointer++;
		}
		soundLine.write(sin, 0, sin.length);
	}
}
