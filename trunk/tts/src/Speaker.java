import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.* ;


public class Speaker {
	POIdata locationData;
	Voice dbVoice;
	
    
	public Speaker(POIdata incoming)
	{
		locationData = incoming;
		/* Set up freetts voice object */ 
		String voiceName = "kevin16";
	    VoiceManager voiceManager = VoiceManager.getInstance();
	    dbVoice = voiceManager.getVoice(voiceName);
	    String welcomeString =  "Welcome to Talking Points." + 
		" At any time, To go back to home, say HOME." + 
"To stop listening, say STOP or SKIP. " + 
"To repeat the previous sentence, say REPEAT. " +
"To go back to the previous menu, say BACK. " + 
"To skip the current item and go to the next one, say NEXT To pause, say PAUSE" + 
"To contine, say CONTINUE"; 
dbVoice.speak(welcomeString);
	    dbVoice.allocate();
	}
	/* create a dialog with the user */ 
	public void createDialog()
	{
		String toSpeak;
		toSpeak = "The name is " + locationData.name() + " and it is of type " + locationData.location_type();
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
