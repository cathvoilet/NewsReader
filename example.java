package com.ezra.test;

import android.os.Bundle;
import android.view.Menu;
import android.util.Log;
import android.os.AsyncTask;
import android.app.Activity;
import android.os.Handler;

public class MainActivity extends Activity {

    private static String BASE="http://math.sysu.edu.cn";
    public static String TESTURL="http://math.sysu.edu.cn/main/news/CommonNews.aspx?ColumnNo=NA01&NewsNo=7a0e33c1-33ab-458b-af2b-a6a669a212b2";
    public static String TESTURL2="http://math.sysu.edu.cn/main/news/CommonNews.aspx?ColumnNo=NA01&NewsNo=e38c6ae5-7d35-4be3-9b6f-2a093632c1a8";

    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //********************************
        //定时器定时执行更新
        //********************************
        runnable = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 在此处添加执行的代码
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
                //************************************
                //以下可用于调用更新
                //************************************
                Html test=new Html(urls[0]);
                Log.v("test!!!","begin");
                //String myString  = test.getHtmlString();
                //test.getInfo();
                test.getTitle();
                //test.divideCol(test.getAllUrl());
                return "";
                //return myString;
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


