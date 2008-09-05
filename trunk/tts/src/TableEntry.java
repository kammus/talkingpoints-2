
public class TableEntry {
	
	private String bluetooth_MAC;
	private long timeStamp;
	
	public TableEntry(String MAC, long time) {
		bluetooth_MAC = MAC;
		timeStamp = time;
	}

	public String getMAC() {
		return bluetooth_MAC;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
} 