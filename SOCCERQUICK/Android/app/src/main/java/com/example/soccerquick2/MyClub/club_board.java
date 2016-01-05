package com.example.soccerquick2.MyClub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.soccerquick2.Board.CustomAdapter;
import com.example.soccerquick2.Board.board_main;
import com.example.soccerquick2.Board.board_write;
import com.example.soccerquick2.Fragment_Club.club_tab;
import com.example.soccerquick2.Match.MatchAdapter;
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

public class club_board extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Intent intent;

    ListView lv1;
    Context context;

    public List<String> title = new ArrayList<String>();
    public List<String> subtitle = new ArrayList<String>();
    List<String> date = new ArrayList<String>();
    List<String> header = new ArrayList<String>();
    List<Integer> board_id = new ArrayList<Integer>();
    List<Integer> logo = new ArrayList<Integer>();

    ArrayList prgmName;
    int board_head, board_team;
    String user_info;
    Spinner spinner1;
    List<String> club_name = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_board);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        user_info = pref.getString("id", "");

        //게시판 글머리 스피너
        Spinner spinner = (Spinner) findViewById(R.id.spinner0);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.item_array1, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString().equals("전체보기"))
                    board_head = 0;
                else
                    board_head = i;
                BackgroundTask1 task1 = new BackgroundTask1();
                task1.execute(null, null, null);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


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

//툴바 + 메뉴 끝

        context=this;
        Button club_board_write = (Button)findViewById(R.id.button12);

        club_board_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), club_board_write.class);
                startActivity(intent);
            }
        });
    }
    //get
    class BackgroundTask1 extends AsyncTask<Integer, Integer, Integer> { //서버로부터 게시판목록 받아와 화면에 출력
        JSONObject list = null;

        protected void onPreExecute() {
            title.clear();
            subtitle.clear();
            date.clear();
            header.clear();
            board_id.clear();
            logo.clear();
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

                urlConn = getHttpURLConnection("http://52.193.2.122:3001/club/board/"+board_head, "GET", getApplicationContext());
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

                JSONArray title_array = new JSONArray(list.getString("title"));
                JSONArray writer_array = new JSONArray(list.getString("writer"));
                JSONArray date_array = new JSONArray(list.getString("date"));
                JSONArray header_array = new JSONArray(list.getString("header"));
                JSONArray id_array = new JSONArray(list.getString("board_id"));
                JSONArray logo_array = new JSONArray(list.getString("logo"));

                for(int i=0; i<title_array.length(); i++){
                    title.add(title_array.getString(i));
                    subtitle.add(writer_array .getString(i));
                    header.add(header_array.getString(i));
                    date.add(date_array.getString(i));
                    board_id.add(id_array.getInt(i));
                    logo.add(logo_array.getInt(i));
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
            lv1=(ListView) findViewById(R.id.listView4);
            lv1.setAdapter(new CustomAdapter(context, title, subtitle, date, header, board_id,logo));
        }
    }

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


}