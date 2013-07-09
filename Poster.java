package com.example.newsreader;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.example.newsreader.NewsData.NewsDataContract;
import com.example.newsreader.NewsData.NewsReaderDbHelper;

class Poster extends Activity{
	NewsReaderDbHelper dbHelper;
    //SQLiteDatabase db;
	Cursor cs;
	TextView textNews;
	NewsData news = new NewsData(this);
	
	// To test function fetch
    int number_of_newly_inserted = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    textNews = (TextView) findViewById(R.id.textNews);
	    
	 // To test function getlatestnewsid
	 //String id = news.getLatestNewsID("www.sysu.org.com");
     //textNews.append(id);
        
	 // To delete all data stored in the database
	 //   news.deleteALLNews();
	 //   Spider one = new Spider();
	 //   news.insert(one);
	    
	 
         
        
     //   news.close();
        
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	   
	    number_of_newly_inserted = news.fetchNewsUpdates("www.sysu.org.com");
	    
	    // To show the number of newly obtained news
	    String s = String.format("the number of new inserted news: %d \n", number_of_newly_inserted);
        textNews.append(s);
	    
	    
	    //test function change status
	    //news.changeStatus("12340000", "UnreadToRead");
	    //news.changeStatus("12340000000000", "UnreadToRead");
	    //news.changeStatus("12340", "ReadToSelected");
	    //news.changeStatus("1234000", "ReadToSelected");
	    //news.changeStatus("123400000000000000", "ReadToSelected");
	    
	    cs = news.readUNREAD();
	    
	    String output;
	    String title, time, website, program, content;
	    while(cs.moveToNext()){
	    	title = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_TITLE));
	    	time = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_TIME));
	    	website = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_WEBSITE));
	    	program = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_PROGRAM));
	    	content = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_CONTENT));
	    	
	    	output = String.format("%s \n %s , %s : %s \n %s \n", title, time, website, program, content);
	    	textNews.append(output);
	    }
	    
	    //news.deleteUnselectedNews();
	}

}