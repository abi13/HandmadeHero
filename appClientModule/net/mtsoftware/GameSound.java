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
	final int TONE_HZ = 261; // middle c tone
	final int TONE_VOLUME = 100; // 0..127
	final int WAVE_PERIOD = SAMPLE_RATE / TONE_HZ;
	
	SourceDataLine soundLine;
	long samplesPlayed;
	long samplesWritten;
	
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
		// we want to keep the sound buffer full;
		// the buffer size in frames is equal to SAMPLE_RATE (1 sec worth of sound data)
		samplesPlayed = soundLine.getLongFramePosition();
		int samplesToWrite = (int) (samplesPlayed + SAMPLE_RATE - samplesWritten);
		if( samplesToWrite>0 ) {
			play(samplesToWrite);
		}
	}
	
	void play(int samplesToWrite) {
		
		int bytesToWrite = samplesToWrite * BYTES_PER_SAMPLE;
		byte[] buf = new byte[bytesToWrite];
		for(int i=0; i<buf.length; ) {
			double angle = (2.0 * Math.PI * samplesWritten) / WAVE_PERIOD;
			byte value = (byte) (Math.sin(angle) * TONE_VOLUME); 
			buf[i++] = value;  
			buf[i++] = value;  
			buf[i++] = value;  
			buf[i++] = value;  
			samplesWritten ++;
		}
		soundLine.write(buf, 0, buf.length);
	}
}
