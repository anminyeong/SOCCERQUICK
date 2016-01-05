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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.Fragment_Club.club_tab;
import com.example.soccerquick2.R;
import com.example.soccerquick2.Setting;
import com.example.soccerquick2.ground.Ground;
import com.example.soccerquick2.Match.match_list;
import com.example.soccerquick2.login;
import com.example.soccerquick2.user_info;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class board_modify extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Intent intent;
    SimpleDateFormat curDate = new SimpleDateFormat("yyyy/MM/dd"); //현재시간
    String cur_date = curDate.format(new Date(System.currentTimeMillis()));

    private String[] navItems = {"매치현황", "구장현황", "MyClub",
            "게시판", "회원정보 수정","로그아웃"};
    private ListView lvNavList;
    private String member_id, board_user_id, board, title, date, content;
    private EditText board_title, board_content;
    private Button del, mod;
    int header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_modify);
        intent = getIntent();
        board = intent.getExtras().getString("board_id");
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        member_id = pref.getString("id", "");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        BackgroundTask1 task1 = new BackgroundTask1();
        task1.execute(null, null, null);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                header = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btn = (Button)findViewById(R.id.writeBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //수정된 내용 서버에 저장

                    protected void onPreExecute() {
                        title = board_title.getText().toString();
                        content = board_content.getText().toString();
                    }

                    @Override
                    protected Integer doInBackground(Integer... arg0) {
                        // TODO Auto-generated method stub
                        Log.e("test", "@");
                        HttpURLConnection urlConn = null;
                        OutputStream outStream = null;

                        BufferedWriter writer = null;
                        Log.e("test", "@");

                        try {
                            //jsonText/
                            JSONObject jsonBody = new JSONObject();
                            jsonBody.put("title", title);
                            jsonBody.put("content", content);
                            jsonBody.put("header", header);
                            jsonBody.put("date", cur_date);

                            Log.i("jsonBody", jsonBody.toString());
                            Log.e("test", "@");
                            urlConn = getHttpURLConnection("http://52.193.2.122:3001/board/update/" + board, "POST", getApplicationContext());

                            outStream = urlConn.getOutputStream();
                            Log.e("test", "@");
                            writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
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
                            if (urlConn != null) {

                                urlConn.disconnect();
                            }
                        }
                        return null;
                    }

                    protected void onPostExecute(Integer a) {
                        Toast.makeText(getApplicationContext(), "글쓰기성공", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), board_info.class);
                        startActivity(intent);
                    }
                }
                new BackgroundTask().execute();
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


        btn = (Button)findViewById(R.id.cancelBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), board_main.class);
                startActivity(intent);
            }
        });
    }


    //get 부분
    class BackgroundTask1 extends AsyncTask<Integer, Integer, Integer> { //서버로부터 게시판 내용 받아와 화면에 출력
        JSONObject list;

        @Override
        protected Integer doInBackground(Integer... params) {
            Log.e("test", "@");
            HttpURLConnection urlConn = null;
            OutputStream outStream = null;
            BufferedReader jsonStreamData = null;
            BufferedWriter writer = null;
            Log.e("test", "@");

            try {

                Log.e("test", "@");
                //첫번째 부분
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/board/detail/"+board, "GET", getApplicationContext());
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

                board_user_id = list.getString("member_id");
                title = list.getString("title");
                content = list.getString("content");
                date = list.getString("date");

                Log.i("get", "여기까지 받아왔을까");

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
            //팀 스피너
            board_title = (EditText)findViewById(R.id.editText2);
            board_title.setText(title);
            board_content = (EditText)findViewById(R.id.editText);
            board_content.setText(content);
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


    //두번째 부분
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