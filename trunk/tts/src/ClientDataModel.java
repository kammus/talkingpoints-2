import java.io.InputStream;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class POIentry {
	private String value;
	private String type;
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
    /* All null for the moment */
    private String comments;
    private String hours;
    private String menu;
    private String specials;
    private String access;
    private String history;
    private String tpid;
    private Hashtable<String, String> extraInfo;    
    
    POIdata(){
    	
    }
        
        POIdata (String name_t, String type_t, String description_t, String country_t,String postalCode_t,String street_t,String state_t,String url_t,String city_t, String phone_t, String hour_t, String access_t, String specials_t, String menu_t, String history_t, String tpid_t)
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
                hours = hour_t;
                specials = specials_t;
                menu = menu_t;
                access = access_t;
                history = history_t;
                tpid = tpid_t;
        }
        
        public String getTpid()
        {
        	return tpid;
        }
        
        public String getHistory()
        {
        	return history;
        }
        
        
        public String hours_array()
        {
        	return hours;
        }
        
        public String getMenu()
        {
        	return menu;
        }
        
        public String getAccess()
        {
        	return access;
        }
        
        public String getSpecials()
        {
        	return specials;
        }
        
        public String comments() {
        	return comments;
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
	   speaker.createDialog(true);	   
   }
}

public class ClientDataModel{
		
        long tpid;
        boolean matched = false;
        boolean blind;
        boolean sighted;
        private POIdata data;
        TalkingPointsGUI ourGUI = new TalkingPointsGUI();
        Speaker locationSpeaker= new Speaker();
        public ClientDataModel (int option)
        {
        	sighted = false;
        	blind = false;
        	if (option == 1)
        		blind = true;
        	else if (option == 2)
        		sighted = true;
        	else if (option == 3)
        	{
        		sighted = true;
        		blind = true;
        	}
        		
        		
        		
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
           
        public void parseXML(InputStream in) {
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
                	 return;
                 }
                 
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
                  * 'comments' part is not developed yet.
                  */
                 String array[] = new String [hashKeys.getLength()];
                 Hashtable<String, String> extraInfo = new Hashtable<String, String>();
                 for (int x = 0; x < hashKeys.getLength(); ++x)
                 {
                	 array[x] = hashKeys.item(x).getChildNodes().item(0).getNodeValue();
                	 extraInfo.put(array[x], getString(doc,array[x],0));
                 }
                 
                 System.out.println(extraInfo);

                 String hours= null, access= null, specials= null, menu= null, history= null;
                 
                 data = new POIdata(name, type, description, country,postalCode,street,state, url,city, phone, hours, access, specials, menu, history, tpid); //object creation
                 data.addHash(extraInfo);
                 objectNotify(data);
                 
                 if (blind)
                 {
                 	SpeechThread speakThread= new SpeechThread(locationSpeaker, data);
                 	speakThread.run();
                 }
                 else if (sighted)
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
