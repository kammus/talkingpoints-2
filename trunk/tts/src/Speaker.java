import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException; 
import java.net.URL;
import java.io.*;

/*
 * Perhaps have some graphical output for the moment 
 * 
 * 
 */
public class Speaker {
	POIdata locationData;
	Voice dbVoice;
	URL url;
	ConfigurationManager manager;
	Microphone microphone;
	Recognizer recognizer;
    enum menuStatus {INITIAL_SPEAK, MORE_INFO, };
    String toSpeak;
    
	public Speaker()
	{
		/* Set up freetts voice object */ 
		String voiceName = "kevin16";
	    VoiceManager voiceManager = VoiceManager.getInstance();
	    dbVoice = voiceManager.getVoice(voiceName);
	    /* some control over whether or not to speak here */ 
	    toSpeak =  "Welcome to Talking Points." + 
		" At any time, To go back to home, say HOME." + 
"To stop listening, say STOP or SKIP. " + 
"To repeat the previous sentence, say REPEAT. " +
"To go back to the previous menu, say BACK. " + 
"To skip the current item and go to the next one, say NEXT To pause, say PAUSE" + 
"To contine, say CONTINUE"; 
		System.out.println("Startup string: " + toSpeak);
	
	    /* Set up for recognizer */
	    url = Speaker.class.getResource("talkingpoints-config.xml");
        System.out.println("Loading...");
        try {
        	manager = new ConfigurationManager(url);
	    	recognizer = (Recognizer) manager.lookup("recognizer");
	    	microphone = (Microphone) manager.lookup("microphone");
	    	recognizer.allocate();
        }
        catch (IOException e)
        {
        	System.err.println("Problen setting up recognizer: " + e);
        	e.printStackTrace();
        }
        catch (PropertyException e) 
        {
        	System.err.println("Problem Creating Recognizer " + e);
        	e.printStackTrace();
        } 
        catch (InstantiationException e) 
        {
        	System.err.println("Problem creating Recognizer: " + e);
        	e.printStackTrace();
        }
	    System.out.println("Done loading!");
	    dbVoice.allocate();
	    //dbVoice.speak(welcomeString);
	}
	public void addPOI(POIdata incoming)
	{
		locationData = incoming; 
	}
	
	public void listener()
	{
		if(microphone.startRecording())
		{	
			while(true)
			{
				Result result = recognizer.recognize();
				if (result != null) 
			    {
					String resultText = result.getBestFinalResultNoFiller();
					System.out.println("You said: " + resultText + "\n");
					//microphone.stopRecording();
					resultHandler(resultText);
					break;
				} 
			    else 
			    {
					String error = "I can't hear or understaid what you said.";
					microphone.stopRecording();
					dbVoice.speak(error);
					dbVoice.speak(toSpeak);
					microphone.startRecording();
					break;
				}
			}
		}
	}
	
	
	/* create a dialog with the user */ 
	public void createDialog()
	{
		toSpeak = "The name is " + locationData.name() + " and it is of type " + locationData.location_type();
		System.out.println("toSpeak: " + toSpeak);
		dbVoice.speak(toSpeak);
		System.out.println();
		toSpeak = "To Hear more, say More.";
		System.out.println("toSpeak: " + toSpeak);
		dbVoice.speak(toSpeak);
		listener();
		
	}
	
	public void resultHandler(String result)
	{
		if (result.toLowerCase().compareTo("more") == 0)
		{			
			toSpeak = "To Listen to comments, say Comments. To Listen to Specials, say Specials. To listen to the Menu, say Menu.";
			System.out.println("toSpeak: " + toSpeak);
			dbVoice.speak(toSpeak);
			listener();
		}
	}
	
	/* Eventually will return some type of object */
	public Object getDialogResult()
	{
		Object empty = new Object();
		return empty;
	}
}
