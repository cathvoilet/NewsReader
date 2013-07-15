package com.ezra.test;

import java.util.ArrayList;

public class SpiderActivity {

    private String URL; //(新闻的url)

    private String website; //（新闻的来源网站:中大数计院）

    private NewsData db;

    public SpiderActivity(String website,String URL,NewsData db){website=website;URL=URL;db=db;}

    //调用即可抓取所有新闻
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
            Html temp=new Html(tempurl);
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
            if (Col=="学术咨询"){
                col_learning.add(tempurl);
            }
        }
        //学生园地：student
        count+=updateProgram(col_student,"学生园地");
        //教学经纬：teaching
        count+=updateProgram(col_teaching,"教学经纬");
        //学院动态：activity
        count+=updateProgram(col_activity,"学院动态");
        //学术咨询：learning
        count+=updateProgram(col_learning,"学术咨询");
        return count;
    }

    private int updateProgram(ArrayList Col,String Program){
        int count=0;
        String tempLatestURL=db.getLatestURL(website,Program);
        //empty
        if (tempLatestURL=="-1"){
            for (int i=Col.size()-1;i>=0;i--){
                String tempurl=Col.get(i).toString();
                Html temp=new Html(tempurl);
                db.insertFirst(tempurl,website,temp.getCol());
                db.insertSecond(tempurl,temp.getTitle(),temp.getTime(),temp.getContent());
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
                db.insertFirst(tempurl,website,temp.getCol());
                db.insertSecond(tempurl,temp.getTitle(),temp.getTime(),temp.getContent());
                count++;
            }
        }  
        return count;
    }

}
