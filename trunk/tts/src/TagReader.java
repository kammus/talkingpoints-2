import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
//import javax.bluetooth.*;

public class TagReader implements DiscoveryListener{
	 
	private ClientMessageHandler clientMessageHandler = null;
	private static Object lock=new Object();
	
	private static Vector<RemoteDevice> vecDevices=new Vector<RemoteDevice>();
	private static String macAddress;
	

	// fakeMacAdress list
	String fakeMacAddress[] = new String [] { "1234567890ab" , "0123456789cd", "1234567890ef"};
	//The Macaddress of Espresso Royale, TCF, Underground Printing in order
	int currentIndex = 0;

	// make default constructor private to ensure it's never called. Can't make a TagReader w/o a message handler!
	private TagReader() {
		
	}

	// constructor with ClientMessageHandler
	public TagReader(ClientMessageHandler cmh) {
		clientMessageHandler = cmh;
	}
	
	// Temporarily, making fakeTag
	public void generateFakeEvent() {
		notifyMacAddressWasRead(fakeMacAddress[currentIndex]);
		currentIndex++;
		if (currentIndex >= fakeMacAddress.length) currentIndex = 0;
	}
	
	private void notifyMacAddressWasRead(String MacAddress) {
		try{
			System.out.println(MacAddress);
			
		clientMessageHandler.tagWasRead(MacAddress);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	// bluetoothSearch function
	public void initBluetoothSearch() throws IOException{
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		
		DiscoveryAgent agent = localDevice.getDiscoveryAgent();
	
		System.out.println("Starting device inquiry...");
		agent.startInquiry(DiscoveryAgent.GIAC, this);
		
		try {
			synchronized(lock)
			{
				lock.wait(); //wait for notify()
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int deviceCount = vecDevices.size();
		if(deviceCount <= 0){
			System.out.println("No Devices Found .");
		}
		else
		{
			//System.out.println("Bluetooth Devices: ");
			macAddress = vecDevices.elementAt(1).toString(); // get string macAddress
			notifyMacAddressWasRead(macAddress);
			//System.out.println(macAddress); // print macAddress
		}
		initBluetoothSearch();
		 
		 
		
	}
	
	
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {

		System.out.println("Device discovered: "+btDevice.getBluetoothAddress());
		//macAddress = btDevice.getBluetoothAddress()

			macAddress = btDevice.getBluetoothAddress(); // get string macAddress
			notifyMacAddressWasRead(macAddress);

			//notifyMacAddressWasRead(macAddress);

	}
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	}
	
	public void serviceSearchCompleted(int transID, int respCode) {
	}
	
	public void inquiryCompleted(int discType) {
	System.out.println("this inquiry is completed.");
	
		synchronized(lock){
			lock.notify(); //if inquiry is completed, notify to the object
		}
		
		switch (discType) {
		case DiscoveryListener.INQUIRY_COMPLETED :
			break;
		case DiscoveryListener.INQUIRY_TERMINATED :
			System.out.println("INQUIRY_TERMINATED");
			break;
		case DiscoveryListener.INQUIRY_ERROR :
			System.out.println("INQUIRY_ERROR");
			break;
		default :
			System.out.println("Unknown Response Code");
		break;
			}
	}//end method

}
