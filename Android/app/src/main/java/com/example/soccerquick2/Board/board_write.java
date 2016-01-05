package com.example.soccerquick2.Board;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.Fragment_Club.club_tab;
import com.example.soccerquick2.Match.match_list;
import com.example.soccerquick2.R;
import com.example.soccerquick2.Setting;
import com.example.soccerquick2.ground.Ground;
import com.example.soccerquick2.login;
import com.example.soccerquick2.user_info;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class board_write extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Intent intent;

    private String[] navItems = {"매치현황", "구장현황", "MyClub",
            "게시판", "회원정보 수정","로그아웃"};
    private ListView lvNavList;
    private String  board_title, board_content; //말머리 변수
    private TextView title, content;
    int board_head;
    SimpleDateFormat curDate = new SimpleDateFormat("yyyy/MM/dd"); //현재시간
    String cur_date = curDate.format(new Date(System.currentTimeMillis()));

    String member_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);




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



        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        member_id = pref.getString("id", "");

        // 게시판 글머리 스피너
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        title = (TextView)findViewById(R.id.editText2); //제목

        content = (TextView)findViewById(R.id.editText); //내용

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                board_head = i+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btn = (Button)findViewById(R.id.writeBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test", "@");
                //post 부분
                class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //작성한 게시판 내용 서버에 저장
                    protected void onPreExecute() {
                        board_title = title.getText().toString();
                        board_content = content.getText().toString();
                    }

                    @Override
                    protected Integer doInBackground(Integer... arg0) {
                        // TODO Auto-generated method stub
                        Log.e("test","@");
                        HttpURLConnection urlConn = null;
                        OutputStream outStream = null;

                        BufferedWriter writer = null;
                        Log.e("test", "@");

                        try {
                            //jsonText/
                            JSONObject jsonBody = new JSONObject();
                            jsonBody.put("title", board_title);
                            jsonBody.put("content", board_content);
                            jsonBody.put("header", board_head);
                            jsonBody.put("date", cur_date);
                            jsonBody.put("type",1);
                            jsonBody.put("member_id", member_id);

                            urlConn = getHttpURLConnection("http://52.193.2.122:3001/board/upload/"+member_id, "POST", getApplicationContext());

                            outStream = urlConn.getOutputStream();
                            Log.e("test","@");
                            writer = new BufferedWriter(new OutputStreamWriter(outStream,"UTF-8"));
                            StringBuilder buf = new StringBuilder();
                            buf.append(jsonBody.toString());
                            writer.write(buf.toString());

                            Log.i("OutStream", outStream.toString());
                            writer.flush();
                            Log.i("inputStream", urlConn.getResponseCode() + "");

                            InputStream inputstream = urlConn.getInputStream();

                            writer.close();
                            outStream.close();

                        } catch (JSONException joe) {
                            Log.e("Group_Page_PostResult", "IOException");
                            joe.getStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } finally {
                            if(urlConn != null){

                                urlConn.disconnect();
                            }
                        }
                        return null;
                    }
                    protected void onPostExecute(Integer a) {
                        Toast.makeText(getApplicationContext(), "글쓰기성공", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), board_main.class);
                        startActivity(intent);
                        finish();
                    }
                }new BackgroundTask().execute();
            }
        });

        btn = (Button)findViewById(R.id.cancelBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), board_main.class);
                startActivity(intent);
            }
        });


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
    //2번째
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