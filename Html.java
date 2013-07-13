package com.ezra.test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.util.*;

public class Html {
    String url_yes;

    Html(String a){url_yes=a;}

    public String getHtmlString() {
        try {
            URL url = new URL(url_yes);
            URLConnection ucon = url.openConnection();
            InputStream instr = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(instr);
            ByteArrayBuffer baf = new ByteArrayBuffer(10000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            //对应网页编码
            return EncodingUtils.getString(baf.toByteArray(), "GBK");
        } 
        catch (Exception e) {
            return "";
        }
    }

    public String[] getAllUrl(String htmlString){
        String[] allOfThem=new String[]{""};
        // TODO
        return allOfThem;
    }
        
    public void getAllContent(){}


}
