package com.ezra.test;

import android.util.Log;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
    public ArrayList getAllUrl(){
        Pattern new_col = Pattern.compile("<table width=\"100%\" cellpadding=\"3\">(.*?)</table>");
        Matcher matchedCol = new_col.matcher(Sources);
        String title=new String();
        ArrayList url=new ArrayList();
        while (matchedCol.find()){
            title = matchedCol.group(1);
            Pattern news = Pattern.compile("<a.*?href=\"(.*?)\"\\s+target=\"_blank\"\\stitle=.*?>(.*?)</a>");
            Matcher matchedTitle = news.matcher(title);
            //get all of them by while
            while (matchedTitle.find()){
                //下面那个换成加入数据库。。
                url.add(url_yes+matchedTitle.group(1));
                //Log.v("testurl",url_yes+matchedTitle.group(1));
                //Log.v("testtitle",matchedTitle.group(2));
            }
        }
        //for (int j=0;j<url.size();j++){
        //    Log.v("test!!!",url.get(j).toString());
        //}
        return url;
    }

    public void divideCol(ArrayList url){
        for (int j=0;j<url.size();j++){
            Log.v("test???",url.get(j).toString());
        }
        //xueshengyuandi
        Pattern patterncol_student=Pattern.compile("(.*ColumnNo=NA05.*)");
        //jiaoxuejingwei
        Pattern patterncol_teaching=Pattern.compile("(.*ColumnNo=NA02.*)");
        //xuesuzixun
        Pattern patterncol_learning=Pattern.compile("(.*ColumnNo=NA03.*)");
        //xueyuandongtai
        Pattern patterncol_activity=Pattern.compile("(.*ColumnNo=NA01.*)");
        ArrayList col_student=new ArrayList();
        ArrayList col_teaching=new ArrayList();
        ArrayList col_learning=new ArrayList();
        ArrayList col_activity=new ArrayList();
        for (int j=0;j<url.size();j++){
            String theurl=url.get(j).toString();
            Matcher matchactivity=patterncol_activity.matcher(theurl);
            Matcher matchlearning=patterncol_learning.matcher(theurl);
            Matcher matchstudent=patterncol_student.matcher(theurl);
            Matcher matchteaching=patterncol_teaching.matcher(theurl);
            if (matchactivity.find()){
                col_activity.add(matchactivity.group(1));
            }
            if (matchlearning.find()){
                col_learning.add(matchlearning.group(1));
            }
            if (matchstudent.find()){
                col_student.add(matchstudent.group(1));
            }
            if (matchteaching.find()){
                col_teaching.add(matchteaching.group(1));
            }
        }
        /*
        for (int j=0;j<col_activity.size();j++){
            Log.v("testactivity",col_activity.get(j).toString());
        }
        for (int j=0;j<col_student.size();j++){
            Log.v("teststudent",col_student.get(j).toString());
        }
        for (int j=0;j<col_learning.size();j++){
            Log.v("testlearning",col_learning.get(j).toString());
        }
        for (int j=0;j<col_teaching.size();j++){
            Log.v("testteaching",col_teaching.get(j).toString());
        }*/
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

    //TODO:
    public void getAllContent(){

    }
}
