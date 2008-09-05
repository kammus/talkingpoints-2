import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class POIcomment {
	private String comment_id;
	private String username;
	private String user_id;
	private String datetime;
	private String text;
	
	//constructor to prevent normal constructing
	private POIcomment(){
		
	}
	
	POIcomment(String comment_id_t, String username_t, String user_id_t, String datetime_t, String text_t)
	{
		comment_id = comment_id_t;
		username = username_t;
		user_id = user_id_t;
		datetime = datetime_t;
		text = text_t;
	}
	
	public String getID() {
		return comment_id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getUserID() {
		return user_id;
	}

	public String getTimestamp() {
		return datetime;
	}
	
	public String getCommentText() {
		return text;
	}
}

class POIdata {
	
	private String name;
    private String type;
    private String description;
    private String country;
    private String postalCode;
    private String street;
    private String phone;
    private String url;
    private String state;
    private String city;
    private String tpid;
    private Hashtable<String, String> extraInfo;
    private Hashtable<String, Object> comment;
        
    POIdata(){
    	
    }
    public boolean equals(POIdata data)    
    {
    	if(data.tpid == this.tpid)
    		return true;
    	else
    		return false;
    }
        POIdata (String name_t, String type_t, String description_t, String country_t,String postalCode_t,String street_t,String state_t,String url_t,String city_t, String phone_t, String tpid_t)
        {
                name = name_t;
                type = type_t;
                description = description_t;
                country = country_t;
                postalCode = postalCode_t;
                street = street_t;
                phone = phone_t;
                url = url_t;
                state = state_t;
                city = city_t;
                tpid = tpid_t;
        }
        
        public String getTpid()
        {
        	return tpid;
        }
        
        public String getHistory()
        {
        	return null;
        }
        
        
        public String hours_array()
        {
        	return null;
        }
        
        public String getMenu()
        {
        	return null;
        }
        
        public String getAccess()
        {
        	return null;
        }
        
        public String getSpecials()
        {
        	return null;
        }
        
        public String comments() {
        	return null;
        }
        
        public String name(){
                return (name);
        }
        
        public String location_type(){
                return (type);
        }
        
        public String description(){
                return (description);
        }
        
        public String country() {
        		return (country);
        }
        
        public String postalCode() {
        	return postalCode;
        }
        
        public String url() {
        	return (url);
        }
        
        public String phone() {
        	return (phone);
        }
        
        public String city() {
        	return (city);
        }
        
        public String street() {
        	return (street);
        }
        
        public String state() {
        	return (state);
        }
        
        public void addHash (Hashtable<String, String> incoming)
        {
        	extraInfo = incoming;
        }
        public Hashtable<String, String> getHash()
        {
        	return extraInfo; 
        }
        
        public void addComment (Hashtable<String, Object> incoming)
        {
        	comment = incoming;
        }
  
        public Hashtable<String, Object> getComment()
        {
        	return comment;
        }
}

class SpeechThread extends Thread
{
	POIdata data;	
   Speaker speaker;
   public SpeechThread (Speaker speak, POIdata incoming)
   {
	   data = incoming;
	   speaker = speak;
   }
   public void run() {
	   speaker.addPOI(data);
	   speaker.createDialog(true, Integer.valueOf(data.getTpid()));	   
   }
}

public class ClientDataModel extends AbstractButton {
		
        long tpid;
        boolean matched = false;
        boolean blind;
        boolean sighted;
        private POIdata data;
        private POIcomment POIcomment;
        TalkingPointsGUI ourGUI;
        Speaker locationSpeaker;
        AudioPlayer ourPlayer;
        
        public ClientDataModel (int option)
        {
        	sighted = false;
        	blind = false;
        	if (option == 1) {
        		blind = true;
        		locationSpeaker = new Speaker();
        	}
        	else if (option == 2) {
        		ourGUI = new TalkingPointsGUI();
        		sighted = true;
        	}
        	else if (option == 3)
        	{
        		locationSpeaker = new Speaker();
        		ourGUI = new TalkingPointsGUI();
        		sighted = true;
        		blind = true;
        	}
        	
        	this.setVisible(false);
        	ourPlayer = new AudioPlayer("sounds/timesup.wav", false);
        	
        	System.out.println("sighted: " + sighted + " blind: " + blind);
        		
        }
     
        //modified version start from here
        private static String getString(Document doc , String tagName , int index ){
            //given document, tagname, index, return node value(string)
            NodeList rows = doc.getElementsByTagName(tagName);
            Element ele = (Element)rows.item(index);

            try {
            	String value = ele.getChildNodes().item(0).getNodeValue();
            	System.out.println(value);
            	return value;
            }
            catch (Exception e)
            {
              System.out.println("Location does not have " + tagName);
              return null;
            }
            
          }
           
        // Returns true if the server gave us valid talking point information, false if not.
        public boolean parseXML(InputStream in) {
            String name = null, type = null, description= null, country= null, postalCode= null;
            String street= null, phone= null, url= null, state= null, city= null, mac= null;
            String tpid = null, wifiMac = null, rfid = null, latitude = null, longitude = null;
        	 //modified version starts from here
            try{
            	// Please don't change from now on. If you want to change this parsing part, let me know.(Travis)
                 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                 DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                 
                 Document doc = docBuilder.parse(in);
                 
                 doc.getDocumentElement().normalize();
                 
                 // location type checking
//                 NodeList locationText = getElement(doc,"location_type",0);
                 type = getString(doc, "location_type", 0);
                 
                 if (type.compareTo("ERROR") == 0)
                 {
                	 System.out.println("THIS IS NOT A VALID TALKING POINT");
                	 in.close();
                	 return false;
                 }
                 
                 // Stop the searching sound, and play the "POI discovered" sound.
                 fireActionPerformed(new ActionEvent(this, 0, "STOP"));
                 ActionListener [] temp = getActionListeners();
                 for(int x = 0 ; x < temp.length ; x++)
                	 removeActionListener(temp[x]);
                 ourPlayer.startPlayback();
                 
                 /*
                  * Core information Parsing
                  * First Part > always filled 
                  * tpid
                  * locaiton_type // is already parsed above
                  * name
                  * description
                  */
                               
                 tpid = getString(doc, "tpid", 0); //tpid
                 name = getString(doc, "name", 0);
                 description = getString(doc, "description",0);
                 
                 /*
                  * Second Part > flexible parts of the core information
                  * If it doesn't exist, return null
                  * 
                  * bluetooth_mac
                  * wifi_mac
                  * rfid
                  * latitude
                  * longitude
                  * street
                  * city
                  * state
                  * postal_code
                  * country
                  * phone
                  */
                 mac = getString(doc, "bluetooth_mac",0);
                 wifiMac = getString(doc, "wifi_mac",0);
                 rfid = getString(doc, "rfid", 0);
                 latitude = getString(doc, "latitude",0);
                 longitude = getString(doc, "longitude", 0);
                 street = getString(doc, "street", 0);
                 city = getString(doc,"city",0);
                 state = getString(doc, "state", 0);
                 postalCode = getString(doc, "postal_code", 0);
                 country = getString(doc, "country", 0);
                 url = getString(doc, "url", 0);
                 phone = getString(doc, "phone", 0);
                 
                 /*
                  * Third Part > parse section and get hashKeys
                  */
                 
                 NodeList hashKeys = doc.getElementsByTagName("section");
                 System.out.println("length of hash keys: " + hashKeys.getLength());
                 
                 /*
                  * Fourth Part -> get dynamic additional information
                  * 
                  * Using two hahstables
                  * 1) extraInfo (every tag name, value for each tag) except comments in additional_information
                  * 2) comments(comment_id, comment Object) comment Object includes every information for each comment
                  */
                 String array[] = new String [hashKeys.getLength()];
                 Hashtable<String, String> extraInfo = new Hashtable<String, String>();
                 Hashtable<String, Object> comments = new Hashtable<String, Object>();
                 
                 for (int x = 0; x < hashKeys.getLength(); ++x)
                 {
                	 array[x] = hashKeys.item(x).getChildNodes().item(0).getNodeValue();
                	 if(array[x].compareTo("comments") == 0) //get comments
                	 {
                		NodeList commentsNodes = doc.getElementsByTagName("comment");
                		for (int k=0; k<commentsNodes.getLength(); k++)
                		{
                			NodeList commentNodes = commentsNodes.item(k).getChildNodes();
                		
                			String comment_tag[] = new String[commentNodes.getLength()];
                		
                			for (int i=0; i<commentNodes.getLength(); i++)
                			{
                				if(commentNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
                				{
                					Element element = (Element)commentNodes.item(i);
                					System.out.println(element.getNodeName());
                					try
                					{
                						comment_tag[i] = element.getChildNodes().item(0).getNodeValue().trim();
                						System.out.println(i);
                					}catch(Exception e)
                					{
                						comment_tag[i] = null;
                					}
                				}	
                			}

                			POIcomment = new POIcomment(comment_tag[1], comment_tag[3], comment_tag[5], comment_tag[7], comment_tag[9]);
                			comments.put(comment_tag[1], POIcomment);
                		}
                	 }
                	 else
                	 {
                		 extraInfo.put(array[x], getString(doc,array[x],0));
                	 }
                 }
                 
                 System.out.println(extraInfo); //test for extraInfo
                 System.out.println(comments); //test for comments

                 data = new POIdata(name, type, description, country,postalCode,street,state, url,city, phone, tpid); //object creation
                 data.addHash(extraInfo);
                 data.addComment(comments);
                 objectNotify(data);
                 
                 if (blind && !sighted)
                 {
                 	SpeechThread speakThread= new SpeechThread(locationSpeaker, data);
                 	speakThread.run();
                 }
                 else if (sighted && !blind)
                 {
                	 ourGUI.addItem(data); 
                 }
                 else if (sighted && blind)  //its a PARADOX~
                 {
                	 ourGUI.addItem(data); 
                	 SpeechThread speakThread= new SpeechThread(locationSpeaker, data);
                     speakThread.run();
                 }
                          
                 }catch (SAXParseException err) {
                         System.out.println ("** Parsing error" + ", line " +err.getLineNumber() + ", uri " + err.getSystemId());
                         System.out.println(" " + err.getMessage ());    
                 }catch (SAXException e) {
                         Exception x = e.getException ();
                         ((x == null) ? e : x).printStackTrace ();
                 }catch (Throwable t) {
                         t.printStackTrace ();
                 }	
                 
                 return true;
        }
        
        public ClientDataModel() {
               /* empty for the moment*/
        }
       
        public void objectNotify(POIdata POI){
                // for test fucntion
                System.out.println("Location: " + POI.name());
                //System.out.println("Type: " + POI.location_type()); // XML file changed
                System.out.println("Description: " + POI.description());
                System.out.println("Street: " + POI.street());
                System.out.println("City: " + POI.city());
                System.out.println("State: " + POI.state());
                System.out.println("Postal_Code: " + POI.postalCode());
                System.out.println("Country: " + POI.country());
                System.out.println("URL: " + POI.url());
                //System.out.println("Phone: " + POI.phone());     //phone is not included in XML         
        }
        
}
