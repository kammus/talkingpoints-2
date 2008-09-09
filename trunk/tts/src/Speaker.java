import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.jsapi.JSGFGrammar;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import java.net.URL;
import java.io.*;
import java.util.*;
/*
 * NOTES FROM DEMO AUGUST 26TH 2008
 * 
 */

/*
 * 	To do: 
 * Explain to Pete problem with duplicates 
 * Add Dynamic Grammar / Dynamic Grammar Recognition
 */
public class Speaker {
	private JSGFGrammar jsgfGrammarManager;
	private POIdata currentLocation;
	private POIdata previousLocation;
	private Voice dbVoice;
	private URL url;
	private ConfigurationManager manager;
	private Microphone microphone;
	private Recognizer recognizer;
	private LinkedList<POIdata> locationCache;
    private final static int ENCOUNTER = 0;
    private final static int MORE_INFO = 1;
    private final static int COMMENTS = 2;
    private final static int HOME = 3;
    private int menuStatus;
    private String toSpeak;
    private boolean inSession;
    private boolean grammarCreated;
	public Speaker()
	{
		locationCache = new LinkedList<POIdata>();
		/* Set up freetts voice object */ 
		String voiceName = "kevin16";
	    VoiceManager voiceManager = VoiceManager.getInstance();
	    dbVoice = voiceManager.getVoice(voiceName);
	    /* some control over whether or not to speak here */ 
	    toSpeak =  "Welcome to Talking Points. At. any time you .can say STOP to .stop..... listening to the current Talking-Point or " +
	    		   "HELP to get help on the available voice commands. " + 
	    		   "Enjoy your walking journey. " +
	    		   "Starting to search for Talking-Points. ";
		
		
	
	    /* Set up for recognizer */
	    url = Speaker.class.getResource("talkingpoints-config.xml");
        System.out.println("Loading...");
        try {
        	manager = new ConfigurationManager(url);
     
	    	recognizer = (Recognizer) manager.lookup("recognizer");
	    	microphone = (Microphone) manager.lookup("microphone");
	    	jsgfGrammarManager = (JSGFGrammar) manager.lookup("jsgfGrammar");
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
	    
	    dbVoice.allocate();
	    System.out.println("Done loading!");
	    inSession = false;
	    System.out.println("Startup string: " + toSpeak);
	    speakWithPauses(toSpeak);
	}
	public void addPOI(POIdata incoming)
	{
		boolean test = locationCache.contains(incoming);
		
		if (test)
		{
			System.out.println("Duplicate Object Dectected");
			long start = System.currentTimeMillis();
			long testTime = System.currentTimeMillis();
			while (testTime <= start + 60000)
				testTime = System.currentTimeMillis();
		}
		else
			locationCache.push(incoming);
		
		if (locationCache.size() == 11)
			locationCache.pop();
		if (!inSession) {
		   grammarCreated = false;
		}
	}
	
	public void speakWithPauses(String speakString)
	{
		String [] speakArray;
		speakArray = speakString.split("\\.");
		for(int x = 0; x < speakArray.length; ++x)
		{
			dbVoice.speak(speakArray[x]);
			try 
			{
				Thread.sleep(200);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}	
		}
	}
	
	public void listener()
	{ 
		boolean flag = true;
		while(flag)
		{	
			System.out.println("Starting recording");
			microphone.startRecording();
			System.out.println("Recording Started");
			/* timer here */
			Result result = recognizer.recognize();
		
			System.out.println("Trying to stop recording");
			microphone.stopRecording();
			System.out.println("Recording stopped");
			if (result != null) 
		    {
				String resultText = result.getBestFinalResultNoFiller();
		        RuleGrammar ruleGrammar = jsgfGrammarManager.getRuleGrammar();
		        try {
		        	RuleParse ruleParse = ruleGrammar.parse(resultText, null);
		        	if (ruleParse != null) {
		        		result = null;
		        		System.out.println("You said: " + resultText);
		        		flag = resultHandler(resultText);
		        	}
		        	else
		        		System.out.println("Coult not recognize!");
		        } catch(GrammarException e) {
		        	System.out.println("Recognizer error!" + e.getMessage());
		        }
			} 
		    else if (menuStatus != HOME) 
		    {
				String error = "I can't hear or understand what you said.";
				dbVoice.speak(error);
				dbVoice.speak(toSpeak);
			}
			microphone.clear();
		}
		inSession = false;
		this.notify();
	}
	
	
	public void createDialog(boolean listen, Integer tpid)
	{
		System.out.println("In create dialog");
		synchronized(this) 
		{
			 if (listen) 
			 {
				 try 
				 {
					 while (inSession)
					 {
						grammarCreated = false;
						System.out.println("Sleeping");
						this.wait();
					 }	
				 } 
				 catch (Exception E) 
				 {	 
					 System.out.println("Error waiting");
				 }		
			}
			inSession = true;
			
			if (!grammarCreated) {
				int index = 0;
				do {
					currentLocation = locationCache.get(index);
					++index;
				}while(Integer.valueOf(currentLocation.getTpid()).compareTo(tpid) != 0);
			   CreateGrammar();
			}
		
			grammarCreated = true;
			menuStatus = ENCOUNTER; 
			toSpeak = currentLocation.name() + " " + currentLocation.location_type() + ". For more information say MORE.";
			System.out.println("toSpeak: " + toSpeak);
			speakWithPauses(toSpeak);
			if (listen)
			{
				listener();
			}		
		}
	}
	public void CreateGrammar()
	{
		try {
			jsgfGrammarManager.loadJSGF("commands");
			RuleGrammar ruleGrammar = jsgfGrammarManager.getRuleGrammar();
			Hashtable<String,String> moreInfo = currentLocation.getHash();
			Enumeration<String> keys = moreInfo.keys();
			String ruleName;
			while (keys.hasMoreElements())
			{
				ruleName = keys.nextElement().toLowerCase();
				System.out.println("Adding rule: " + ruleName + " which has the value " + ruleName);
				Rule newRule = ruleGrammar.ruleForJSGF(ruleName);
				ruleGrammar.setRule(ruleName, newRule, true);
				ruleGrammar.setEnabled(ruleName, true);
			}
			if (currentLocation.getComment().size() > 0)
			{
				ruleName = "comments";
				System.out.println("Adding comments to the grammar");
				Rule newRule = ruleGrammar.ruleForJSGF(ruleName);
				ruleGrammar.setRule(ruleName,newRule,true);
				ruleGrammar.setEnabled(ruleName,true);
			}
			jsgfGrammarManager.commitChanges();
		}catch (Exception e) {
			System.out.println("Error building grammar: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public boolean resultHandler(String result)
	{
		switch(menuStatus)
		{
		case MORE_INFO:  //menu
			if (result.toLowerCase().compareTo("repeat") == 0)
			{
				speakWithPauses(toSpeak);
				return true; 
			}
			else if(result.toLowerCase().compareTo("back") == 0)
			{
				menuStatus = ENCOUNTER;
				createDialog(false, Integer.valueOf(0));
				return true;
			}
			else if(result.toLowerCase().compareTo("stop") == 0 || result.toLowerCase().compareTo("skip") == 0)
			{
				menuStatus = HOME;
				System.out.println("Welcome home");
				toSpeak = "Stopped listening to " + currentLocation.name(); 
				toSpeak += "Searching for new Talking-Points.";
				speakWithPauses(toSpeak);
				System.out.println(toSpeak);
				return false;
			}
			else if(result.toLowerCase().compareTo("help") == 0)
			{
				System.out.println("In the help menu ");
				Hashtable<String,String> table = currentLocation.getHash();
				Hashtable<String,Object> commentTable = currentLocation.getComment();
				toSpeak = "You can say: ";
				if (table.size() != 0)
				{
					Enumeration<String> keys = table.keys();
					toSpeak += ( keys.nextElement());
					while (keys.hasMoreElements())
						toSpeak += (", " + keys.nextElement());
					if (commentTable.size() != 0)
						toSpeak += ", comments";
					toSpeak += ".";
				}
				else
					toSpeak = "This location has no extra info.";
				speakWithPauses(toSpeak);
				System.out.println(toSpeak);
				
			}
			else 
			{
				System.out.println(result.toLowerCase());
				Hashtable<String,String> table = currentLocation.getHash();
				if (table.containsKey(result.toLowerCase()))
				{
					System.out.println("Location has key!");
					toSpeak = table.get(result.toLowerCase());
					System.out.println("toSpeak: " + toSpeak);
					dbVoice.speak(toSpeak);
				}
				else if (result.toLowerCase().compareTo("comments") == 0)
				{
					int end;
					if (currentLocation.getComment().size() == 1)
						end = 1;
					else
						end = 2;
					Hashtable<String, Object> comments = currentLocation.getComment();
					Enumeration<Object> individualComments = comments.elements();
					for (int x = 0; x < end; ++x)
					{
						POIcomment comment = (POIcomment) individualComments.nextElement();
						toSpeak = "User " + comment.getUsername() + " said " + comment.getCommentText() + " " + comment.getTimestamp();
						System.out.println("toSpeak: " + toSpeak);
						speakWithPauses(toSpeak);
						
					}
				}
				else {
					String error = "I'm sorry, there is no " + result.toLowerCase() + "available for " + currentLocation.name();
					speakWithPauses(toSpeak);
				}
				System.out.println(toSpeak);
				return true;
				
			}
			
		case ENCOUNTER:
			if (result.toLowerCase().compareTo("repeat") == 0)
			{
				createDialog(false, Integer.valueOf(0));
			}
			else if (result.toLowerCase().compareTo("help") == 0)
			{
				Hashtable<String,String> table = currentLocation.getHash();
				Hashtable<String,Object> commentTable = currentLocation.getComment();
				toSpeak = "You can say: ";
				if (table.size() != 0)
				{
					Enumeration<String> keys = table.keys();
					toSpeak += (keys.nextElement());
					while (keys.hasMoreElements())
						toSpeak += (", " + keys.nextElement());
					if (commentTable.size() != 0)
						toSpeak += ", comments";
					
				}
			   if (table.size() != 0)
				   toSpeak += ", ";
			   toSpeak += " MORE, STOP, HELP, or REPEAT.";
			   speakWithPauses(toSpeak);
			   System.out.println(toSpeak);
			   return true;
			}
			else if (result.toLowerCase().compareTo("more") == 0)
			{
				menuStatus = MORE_INFO;
				toSpeak = "";
				
				Hashtable<String,String> table = currentLocation.getHash();
				Hashtable<String,Object> commentTable = currentLocation.getComment(); 
				if (table.size() == 0)
					toSpeak ="I'm sorry. This location has no extra info.";
				else {
					toSpeak = currentLocation.description() + ". You can say ";
					Enumeration<String> keys = table.keys();
					
					while (keys.hasMoreElements())
						toSpeak += (", " + keys.nextElement());
					if (commentTable.size() != 0)
						toSpeak += ", comments";
					toSpeak += ".";
				}
				
				System.out.println("toSpeak: " + toSpeak);
				speakWithPauses(toSpeak);
				return true;
			}
			else if(result.toLowerCase().compareTo("back") == 0 || result.toLowerCase().compareTo("home") == 0 
					|| result.toLowerCase().compareTo("skip") == 0 || result.toLowerCase().compareTo("stop") == 0)
			{
				menuStatus = HOME;
				System.out.println("You said: " + result.toLowerCase());
				toSpeak = "Stopped listening to " + currentLocation.name();
				toSpeak += " Searching for new Talking-Points.";
				speakWithPauses(toSpeak);
				return false;
			}
			else
			{
				System.out.println(result.toLowerCase());
				Hashtable<String,String> table = currentLocation.getHash();
				if (table.containsKey(result.toLowerCase()))
				{
					System.out.println("Location has key!");
					toSpeak = table.get(result.toLowerCase());
					System.out.println("toSpeak: " + toSpeak);
					speakWithPauses(toSpeak);
				}
				else if (result.toLowerCase().compareTo("comments") == 0)
				{
					int end;
					if (currentLocation.getComment().size() == 1)
						end = 1;
					else
						end = 2;
					Hashtable<String, Object> comments = currentLocation.getComment();
					Enumeration<Object> individualComments = comments.elements();
					for (int x = 0; x < end; ++x)
					{
						POIcomment comment = (POIcomment) individualComments.nextElement();
						toSpeak = "User " + comment.getUsername() + " said " + comment.getCommentText() + " " + comment.getTimestamp();
						System.out.println("toSpeak: " + toSpeak);
		
						speakWithPauses(toSpeak);
					}
				}
				else {
					String error = "I'm sorry, there is no " + result.toLowerCase() + "available for " + currentLocation.name();
					speakWithPauses(toSpeak);
				}
				return true;
			}
			break;
		case HOME:
			if (result.toLowerCase().compareTo("stop") == 0)
			{
				menuStatus = HOME;
				toSpeak = "Stopped listening to " + currentLocation.name();
				toSpeak += "Searching for new Talking-Points.";
				speakWithPauses(toSpeak);
				return false;
			}
			else if (result.toLowerCase().compareTo("start") == 0)
			{
				toSpeak = "Starking talking points. Enjoy your walking journey.";
				speakWithPauses(toSpeak);
				return true;
			}
			else if(result.toLowerCase().compareTo("repeat") == 0)
			{
				speakWithPauses(toSpeak);
				return true;
			}
			else if(result.toLowerCase().compareTo("help") == 0)
			{
				toSpeak = "You can say Start to start searching or stop to stop searching.";
				speakWithPauses(toSpeak);
				return true;
			}
			else
			{
				toSpeak = "I'm sorry that command is not available at this point. Please try again.";
				speakWithPauses(toSpeak);
			}
			break;
		}
		return true;
	}
}
