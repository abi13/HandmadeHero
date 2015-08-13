package net.mtsoftware;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class GameSound {

	final int SAMPLE_RATE = 48000;
	final int SAMPLE_SIZE_IN_BITS = 16;
	final int CHANNELS = 2;
	final int BYTES_PER_SAMPLE = CHANNELS * SAMPLE_SIZE_IN_BITS / 8;
	final boolean DATA_IS_SIGNED = true;
	final boolean DATA_IS_BIGENDIAN = true;
	final int TONE_VOLUME = 100; // 0..127
	final int LATENCY_SAMPLE_COUNT = SAMPLE_RATE/15; // 1/15 sec
	
	SourceDataLine soundLine;
	long samplesPlayed;
	long samplesWritten;
	double sineAngle = 0.0;
	int toneHz = 256; // middle c tone
	int wavePeriod = SAMPLE_RATE / toneHz;

	void increaseTone() {
		toneHz += 1;
		wavePeriod = SAMPLE_RATE / toneHz;
	}
	
	void decreaseTone() {
		toneHz -= 1;
		wavePeriod = SAMPLE_RATE / toneHz;
	}

	void init() throws LineUnavailableException {
		
		AudioFormat af = new AudioFormat(
			SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS,
	        DATA_IS_SIGNED, DATA_IS_BIGENDIAN
	        );
        
		// buffer size in bytes, to hold 1 sec of sound data
		int bufferSizeInBytes = SAMPLE_RATE * BYTES_PER_SAMPLE;
		soundLine = (SourceDataLine) AudioSystem.getSourceDataLine(af);
        soundLine.open(af, bufferSizeInBytes);
        soundLine.start();
        
        samplesPlayed = 0;
        samplesWritten = 0;
    }
	
	void play() {
		// determine number of sound frames to be written ahead;
		// we want to have in the sound buffer up to LATENCY_SAMPLE_COUNT samples
		samplesPlayed = soundLine.getLongFramePosition();
		int samplesToWrite = (int) (samplesPlayed + LATENCY_SAMPLE_COUNT - samplesWritten);
		if( samplesToWrite>0 ) {
			play(samplesToWrite);
		}
	}
	
	void play(int samplesToWrite) {
		
		int bytesToWrite = samplesToWrite * BYTES_PER_SAMPLE;
		byte[] buf = new byte[bytesToWrite];
		for(int i=0; i<buf.length; ) {
			sineAngle += (2.0 * Math.PI) / wavePeriod;
			byte value = (byte) (Math.sin(sineAngle) * TONE_VOLUME); 
			buf[i++] = value;  
			buf[i++] = value;  
			buf[i++] = value;  
			buf[i++] = value;  
			samplesWritten ++;
		}
		soundLine.write(buf, 0, buf.length);
	}
}
