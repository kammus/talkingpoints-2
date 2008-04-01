import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
        	
    POIdata(){
    	
    }
        
        POIdata (String name_t, String type_t, String description_t, String country_t,String postalCode_t,String street_t,String state_t,String url_t,String city_t, String phone_t, String hour_t, String access_t, String specials_t, String menu_t, String history_t)
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
        
}

public class ClientDataModel{
        long tpid;
        boolean matched = false;
        private POIdata data;
        TalkingPointsGUI ourGUI = new TalkingPointsGUI();
        Speaker locationSpeaker = new Speaker();
        private static NodeList getElement(Document doc , String tagName , int index ){
            //given an XML document and a tag, return an Element at a given index
            NodeList rows = doc.getDocumentElement().getElementsByTagName(tagName);
            Element ele = (Element)rows.item(index);
            try {
            return ele.getChildNodes();
            }
            catch (Exception e)
            {
              System.out.println("Location does not have " + tagName + "!");
              return null;
            }
            
          }
           
        public void parseXML(InputStream in) {
            String name, type, description, country, postalCode, street, phone, url, state, city;
        	 try{
        		 // now I am using DOM document for XML parser, but another type 
        		 //SAX is more efficient in terms of 
        		 // It is just difficult to implement, so I'll modify later.
                 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                 DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                 
                 Document doc = docBuilder.parse(in);
                 
                 doc.getDocumentElement();//.normalize();
                 NodeList nameText, typeText, descriptionText, countryText, postalCodeText;
                 NodeList streetText, phoneText, urlText, stateText, cityText, hoursText;
          
                 nameText = getElement(doc, "name", 0);
                 typeText = getElement(doc, "location_type", 0); //XML changed
                 descriptionText = getElement(doc, "description", 0);
                 streetText = getElement(doc, "street",0);
                 cityText = getElement(doc, "city",0);
                 stateText = getElement(doc, "state",0);
                 postalCodeText = getElement(doc,"postal_code",0);
                 countryText = getElement(doc, "country",0);
                 urlText = getElement(doc, "url",0);
                 phoneText = getElement(doc, "phone",0);
                 hoursText = getElement(doc,"hours",0);
                 NodeList accessText, specialsText, menuText, historyText;
                 accessText = getElement(doc,"accessibility",0);
                 specialsText = getElement(doc,"specials",0);
                 menuText = getElement(doc,"menu",0);
                 historyText = getElement(doc,"history",0);
                 if (hoursText == null)
                	 System.out.println("Additional info not found");
                
                 String hours, access, specials, menu, history;
                 
                 name = ((Node)nameText.item(0)).getNodeValue();
                 type = ((Node)typeText.item(0)).getNodeValue(); //type is not included in XML now.
                 description = ((Node)descriptionText.item(0)).getNodeValue();
                 country = ((Node)countryText.item(0)).getNodeValue();
                 postalCode =((Node)postalCodeText.item(0)).getNodeValue();
                 street = ((Node)streetText.item(0)).getNodeValue();
                 state = ((Node)stateText.item(0)).getNodeValue();
                 phone = ((Node)phoneText.item(0)).getNodeValue();
                 url = ((Node)urlText.item(0)).getNodeValue();
                 city = ((Node)cityText.item(0)).getNodeValue();
                 hours = ((Node)hoursText.item(0)).getNodeValue();
                 access = ((Node)accessText.item(0)).getNodeValue();
                 specials = ((Node)specialsText.item(0)).getNodeValue();
                 menu = ((Node)menuText.item(0)).getNodeValue();
                 history = ((Node)historyText.item(0)).getNodeValue();
                 data = new POIdata(name, type, description, country,postalCode,street,state, url,city, phone, hours, access, specials, menu, history); //object creation
                 objectNotify(data);
                 boolean blind = true;
                 if (blind == true)
                 {
                 	locationSpeaker.addPOI(data);
                 	locationSpeaker.createDialog(); 
                 }
                 else 
                 {	
                 	ourGUI.addItem(data);
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
