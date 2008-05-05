import java.io.IOException;


public class TagReaderTest {

	// Please note: this will never die. You'll have to kill it manually.
	public static void main (String args[]){
		ClientMessageHandler clientMessageHandler;
		if (args[0].compareTo("1") == 0)
		{
			clientMessageHandler = new ClientMessageHandler(1);
		}
		else if (args[0].compareTo("2") == 0)
		{
			clientMessageHandler = new ClientMessageHandler(2);	
		}
		else  //if args[0] is three
		{
			clientMessageHandler = new ClientMessageHandler(3);	
		}
		final TagReader tagReader = new TagReader(clientMessageHandler);
		try{
			tagReader.initBluetoothSearch();				// search Bluetooth MacAddress
			
		}catch(IOException e){
			System.out.println("This device is not Bluetooth Capable! Exiting now.");
		}	
	}
}
	
