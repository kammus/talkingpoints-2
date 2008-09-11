import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

// Takes the filename of a sound file to play, and starts playing it when the inherited 
// .start() method is invoked.  Playback can be halted by calling the stopPlayback() method.
public class AudioPlayer implements ActionListener {

	private String filename;
	
	private boolean keepGoing;
	private boolean loopSound;
	private boolean playing;
	
	private audioThread ourPlayer;
	float gain;
	
	// Default constructor
	public AudioPlayer(String fileToPlay, boolean loop, float newGain) {
		filename = fileToPlay;
		loopSound = loop;
		playing = false;
		if(newGain < 6.0206);
			gain = newGain;
	}
	
	public void startPlayback() {
		ourPlayer = new audioThread();
	}
	
	public void stopPlayback() {
		keepGoing = false;
		playing = false;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent actEv) {
		String action = actEv.getActionCommand();
		
		if(action.compareTo("PLAY") == 0) {
			this.startPlayback();
		}
		
		if(action.compareTo("STOP") == 0) {
			this.stopPlayback();
		}
		
	}



	class audioThread implements Runnable {
			
		audioThread() {
			Thread ourThread = new Thread(this);
			ourThread.start();
		}
		
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
		
			// Set volume
			FloatControl gainControl = (FloatControl) audioLine.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(gain); 
			
			audioLine.start();
		
			int bytesPerLoop = audioFormat.getFrameSize() * 3;
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
		} // end run()	
		
	} // End AudioThread()
	
}
