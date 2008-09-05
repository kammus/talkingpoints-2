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
			System.out.println("This device is not Bluetooth Capable! Entering Test mode");
			System.out.println("Three detection events will be generated each 10 seconds apart.");
			long time = System.currentTimeMillis();
			int counter = 0;
			while (counter < 3) {
				if (System.currentTimeMillis() == time + 10000)
				{
					tagReader.generateFakeEvent();
					time += 10000;
					++counter;
				}
			}
			
		}	
	}
}
	
