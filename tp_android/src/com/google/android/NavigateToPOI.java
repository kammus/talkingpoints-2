package com.google.android;

import java.util.ArrayList;

import com.google.tts.TTS;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class NavigateToPOI extends Activity{
	int i = 0;// index for which POI to deal with
	private TTS myTts;
	private TTS previous;
	private TTS bearing;
	private TTS next;
	private TTS detail;
	private TTS notification;
	float roll = 0;// a compass variable
	ArrayList<POI> currentPOI=LoadingList.getPOI();
	String lat ;
	String lng ;
	SensorManager sensorManager;
	TextView myLocationText1;
	String serviceString = Context.LOCATION_SERVICE;
	LocationManager locationManager;
	private Location currentlocation;
	private double curlat;
	private double curlng;
	double pi = Math.PI;
	double poiarch; 
	double poiangle;
	double standar;
	double judge;
	double curarch;
	TextView myLocationText;
	int yPos  ;// screen's absolute y coordinate

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get compass work
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		setContentView(R.layout.poiview);
		showPOI();

		myTts = new TTS(this, ttsInitListener, true);
	}
	
	// show POI name and description
	private void showPOI(){
		myLocationText = (TextView)findViewById(R.id.POI);
		myLocationText.setText
		(currentPOI.get(i).getName());
		myLocationText1 = (TextView)findViewById(R.id.Distance);
		myLocationText1.setText("Detail: "+currentPOI.get(i).getDetails());
	}
	
	//voice instructions
	private TTS.InitListener ttsInitListener = new TTS.InitListener() {
		public void onInit(int version) {
			myTts.speak("The closest POI is "+currentPOI.get(i).getName(), 1, null);
		}
	};
	private TTS.InitListener ttsInitListener1 = new TTS.InitListener() {
		public void onInit(int version) {
			bearing.speak("turn around", 0, null);
		}
	};
	private TTS.InitListener ttsInitListener2 = new TTS.InitListener() {
		public void onInit(int version) {
			bearing.speak("go straight", 0, null);
		}
	};
	private TTS.InitListener ttsInitListener3 = new TTS.InitListener() {
		public void onInit(int version) {
			bearing.speak("turn right", 0, null);
		}
	};
	private TTS.InitListener ttsInitListener4 = new TTS.InitListener() {
		public void onInit(int version) {
			bearing.speak("turn left", 0, null);
		}
	};
	private TTS.InitListener ttsInitListener5 = new TTS.InitListener() {
		public void onInit(int version) {
			next.speak("Next P O I is "+currentPOI.get(i).getName(), 0, null);
		}
	};
	private TTS.InitListener ttsInitListener6 = new TTS.InitListener() {
		public void onInit(int version) {
			detail.speak(currentPOI.get(i).getDetails(), 0, null);
		}
	};
	private TTS.InitListener ttsInitListener7 = new TTS.InitListener() {
		public void onInit(int version) {
			previous.speak("previous P O I is"+currentPOI.get(i).getName(), 0, null);
		}
	};
	private TTS.InitListener ttsInitListener8 = new TTS.InitListener() {
		public void onInit(int version) {
			notification.speak("No more p o i's" , 0, null);
		}
	};
    
	//detect the movement and orientation of the phone
	private final SensorListener myOrientationListener = new SensorListener() {
		public void onSensorChanged(int sensor, float[] values) {
			updateOrientation(values[SensorManager.DATA_X], values[SensorManager.DATA_Y], values[SensorManager.DATA_Z]);
			lat = currentPOI.get(i).getLat();
			lng = currentPOI.get(i).getLng();
			
			//get current coordination
			locationManager = (LocationManager)getSystemService(serviceString);
			currentlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);	
			curlat=currentlocation.getLatitude();
			curlng=currentlocation.getLongitude();
			double curx = Math.cos(curlat)*curlng;
			double cury = curlat;

            //calculate the arch between current orientation and East orientation
			if(90-roll>=0){
				curarch= 90-roll;
			}
			else
				curarch= 450-roll;

			// calculate POI's coordination
			double POIx = Math.cos(Double.parseDouble(lat))*Double.parseDouble(lng);
			double POIy = Double.parseDouble(lat);
			double distance =Math.sqrt( Math.pow((POIx-curx),2) +Math.pow((POIy-cury), 2));

			double sin = (POIy-cury)/distance;
			double cos = (POIx-curx)/distance;
			
			//calculate the angle between current orientation and POI
			if(sin>0&&cos>0){
				poiarch = Math.abs(Math.asin(sin));
			}
			if(sin>0&&cos<0){
				poiarch = pi-Math.abs(Math.asin(sin));
			}
			if(sin<0&&cos<0){
				poiarch = pi+Math.abs(Math.asin(sin));
			}
			if(sin<0&&cos>0){
				poiarch = 2*pi-Math.abs(Math.asin(sin));
			}
			poiangle = poiarch*180/pi; 

            // if the phone is facing the POI, the text will become red and vibrate to notify the user
			if(curarch>=poiangle){
				judge = curarch-poiangle;
				if(judge <=10||judge>=350){
					String vibratorService = Context.VIBRATOR_SERVICE;
					Vibrator vibrator = (Vibrator)getSystemService(vibratorService);
					long[] pattern = {100};
					vibrator.vibrate(pattern, 0);
					vibrator.vibrate(100); 
					myLocationText.setTextColor(Color.RED);
				}
				else 
					myLocationText.setTextColor(Color.WHITE);
			}
			else
				judge = poiangle-curarch;
			if(judge <=10||judge>=350){
				String vibratorService = Context.VIBRATOR_SERVICE;
				Vibrator vibrator = (Vibrator)getSystemService(vibratorService);
				long[] pattern = {100};
				vibrator.vibrate(pattern, 0);
				vibrator.vibrate(100); 
				myLocationText.setTextColor(Color.RED);
			}// Vibrate for 0.1 second
			else 
				myLocationText.setTextColor(Color.WHITE);
			myLocationText.setText(currentPOI.get(i).getName()+" (angle: "+ Math.round(judge)+")");
		}

		public void onAccuracyChanged(int sensor, int accuracy) { }
	};

	private void updateOrientation(float _roll, float _pitch, float _heading) {
		roll = _roll;	
	}

	protected void onResume()
	{
		super.onResume();
		sensorManager.registerListener(myOrientationListener, SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onStop()
	{
		sensorManager.unregisterListener(myOrientationListener);
		super.onStop();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		//KEYCODE_MENU to read turn by turn direction
		//KEYCODE_VOLUME_DOWN to navigate to next POI
		//KEYCODE_VOLUME_UP to navigate to previous POI
		//KEYCODE_CAMERA to read description
		super.onKeyDown(keyCode, event);
		switch(keyCode){
		case KeyEvent.KEYCODE_MENU: 

			if(curarch>=poiangle){
				judge = curarch-poiangle;
				if(judge <=30||judge>=330){
					bearing = new TTS(this, ttsInitListener2, true);

				}
				if(judge>30&&judge<150){
					bearing = new TTS(this, ttsInitListener3, true);
				}
				if(judge>=150&&judge<=220){
					bearing = new TTS(this, ttsInitListener1, true);
				}
				if(judge>220&&judge<330){
					bearing = new TTS(this, ttsInitListener4, true);
				}

			}
			else
				judge = poiangle-curarch;
			if(judge <=30||judge>=330){
				bearing = new TTS(this, ttsInitListener2, true);

			}
			if(judge>30&&judge<150){
				bearing = new TTS(this, ttsInitListener4, true);
			}
			if(judge>=160&&judge<=220){
				bearing = new TTS(this, ttsInitListener1, true);
			}
			if(judge>220&&judge<330){
				bearing = new TTS(this, ttsInitListener3, true);
			}
			return true;
			
			
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			i++;
			if(i>currentPOI.size()-1){
				i--;
				notification= new TTS(this, ttsInitListener8, true);
			}
			showPOI();
			next = new TTS(this, ttsInitListener5, true);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if(i>0)
				i--;			
			showPOI();
			previous = new TTS(this, ttsInitListener7, true);
			return true;

		case KeyEvent.KEYCODE_CAMERA:
			detail = new TTS(this, ttsInitListener6, true);
		}


		return true;
	}

     // use touch screen to navigate
	public boolean onTouchEvent(MotionEvent event) {
		
		int action = event.getAction();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
						
			yPos = (int)event.getRawY();
			myLocationText.setText(currentPOI.get(i).getName()+" (angle: "+ Math.round(judge)+")"+yPos);
		case MotionEvent.ACTION_UP:	
			int NEWyPos = (int)event.getRawY();
			
			   //drag down
				if(NEWyPos>yPos  ){
					i++;
					if(i>currentPOI.size()-1){
						i--;
						notification= new TTS(this, ttsInitListener8, true);
					}
					showPOI();
					next = new TTS(this, ttsInitListener5, true);
			
					return true;
				}
				//drag up
				if(NEWyPos<yPos  ) {

					if(i>0)
						i--;			
					showPOI();
					previous = new TTS(this, ttsInitListener7, true);
					
					return true;
				}
				
				}
		
		
		return super.onTouchEvent(event);
	}
}
