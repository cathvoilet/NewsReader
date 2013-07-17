package com.example.newsreader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	
	public final static String WEBSITE1 = "http://math.sysu.edu.cn";
	
	    private static String BASE="http://math.sysu.edu.cn";
	    public static String TESTURL="http://math.sysu.edu.cn/main/news/CommonNews.aspx?ColumnNo=NA01&NewsNo=7a0e33c1-33ab-458b-af2b-a6a669a212b2";
	    public static String TESTURL2="http://math.sysu.edu.cn/main/news/CommonNews.aspx?ColumnNo=NA01&NewsNo=e38c6ae5-7d35-4be3-9b6f-2a093632c1a8";

    Handler handler = new Handler();
	Runnable runnable;
	       
	Cursor cs;
	ListView listNews;
	NewsData news = new NewsData(this);
	static final String[] FROM = {NewsDataContract._ID, NewsDataContract.COLUMN_TITLE, NewsDataContract.COLUMN_WEBSITE, NewsDataContract.COLUMN_PROGRAM, NewsDataContract.COLUMN_TIME};
    static final int[] TO = { R.id.textID, R.id.textTITLE, R.id.textWebsite, R.id.textProgram, R.id.textTime};
    SimpleCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_main);
	    
	    listNews = (ListView) findViewById(R.id.listNews);
	    
	    setupActionBar();
	     
	    //自动删除超过500条的未被收藏的新闻
	    news.deleteUnselectedNews();
	    //news.deleteALLNews();
	   	   
	    //********************************
        //定时器定时执行更新
        //********************************
        runnable = new Runnable(){
            @Override
            public void run() {
                new GetElements().execute(TESTURL);
                handler.postDelayed(this, 10000);// 10000ms后执行this，即runable
            }
        };
        handler.postDelayed(runnable, 10000);// 打开定时器，10000ms后执行runnable操作 
	}
	
	@Override
	protected void onDestroy(){
	    super.onDestroy();
	    handler.removeCallbacks(runnable);// 关闭定时器处理
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
	    super.onResume();

	    cs = news.readUNREAD();
	   
	    adapter = new SimpleCursorAdapter(this, R.layout.row, cs, FROM, TO, 0);

	    listNews.setAdapter(adapter);
	    
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {
	    	
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        
	        	jump_to_content(position, 1);
	        	
	        }
	        
	    };

	    listNews.setOnItemClickListener(mMessageClickedHandler); 	    
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
    	String status = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_STATUS));
    	
    	if(condition == 1 ){
    	    news.changeStatus(entryid, "UnreadToRead");
    	}
    	
    	if(condition == 3 ){
    		if(status.equals("unread")){
    	      news.changeStatus(entryid, "UnreadToRead");
    		}
    	}
    	
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
            	Intent intent = new Intent(this, SubscriptionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
			    return true;
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
    	 
    }
    
    private class GetElements extends AsyncTask<String, Integer, String> {

        @SuppressWarnings("unused")
		private Exception exception;

        @Override
        protected String doInBackground(String... urls) {
            try {
                //************************************
                //以下可用于调用更新
                //************************************
                Log.v("test!!!","begin");
               
                news.fetchNewsUpdate(WEBSITE1);
                return "";
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Not used in this case
        }

        @Override
        protected void onPostExecute(String myString) {
            super.onPostExecute(myString);
            // Not used in this case
        }
    }
    
   
}
   
