package com.example.newsreader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.newsreader.NewsData.NewsDataContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity {
	public final static String EXTRA_MESSAGE1 = "com.example.Newsreader.MESSAGE1";
	public final static String EXTRA_MESSAGE2 = "com.example.Newsreader.MESSAGE2";
	public final static String EXTRA_MESSAGE3 = "com.example.Newsreader.MESSAGE3";
	public final static String EXTRA_MESSAGE4 = "com.example.Newsreader.MESSAGE4";
	public final static String EXTRA_MESSAGE5 = "com.example.Newsreader.MESSAGE5";
	public final static String EXTRA_MESSAGE6 = "com.example.Newsreader.MESSAGE6";
    public final static String EXTRA_MESSAGE7 = "com.example.Newsreader.MESSAGE7";

    RefreshableView refreshableView;

    //Handler handler = new Handler();
	//Runnable runnable;

    SharedPreferences sp;

    Cursor cs;
	ListView listNews;
    int index;
    int top;
	NewsData news = new NewsData(this);

    static final String[] FROM = {NewsDataContract.COLUMN_TITLE,NewsDataContract.COLUMN_WEBSITE ,NewsDataContract.COLUMN_PARSED_CONTENT
    };
    static final int[] TO = {R.id.textTITLE,R.id.textWebsite, R.id.textContent};

    SimpleCursorAdapter adapter;
    int slideNum=1;//0 for all,1 for unread,2 for selected


    private LinearLayout testLAYOUT;
    private TextView readMode;
    private TextView chooseWebsite;
    ListView mDrawerList;
    ListView mWebsiteDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    String[] slideMenu;
    private String WEBSITE_CHOSEN="all";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        sp=getSharedPreferences("com.example.newsreader_preferences",0);

        listNews = (ListView) findViewById(R.id.listNews);
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);

        setupActionBar();

        //自动删除超过100条的未被收藏的新闻
        news.deleteUnselectedNews();
        //news.deleteALLNews();

        mTitle = mDrawerTitle = getTitle();
        slideMenu = getResources().getStringArray(R.array.slideMenu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        testLAYOUT=(LinearLayout)findViewById(R.id.testLinearLayout);
        readMode=(TextView)testLAYOUT.findViewById(R.id.readMODE);
        chooseWebsite=(TextView)testLAYOUT.findViewById(R.id.chooseWebsite);
        mDrawerList = (ListView) testLAYOUT.findViewById(R.id.left_drawer);
        mWebsiteDrawerList=(ListView)testLAYOUT.findViewById(R.id.left_WebsiteDrawer);

        
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, slideMenu));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        
        readMode.setOnClickListener(new DrawerItemClickListener());
        chooseWebsite.setOnClickListener(new DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */)
        {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(slideNum);
        }



        //********************************
        //定时器定时执行更新
        //********************************
        /*
	    runnable = new Runnable(){
            @Override
            public void run() {
                new GetElements().execute();
                handler.postDelayed(this, 3600000);// 1h后执行this，即runable
            }
        };
        */
        Log.v("test???","onCreate");


    }

    @Override
    protected void onPause(){
        super.onPause();
        //save scroll position
        index = listNews.getFirstVisiblePosition();
        View v = listNews.getChildAt(0);
        top = (v == null) ? 0 : v.getTop();
        Log.v("test???","onPause");
    }

	@Override
	protected void onDestroy(){
	    super.onDestroy();
	    //handler.removeCallbacks(runnable);// 关闭定时器处理
        Log.v("test???","onDestory");
    }

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
	    super.onResume();
        
        
        // Set the adapter for the list view
        mWebsiteDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, makeWebsiteMenuList()));
        // Set the list's click listener
        mWebsiteDrawerList.setOnItemClickListener(new WebsiteDrawerItemClickListener());

        
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    new GetElements().execute();
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
        selectWebsite(makeWebsiteList());
        selectItem(slideNum);

        //restore scroll position
        listNews.setSelectionFromTop(index, top);
        Log.v("test???","onResume");



    }

    public void jump_to_content(int position,ArrayList sources){
        Intent intent = new Intent(this, ContentActivity.class);
        Cursor cs;
        cs = news.readByPositionModeUnread(position,sources);

        switch (slideNum){
            case 0:cs=news.readByPositionModeAll(position,sources);break;
            case 1:cs=news.readByPositionModeUnread(position,sources);break;
            case 2:cs=news.readByPositionModeSelected(position,sources);break;
        }

        String entryid = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
        String title = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_TITLE));
        String time = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_TIME));
        String website = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_WEBSITE));
        String program = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_PROGRAM));
        String content = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_CONTENT));
        String status = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_STATUS));

        if(slideNum == 1 ){
            news.changeStatus(entryid, "UnreadToRead");
        }

        if(slideNum == 0 ){
            assert status != null;
            if(status.equals("unread")){
                news.changeStatus(entryid, "UnreadToRead");
            }
        }

        intent.putExtra(EXTRA_MESSAGE1,title);
        intent.putExtra(EXTRA_MESSAGE2,time);
        intent.putExtra(EXTRA_MESSAGE3,website);
        intent.putExtra(EXTRA_MESSAGE4,program);
        intent.putExtra(EXTRA_MESSAGE5,content);
        intent.putExtra(EXTRA_MESSAGE6,entryid);
        intent.putExtra(EXTRA_MESSAGE7,status);
        startActivity(intent);
    }
	
	public void jump_to_content(int position,String sources){
		Intent intent = new Intent(this, ContentActivity.class);
		Cursor cs;
	    cs = news.readByPositionModeUnread(position,sources);
		
	    switch (slideNum){
            case 0:cs=news.readByPositionModeAll(position,sources);break;
            case 1:cs=news.readByPositionModeUnread(position,sources);break;
            case 2:cs=news.readByPositionModeSelected(position,sources);break;
        }
    	
    	String entryid = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_ENTRY_ID));
    	String title = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_TITLE));
    	String time = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_TIME));
    	String website = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_WEBSITE));
    	String program = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_PROGRAM));
    	String content = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_CONTENT));
    	String status = cs.getString(cs.getColumnIndex(NewsDataContract.COLUMN_STATUS));

    	if(slideNum == 1 ){
    	    news.changeStatus(entryid, "UnreadToRead");
    	}
    	
    	if(slideNum == 0 ){
            assert status != null;
            if(status.equals("unread")){
    	      news.changeStatus(entryid, "UnreadToRead");
    		}
    	}
    	
		intent.putExtra(EXTRA_MESSAGE1,title);
		intent.putExtra(EXTRA_MESSAGE2,time);
		intent.putExtra(EXTRA_MESSAGE3,website);
		intent.putExtra(EXTRA_MESSAGE4,program);
		intent.putExtra(EXTRA_MESSAGE5,content);
		intent.putExtra(EXTRA_MESSAGE6,entryid);
        intent.putExtra(EXTRA_MESSAGE7,status);
        startActivity(intent);
	}

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements OnItemClickListener, View.OnClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        @Override
        public void onClick(View view) {

        }
    }

    //TODO:should be changed when website was added
    public ArrayList makeList(){
        ArrayList temp=new ArrayList();
        if (sp.getBoolean("show_sysumc", false)){
            temp.add("中山大学数计院");
        }
        if (sp.getBoolean("show_sysujwc", false)){
            temp.add("中山大学教务处");
        }
        if (sp.getBoolean("show_sysuxsc", false)){
            temp.add("中山大学学生处");
        }
        if (sp.getBoolean("show_tongjizhidu", false)){
            temp.add("统计之都");
        }
        if (sp.getBoolean("show_kexuesongshuhui", false)){
            temp.add("科学松鼠会 &#187; 计算机科学");
        }
        return temp;
    }

    //TODO:should be changed when website was added
    public ArrayList makeWebsiteMenuList(){
        ArrayList temp=new ArrayList();
        temp.add("所有网站");
        if (sp.getBoolean("show_sysumc", false)){
            temp.add("中山大学数学与计算科学学院");
        }
        if (sp.getBoolean("show_sysujwc", false)){
            temp.add("中山大学教务处");
        }
        if (sp.getBoolean("show_sysuxsc", false)){
            temp.add("中山大学学生处");
        }
        if (sp.getBoolean("show_tongjizhidu", false)){
            temp.add("统计之都");
        }
        if (sp.getBoolean("show_kexuesongshuhui", false)){
            temp.add("科学松鼠会 » 计算机科学");
        }
        return temp;
    }

    //TODO:should be changed when website was added
    private int makeWebsiteList() {
        ArrayList temp=new ArrayList();
        int i=0;
        temp.add("all");
        if (sp.getBoolean("show_sysumc", false)){
            temp.add("http://math.sysu.edu.cn");
        }
        if (sp.getBoolean("show_sysujwc", false)){
            temp.add("http://jwc.sysu.edu.cn/");
        }
        if (sp.getBoolean("show_sysuxsc", false)){
            temp.add("http://xsc2000.sysu.edu.cn/");
        }
        if (sp.getBoolean("show_tongjizhidu", false)){
            temp.add("http://cos.name/feed/");
        }
        if (sp.getBoolean("show_kexuesongshuhui", false)){
            temp.add("http://songshuhui.net/archives/category/major/cs/feed");
        }
        for (i=0;i<temp.size();i++){
            if (temp.get(i).toString().equals(WEBSITE_CHOSEN)){
                break;
            }
        }
        return i;
    }

    //TODO:should be changed when website was added
    private void displayControl(){
        if (WEBSITE_CHOSEN.equals("http://math.sysu.edu.cn")){
            display("中山大学数计院");
        }
        else if (WEBSITE_CHOSEN.equals("http://cos.name/feed/")){
            display("统计之都");
        }
        else if (WEBSITE_CHOSEN.equals("http://songshuhui.net/archives/category/major/cs/feed")){
            display("科学松鼠会 &#187; 计算机科学");
        }
        else if (WEBSITE_CHOSEN.equals("http://jwc.sysu.edu.cn/")){
            display("中山大学教务处");
        }
        else if (WEBSITE_CHOSEN.equals("http://xsc2000.sysu.edu.cn/")){
            display("中山大学学生处");
        }
        else {
            if (makeList().size()==0){
                empty();
            }
            else display(makeList());
        }
    }

    private void empty() {
        List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("warn", "请在右上角选择订阅");
        data.add(item);
        HashMap<String, String> item1 = new HashMap<String, String>();
        item1.put("warn", "下拉可以刷新");
        data.add(item1);
        HashMap<String, String> item2 = new HashMap<String, String>();
        item2.put("warn", "菜单可以从左边拉出");
        data.add(item2);
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.row,
                new String[]{"warn"}, new int[]{R.id.textTITLE});
        listNews.setAdapter(adapter);
    }

    private void selectItem(int position) {
        // update selected item and title, then close the drawer
        slideNum=position;
        mDrawerList.setItemChecked(position, true);
        setTitle(slideMenu[position]);
        mDrawerLayout.closeDrawer(testLAYOUT);
        displayControl();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            mTitle = title;
            getActionBar().setTitle(mTitle);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
	}

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(testLAYOUT);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent preferencesIntent = new Intent(this, SettingsActivity.class);
                startActivity(preferencesIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Set up the {@link android.app.ActionBar}, if the API is available.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // enable ActionBar app icon to behave as action to toggle nav drawer
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);

        }
    }
    @SuppressLint("NewApi")
    public void display(final ArrayList website){
        cs = news.readALL(website);
        switch (slideNum){
            case 0:cs=news.readALL(website);break;
            case 1:cs=news.readUNREAD(website);break;
            case 2:cs=news.readSELECTED(website);break;
            default:break;
        }
        adapter = new SimpleCursorAdapter(this, R.layout.row, cs, FROM, TO, 0);
        listNews.setAdapter(adapter);
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                jump_to_content(position,website);
            }

        };
        listNews.setOnItemClickListener(mMessageClickedHandler);
    }
    @SuppressLint("NewApi")
    public void display(final String website){
        cs = news.readALL(website);
        switch (slideNum){
            case 0:cs=news.readALL(website);break;
            case 1:cs=news.readUNREAD(website);break;
            case 2:cs=news.readSELECTED(website);break;
            default:break;
        }
        adapter = new SimpleCursorAdapter(this, R.layout.row, cs, FROM, TO, 0);
        listNews.setAdapter(adapter);
        OnItemClickListener mMessageClickedHandler = new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                jump_to_content(position,website);
            }

        };
        listNews.setOnItemClickListener(mMessageClickedHandler);
    }

    //TODO:should be changed when website was added
    private class GetElements extends AsyncTask<String, Integer, String> {

        @SuppressWarnings("unused")
		private Exception exception;


        @Override
        protected String doInBackground(String... urls) {
            try {
                //************************************
                //以下可用于调用更新
                //************************************
                Log.v("test!!!","begin");


                if (WEBSITE_CHOSEN.equals("all")){
                    if (sp.getBoolean("show_sysumc", true)){
                        news.fetchNewsUpdate("http://math.sysu.edu.cn");
                    }
                    if (sp.getBoolean("show_sysujwc", true)){
                        news.fetchNewsUpdate("http://jwc.sysu.edu.cn/");
                    }
                    if (sp.getBoolean("show_sysuxsc", true)){
                        news.fetchNewsUpdate("http://xsc2000.sysu.edu.cn/");
                    }
                    if (sp.getBoolean("show_tongjizhidu", true)){
                        news.fetchNewsUpdate("http://cos.name/feed/");
                    }

                    if (sp.getBoolean("show_kexuesongshuhui", true)){
                        news.fetchNewsUpdate("http://songshuhui.net/archives/category/major/cs/feed");
                    }
                }
                else {
                    news.fetchNewsUpdate(WEBSITE_CHOSEN);
                }
                return "";
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

        //after refresh, display the unread item
        @Override
        protected void onPostExecute(String myString) {
            super.onPostExecute(myString);
            displayControl();
        }
    }

    private class WebsiteDrawerItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectWebsite(position);
        }

        
    }

    //TODO:should be changed when website was added

    private void selectWebsite(int position) {
        ArrayList SubscripitionsArray=makeWebsiteMenuList();
        String temp=SubscripitionsArray.get(position).toString();
        if (temp.equals("中山大学数学与计算科学学院")){
            WEBSITE_CHOSEN="http://math.sysu.edu.cn";
        }
        else if (temp.equals("中山大学教务处")){
            WEBSITE_CHOSEN="http://jwc.sysu.edu.cn/";
        }
        else if (temp.equals("中山大学学生处")){
            WEBSITE_CHOSEN="http://xsc2000.sysu.edu.cn/";
        }
        else if (temp.equals("统计之都")){
            WEBSITE_CHOSEN="http://cos.name/feed/";
        }
        else if (temp.equals("科学松鼠会 » 计算机科学")){
            WEBSITE_CHOSEN="http://songshuhui.net/archives/category/major/cs/feed";
        }
        else {
            WEBSITE_CHOSEN="all";
        }
        mWebsiteDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(testLAYOUT);
        displayControl();
    }
}
   
