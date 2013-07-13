package com.example.newsreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class NewsData {
	//basic information about database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NewsData.db";
    private static final String TAG = NewsData.class.getSimpleName();
    private static final int MAX_UNSELECTED_ROWS = 10;
    private static final int MAX_INSERT_ROWS = 20;
    

    
	// To define the table contents 
	public static abstract class NewsDataContract implements BaseColumns {
	        public static final String TABLE_NAME = "news";
	        public static final String COLUMN_ENTRY_ID = "entryid";
	        public static final String COLUMN_TITLE = "title";
	        public static final String COLUMN_TIME = "time";
	        public static final String COLUMN_WEBSITE = "website";
	        public static final String COLUMN_PROGRAM = "program";
	        public static final String COLUMN_CONTENT = "content";
	        public static final String COLUMN_STATUS = "status";
	}
	
	public class NewsReaderDbHelper extends SQLiteOpenHelper {
		//define create and delete entries strings
		private static final String TEXT_TYPE = " TEXT";
		private static final String COMMA_SEP = ",";
		private static final String SQL_CREATE_ENTRIES =
		    "CREATE TABLE " + NewsDataContract.TABLE_NAME + " (" +
		    NewsDataContract._ID + " INTEGER PRIMARY KEY," +
		    NewsDataContract.COLUMN_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
		    NewsDataContract.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
		    NewsDataContract.COLUMN_TIME + TEXT_TYPE + COMMA_SEP +
		    NewsDataContract.COLUMN_WEBSITE + TEXT_TYPE + COMMA_SEP +
		    NewsDataContract.COLUMN_PROGRAM + TEXT_TYPE + COMMA_SEP +
		    NewsDataContract.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
		    NewsDataContract.COLUMN_STATUS + TEXT_TYPE +
		    " )";
		private static final String SQL_DELETE_ENTRIES =
		    "DROP TABLE IF EXISTS " + NewsDataContract.TABLE_NAME;
	    
	    //constructor of NewsReaderDbHelper
	    public NewsReaderDbHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	    
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        db.execSQL(SQL_CREATE_ENTRIES);
	        Log.i(TAG, "On create sql: " + DATABASE_NAME);
	    }
	    
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        db.execSQL(SQL_DELETE_ENTRIES);
	        onCreate(db);
	        Log.i(TAG, "onUpdated");
	    }
	    
	}
	
	final NewsReaderDbHelper mDbHelper;
	
	//constructor of NewsData
	public NewsData(Context context) {
	    this.mDbHelper = new NewsReaderDbHelper(context);
	    Log.i(TAG, "Initialized data");
	}
	
	public void close() {
	    this.mDbHelper.close();
	  }

	public void insert(Spider news) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Create a new map of values
		ContentValues values = new ContentValues();
		values.clear();
		values.put(NewsDataContract.COLUMN_ENTRY_ID, news.entryid);
		values.put(NewsDataContract.COLUMN_TITLE, news.title);
		values.put(NewsDataContract.COLUMN_TIME, news.time);
		values.put(NewsDataContract.COLUMN_WEBSITE, news.website);
		values.put(NewsDataContract.COLUMN_PROGRAM, news.program);
		values.put(NewsDataContract.COLUMN_CONTENT, news.content);
		values.put(NewsDataContract.COLUMN_STATUS, "unread");

		// Insert the new row
		db.insert(
				 NewsDataContract.TABLE_NAME,
				 null,
		         values);
		
		Log.d(TAG, "insert on " + values);
		db.close();
	}
	
	// Define a projection that specifies which columns from the database
	// you will actually use after this query and how data order by 
	String[] projection = {
						NewsDataContract.COLUMN_TITLE,
						NewsDataContract.COLUMN_TIME,
						NewsDataContract.COLUMN_WEBSITE,
						NewsDataContract.COLUMN_PROGRAM,
						NewsDataContract.COLUMN_CONTENT
					    };
	String sortOrder = NewsDataContract._ID + " DESC";
	
	public Cursor readALL(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		return db.query(NewsDataContract.TABLE_NAME, 
				new String[]{"title as _id", "website", "program", "time"},
				null, null, null, null, sortOrder);
	}
	
	
	public Cursor readUNREAD(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
	    String[] selectionArgs = new String[]{"unread"};
		return db.query(NewsDataContract.TABLE_NAME, 
				new String[]{"title as _id", "website", "program", "time"},
				selection, selectionArgs, null, null, sortOrder);
	}
	
	public Cursor readSELECTED(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
	    String[] selectionArgs = new String[]{"selected"};
		return db.query(NewsDataContract.TABLE_NAME, 
				new String[]{"title as _id", "website", "program", "time"},
				selection, selectionArgs, null, null, sortOrder);
	}
	
	
	public Cursor readByPositionModeAll(int position){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c;
		c =  db.query(NewsDataContract.TABLE_NAME, 
				new String[]{"entryid", "title", "time", "website", "program","content"},
				null, null, null, null, sortOrder);
		int i = 0;
		
		while(i < position){
		  c.moveToNext();
		  i = i + 1;
		}
		
		c.moveToNext();
		
		return c;
	}
		
	public Cursor readByPositionModeUnread(int position){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c;
		String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
	    String[] selectionArgs = new String[]{"unread"};
		c =  db.query(NewsDataContract.TABLE_NAME, 
				new String[]{"entryid", "title", "time", "website", "program","content"},
				selection, selectionArgs, null, null, sortOrder);
		int i = 0;
		
		while(i < position){
		  c.moveToNext();
		  i = i + 1;
		}
		
		c.moveToNext();
		
		return c;
	}
	
	public Cursor readByPositionModeSelected(int position){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c;
		String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
	    String[] selectionArgs = new String[]{"selected"};
		c =  db.query(NewsDataContract.TABLE_NAME, 
				new String[]{"entryid", "title", "time", "website", "program","content"},
				selection, selectionArgs, null, null, sortOrder);
		int i = 0;
		
		while(i < position){
		  c.moveToNext();
		  i = i + 1;
		}
		
		c.moveToNext();
		
		return c;
	}
		
	
	// To find out the entry_id of a piece of news, which is the latest one stored in database from a given web site 
	public String getLatestNewsID(String site) {
		    SQLiteDatabase db = mDbHelper.getReadableDatabase();
		    String item_entryID;
		    String[] projection = {
		    		NewsDataContract.COLUMN_ENTRY_ID
		    };
		    String selection = NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
		    String[] selectionArgs = new String[]{site};
		    // DESC means sorting from bigger to smaller
		    String sortOrder = NewsDataContract._ID + " DESC";
		    
		    // find out all the news from the web site, ordered by latest to oldest
		    Cursor cursor = db.query(NewsDataContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		    cursor.moveToFirst();
		    item_entryID = cursor.getString(cursor.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
		    cursor.close();
		    db.close();
		    return item_entryID;
    }
	
	// To insert news which are not stored in the database from a given web site
	public int fetchNewsUpdates(String site){
		int count = 0;
		String latest_ID = getLatestNewsID(site);
		
		// To figure out how many news (not stored) are there in the web site
		Spider news = new Spider();
		while(count <= MAX_INSERT_ROWS){
			// to test function fetch, replace getnews to spidernews
			news.spidernews();
			
			if(news.entryid.equals(latest_ID)){
			    break;
		    }
			else {
				count = count + 1;
			}
			
		}
		
		// To store the value of count 
		int number = count;
		
		
		// To insert news into database according to count
		Spider newsToInsert = new Spider();
		while(count > 0){
			newsToInsert.spidernews();
		    insert(newsToInsert);
		    count = count - 1;
	    }
	
		// To return the number of newly inserted news 
		return number;
    }
	
	// three conditions to change status: unread to read, read to selected, selected to read
	public void changeStatus(String item_entryID, String condition){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		// New value for one column
		ContentValues values = new ContentValues();
		if(condition.equals("UnreadToRead")){
		    values.put(NewsDataContract.COLUMN_STATUS, "read");
		}
		
		if(condition.equals("ReadToSelected")){
			values.put(NewsDataContract.COLUMN_STATUS, "selected");
	    }
		
		if(condition.equals("SelectedToRead")){
			values.put(NewsDataContract.COLUMN_STATUS, "read");
	    }
		
		if(condition.equals("ReadToUnread")){
			values.put(NewsDataContract.COLUMN_STATUS, "unread");
	    }

		// Which row to update, based on the item_ID
		String selection = NewsDataContract.COLUMN_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = { item_entryID };

		db.update(
			NewsDataContract.TABLE_NAME,
		    values,
		    selection,
		    selectionArgs);
    
	}
	
	// In order to save space, unselected news are abandoned when the number of all the news is more than 500
	public void deleteUnselectedNews(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor;
		String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
	    String[] selectionArgs = new String[]{"read", "unread"};
	    
		cursor = db.query(NewsDataContract.TABLE_NAME, null, selection, selectionArgs, null, null, sortOrder);

		if( cursor.getCount() >= MAX_UNSELECTED_ROWS){
			int count  = cursor.getCount() - MAX_UNSELECTED_ROWS;
			cursor.moveToLast();
			
			while (count > 0){
				     String d_ID = cursor.getString(cursor.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
			         // Define 'where' part of query.
			         String d_selection = NewsDataContract.COLUMN_ENTRY_ID + " LIKE ?";
			         
			         String[] d_selectionArgs = { d_ID };
			         // Issue SQL statement.
			         db.delete(NewsDataContract.TABLE_NAME, d_selection, d_selectionArgs);
			         
				     cursor.moveToPrevious();
			}
			
		}
			
	}
	
	public void deleteALLNews(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.delete(NewsDataContract.TABLE_NAME, null, null);
	}
	
}