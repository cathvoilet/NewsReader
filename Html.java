package com.ezra.test;

import android.util.Log;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.*;

public class Html {
    public String url_yes;

    public String Sources;

    Html(String a){url_yes=a;}

    public String getHtmlString() {
        try {
            URL url = new URL(url_yes);
            URLConnection ucon = url.openConnection();
            InputStream instr = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(instr);
            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            //对应网页编码
            Sources=EncodingUtils.getString(baf.toByteArray(), "GBK");
            return EncodingUtils.getString(baf.toByteArray(), "GBK");
        } catch (Exception e) {
            return "";
        }
    }

    //for home page
    public void getAllUrl(){
        Pattern new_col = Pattern.compile("<table width=\"100%\" cellpadding=\"3\">(.*?)</table>");
        Matcher matchedCol = new_col.matcher(Sources);
        String title=new String();
        while (matchedCol.find()){
            title = matchedCol.group(1);
            Pattern news = Pattern.compile("<a.*?href=\"(.*?)\"\\s+target=\"_blank\"\\stitle=.*?>(.*?)</a>");
            Matcher matchedTitle = news.matcher(title);
            //get all of them by while
            while (matchedTitle.find()){
                //下面那个换成加入数据库。。
                Log.v("testurl",url_yes+matchedTitle.group(1));
                Log.v("testtitle",matchedTitle.group(2));
            }
        }
    }

    //get time and title
    public void getInfo(){
        String Title=new String();
        String Time=new String();
        //识别4个汉字。。。
        Pattern news = Pattern.compile("<font size='2'>(.*?)</font>.*?[\\u4E00-\\u9FA5]{4}:</b>(.*?)&nbsp;&nbsp;<b>");
        Matcher matchedNews = news.matcher(Sources);
        Log.v("test!!!","!!!");
        if (matchedNews.find()){
            Title=matchedNews.group(1);
            Time=matchedNews.group(2);
            Log.v("test!!!",Title);
            Log.v("test!!!",Time);
        }
        //TODO:
    }

    //get content with some bugs....- -
    public void getContent(){
        String Content=new String();
        Pattern news = Pattern.compile("<table width=\"100%\"  align=\"center\">(.*?)</table>");
        Matcher matchedNews = news.matcher(Sources);
        Log.v("test!!!","!!!");
        if (matchedNews.find()){
            Content=matchedNews.group(1);
            Log.v("test!!!",Content);
        }
        //TODO:
    }
    
    public void getAllContent(){}

}
