
public class TagReader {
	
	private ClientMessageHandler clientMessageHandler = null;
	
	// array of tag ids for testing--these need to match the ids in the DummyDatabase
	long fakeTagIDs[] = new long [] { 123 , 124, 125, 126 };
	int currentFakeTagIndex = 0;

	// make default constructor private to ensure it's never called. Can't make a TagReader w/o a message handler!
	private TagReader() {
		
	}
	
	// constructuror with ClientMessageHandler
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
		clientMessageHandler.tagWasRead(tagID);		
	}
}
