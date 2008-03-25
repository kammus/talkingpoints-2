import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException; ;


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
}
