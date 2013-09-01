package com.example.newsreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;


public class NewsData {
    //basic information about database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NewsData.db";
    private static final String TAG = NewsData.class.getSimpleName();
    private static final int MAX_UNSELECTED_ROWS = 100;
    //private static final int MAX_INSERT_ROWS = 20;



    // To define the table contents
    public static abstract class NewsDataContract implements BaseColumns {
        public static final String TABLE_NAME = "news";
        public static final String COLUMN_ENTRY_ID = "entryid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_WEBSITE = "website";
        public static final String COLUMN_PROGRAM = "program";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_PARSED_CONTENT = "parsed_content";
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
                        NewsDataContract.COLUMN_PARSED_CONTENT + TEXT_TYPE + COMMA_SEP +
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

    public void insert(String URL, String title, String website, String program, String time, String content, String parsed_content){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values
        ContentValues values = new ContentValues();
        values.clear();
        values.put(NewsDataContract.COLUMN_ENTRY_ID, URL);
        values.put(NewsDataContract.COLUMN_WEBSITE, website);
        values.put(NewsDataContract.COLUMN_PROGRAM, program);
        values.put(NewsDataContract.COLUMN_TITLE, title);
        values.put(NewsDataContract.COLUMN_TIME, time);
        values.put(NewsDataContract.COLUMN_CONTENT, content);
        values.put(NewsDataContract.COLUMN_PARSED_CONTENT, parsed_content);
        values.put(NewsDataContract.COLUMN_STATUS, "unread");

        // Insert the new row
        assert db != null;
        db.insert(
                NewsDataContract.TABLE_NAME,
                null,
                values);

        Log.d(TAG, "insert on " + values);
        db.close();

    }

    // Define a projection that specifies which columns from the database
    // you will actually use after this query and how data order by

    //for a set
    String[] projectionBrief = {
            NewsDataContract._ID,
            NewsDataContract.COLUMN_TITLE,
            NewsDataContract.COLUMN_WEBSITE,
            NewsDataContract.COLUMN_PROGRAM,
            NewsDataContract.COLUMN_TIME,
            NewsDataContract.COLUMN_CONTENT,
            NewsDataContract.COLUMN_PARSED_CONTENT
    };

    //for a single one
    String[] projectionContent = {
            NewsDataContract.COLUMN_ENTRY_ID,
            NewsDataContract.COLUMN_TITLE,
            NewsDataContract.COLUMN_TIME,
            NewsDataContract.COLUMN_WEBSITE,
            NewsDataContract.COLUMN_PROGRAM,
            NewsDataContract.COLUMN_CONTENT,
            NewsDataContract.COLUMN_PARSED_CONTENT,
            NewsDataContract.COLUMN_STATUS
    };


    String sortOrder = NewsDataContract._ID + " DESC";

    public Cursor readALL(ArrayList website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] selectionArgs=new String[website.size()];
        website.toArray(selectionArgs);

        String selection;
        if (website.size()==1){
            selection=NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
        }
        else {
            selection=NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            for (int i=1;i<website.size();i++){
                selection=selection+" OR "+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            }
        }
        Cursor cs;
        assert db != null;
        cs = db.query(NewsDataContract.TABLE_NAME,
                projectionBrief,
                selection, selectionArgs, null, null, sortOrder);

        return cs;
    }

    public Cursor readALL(String website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection=NewsDataContract.COLUMN_WEBSITE+" LIKE ?";
        String[] selectionArgs=new String[]{website};
        Cursor cs;
        assert db != null;
        cs = db.query(NewsDataContract.TABLE_NAME,
                    projectionBrief,
                    selection, selectionArgs, null, null, sortOrder);
        return cs;
    }

    public Cursor readUNREAD(ArrayList website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
        ArrayList temp=new ArrayList();
        temp.add("unread");
        temp.addAll(website);
        String[] selectionArgs=new String[temp.size()];
        temp.toArray(selectionArgs);
        if (website.size()==1){
            selection=selection+" AND ("+NewsDataContract.COLUMN_WEBSITE + " LIKE ?)";
        }
        else {
            selection=selection+" AND ("+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            for (int i=1;i<website.size();i++){
                selection=selection+" OR "+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            }
            selection=selection+")";

        }

        assert db != null;
        return db.query(NewsDataContract.TABLE_NAME,
                projectionBrief,
                selection, selectionArgs, null, null, sortOrder);

    }

    public Cursor readUNREAD(String website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = NewsDataContract.COLUMN_STATUS + " LIKE ? AND "+NewsDataContract.COLUMN_WEBSITE+" LIKE ?";
        String[] selectionArgs = new String[]{"unread",website};

        assert db != null;
        return db.query(NewsDataContract.TABLE_NAME,
                projectionBrief,
                selection, selectionArgs, null, null, sortOrder);

    }

    public Cursor readSELECTED(ArrayList website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
        ArrayList temp=new ArrayList();
        temp.add("selected");
        temp.addAll(website);
        String[] selectionArgs=new String[temp.size()];
        temp.toArray(selectionArgs);
        if (website.size()==1){
            selection=selection+" AND ("+NewsDataContract.COLUMN_WEBSITE + " LIKE ?)";
        }
        else {
            selection=selection+" AND ("+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            for (int i=1;i<website.size();i++){
                selection=selection+" OR "+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            }
            selection=selection+")";
        }
        Cursor cs;
        assert db != null;
        cs = db.query(NewsDataContract.TABLE_NAME,
                projectionBrief,
                selection, selectionArgs, null, null, sortOrder);

        return cs;

    }

    public Cursor readSELECTED(String website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = NewsDataContract.COLUMN_STATUS + " LIKE ? AND "+NewsDataContract.COLUMN_WEBSITE+" LIKE ?";
        String[] selectionArgs = new String[]{"selected",website};
        Cursor cs;
        assert db != null;
        cs = db.query(NewsDataContract.TABLE_NAME,
                projectionBrief,
                selection, selectionArgs, null, null, sortOrder);

        return cs;

    }

    public Cursor readByPositionModeAll(int position,ArrayList website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] selectionArgs=new String[website.size()];
        website.toArray(selectionArgs);

        String selection;
        if (website.size()==1){
            selection=NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
        }
        else {
            selection=NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            for (int i=1;i<website.size();i++){
                selection=selection+" OR "+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            }
        }
        Cursor c;
        assert db != null;
        c =  db.query(NewsDataContract.TABLE_NAME,
                projectionContent,
                selection, selectionArgs, null, null, sortOrder);
        int i = 0;

        while(i < position){
            c.moveToNext();
            i = i + 1;
        }

        c.moveToNext();
        db.close();
        return c;
    }


    public Cursor readByPositionModeAll(int position,String website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection=NewsDataContract.COLUMN_WEBSITE+" LIKE ?";
        String[] selectionArgs=new String[]{website};
        Cursor c;
        assert db != null;
        c =  db.query(NewsDataContract.TABLE_NAME,
                projectionContent,
                selection, selectionArgs, null, null, sortOrder);
        int i = 0;

        while(i < position){
            c.moveToNext();
            i = i + 1;
        }

        c.moveToNext();
        db.close();
        return c;
    }

    public Cursor readByPositionModeUnread(int position,ArrayList website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
        ArrayList temp=new ArrayList();
        temp.add("unread");
        temp.addAll(website);
        String[] selectionArgs=new String[temp.size()];
        temp.toArray(selectionArgs);
        if (website.size()==1){
            selection=selection+" AND ("+NewsDataContract.COLUMN_WEBSITE + " LIKE ?)";
        }
        else {
            selection=selection+" AND ("+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            for (int i=1;i<website.size();i++){
                selection=selection+" OR "+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            }
            selection=selection+")";

        }
        Cursor c;
        assert db != null;
        c =  db.query(NewsDataContract.TABLE_NAME,
                projectionContent,
                selection, selectionArgs, null, null, sortOrder);
        int i = 0;

        while(i < position){
            c.moveToNext();
            i = i + 1;
        }

        c.moveToNext();
        db.close();
        return c;
    }


    public Cursor readByPositionModeUnread(int position,String website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c;
        String selection = NewsDataContract.COLUMN_STATUS + " LIKE ? AND "+NewsDataContract.COLUMN_WEBSITE+" LIKE ?";
        String[] selectionArgs = new String[]{"unread",website};
        assert db != null;
        c =  db.query(NewsDataContract.TABLE_NAME,
                projectionContent,
                selection, selectionArgs, null, null, sortOrder);
        int i = 0;

        while(i < position){
            c.moveToNext();
            i = i + 1;
        }

        c.moveToNext();
        db.close();
        return c;
    }

    public Cursor readByPositionModeSelected(int position,ArrayList website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c;

        String selection = NewsDataContract.COLUMN_STATUS + " LIKE ?";
        ArrayList temp=new ArrayList();
        temp.add("selected");
        temp.addAll(website);
        String[] selectionArgs=new String[temp.size()];
        temp.toArray(selectionArgs);
        if (website.size()==1){
            selection=selection+" AND ("+NewsDataContract.COLUMN_WEBSITE + " LIKE ?)";
        }
        else {
            selection=selection+" AND ("+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            for (int i=1;i<website.size();i++){
                selection=selection+" OR "+NewsDataContract.COLUMN_WEBSITE + " LIKE ?";
            }
            selection=selection+")";

        }

        assert db != null;
        c =  db.query(NewsDataContract.TABLE_NAME,
                projectionContent,
                selection, selectionArgs, null, null, sortOrder);
        int i = 0;

        while(i < position){
            c.moveToNext();
            i = i + 1;
        }

        c.moveToNext();
        db.close();
        return c;
    }

    public Cursor readByPositionModeSelected(int position,String website){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c;

        String selection = NewsDataContract.COLUMN_STATUS + " LIKE ? AND "+NewsDataContract.COLUMN_WEBSITE+" LIKE ?";
        String[] selectionArgs = new String[]{"selected",website};
        assert db != null;
        c =  db.query(NewsDataContract.TABLE_NAME,
                projectionContent,
                selection, selectionArgs, null, null, sortOrder);
        int i = 0;

        while(i < position){
            c.moveToNext();
            i = i + 1;
        }

        c.moveToNext();
        db.close();
        return c;
    }


    // To find out the entry_id of a piece of news, which is the latest one stored in database from a given web site
    public String getLatestURL(String website, String program) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String item_url;
        String[] projection = {
                NewsDataContract.COLUMN_ENTRY_ID
        };
        String selection = NewsDataContract.COLUMN_WEBSITE + "=" +"?"
                + " AND " + NewsDataContract.COLUMN_PROGRAM  + "=?";
        String[] selectionArgs = new String[]{website, program};
        // DESC means sorting from bigger to smaller
        String sortOrder = NewsDataContract._ID + " DESC";

        // find out all the news from the web site, ordered by latest to oldest
        assert db != null;
        Cursor cursor = db.query(NewsDataContract.TABLE_NAME, projection,
                selection,
                selectionArgs,
                null, null, sortOrder);

        // if cursor not found, return -1
        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            item_url = cursor.getString(cursor.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
            cursor.close();

        }
        else{
            item_url = "-1";
        }


        db.close();
        return item_url;
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

        assert db != null;
        db.update(
                NewsDataContract.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();

    }

    // In order to save space, unselected news are abandoned when the number of all the news is more than MAX_UNSELECTED_ROWS
    public void deleteUnselectedNews(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        String selection = NewsDataContract.COLUMN_STATUS + "=" + "?"
                + " OR " + NewsDataContract.COLUMN_STATUS + "=?";
        String[] selectionArgs = new String[]{"read", "unread"};

        assert db != null;
        cursor = db.query(NewsDataContract.TABLE_NAME, null, selection, selectionArgs, null, null, sortOrder);

        if( cursor.getCount() >= MAX_UNSELECTED_ROWS){
            int count  = cursor.getCount() - MAX_UNSELECTED_ROWS;
            cursor.moveToLast();

            while (count > 0){
                String d_ID = cursor.getString(cursor.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
                // Define 'where' part of query.
                String d_selection = NewsDataContract.COLUMN_ENTRY_ID + "=" + "?";

                String[] d_selectionArgs = { d_ID };
                // Issue SQL statement.
                db.delete(NewsDataContract.TABLE_NAME, d_selection, d_selectionArgs);

                //TODO :check if cursor remains the same here
                cursor.moveToPrevious();

                count = count - 1;
            }

        }
        db.close();

    }

    public void deleteALLNews(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        if (db != null) {
            db.delete(NewsDataContract.TABLE_NAME, null, null);
            db.close();
        }
    }

    public int fetchNewsUpdate(String URL){
        int count=0;
        if (URL.equals("http://math.sysu.edu.cn")){
            count+=fetchSYSUMCNewsUpdate(URL);
        }
        else if (URL.equals("http://jwc.sysu.edu.cn/")){
            count+=fetchJWCNewsUpdate();
        }
        else if (URL.equals("http://xsc2000.sysu.edu.cn/")){
            count+=fetchXSCNewsUpdate();
        }
        else {
            count+=fetchRSSNewsUpdate(URL);
        }
        return count;
    }

    public int fetchRSSNewsUpdate(String URL){
        int count=0;
        Html temp=new Html(URL,"test");
        ArrayList items=temp.getAllItem();
        String rssTitle=temp.getRSSTitle();
        String tempLatestURL=getLatestURL(rssTitle," ");
        String title, website, col, time, content, parsedHtml,tempURL;
        if (tempLatestURL.equals("-1")){
            for (int i=items.size()-1;i>=0;i--){
                String tempItem=items.get(i).toString();

                title = temp.getTitle(tempItem);
                website = rssTitle;
                col = " ";
                time = temp.getTime(tempItem);
                content = "<div><tr><td><b><font size='5'>" + title + "</font></b></td></tr></div>"
                        + "<div><tr><td><font size='4'>" + time + "</font></td></tr></div>"
                        + "<div>来自 "+ "<tr><td><font size='3'>" + website + "</font></td></tr></div>"
                        + "<tr><td><hr width=\"98%\" noshade='noshade' style=\"height:1px\"></td></tr>"
                        +temp.getContent(tempItem);
                parsedHtml = temp.getDescription(tempItem);
                tempURL=temp.getURL(tempItem);
                insert(tempURL, title, website, col, time, content, parsedHtml);
                count++;
            }
        }
        else if (tempLatestURL.equals(temp.getURL(items.get(0).toString()))){
            //dont have to do anything
            Log.v("test!!!","nothing to update");
        }
        else {
            int i;
            for (i=items.size()-1;i>=0;i--){
                if (temp.getURL(items.get(i).toString()).equals(tempLatestURL)){
                    break;
                }
            }
            i--;
            for (;i>=0;i--){
                String tempItem=items.get(i).toString();

                title = temp.getTitle(tempItem);
                website = rssTitle;
                col = " ";
                time = temp.getTime(tempItem);
                content = "<div><tr><td><b><font size='5'>" + title + "</font></b></td></tr></div>"
                        + "<div><tr><td><font size='4'>" + time + "</font></td></tr></div>"
                        + "<div>来自 "+ "<tr><td><font size='3'>" + website + "</font></td></tr></div>"
                        + temp.getContent(tempItem);
                parsedHtml = temp.getDescription(tempItem);
                tempURL=temp.getURL(tempItem);
                insert(tempURL, title, website, col, time, content, parsedHtml);
                count++;
            }
        }
        return count;
    }

    public int fetchXSCNewsUpdate(){
        int count=0;
        Html homepage=new Html("http://xsc2000.sysu.edu.cn/","utf");
        ArrayList allurlpart1=homepage.getAllUrlFromXscPart1();
        ArrayList col_tz=homepage.getAllUrlFromXscPart2(allurlpart1.get(0).toString());
        ArrayList col_xyhd=homepage.getAllUrlFromXscPart2(allurlpart1.get(3).toString());
        ArrayList col_gzwj=homepage.getAllUrlFromXscPart2(allurlpart1.get(1).toString());
        ArrayList col_qgzx=homepage.getAllUrlFromXscPart2(allurlpart1.get(2).toString());
        count+=updateProgramForXsc(col_gzwj, "工作文件");
        count+=updateProgramForXsc(col_qgzx, "勤工助学");
        count+=updateProgramForXsc(col_tz, "通知");
        count+=updateProgramForXsc(col_xyhd, "学院活动");
        return count;
    }

    @SuppressWarnings("rawtypes")
    private int updateProgramForXsc(ArrayList Col,String program){
        int count=0;
        String tempLatestURL=getLatestURL("中山大学学生处",program);
        String title, website, time, content, parsedHtml;
        //empty
        if (tempLatestURL.equals("-1")){
            for (int i=Col.size()-1;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl,"utf");
                title = temp.getTitleForXsc();
                website = "中山大学学生处";
                time = temp.getTimeForXsc();
                content = "<div><tr><td><b><font size='5'>" + title + "</font></b></td></tr></div>"
                        + "<div><tr><td><font size='4'>" + time + "</font></td></tr></div>"
                        + "<div>来自 "+ "<tr><td><font size='3'>" + website + ">" + program + "</font></td></tr></div>"
                        + "<tr><td><hr width=\"98%\" noshade='noshade' style=\"height:1px\"></td></tr>"
                        + temp.getContentForXsc();
                parsedHtml = temp.parseHtmlForXsc();
                insert(tempurl, title, website, program, time, content, parsedHtml);
                count++;
            }
        }
        else if (tempLatestURL.equals(Col.get(0).toString())){
            //dont have to do anything
        }
        else {
            int i;
            for (i=Col.size()-1;i>=0;i--){
                if (Col.get(i).toString().equals(tempLatestURL)){
                    break;
                }
            }
            i--;
            for (;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl,"utf");

                title = temp.getTitleForXsc();
                website = "中山大学学生处";
                time = temp.getTimeForXsc();
                content = "<div><tr><td><b><font size='5'>" + title + "</font></b></td></tr></div>"
                        + "<div><tr><td><font size='4'>" + time + "</font></td></tr></div>"
                        + "<div>来自 "+ "<tr><td><font size='3'>" + website + ">" + program + "</font></td></tr></div>"
                        + temp.getContentForXsc();
                parsedHtml = temp.parseHtmlForXsc();

                insert(tempurl, title, website, program, time, content, parsedHtml);

                count++;
            }
        }
        Log.v("test!!!", String.valueOf(count));

        return count;
    }


    public int fetchJWCNewsUpdate(){
        int count=0;
        Html departmentnews=new Html("http://jwc.sysu.edu.cn/departmentnews/Index.aspx","utf");
        Html information=new Html("http://jwc.sysu.edu.cn/information/Index.aspx","utf");
        ArrayList allUrlFromDepartmentnews=departmentnews.getAllUrlFromJwc();
        ArrayList allUrlFromInformation=information.getAllUrlFromJwc();
        count+=updateProgramForJwc(allUrlFromDepartmentnews,"院系教务信息");
        count+=updateProgramForJwc(allUrlFromInformation,"信息荟萃");
        Log.v("test!!!", String.valueOf(count));
        return count;
    }

    @SuppressWarnings("rawtypes")
    private int updateProgramForJwc(ArrayList Col,String program){
        int count=0;
        String tempLatestURL=getLatestURL("中山大学教务处",program);
        String title, website, time, content, parsedHtml;

        //empty
        if (tempLatestURL.equals("-1")){
            for (int i=Col.size()-1;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl,"utf");

                title = temp.getTitleForJwc();
                website = "中山大学教务处";
                time = temp.getTimeForJwc();
                content = "<div><tr><td><b><font size='5'>" + title + "</font></b></td></tr></div>"
                        + "<div><tr><td><font size='4'>" + time + "</font></td></tr></div>"
                        + "<div>来自 "+ "<tr><td><font size='3'>" + website + ">" + program + "</font></td></tr></div>"
                        + "<tr><td><hr width=\"98%\" noshade='noshade' style=\"height:1px\"></td></tr>"
                        + temp.getContentForJwc();
                parsedHtml = temp.parseHtmlForJwc();

                insert(tempurl, title, website, program, time, content, parsedHtml);
                count++;
            }
        }
        else if (tempLatestURL.equals(Col.get(0).toString())){
            //dont have to do anything
        }
        else {
            int i;
            for (i=Col.size()-1;i>=0;i--){
                if (Col.get(i).toString().equals(tempLatestURL)){
                    break;
                }
            }
            i--;
            for (;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl,"utf");

                title = temp.getTitleForJwc();
                website = "中山大学教务处";
                time = temp.getTimeForJwc();
                content = "<div><tr><td><b><font size='5'>" + title + "</font></b></td></tr></div>"
                        + "<div><tr><td><font size='4'>" + time + "</font></td></tr></div>"
                        + "<div>来自 "+ "<tr><td><font size='3'>" + website + ">" + program + "</font></td></tr></div>"
                        + temp.getContentForJwc();
                parsedHtml = temp.parseHtmlForJwc();

                insert(tempurl, title, website, program, time, content, parsedHtml);

                count++;
            }
        }
        return count;
    }

    //调用即可抓取所有新闻
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public int fetchSYSUMCNewsUpdate(String URL){
        Html homepage=new Html(URL);
        int count=0;
        ArrayList allurl=homepage.getAllUrlFromSysumcPart1();
        ArrayList col_student=homepage.getAllUrlFromSysumcPart2(allurl.get(3).toString());
        ArrayList col_teaching=homepage.getAllUrlFromSysumcPart2(allurl.get(1).toString());
        ArrayList col_learning=homepage.getAllUrlFromSysumcPart2(allurl.get(2).toString());
        ArrayList col_activity=homepage.getAllUrlFromSysumcPart2(allurl.get(0).toString());
        count+=updateProgram(col_student,"学生园地");
        count+=updateProgram(col_teaching,"教学经纬");
        count+=updateProgram(col_activity,"学院动态");
        count+=updateProgram(col_learning,"学术资讯");
        Log.v("test!!!", String.valueOf(count));
        return count;
    }

    @SuppressWarnings("rawtypes")
    private int updateProgram(ArrayList Col,String program){
        int count=0;
        String tempLatestURL=getLatestURL("中山大学数计院",program);
        String title, website, time, content, parsedHtml;

        //empty
        if (tempLatestURL.equals("-1")){
            for (int i=Col.size()-1;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl);

                title = temp.getTitle();
                website = "中山大学数计院";
                time = temp.getTime();
                content = "<div><tr><td><b><font size='5'>" + title + "</font></b></td></tr></div>"
                        + "<div><tr><td><font size='4'>" + time + "</font></td></tr></div>"
                        + "<div>来自 "+ "<tr><td><font size='3'>" + website + ">" + program + "</font></td></tr></div>"
                        + temp.getContent();
                parsedHtml = temp.parseHtml();

                insert(tempurl, title, website, program, time, content, parsedHtml);
                count++;
            }
        }
        else if (tempLatestURL.equals(Col.get(0).toString())){
            //dont have to do anything
        }
        else {
            int i;
            for (i=Col.size()-1;i>=0;i--){
                if (Col.get(i).toString().equals(tempLatestURL)){
                    break;
                }
            }
            i--;
            for (;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl);

                title = temp.getTitle();
                website = "中山大学数计院";
                time = temp.getTime();
                content = "<div><tr><td><b><font size='5'>" + title + "</font></b></td></tr></div>"
                        + "<div><tr><td><font size='4'>" + time + "</font></td></tr></div>"
                        + "<div>来自 "+ "<tr><td><font size='3'>" + website + ">" + program + "</font></td></tr></div>"
                        + temp.getContent();
                parsedHtml = temp.parseHtml();

                insert(tempurl, title, website, program, time, content, parsedHtml);

                count++;
            }
        }
        return count;
    }


}
