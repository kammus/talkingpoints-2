package talkingPoints;

import java.io.IOException;


public class TagReaderTest {

	// Please note: this will never die. You'll have to kill it manually.
	public static void main (String args[]){
		ClientMessageHandler clientMessageHandler = new ClientMessageHandler(); // also loading xml file
		final TagReader tagReader = new TagReader(clientMessageHandler);
		final java.util.Timer timer = new java.util.Timer();
		java.util.TimerTask timerTask = new java.util.TimerTask() {
			public void run() {
				try{
					tagReader.bluetoothSearch();				// search Bluetooth MacAddress
					//tagReader.generateFakeTagEvent();
				}catch(IOException e){
					
				}	
			}
		};
		// will fire a new TimerTask every 1 sec, each of which will generate a fake tag event
		timer.scheduleAtFixedRate(timerTask, 0, 200);
	}	
}
