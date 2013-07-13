package com.example.newsreader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ContentActivity extends Activity {
	NewsData news = new NewsData(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Bundle bundle = getIntent().getExtras();
		//String name = bundle.getString("name");
		
		setContentView(R.layout.activity_content);
		
		Intent intent = getIntent();
		
		String textTitle = intent.getStringExtra(MainActivity.EXTRA_MESSAGE1);
        TextView Title = (TextView) findViewById(R.id.CTitle);
		Title.append(textTitle);
		
		String textTime = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
        TextView Time = (TextView) findViewById(R.id.CTime);
		Time.append(textTime);
		
		String textWebsite = intent.getStringExtra(MainActivity.EXTRA_MESSAGE3);
        TextView Website = (TextView) findViewById(R.id.CWebsite);
		Website.append(textWebsite);
		
		String textProgram = intent.getStringExtra(MainActivity.EXTRA_MESSAGE4);
        TextView Program = (TextView) findViewById(R.id.CProgram);
		Program.append(textProgram);
		
		String textContent = intent.getStringExtra(MainActivity.EXTRA_MESSAGE5);
        TextView Content = (TextView) findViewById(R.id.CContent);
		Content.append(textContent);

		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.content, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		    case R.id.save_news:
		    	readToSelected();
                return true;
		    case R.id.unsave_news:
                selectedToRead();
                return true;
            default:
                return super.onOptionsItemSelected(item);
		}

	}
	
	public void readToSelected(){
		Intent intent = getIntent();
		String entryid = intent.getStringExtra(MainActivity.EXTRA_MESSAGE6);
		news.changeStatus(entryid, "ReadToSelected");
	}
	
    public void selectedToRead(){
    	Intent intent = getIntent();
		String entryid = intent.getStringExtra(MainActivity.EXTRA_MESSAGE6);
		news.changeStatus(entryid, "SelectedToRead");
	}

}
