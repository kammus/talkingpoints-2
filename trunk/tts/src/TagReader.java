package talkingPoints;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;


public class TagReader implements DiscoveryListener{
	
	private ClientMessageHandler clientMessageHandler = null;
	private static Object lock=new Object();
	private static Vector vecDevices=new Vector();
	private static String macAddress;

	// array of tag ids for testing--these need to match the ids in the DummyDatabase
	long fakeTagIDs[] = new long [] { 123 , 124, 125, 126 };
	int currentFakeTagIndex = 0;

	// make default constructor private to ensure it's never called. Can't make a TagReader w/o a message handler!
	private TagReader() {
		
	}
	
	// constructor with ClientMessageHandler
	public TagReader(ClientMessageHandler cmh) {
		clientMessageHandler = cmh;
	}
	
	// Temporarily, making fakeTag
	public void generateFakeTagEvent() {
		notifyTagWasRead(fakeTagIDs[currentFakeTagIndex]);
		currentFakeTagIndex++;
		if (currentFakeTagIndex >= fakeTagIDs.length) currentFakeTagIndex = 0;
	}
	
	private void notifyTagWasRead(long tagID) {
		try{
		clientMessageHandler.tagWasRead(tagID);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	// bluetoothSearch function

	public static void bluetoothSearch() throws IOException{
	
		TagReader tagReader = new TagReader();
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		
		DiscoveryAgent agent = localDevice.getDiscoveryAgent();
	
		System.out.println("Starting device inquiry...");
		agent.startInquiry(DiscoveryAgent.GIAC, tagReader);
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
			System.out.println("Bluetooth Devices: ");
			macAddress = vecDevices.elementAt(0).toString(); // get string macAddress
			System.out.println(macAddress); // print macAddress
		}
	}
	
	
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		//System.out.println("Device discovered: "+btDevice.getBluetoothAddress());
		if(!vecDevices.contains(btDevice)){
			vecDevices.addElement(btDevice);
		}
	}
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	}
	
	public void serviceSearchCompleted(int transID, int respCode) {
	}
	
	public void inquiryCompleted(int discType) {
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
