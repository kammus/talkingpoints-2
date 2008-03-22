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
        String name;
        String type;
        String description;
        String country;
        String postalCode;
        String street;
        String phone;
        String url;
        String state;
        String city;
        POIdata(){
        }
        
        POIdata (String name_t, String type_t, String description_t, String country_t,String postalCode_t,String street_t,String state_t, String phone_t,String url_t,String city_t)
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
        //TalkingPointsGUI ourGUI = new TalkingPointsGUI();
        
        private static NodeList getElement(Document doc , String tagName , int index ){
            //given an XML document and a tag, return an Element at a given index
            NodeList rows = doc.getDocumentElement().getElementsByTagName(tagName);
            Element ele = (Element)rows.item(index);
            return ele.getChildNodes();
            
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
                 
                 doc.getDocumentElement().normalize();
                 
                 //NodeList listofLocations = doc.getElementsByTagName ("location");
                 
                 //NodeList tpidText = getElement(doc, "tpid", 0);
                 NodeList nameText, typeText, descriptionText, countryText, postalCodeText, streetText, phoneText, urlText, stateText, cityText;
                 nameText = getElement(doc, "name", 0);
                 typeText = getElement(doc, "type", 0);
                 descriptionText = getElement(doc, "description", 0);
                 countryText = getElement(doc, "country",0);
                 postalCodeText = getElement(doc,"postalCode",0);
                 streetText = getElement(doc, "street",0);
                 stateText = getElement(doc, "street",0);
                 phoneText = getElement(doc, "street",0);
                 urlText = getElement(doc, "street",0);
                 cityText = getElement(doc, "street",0); 
                 //String temp = ((Node)tpidText.item(0)).getNodeValue(); // temporarily, use tpid as an identifier
                 //tpid = Long.valueOf(temp).longValue();
                 
                 name = ((Node)nameText.item(0)).getNodeValue();
                 type = ((Node)typeText.item(0)).getNodeValue(); //type doesn't include XML, so I excluded
                 description = ((Node)descriptionText.item(0)).getNodeValue();
                 country = ((Node)countryText.item(0)).getNodeValue();
                 postalCode =((Node)postalCodeText.item(0)).getNodeValue();
                 street = ((Node)streetText.item(0)).getNodeValue();
                 state = ((Node)stateText.item(0)).getNodeValue();
                 phone = ((Node)phoneText.item(0)).getNodeValue();
                 url = ((Node)urlText.item(0)).getNodeValue();
                 city = ((Node)cityText.item(0)).getNodeValue();
                 
                 data = new POIdata(name, type, description, country, postalCode, street, state, phone, url, city); //object creation
                 objectNotify(data);
                 /* What are we doing here? Did we change how the GUI / SUI gets activated */ 
                 //boolean blind = false;
                 /*
                 if (blind == true)
                 {
                 	Speaker locationSpeaker = new Speaker(data);
                 	locationSpeaker.createDialog(); 
                 }
                 else 
                 {	
                 	ourGUI.addItem(data);
                 }
                 
                 */
                 
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
        
        public void tagChecking(long tagID){ //for test function
                if(tagID == tpid)
                        matched = true;
                if (matched)
                {
                        objectNotify(data);
                        matched = !matched;
                }
        }
        
        public void objectNotify(POIdata POI){
                // for test fucntion
                System.out.println("Location: " + POI.name());
                //System.out.println("Type: " + POI.location_type()); // XML file changed
                System.out.println("Description: " + POI.description());
        }
        
}
