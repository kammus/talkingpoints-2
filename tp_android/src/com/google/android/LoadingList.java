package com.google.android;



import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.tts.TTS;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LoadingList extends Activity {
	
	private TTS myTts;
	private Location currentlocation;	
	ListView POIListView;
	ArrayAdapter<POI> aa;
	
	public static ArrayList<POI> poi = new ArrayList<POI>();// an array list store POIs
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		POIListView =
			(ListView)this.findViewById(R.id.POIListView);
		int layoutID = android.R.layout.simple_list_item_1;
		poi.clear();//clear up previous results
		aa = new ArrayAdapter<POI>(this, layoutID , poi);
		POIListView.setAdapter(aa);
		refreshPOIs();
	    myTts = new TTS(this, ttsInitListener1, true);//read how many POIs are found
	}
	
	
	
	private TTS.InitListener ttsInitListener1 = new TTS.InitListener() {
		public void onInit(int version) {
			myTts.speak(poi.size()+"POI'S was found", 0, null);
		}
	};

	//get POIs
	public static ArrayList<POI> getPOI(){
		return poi;		
	}


	public void refreshPOIs() {
		// Get the XML
		URL url;
		try {
			//get current location
			String serviceString = Context.LOCATION_SERVICE;
			LocationManager locationManager;
			locationManager = (LocationManager)getSystemService(serviceString);
			
			//change to GPS_PROVIDER to use GPS
			currentlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			//for debugging using emulator, use following commands
			
			/*Location currentlocation = new Location("reverseGeocoded");;
			  currentlocation.setLatitude(42.298678);
			  currentlocation.setLongitude(-83.727398);*/
			
			//Iterate to open XML to get POI information
			for (int i = 1; i<16; i++){
				
				String poilist = "http://test.talking-points.org/locations/" + i + ".xml";
				url = new URL(poilist);

				URLConnection connection = url.openConnection(); 

				HttpURLConnection httpConnection = (HttpURLConnection)connection; 
				int responseCode = httpConnection.getResponseCode(); 

				if (responseCode == HttpURLConnection.HTTP_OK) { 
					InputStream in = httpConnection.getInputStream(); 

					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();

					// Parse the POI list.
					Document dom = db.parse(in);      
					Element docEle = dom.getDocumentElement();

					//Retrieve POI names, locations, and Description
					Element POIName = (Element)docEle.getElementsByTagName("name").item(0);
					Element POILat = (Element)docEle.getElementsByTagName("lat").item(0);
					Element POILng = (Element)docEle.getElementsByTagName("lng").item(0);
					Element POIDes = (Element)docEle.getElementsByTagName("description").item(0);
					try {
						if (POIName == null) {

							break;

						}
						String name = POIName.getFirstChild().getNodeValue(); 
						String lat = POILat.getFirstChild().getNodeValue();
						String lng = POILng.getFirstChild().getNodeValue();
						String des = POIDes.getFirstChild().getNodeValue();
						//Calculate distance from current location
						Location poiLocation = new Location("reverseGeocoded");
						poiLocation.setLatitude(Double.parseDouble(lat));
						poiLocation.setLongitude(Double.parseDouble(lng));
						int currentDistance = (int)currentlocation.distanceTo(poiLocation);
						
						//add the POI into list base on their distance to current location
						int tempDistance;
						int currentIndex = 0;
						if (poi.size() == 0) {	//poi list is empty

							poi.add(new POI(name, currentDistance+"", " ", lat, lng,des));
						} else {	// poi list is not empty
							while (currentIndex < poi.size()) {
								tempDistance = Integer.parseInt(poi.get(currentIndex).getDistance());
								if (currentDistance <= tempDistance) {

									poi.add(currentIndex, new POI(name, currentDistance + "", " ", lat, lng,des));
									currentIndex=poi.size()+1;
								} else {
									currentIndex++;
								}
							}
							if (currentIndex == poi.size()) {
								poi.add(new POI(name, currentDistance +"", " ", lat, lng,des));
							}
						}
						aa.notifyDataSetChanged();
						
										
					}
					
					catch (Exception e) {
						continue;
					}

				}

			}





		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	
	  public boolean onKeyDown(int keyCode, KeyEvent event)
      {
      //KEYCODE_MENU to go to next page and start navigating
	  //KEYCODE_CAMERA to refresh the POI list
      super.onKeyDown(keyCode, event);
      switch(keyCode){
      case KeyEvent.KEYCODE_MENU:
      startActivity(new Intent(this, NavigateToPOI.class));
      return true;
      
      case KeyEvent.KEYCODE_CAMERA:
		startActivity(new Intent(this, LoadingList.class));
	return true;
      }
      return true;
      }
     
	
}



