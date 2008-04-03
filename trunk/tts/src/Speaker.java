import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException; 
import java.net.URL;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
/*
 * Perhaps have some graphical output for the moment 
 * 
 * 
 */
public class Speaker {
	POIdata currentLocation;
	Voice dbVoice;
	URL url;
	ConfigurationManager manager;
	Microphone microphone;
	Recognizer recognizer;
	LinkedList<POIdata> locationCache;
    final static int ENCOUNTER = 0;
    final static int MORE_INFO = 1;
    final static int COMMENTS = 2;
    final static int HOME = 3;
    int menuStatus;
    String toSpeak;
    
	public Speaker()
	{
		locationCache = new LinkedList<POIdata>();
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
		currentLocation = incoming;
		locationCache.add(incoming);
		if (locationCache.size() == 11)
			locationCache.pop();
	}
	
	/* Have a listener thread 
	 tell listener thread to get objects at the appropriate time
	 have handler get result at appropriate time. 
	 */
	
	public void listener()
	{ 
		boolean flag = true;
		while(flag)
		{	
			System.out.println("Starting recording");
			microphone.startRecording();
			System.out.println("Recording Started");
			Result result = recognizer.recognize();
			System.out.println("Trying to stop recording");
			microphone.stopRecording();
			System.out.println("Recording stopped");
			if (result != null) 
		    {
				String resultText = result.getBestFinalResultNoFiller();
				result = null;
				System.out.println("You said: " + resultText);
				flag = resultHandler(resultText);
			} 
		    else 
		    {
				String error = "I can't hear or understand what you said.";
				dbVoice.speak(error);
				dbVoice.speak(toSpeak);
			}
			microphone.clear();
		}
	}
	
	/* create a dialog with the user 
	 * 
	 * 
	 * 
	 */
	
	public void createDialog()
	{
		menuStatus = ENCOUNTER; 
		toSpeak = "The name is " + currentLocation.name() + " and it is of type " + currentLocation.location_type() + ".  To Hear more, say More.";
		System.out.println("toSpeak: " + toSpeak);
		dbVoice.speak(toSpeak);
		if (!(microphone.isRecording()))
			microphone.startRecording();
		/* perhaps do something gunky here */
		listener();
	}
	
	public boolean resultHandler(String result)
	{
		switch(menuStatus)
		{
		case MORE_INFO:  //menu
			if (result.toLowerCase().compareTo("repeat") == 0)
			{
				dbVoice.speak(toSpeak);
				return true; 
			}
			else if(result.toLowerCase().compareTo("back") == 0)
			{
				menuStatus = ENCOUNTER;
				createDialog();
			}
			else if(result.toLowerCase().compareTo("comments") == 0)
			{
				if (currentLocation.comments() ==  null)
				{
					String error = "I'm sorry, there are no comments available for " + currentLocation.name() + ". Please pick a different command."; 
					dbVoice.speak(error);
					return true;
				}
				else{
					menuStatus = COMMENTS;
					toSpeak = currentLocation.comments();
					System.out.println("toSpeak: " + toSpeak);
					dbVoice.speak(toSpeak);		
					return true;
				}
			}
			else if(result.toLowerCase().compareTo("stop") == 0)
			{
				menuStatus = HOME;
				System.out.println("Welcome home");
				return false;
			}
			else if (result.toLowerCase().compareTo("history") == 0)
			{
				if (currentLocation.getHistory() ==  null)
				{
					String error = "I'm sorry, there are no comments available for " + currentLocation.name() + ". Please pick a different command."; 
					dbVoice.speak(error);
					return true;
				}
				else{
					toSpeak = currentLocation.getHistory();
					dbVoice.speak(toSpeak);
					return true;
				}
			}
			else if(result.toLowerCase().compareTo("menu") == 0)
			{
				if (currentLocation.getMenu() ==  null)
				{
					String error = "I'm sorry, there are no comments available for " + currentLocation.name() + ". Please pick a different command."; 
					dbVoice.speak(error);
					return true;
				}
				else{
					toSpeak = currentLocation.getMenu();
					dbVoice.speak(toSpeak);
					return true;
				}
			}
			else if(result.toLowerCase().compareTo("specials") == 0)
			{
				if (currentLocation.getSpecials() ==  null)
				{
					String error = "I'm sorry, there are no comments available for " + currentLocation.name() + ". Please pick a different command."; 
					dbVoice.speak(error);
					return true;
				}
				else{
				   toSpeak = currentLocation.getSpecials();
				   dbVoice.speak(toSpeak);
				   return true;
				}
			}
			else if(result.toLowerCase().compareTo("hours") == 0)
			{
				if (currentLocation.comments() ==  null)
				{
					String error = "I'm sorry, there are no comments available for " + currentLocation.name() + ". Please pick a different command."; 
					dbVoice.speak(error);
					return true;
				}
				else{
					toSpeak = currentLocation.hours_array();
					dbVoice.speak(toSpeak);
					return true;
				}
			}
			else if( result.toLowerCase().compareTo("access") == 0)
			{
				if (currentLocation.comments() ==  null)
				{
					String error = "I'm sorry, there are no comments available for " + currentLocation.name() + ". Please pick a different command."; 
					dbVoice.speak(error);
					return true;
				}
				else{
					toSpeak = currentLocation.getAccess();
					dbVoice.speak(toSpeak);
					return true;
				}
			}
			else if(result.toLowerCase().compareTo("home") == 0)
			{
				
			}
			else
			{
				String error  = "I'm sorry that command is not available at this menu. Please try again.";
				dbVoice.speak(error);
				return true;
			}	
			break;
		case ENCOUNTER:
			if (result.toLowerCase().compareTo("repeat") == 0)
			{
				createDialog();
			}
			else if (result.toLowerCase().compareTo("more") == 0)
			{
				menuStatus = MORE_INFO;
				toSpeak = "";
				toSpeak = currentLocation.description();
				
				if (currentLocation.comments() != null)
				{
					toSpeak += " To hear the comments, say Comments.";
				}
				if (currentLocation.getHistory() != null)
				{
					toSpeak += " To hear the history of this location, say History.";
				}
				if (currentLocation.getAccess() != null)
				{
					toSpeak += " To hear accessibility information, say Access.";
				}
				if (currentLocation.getMenu() != null)
				{
					toSpeak += " To hear the menu, say Menu.";
				}
				if (currentLocation.getSpecials() != null)
				{
					toSpeak += " To hear the specials, say Specials.";
				}
				System.out.println("toSpeak: " + toSpeak);
				dbVoice.speak(toSpeak);
				return true;
			}
			else if(result.toLowerCase().compareTo("back") == 0 || result.toLowerCase().compareTo("home") == 0)
			{
				menuStatus = HOME;
				System.out.println("Welcome Home");
				return false;
			}
			else
			{
				String error  = "I'm sorry that command is not available at this menu. Please try again.";
				dbVoice.speak(error);
				return true;
			}
			break;
		case HOME:
			if (result.toLowerCase().compareTo("previous") == 0)
			{
				 /* go to previous location, if available */ 
			}
			else if(result.toLowerCase().compareTo("next") == 0)
			{
				/*go to next location, if available */
			}
			
			break;
		/*case MORE_SUB_INFO:
			if (result.toLowerCase().compareTo("repeat") == 0)
			{
				dbVoice.speak(toSpeak);
			}
			else if (result.toLowerCase().compareTo("home") == 0)
			{
				ConstructComments();
			}
			break; */
		}
		return true;
	}
	
	/* Eventually will return some type of object */
	public Object getDialogResult()
	{
		Object empty = new Object();
		return empty;
	}
}
