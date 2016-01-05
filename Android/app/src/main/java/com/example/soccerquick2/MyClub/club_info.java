package com.example.soccerquick2.MyClub;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.Board.CustomAdapter;
import com.example.soccerquick2.Board.board_main;
import com.example.soccerquick2.Fragment_Club.club_list;
import com.example.soccerquick2.Fragment_Club.club_tab;
import com.example.soccerquick2.R;
import com.example.soccerquick2.Setting;
import com.example.soccerquick2.ground.Ground;
import com.example.soccerquick2.Match.match_list;
import com.example.soccerquick2.login;
import com.example.soccerquick2.user_info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import static com.example.soccerquick2.MyClub.club_info_list_custom.getHttpURLConnection;

public class club_info extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Intent intent;
    ListView lv1;
    ListView lv2;
    Context context;
    int my_club_img;
    String club_name, club_stadium;
    int club_image, club_people;
    ListView lv;
    String club_id, user_id, club_master, club_content;
    private String[] navItems = {"매치현황", "구장현황", "MyClub",
            "게시판", "회원정보 수정","로그아웃"};
    private ListView lvNavList;
    List<String> member_nick = new ArrayList<String>();
    List<String> member_kakao = new ArrayList<String>();
    List<String> apply_nick = new ArrayList<String>();
    List<String> apply_id = new ArrayList<String>();
    List<String> apply_kakao = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_info);
        intent = getIntent();
        club_id = intent.getExtras().getString("club_id");
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        user_id = pref.getString("id", "");
        context = this;

        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);


//툴바 + 메뉴

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.drawer_item_1:
                        intent = new Intent(getApplicationContext(), match_list.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_2:
                        intent = new Intent(getApplicationContext(), Ground.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_3:
                        intent = new Intent(getApplicationContext(), club_tab.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_4:
                        intent = new Intent(getApplicationContext(), board_main.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_5:
                        intent = new Intent(getApplicationContext(), user_info.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_6:
                        intent = new Intent(getApplicationContext(), login.class);
                        startActivity(intent);
                        break;
                    default:
                        return false;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //서버로부터 클럽 상세정보 불러와 화면에 출력
        JSONObject list = null;

        protected void onPreExecute() {
            member_nick.clear();
           member_kakao.clear();
            apply_nick.clear();
            apply_id.clear();
            apply_kakao.clear();
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            // TODO Auto-generated method stub
            Log.e("test", "@");
            HttpURLConnection urlConn = null;
            OutputStream outStream = null;
            BufferedReader jsonStreamData = null;
            BufferedWriter writer = null;
            Log.e("test", "@");

            try {
                Log.e("test", "@");
                //첫번째 부분
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/club/detail/"+user_id+"/"+club_id, "GET", getApplicationContext());
                Log.e("test", "과연");
                int response = urlConn.getResponseCode();   //받을 권리를 받음.
                if (response >= 200 && response < 300)      //서버에서 응답
                    jsonStreamData = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));   //json  내용을 받아온다.
                else {
                    Log.e("MynoteCall", "jsonSteamData Not Found");
                    return null;
                }
                String line = "";
                StringBuilder buf = new StringBuilder();
                while ((line = jsonStreamData.readLine()) != null) {
                    Log.i("lineResult", line.toString());
                    buf.append(line);
                }
                list = new JSONObject(buf.toString());            //json형태로 가져와서 값을 정리

//                JSONArray array = new JSONArray(list.getString("club_info"));
                JSONObject task = new JSONObject(list.getString("club_info"));
                club_name = task.getString("title");
                club_image = task.getInt("img");
                club_people = task.getInt("members");
                club_stadium = task.getString("stadium");
                club_master = task.getString("master");
                club_content = task.getString("content");
                JSONArray member_nick_array = new JSONArray(task.getString("member_nick"));
                JSONArray member_kakao_array = new JSONArray(task.getString("member_kakao"));
                JSONArray apply_nick_array = new JSONArray(task.getString("apply_nick"));
                JSONArray apply_kakao_array = new JSONArray(task.getString("apply_kakao"));
                JSONArray apply_id_array = new JSONArray(task.getString("apply_id"));
                for(int i =0; i<member_nick_array.length();i++){
                    member_kakao.add(member_kakao_array.getString(i));
                    member_nick.add(member_nick_array.getString(i));
                }

                for(int i =0; i<apply_nick_array.length();i++){
                    apply_nick.add(apply_nick_array.getString(i));
                    apply_kakao.add(apply_kakao_array.getString(i));
                    apply_id.add(apply_id_array.getString(i));
                }
            } catch (IOException ioe) {
                Log.e("MynoteCall", "IOException");
                ioe.getStackTrace();
            } catch (JSONException jse) {
                Log.i("MainViewPagerJsonerror", jse.toString());
                jse.getStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Integer a) {

            ImageView img = (ImageView)findViewById(R.id.imageView2);
            img.setImageResource(my_club_img);
            String image = "images"+ club_image;
            String packName = context.getPackageName();
            int resID = context.getResources().getIdentifier(image, "drawable", packName);
            img.setImageResource(resID);

            TextView name = (TextView)findViewById(R.id.textView15);
            name.setText(club_name);

            TextView content = (TextView)findViewById(R.id.textView16);
            content.setText(club_content);

            TextView stadium = (TextView)findViewById(R.id.textView22);
            stadium.setText(club_stadium);

            TextView members = (TextView)findViewById(R.id.textView21);
            members.setText(Integer.toString(club_people));

            lv1 = (ListView) findViewById(R.id.listView6);
            lv1.setAdapter(new club_info_list(context, member_nick,member_kakao));

            lv2 = (ListView) findViewById(R.id.listView5);
            lv2.setAdapter(new club_info_apply_list(context, apply_nick, apply_kakao, club_master, apply_id, club_id));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //메뉴바
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);

                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(),Setting.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
//메뉴바 끝




    public static HttpURLConnection getHttpURLConnection(String targetURL, String reqMethod, Context context) {
        HttpURLConnection httpConnetion = null;
        try {
            URL url = new URL(targetURL);
            httpConnetion = (HttpURLConnection) url.openConnection();

            if (reqMethod.equals("POST")) {
                httpConnetion.setRequestMethod(reqMethod);
                httpConnetion.setDoOutput(true);
                Log.i("Post", "post");
            }
            if (reqMethod.equals("GET")) {
                httpConnetion.setRequestMethod(reqMethod);
                Log.e("GET", "get");
            }
            httpConnetion.setDoInput(true);
            httpConnetion.setConnectTimeout(15000);
            httpConnetion.setUseCaches(false);
            httpConnetion.setReadTimeout(15000);
            httpConnetion.setRequestProperty("Content-Type", "application/json");
//            httpConnetion.setRequestProperty("Accept-Encoding",
//                    "musixmatch");
        } catch (RuntimeException e) {
            Log.e("getHttp", "getHttp 에러 발생", e);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return httpConnetion;
    }




}