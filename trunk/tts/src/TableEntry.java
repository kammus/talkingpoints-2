
/* Author: Peter Kretschman
 * Created: 3/16/08
 * 
 * Container class for a single line in the locationList table.  
 * Consists of three String objects. 
 */

//package talkingPoints;

public class TableEntry {

	// Default constructor
	TableEntry() {
		System.out.println("New " + this.toString() + "created.");
		locationName = new String("empty");
		locationDesc = new String("empty");
	}
	
	// Getter function for name
	public String getName() {
		return locationName;
	}
	
	// Getter function for location
	public String getLoc() {
		return locationDesc;
	}
	
	// Setter function
	public void setValues(POIdata p) {
		
		if(locationName == null)
			locationName = new String();
		
		if(locationDesc == null)
			locationDesc = new String();
		
		if(locationType == null)
			locationType = new String();
		
		locationName = p.name();
		locationDesc = p.description();
		locationType = p.location_type();
	}
	
	// Private variable definitions
	private String locationName;
	private String locationDesc;
	private String locationType;
	
} 