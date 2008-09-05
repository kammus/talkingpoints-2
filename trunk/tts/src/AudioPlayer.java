import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

// Takes the filename of a sound file to play, and starts playing it when the inherited 
// .start() method is invoked.  Playback can be halted by calling the stopPlayback() method.
public class AudioPlayer extends Thread {

	private String filename;
	
	private boolean keepGoing;
	private boolean loopSound;
	private boolean playing;
	
	// Default constructor
	public AudioPlayer(String fileToPlay, boolean loop) {
		filename = fileToPlay;
		loopSound = loop;
		playing = false;
	}
	
	public void stopPlayback() {
		keepGoing = false;
		playing = false;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		File soundFile = new File(filename);
		if(!soundFile.exists()) {
			System.out.println("Sound file not found: " + filename);
			return;
		}
		
		keepGoing = true;
		
		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch(UnsupportedAudioFileException e) {
			e.printStackTrace();
			return;
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
		
		AudioFormat audioFormat = audioInputStream.getFormat();
		SourceDataLine audioLine = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		
		try {
			audioLine = (SourceDataLine) AudioSystem.getLine(info);
			audioLine.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		audioLine.start();
		
		int bytesPerLoop = audioFormat.getFrameSize() * 6;
		int numBytesRead = 0;
		byte [] abData = new byte[bytesPerLoop];
		playing = true;		
		
		try {
			while((numBytesRead != -1) && keepGoing) {
				numBytesRead = audioInputStream.read(abData, 0, abData.length);
				if(numBytesRead >= 0)
					audioLine.write(abData, 0, abData.length);
				if((numBytesRead == -1) && loopSound) {
					audioInputStream.close();
					try {
						audioInputStream = AudioSystem.getAudioInputStream(soundFile);
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
						return;
					}
					numBytesRead = 0;
				}
					
			}
		} catch(IOException e) {
			e.printStackTrace();
			return;
		} finally {
			audioLine.drain();
			audioLine.close();
		}
		
		
	}
	
}
