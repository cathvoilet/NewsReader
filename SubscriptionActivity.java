package com.example.newsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SubscriptionActivity extends Activity {
	public final static String[] SubscripitionsArray={"所有网站","中山大学数学与计算科学学院"}; 
	
	ListView listSubscriptions;
	public final static String WEBSITE_CHOSEN = "com.example.Newsreader.WEBSITE_CHOSEN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
	    super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_subscription);
	
		listSubscriptions = (ListView) findViewById(R.id.listSubscriptions);
	}	
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void onResume() {
	    super.onResume();
	    
	    ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SubscripitionsArray);
	
	    listSubscriptions.setAdapter(adapter);
	    
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
	    	
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        
	        	jump_to_subscription(position);
	        	
	        }
	        
	    };

	    listSubscriptions.setOnItemClickListener(mMessageClickedHandler); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.subscription, menu);
		return true;
	}
	
	public void jump_to_subscription(int position){
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(WEBSITE_CHOSEN,position);
    	startActivity(intent);
	}
	
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle item selection
	        switch (item.getItemId()) {
	            case R.id.action_settings:
			        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
			        startActivity(preferencesIntent);
	                return true;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	    }

}

