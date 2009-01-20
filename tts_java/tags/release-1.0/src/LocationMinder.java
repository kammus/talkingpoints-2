import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


// class LocationMinder
// Runs a thread that maintains a list of recently visited talking points, periodically checking the entire list 
// and expiring the entries that are older than the provided # of milliseconds.
public class LocationMinder extends Thread {

	private long removalTime;
	private Hashtable<String, TableEntry> locationCache;
	private final Lock hashLock = new ReentrantLock();
	private boolean done;
	
	public LocationMinder(long remove) {
		done = false;
		removalTime = remove;
		locationCache = new Hashtable<String, TableEntry>(30);
	}
	
	public void insertItem(String mac) {
		TableEntry newEntry = new TableEntry(mac, System.currentTimeMillis());
		hashLock.lock();
		locationCache.put(mac, newEntry);
		hashLock.unlock();
	}
	
	public boolean wasRecentlyEncountered(String mac) {
		hashLock.lock();
		TableEntry entry = locationCache.get(mac);
		if(entry != null) {
			hashLock.unlock();
			return true;
		}
		else {
			hashLock.unlock();
			return false;
		}
	}

	public void terminateThread() {
		done = true;
	}
	
	/* 
	 * Thread that maintains list of recently-seen POIs, and eliminates those that
	 * have exceeded their removalTime.
	 */
	@Override
	public void run() {
		while(!done) {
			try {
					Thread.sleep(4000);
			}
			catch (InterruptedException e) {
				System.out.println("Thread interrupted.");
			}
	
			hashLock.lock();
			Enumeration<String> allKeys = locationCache.keys();
			while(allKeys.hasMoreElements()) {
				TableEntry entry = locationCache.get((String)allKeys.nextElement());
				long difference = System.currentTimeMillis() - entry.getTimeStamp();
				if(difference > removalTime) {
					locationCache.remove(entry.getMAC());
					System.out.println("Entry with MAC " + entry.getMAC() + " exceeded its lifetime and was removed.");
				}
			}
			hashLock.unlock();
		}
	}
	
	
	
}
