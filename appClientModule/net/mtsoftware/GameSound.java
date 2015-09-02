package net.mtsoftware;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/** 
 *   Here is how sound output computation works.
 *   We define a safety value that is the number of samples we think our game update loop 
 * may vary by (let's say up to 2ms).
 *   When we wake up to write audio, we will look and see what the play cursor position is
 * and we will forecast ahead where we think the play cursor will be on the next frame boundary.
 *   We will then look to see if the write cursor is before that by at least our safety value.
 * If it is, the target fill position is that frame boundary plus one frame. This gives us 
 * perfect audio sync in the case of a card that has low enough latency.
 *   If the write cursor is after that safety margin, then we assume we can never sync the audio
 * perfectly, so we will write one frame's worth of audio plus the safety margin's worth
 * of guard samples.
 *  
 * @author mtutaj
 *
 */
public class GameSound {

	final double PI2 = Math.PI*2;
	final int SAMPLE_RATE = 48000;
	final int SAMPLE_SIZE_IN_BITS = 16;
	final int CHANNELS = 2;
	final int BYTES_PER_SAMPLE = CHANNELS * SAMPLE_SIZE_IN_BITS / 8;
	final boolean DATA_IS_SIGNED = true;
	final boolean DATA_IS_BIGENDIAN = true;
	final int TONE_VOLUME = 100; // 0..127
	final int LATENCY_SAMPLE_COUNT = SAMPLE_RATE/10; // 1/10 sec, 3 frames
	final int AUDIO_SAFETY_SAMPLE_COUNT = 2*SAMPLE_RATE/1000; // 2ms
	final int EXPECTED_SOUND_SAMPLES_PER_FRAME = SAMPLE_RATE/30; // 30fps
	
	SourceDataLine soundLine;
	long samplesPlayed;
	long samplesWritten; // runningSampleIndex
	
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
		
		long expectedFrameBoundary = samplesPlayed + EXPECTED_SOUND_SAMPLES_PER_FRAME;
		
		long safeWriteCursor = samplesWritten + AUDIO_SAFETY_SAMPLE_COUNT;
		boolean audioCardIsLowLatency = safeWriteCursor<expectedFrameBoundary;
		
		long samplesTarget = 0;
		if( audioCardIsLowLatency ) {
			samplesTarget = expectedFrameBoundary + EXPECTED_SOUND_SAMPLES_PER_FRAME;
		} else {
			samplesTarget = samplesWritten + EXPECTED_SOUND_SAMPLES_PER_FRAME + AUDIO_SAFETY_SAMPLE_COUNT;
		}
		
		// guard against having more than 3 frames of audio in the buffer
		// note: this check is not handmadehero code, to the best of my knowledge,
		//    so I must have made something not right
		if( samplesTarget > samplesPlayed + LATENCY_SAMPLE_COUNT ) {
			samplesTarget = samplesPlayed + LATENCY_SAMPLE_COUNT;
		}
		
		int samplesToWrite = (int) (samplesTarget - samplesWritten);
		
		System.out.println("  WC: "+samplesWritten+"  PC: "+samplesPlayed
				+"  DELTA: "+(samplesWritten-samplesPlayed)
				+ String.format(" %.1f fps", (samplesWritten-samplesPlayed)/((float)EXPECTED_SOUND_SAMPLES_PER_FRAME))
				+"  WRITE: "+samplesToWrite);
		
		if( samplesToWrite>0 ) {
			play(samplesToWrite);
		}
	}
	
	void play(int samplesToWrite) {
		
		int bytesToWrite = samplesToWrite * BYTES_PER_SAMPLE;
		byte[] buf = new byte[bytesToWrite];
		for(int i=0; i<buf.length; ) {
			
			sineAngle += PI2 / wavePeriod;
			if( sineAngle > PI2 ) {
				sineAngle -= PI2; // to avoid losing precision when sineAngle becomes quite big
			}
			
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
