import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

   

public class ClientMessageHandler {
	 
	ClientDataModel clientDataModel;
		
	public ClientMessageHandler(int option){
		 clientDataModel = new ClientDataModel(option);  //changing 
	}
	
	public void tagWasRead(String macAddress) throws Exception{
		
		URL url = new URL("http://grocs.dmc.dc.umich.edu:3000/locations/show_by_bluetooth_mac/");
		
		StringBuffer urlSB = new StringBuffer(url.toString());
		//String fakeMacAddress = "1234567890ab";
		urlSB.append(macAddress);
		//urlSB.append(fakeMacAddress);
		
		URL nurl = new URL(urlSB.toString());
			
		HttpURLConnection conn = (HttpURLConnection)nurl.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type","text/xml");
		//conn.connect();
		
		InputStream in = conn.getInputStream(); // Inputstream for xml data
	
		System.out.println("MacAddress was read: " + macAddress);
		
		clientDataModel.parseXML(in);
		
		conn.disconnect();
	
	}	
}
