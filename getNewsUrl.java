package com.ezra.test;

import android.os.Bundle;
import android.view.Menu;
import android.widget.SimpleAdapter;
import android.util.Log;
import android.os.AsyncTask;
import android.app.ListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ListActivity {

    public ArrayList<HashMap<String,String>> items=new ArrayList<HashMap<String, String>>();

    private static String BASE="http://math.sysu.edu.cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetElements().execute(BASE);
        setContentView(R.layout.activity_main);
        
        SimpleAdapter listApadter = new  SimpleAdapter(
                this,items,R.layout.data,new String[]{"title","url"},
                new int[]{R.id.title,R.id.url});
        setListAdapter(listApadter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //HTML shouldn't be used in mainactivity
    //otherwise:Caused by: android.os.NetworkOnMainThreadException
    private class GetElements extends AsyncTask<String, Integer, String> {

        private Exception exception;

        @Override
        protected String doInBackground(String... urls) {
            try {
                Html test=new Html(urls[0]);
                String myString  = test.getHtmlString();
                return myString;
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
            //do something with the html_string
            Pattern new_col = Pattern.compile("<table width=\"100%\" cellpadding=\"3\">(.*?)</table>");
            Matcher matchedCol = new_col.matcher(myString);
            String title=new String();
            while (matchedCol.find()){
                title = matchedCol.group(1);
                Pattern news = Pattern.compile("<a.*?href=\"(.*?)\"\\s+target=\"_blank\"\\stitle=.*?>(.*?)</a>");
                Matcher matchedTitle = news.matcher(title);
                //get all of them by while
                while (matchedTitle.find()){
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("url",BASE+matchedTitle.group(1));
                    item.put("title",matchedTitle.group(2));
                    //下面那个换成加入数据库。。
                    Log.v("testurl",BASE+matchedTitle.group(1));
                    Log.v("testtitle",matchedTitle.group(2));
                    items.add(item);
                }
            }
        }
    }
}


