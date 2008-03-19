public class ClientMessageHandler {
	 // after contacting the server, we'll pass xml file
	ClientDataModel clientDataModel = new ClientDataModel();  //changing 
		
	public ClientMessageHandler(){
		
	}
	
	public void tagWasRead(long tagID) {
		// in real life, we will send a message to the backend (TP2S)
		// in the first iteration, we will look up the tagID in the DummyDatabase
		// for now, we are just testing...
		System.out.println("Tag was read: " + tagID);
		if(tagID == 123)
			clientDataModel.parseXML("espresso-royale.xml");
		if(tagID == 124)
			clientDataModel.parseXML("stucchis.xml");
		//clientDataModel.tagChecking(tagID); // just for test, pass tagID to tagChecking
	}

	
}