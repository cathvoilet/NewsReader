package com.example.newsreader;


import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html {
    private String url_yes;

    private String Sources;

    Html(String a){url_yes=a;Sources=getHtmlString();}
    Html(String a,int i){url_yes=a;}
    //for utf-8
    Html(String a,String t){url_yes=a;Sources=getHtmlStringForUTF8();}

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
            Log.v("test!!!","finishgettingsource");
            return EncodingUtils.getString(baf.toByteArray(), "GBK");
        } catch (Exception e) {
            return "";
        }
    }

    public String getHtmlStringForUTF8() {
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
            Sources=EncodingUtils.getString(baf.toByteArray(), "UTF-8");
            Log.v("test!!!","finishgettingsource");
            return EncodingUtils.getString(baf.toByteArray(), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public ArrayList getAllUrlFromSysumcPart1(){
        Pattern new_col = Pattern.compile("<table width=\"100%\" cellpadding=\"3\">(.*?)</table>");
        Matcher matchedCol = new_col.matcher(Sources);
        ArrayList url=new ArrayList();
        while (matchedCol.find()){
            url.add(matchedCol.group(1));
        }
        return url;
    }

    public ArrayList getAllUrlFromSysumcPart2(String temp){
        Pattern news = Pattern.compile("<a.*?href=\"(.*?)\"");
        Matcher matchedTitle = news.matcher(temp);
        ArrayList url=new ArrayList();
        int count=0;
        //get all of them by while
        while (matchedTitle.find()){
            url.add(url_yes+matchedTitle.group(1));
            count++;
            Log.v("test!!!", url_yes+matchedTitle.group(1));
        }
        Log.v("test!!!",String.valueOf(count));
        return url;
    }

    public ArrayList getAllUrlFromJwc(){
        Pattern new_col = Pattern.compile("<li><a href=\"(.*?)\" title");
        Matcher matchedCol = new_col.matcher(Sources);
        ArrayList url=new ArrayList();
        int count=0;
        while (matchedCol.find()){
            url.add(matchedCol.group(1));
            count++;
        }
        Log.v("test!!!", String.valueOf(count));
        return url;
    }

    public ArrayList getAllUrlFromXscPart1(){
        Pattern new_col = Pattern.compile("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">([\\s\\S]*?)</table>");
        Matcher matchedCol = new_col.matcher(Sources);
        ArrayList url=new ArrayList();
        while (matchedCol.find()){
            url.add(matchedCol.group(1));
        }
        return url;
    }

    public ArrayList getAllUrlFromXscPart2(String temp){
        Pattern new_col = Pattern.compile("  <a href=\"(.*?)\"");
        Matcher matchedCol = new_col.matcher(temp);
        ArrayList url=new ArrayList();
        int count=0;
        while (matchedCol.find()){
            url.add("http://xsc2000.sysu.edu.cn/"+matchedCol.group(1));
            count++;
        }
        Log.v("test!!!",String.valueOf(count));
        return url;
    }

    public ArrayList getAllItem(){
        Pattern item=Pattern.compile("<item>([\\s\\S]*?)</item>");
        Matcher matchedItem=item.matcher(Sources);
        ArrayList items=new ArrayList();
        int count=0;
        while (matchedItem.find()){
            items.add(matchedItem.group(1));
            count++;
        }
        Log.v("test???", String.valueOf(count));
        return items;
    }

    public String getRSSTitle(){
        String title="cant get it";
        Pattern item=Pattern.compile("<title>(.*?)</title>");
        Matcher matchedTitle=item.matcher(Sources);
        if (matchedTitle.find()){
            title=matchedTitle.group(1);
        }
        return title;
    }

    public String getTime(){
        String Time=new String("cant get it");
        //识别4个汉字。。。
        Pattern news = Pattern.compile("<font size='2'>.*?</font>.*?[\\u4E00-\\u9FA5]{4}:</b>(.*?)&nbsp;&nbsp;<b>");
        Matcher matchedNews = news.matcher(Sources);
        if (matchedNews.find()){
            Time=matchedNews.group(1);
        }
        return Time;
    }

    public String getTimeForXsc(){
        String Time=new String("cant get it");
        //识别4个汉字。。。
        Pattern news = Pattern.compile("&nbsp;[\\u4E00-\\u9FA5]{2}：(.*?) &nbsp;");
        Matcher matchedNews = news.matcher(Sources);
        if (matchedNews.find()){
            Time=matchedNews.group(1);
        }
        return Time;
    }

    public String getTimeForJwc(){
        String Time= "cant get it";
        //识别4个汉字。。。
        Pattern news = Pattern.compile("<div class=\"art_property\">.*?[\\u4E00-\\u9FA5]{4}：(.*?)　<a.*?</div>");
        Matcher matchedNews = news.matcher(Sources);
        if (matchedNews.find()){
            Time=matchedNews.group(1);
        }
        return Time;
    }

    public String getTime(String item){
        String Time= "cant get it";
        Pattern news = Pattern.compile("<pubDate>(.*?)</pubDate>");
        Matcher matchedNews = news.matcher(item);
        if (matchedNews.find()){
            Time=matchedNews.group(1);
        }
        return Time;
    }

    public String getTitle(){
        String Title="cant get it";
        Pattern news = Pattern.compile("<font size='2'>(.*?)</font>");
        Matcher matchedNews = news.matcher(Sources);
        if (matchedNews.find()){
            Title=matchedNews.group(1);
        }
        return Title;
    }

    public String getTitleForXsc(){
        String Title="cant get it";
        Pattern news = Pattern.compile("<td height=\"50\" colspan=\"2\" align=\"center\" class=\"tit\">(.*?)</td>");
        Matcher matchedNews = news.matcher(Sources);
        if (matchedNews.find()){
            Title=matchedNews.group(1);
        }
        return Title;
    }

    public String getTitleForJwc(){
        String Title="客户端IP地址访问受限！";
        Pattern news = Pattern.compile("<h1 class=\"art_title o_h\">(.*?)</h1>");
        Matcher matchedNews = news.matcher(Sources);
        if (matchedNews.find()){
            Title=matchedNews.group(1);
        }
        return Title;
    }

    public String getTitle(String item){
        String Title="cant get it";
        Pattern news = Pattern.compile("<title>(.*?)</title>");
        Matcher matchedNews = news.matcher(item);
        if (matchedNews.find()){
            Title=matchedNews.group(1);
        }
        return Title;
    }

    public String getURL(String item){
        String URL="cant get it";
        Pattern news = Pattern.compile("<link>(.*?)</link>");
        Matcher matchedNews = news.matcher(item);
        if (matchedNews.find()){
            URL=matchedNews.group(1);
        }
        return URL;
    }

    public String getDescription(String item){
        String Description="cant get it";
        Pattern news = Pattern.compile("<description><!\\[CDATA\\[([\\s\\S]*?)\\]\\]></description>");
        Matcher matchedNews = news.matcher(item);
        if (matchedNews.find()){
            Description=matchedNews.group(1);
        }
        return Description;
    }

    public String getContent(String item){
        String tempContent="cant get it";
        String Content= "";
        Pattern news = Pattern.compile("<content:encoded>([\\s\\S]*?)</content:encoded>");
        Matcher matchedNews = news.matcher(item);
        if (matchedNews.find()){
            tempContent=matchedNews.group(1);
            Pattern temp=Pattern.compile("<!\\[CDATA\\[([\\s\\S]*?)\\]\\]>");
            Matcher matched=temp.matcher(tempContent);
            while (matched.find()){
                Content+=matched.group(1);
            }
        }

        return Content;
    }


    public String getContent(){
        String Content="cant get it";
        String finalContent="cant get it";
        Pattern news = Pattern.compile("(<table\\s*width=\"100%\"\\s*align=\"center\">[\\s\\S]*?</table>)");
        Matcher matchedNews = news.matcher(Sources);
        if (matchedNews.find()){
            Content=matchedNews.group(1);
            Pattern del=Pattern.compile("(<tr><td>[\\u4E00-\\u9FA5]{3}:.*?</td></tr>)");
            Matcher delNews=del.matcher(Content);
            if (delNews.find()){
                finalContent=delNews.replaceAll("");
            }
        }

        Document doc = null;
        doc = Jsoup.parse(finalContent);

        doc.select("tr").first().remove();
        doc.select("tr").first().remove();

        return doc.html();
    }

    public String getContentForJwc(){
        String Content=new String("cant get it");
        Document doc=Jsoup.parse(Sources);
        Elements test=doc.getElementsByClass("content");
        Content=test.toString();
        return Content;
    }

    public String getContentForXsc(){
        String Content="cant get it";
        Pattern news = Pattern.compile("<td colspan=\"2\">([\\s\\S]*?)</td>");
        Matcher matchedNews = news.matcher(Sources);
        if (matchedNews.find()){
            Content=matchedNews.group(1);
        }
        return Content;
    }

    //提取content
    public String parseHtml(){
        String finalContent = getContent();

        String parsedContent = "failed";
        finalContent = "<html>"+ finalContent + "</html>";
        parsedContent = Jsoup.clean(finalContent, Whitelist.relaxed());
        Document doc = null;
        doc = Jsoup.parse(finalContent);
        return doc.text();
    }

    //提取content
    public String parseHtmlForJwc(){
        String finalContent = getContentForJwc();

        String parsedContent = "failed";
        finalContent = "<html>"+ finalContent + "</html>";
        parsedContent = Jsoup.clean(finalContent, Whitelist.relaxed());
        Document doc = null;
        doc = Jsoup.parse(finalContent);
        return doc.text();
    }

    public String parseHtmlForXsc(){
        String finalContent = getContentForXsc();

        String parsedContent = "failed";
        finalContent = "<html>"+ finalContent + "</html>";
        parsedContent = Jsoup.clean(finalContent, Whitelist.relaxed());
        Document doc = null;
        doc = Jsoup.parse(finalContent);
        return doc.text();
    }

}
