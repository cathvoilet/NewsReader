package com.example.newsreader;

import java.util.ArrayList;

import android.app.Activity;

public class SpiderActivity extends Activity {
	private String URL; //(新闻的url)

    private String website; //（新闻的来源网站:中大数计院）

    private NewsData db;

    public SpiderActivity(String site,String url,NewsData database){
        website=site;
        URL=url;
        db=database;
    }
    
  //调用即可抓取所有新闻
    @SuppressWarnings("rawtypes")
	public int fetchNewsUpdate(){
        Html homepage=new Html(URL);
        int count=0;
        ArrayList allurl=homepage.getAllUrl();
        ArrayList col_student=new ArrayList();
        ArrayList col_teaching=new ArrayList();
        ArrayList col_learning=new ArrayList();
        ArrayList col_activity=new ArrayList();
        for (int i=0;i<allurl.size();i++){
            String tempurl=allurl.get(i).toString();
            Html temp=new Html(tempurl,0);
            String Col=temp.getCol();
            if (Col=="学生园地"){
                col_student.add(tempurl);
            }
            if (Col=="教学经纬"){
                col_teaching.add(tempurl);
            }
            if (Col=="学院动态"){
                col_activity.add(tempurl);
            }
            if (Col=="学术资讯"){
                col_learning.add(tempurl);
            }
        }
        count+=updateProgram(col_student,"学生园地");
        count+=updateProgram(col_teaching,"教学经纬");
        count+=updateProgram(col_activity,"学院动态");
        count+=updateProgram(col_learning,"学术资讯");
        return count;
    }

    @SuppressWarnings("rawtypes")
	private int updateProgram(ArrayList Col,String Program){
        int count=0;
        String tempLatestURL=db.getLatestURL(website,Program);
        //empty
        if (tempLatestURL=="-1"){
            for (int i=Col.size()-1;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl);
                db.insert(tempurl, temp.getTitle(), "中山大学数计院", temp.getCol(), temp.getTime(), temp.getContent());
                count++;
            }
        }
        else if (tempLatestURL==Col.get(0).toString()){
            //dont have to do anything
        }
        else {
            int i;
            for (i=Col.size()-1;i>=0;i--){
                if (Col.get(i).toString()==tempLatestURL){
                    break;
                }
            }
            i--;
            for (;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl);
                db.insert(tempurl, temp.getTitle(), "中山大学数计院", temp.getCol(), temp.getTime(), temp.getContent());
                count++;
            }
        }
        return count;
    }

}
