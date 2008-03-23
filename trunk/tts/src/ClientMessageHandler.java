import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class ClientMessageHandler {
	 
	ClientDataModel clientDataModel = new ClientDataModel();  //changing 
		
	public ClientMessageHandler(){
		
	}
	
	public void tagWasRead(long tagID) throws Exception{
		// for test, use absolute URL address
		URL url = new URL("http://grocs.dmc.dc.umich.edu:3000/locations/show_by_bluetooth_mac");
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type","text/xml");
		//conn.connect();
		
		InputStream in = conn.getInputStream(); // InputStream for espresso-royale.xml
		
		// in real life, we will send a message to the backend (TP2S)

		System.out.println("Tag was read: " + tagID);
		if(tagID == 123)
		{
			clientDataModel.parseXML(in);
			clientDataModel.tagChecking(123);
		}
		
		conn.disconnect();
		/*
		if(tagID == 124)
			clientDataModel.parseXML("stucchis.xml"); // no later
			*/
		//clientDataModel.tagChecking(tagID); // just for test, pass tagID to tagChecking
	}

	
}
