import java.io.File;

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
        
        POIdata(){
        }
        
        POIdata (String name_t, String type_t, String description_t)
        {
                name = name_t;
                type = type_t;
                description = description_t;
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
        String name, type, description;
        boolean matched = false;
        private POIdata data;
        TalkingPointsGUI ourGUI = new TalkingPointsGUI();
        
        private static NodeList getElement(Document doc , String tagName , int index ){
                //given an XML document and a tag, return an Element at a given index
                NodeList rows = doc.getDocumentElement().getElementsByTagName(tagName);
            Element ele = (Element)rows.item(index);
            return ele.getChildNodes();
            
          }
           
        public void parseXML(String file) {
        	 try{
                 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                 DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                 
                 Document doc = docBuilder.parse(new File(file));
                 
                 doc.getDocumentElement().normalize();
                 
                 //NodeList listofLocations = doc.getElementsByTagName ("location");
                 
                 NodeList tpidText = getElement(doc, "tpid", 0);
                 NodeList nameText = getElement(doc, "name", 0);
                 NodeList typeText = getElement(doc, "type", 0);
                 NodeList descriptionText = getElement(doc, "description", 0);
                                                 
                 String temp = ((Node)tpidText.item(0)).getNodeValue(); // temporarily, use tpid as an identifier
                 tpid = Long.valueOf(temp).longValue();
                 
                 name = ((Node)nameText.item(0)).getNodeValue();
                 type = ((Node)typeText.item(0)).getNodeValue();
                 description = ((Node)descriptionText.item(0)).getNodeValue();
                 
                 data = new POIdata(name, type, description); //object creation
                 boolean blind = false;
                 if (blind == true)
                 {
                 	Speaker locationSpeaker = new Speaker(data);
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
        
        /*public void tagChecking(long tagID){ //for test function
                if(tagID == tpid)
                        matched = true;
                if (matched)
                {
                        objectNotify(data);
                        matched = !matched;
                }
        }*/
        
        /*public void objectNotify(POIdata POI){
                // for test fucntion
                System.out.println("Location: " + POI.name());
                System.out.println("Type: " + POI.location_type());
                System.out.println("Description: " + POI.description());
        }*/
        
}
