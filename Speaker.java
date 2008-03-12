import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
//import com.sun.speech.freetts.audio.JavaClipAudioPlayer;
import java.io.*;

public class Speaker {
	Object locationData;
	Voice dbVoice;
	
    
	public Speaker(Object incoming)
	{
		locationData = incoming;
		/* Set up freetts voice object */ 
		String voiceName = "kevin16";
	    VoiceManager voiceManager = VoiceManager.getInstance();
	    dbVoice = voiceManager.getVoice(voiceName);
	    dbVoice.allocate();
	}
	/* create a dialog with the user */ 
	public void createDialog()
	{
		String toSpeak;
		toSpeak = "The location is " + locationData.name() + " and it is of type " + locationData.location_type();
		dbVoice.speak(toSpeak);
		dbVoice.deallocate();
	}
	/* Eventually will return some type of object */
	public Object getDialogResult()
	{
		Object empty = new Object();
		return empty;
	}
	
	/* public static void main(String args[]){
		System.out.println("Running main");
	      //voice manager, gets and verifies voice for use
		;
	     
	      //verifies voice
	    if (dbVoice == null) {
	    	System.err.println("Cannot find " +voiceName);
	    	System.exit(1);
	    }//end if
	    
	    dbVoice.allocate();
	    
		try {
		} catch (Exception e){
			e.printStackTrace();
		}
		
		String toSpeak = "";
		while(toSpeak != "quit")
		{
			try{
				BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Speak this: ");
				toSpeak = input.readLine();
				System.out.println("\nid= " + toSpeak);   
			}catch(IOException ioe){ioe.printStackTrace();}
			if (toSpeak != "")
			{
				System.out.println("speaking...");
				dbVoice.speak(toSpeak);
			}	
		}
	}*/
}
