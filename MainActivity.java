package com.example.newsreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.newsreader.NewsData.NewsDataContract;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE1 = "com.example.Newsreader.MESSAGE1";
	public final static String EXTRA_MESSAGE2 = "com.example.Newsreader.MESSAGE2";
	public final static String EXTRA_MESSAGE3 = "com.example.Newsreader.MESSAGE3";
	public final static String EXTRA_MESSAGE4 = "com.example.Newsreader.MESSAGE4";
	public final static String EXTRA_MESSAGE5 = "com.example.Newsreader.MESSAGE5";
	public final static String EXTRA_MESSAGE6 = "com.example.Newsreader.MESSAGE6";
	
	Cursor cs;
	ListView listNews;
	NewsData news = new NewsData(this);
	static final String[] FROM = {NewsDataContract._ID, NewsDataContract.COLUMN_WEBSITE, NewsDataContract.COLUMN_PROGRAM, NewsDataContract.COLUMN_TIME};
    static final int[] TO = { R.id.textID, R.id.textWebsite, R.id.textProgram, R.id.textTime};
    SimpleCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_main);
	    
	    listNews = (ListView) findViewById(R.id.listNews);
	       
	    news.fetchNewsUpdates("www.sysu.org.com");
	    
	 // To delete all data stored in the database
	 //   news.deleteALLNews();
	 //   Spider one = new Spider();
	 //   news.insert(one);
     //   news.close();
        
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
	    super.onResume();
	    
	    //test function change status
	    //news.changeStatus("1234", "ReadToUnread");
	    //news.changeStatus("12340", "ReadToUnread");
	    //news.changeStatus("123400", "ReadToUnread");
	    //news.changeStatus("1234000", "ReadToUnread");
	    //news.changeStatus("12340000", "ReadToUnread");
	    //news.changeStatus("123400000", "ReadToUnread");
	    //news.changeStatus("1234000000", "ReadToUnread");
	    //news.changeStatus("12340000000", "ReadToUnread");
	    //news.changeStatus("123400000000", "ReadToUnread");
	    //news.changeStatus("1234000000000", "ReadToUnread");
	    //news.changeStatus("12340000000000", "ReadToUnread");
	    //news.changeStatus("123400000000000", "ReadToUnread");
	    
	    cs = news.readUNREAD();
	   
	    adapter = new SimpleCursorAdapter(this, R.layout.row, cs, FROM, TO, 0);

	    listNews.setAdapter(adapter);
	    
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
	    	
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        
	        	jump_to_content(position, 1);
	        	
	        }
	        
	    };

	    listNews.setOnItemClickListener(mMessageClickedHandler); 
       
	    //news.deleteUnselectedNews();
	    
	}
	
	public void jump_to_content(int position, int condition){
		Intent intent = new Intent(this, ContentActivity.class);
		Cursor cs;
	    cs = news.readByPositionModeUnread(position);
		
	    if(condition == 1){
    	cs = news.readByPositionModeUnread(position);
		}
		
	    if(condition == 2){
	    	cs = news.readByPositionModeSelected(position);
		}
		
	    if(condition == 3){
	    	cs = news.readByPositionModeAll(position);
		}
    	
    	String entryid = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
    	String title = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_TITLE));
    	String time = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_TIME));
    	String website = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_WEBSITE));
    	String program = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_PROGRAM));
    	String content = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_CONTENT));
    	
    	news.changeStatus(entryid, "UnreadToRead");
		
		//String message = String.valueOf(position);
		intent.putExtra(EXTRA_MESSAGE1,title);
		intent.putExtra(EXTRA_MESSAGE2,time);
		intent.putExtra(EXTRA_MESSAGE3,website);
		intent.putExtra(EXTRA_MESSAGE4,program);
		intent.putExtra(EXTRA_MESSAGE5,content);
		intent.putExtra(EXTRA_MESSAGE6,entryid);
    	startActivity(intent);
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
	} 
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
            	refresh();
            	return true;
        
            case R.id.unread_mode:
                displayUnread();
                return true;
        
            case R.id.selected_mode:
                displaySelected();
                return true;
            case R.id.all_mode:
                displayAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @SuppressLint("NewApi")
	public void displayUnread(){
    	cs = news.readUNREAD();
  	   
	    adapter = new SimpleCursorAdapter(this, R.layout.row, cs, FROM, TO, 0);

	    listNews.setAdapter(adapter);
	    
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
	    	
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	int condition = 1;
	        
	        	jump_to_content(position, condition);
	        	//Cursor cs = news.readByPositionModeUnread(position);
	        	
	        	//String entryid = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
	        	//news.changeStatus(entryid, "UnreadToRead");
	        }
	        
	    };

	    listNews.setOnItemClickListener(mMessageClickedHandler); 
    	
    } 
    
    @SuppressLint("NewApi")
	public void displaySelected(){
    	cs = news.readSELECTED();
  	   
	    adapter = new SimpleCursorAdapter(this, R.layout.row, cs, FROM, TO, 0);

	    listNews.setAdapter(adapter);
	    
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
	    	
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	int condition = 2;
		        
	        	jump_to_content(position, condition);
	        	//Cursor cs = news.readByPositionModeUnread(position);
	        	
	        	//String entryid = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
	        	//news.changeStatus(entryid, "UnreadToRead");
	        }
	        
	    };

	    listNews.setOnItemClickListener(mMessageClickedHandler); 
    	
    } 
    
    @SuppressLint("NewApi")
	public void displayAll(){
    	cs = news.readALL();
  	   
	    adapter = new SimpleCursorAdapter(this, R.layout.row, cs, FROM, TO, 0);

	    listNews.setAdapter(adapter);
	    
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
	    	
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	int condition = 3;
		        
	        	jump_to_content(position, condition);
	        	
	        }
	        
	    };

	    listNews.setOnItemClickListener(mMessageClickedHandler); 
    	
    } 
    
    public void refresh(){
    	news.fetchNewsUpdates("www.sysu.org.com");
    }
   
}