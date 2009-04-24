/*
 * It is the welcome page to prompt use to
 * press menu button to start search POI 
 */


package com.google.android;



import com.google.tts.TTS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;





public class HellowAndroid extends Activity {


	TTS loading;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set the page presentation
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.loading);
		setProgressBarVisibility(true);     
		TextView myLocationText = (TextView)findViewById(R.id.searching);
		myLocationText.setText("ready to search POI...");
        //read the prompt  
		loading = new TTS(this, ttsInitListener, true);


	}

	private TTS.InitListener ttsInitListener = new TTS.InitListener() {
		public void onInit(int version) {
			loading.speak("press menu to start searching P O I",0, null);
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		//if we get the menu key, go to next page and start searching
		super.onKeyDown(keyCode, event);
		switch(keyCode){
		case KeyEvent.KEYCODE_MENU: 
			startActivity(new Intent(this, LoadingList.class));

		}
		return true;
	}
}


