package com.google.android;

import java.util.Date;

import android.location.Location;

public class POI {
	private Date date;
	private String Description;
	private Location location;
	private double Phone;
	private String Name;
	private String Address;
	private String Distance;
	private String Lat;
	private String Lng;
	
	public String getLng() { return Lng; }
	public String getLat() { return Lat; }
	public Date getDate() { return date; }
	public String getDetails() { return Description; }
	public Location getLocation() { return location; }
	public double getPhone() { return Phone; }
	public String getName() { return Name; }
	public String getAddress() { return Address; }
	public String getDistance() {return Distance; }
	public POI(String _name,String _distance, String _address, String _lat, String _lng, String _desc) {
	
	Description = _desc;
	//Address = _add;
	//Phone = _ph;
	Name = _name;
	Distance = _distance;
	Address=_address;
	Lat=_lat;
	Lng=_lng;
	}
	@Override
	public String toString() {
	
	return ""+Name+" "+Distance+"m"  ;
	}
}