package com.example.soccerquick2.Match;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.soccerquick2.Board.board_main;
import com.example.soccerquick2.Fragment_Club.club_tab;
import com.example.soccerquick2.R;
import com.example.soccerquick2.Setting;
import com.example.soccerquick2.ground.Ground;
import com.example.soccerquick2.login;
import com.example.soccerquick2.user_info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by YSH on 2015-11-13.
 */
public class CreateMatch extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Intent intent;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat startFormat = new SimpleDateFormat("kk");
    SimpleDateFormat endFormat = new SimpleDateFormat("kk");

    private String team , local, people, title, content, match_date=""; //게시글 쓸때 넘겨줄 거
    int start=0,end=0;

    SimpleDateFormat curDate = new SimpleDateFormat("yyyy/MM/dd"); //현재시간
    String cur_date = curDate.format(new Date(System.currentTimeMillis()));
    Button btn;
    Calendar dateTime = Calendar.getInstance();
    Calendar startTime = Calendar.getInstance();
    Calendar endTime = Calendar.getInstance();
    //id 받아오기
    String member_id, stadium;
    private String[] navItems = {"매치현황", "구장현황", "MyClub",
            "게시판", "회원정보 수정", "로그아웃"};
    private ListView lvNavList;
    Spinner spinner0, spinner3;
    List<String> club_name = new ArrayList<String>();
    List<String> ground_name = new ArrayList<String>();
    Context context = this;
    TextView tv_title ;
    TextView tv_content;

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };

    TimePickerDialog.OnTimeSetListener st = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startTime.set(Calendar.MINUTE, minute);
            updateStart();
        }
    };
    TimePickerDialog.OnTimeSetListener et = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endTime.set(Calendar.MINUTE, minute);
            updateEnd();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);



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
        Button btn = (Button) findViewById(R.id.dateBtn);
        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);
        BackgroundTask2 task2 = new BackgroundTask2();
        task2.execute(null,null,null);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //날짜 버튼
                new DatePickerDialog(CreateMatch.this, d, dateTime.get(Calendar.YEAR),
                        dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn = (Button) findViewById(R.id.startBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //시작시간 버튼
                new TimePickerDialog(CreateMatch.this, st, startTime.get(Calendar.HOUR_OF_DAY),
                        startTime.get(Calendar.MINUTE), true).show();
            }
        });

        btn = (Button) findViewById(R.id.endBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //끝시간 버튼
                new TimePickerDialog(CreateMatch.this, et, endTime.get(Calendar.HOUR_OF_DAY),
                        endTime.get(Calendar.MINUTE), true).show();
            }
        });

        //인원선택 스피너
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.match_people, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                people = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //지역스피너
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.ground_local, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                local = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //취소버튼
        btn = (Button) findViewById(R.id.cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), match_list.class);
                startActivity(intent);
            }
        });

        btn = (Button)findViewById(R.id.createBtn); //매치생성버튼
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test", "@");
                tv_title = (TextView) findViewById(R.id.match_title); //제목 받아올 text
                tv_content = (TextView) findViewById(R.id.match_content); //내용 받아올 text
                title = tv_title.getText().toString();
                content = tv_content.getText().toString();
                if (title.equals("") || content.equals("") || match_date.equals("") || start == 0 || end == 0) {
                    Toast.makeText(getApplicationContext(), "입력값을 확인해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    BackgroundTask1 task1 = new BackgroundTask1();
                    task1.execute(null, null, null);
                }
            }
        });



    }

    class BackgroundTask1 extends AsyncTask<Integer, Integer, Integer> { //매치방 정보를 서버에 저장
        protected void onPreExecute() {
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
                jsonBody.put("title", title);
                jsonBody.put("content", content);
                jsonBody.put("people", people);
                jsonBody.put("local", local);
                jsonBody.put("club_name", team);
                jsonBody.put("match_date", match_date);
                jsonBody.put("date", cur_date);
                jsonBody.put("start", start);
                jsonBody.put("end", end);
                jsonBody.put("stadium",stadium);

                Log.i("jsonBody", jsonBody.toString());
                Log.e("test", "@");
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/match/upload/"+member_id, "POST", getApplicationContext());
                //int response = urlConn.getResponseCode();
                //String reString = urlConn.getRequestMethod();
                //urlConn.getRequestProperty("application/json");
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
            Intent intent = new Intent(getApplicationContext(), match_list.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateDate() {
        btn = (Button) findViewById(R.id.dateBtn);
        btn.setText(dateFormat.format(dateTime.getTime()));
        match_date = dateFormat.format(dateTime.getTime());
    }

    private void updateStart() {
        btn = (Button) findViewById(R.id.startBtn);
        btn.setText(startFormat.format(startTime.getTime()));
        start = Integer.parseInt(startFormat.format(startTime.getTime()));
    }

    private void updateEnd() {
        btn = (Button) findViewById(R.id.endBtn);
        btn.setText(endFormat.format(endTime.getTime()));
        end = Integer.parseInt(endFormat.format(endTime.getTime()));
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

    //팀 스피너 전용 background
    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        JSONObject list = null;

        protected void onPreExecute() {
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
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/clubinfo/"+member_id, "GET", getApplicationContext());
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
                //JSONObject title = list.getJSONObject("club_name");
                //여기서 값을 처리

                //Log.i("title&&&&&&&&&&&&",title.toString());
                JSONArray clubname_array = new JSONArray(list.getString("club_name"));
                Log.i("tit99999&&&&&&",clubname_array.toString());

//                JSONArray content_array = new JSONArray(list.getString("content"));

                for(int i=0; i<clubname_array.length(); i++){
                    club_name.add(clubname_array.getString(i));
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
            //팀 스피너
            spinner0 = (Spinner) findViewById(R.id.spinner0);
            ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,  club_name);
            adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner0.setAdapter(adapter0);
            spinner0.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    team = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    //구장 스피너 전용 background
    class BackgroundTask2 extends AsyncTask<Integer, Integer, Integer> {
        JSONObject list = null;

        protected void onPreExecute() {
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
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/groundinfo/", "GET", getApplicationContext());
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
                //JSONObject title = list.getJSONObject("club_name");
                //여기서 값을 처리

                //Log.i("title&&&&&&&&&&&&",title.toString());
                JSONArray ground_array = new JSONArray(list.getString("stadium_name"));


//                JSONArray content_array = new JSONArray(list.getString("content"));

                for(int i=0; i<ground_array.length(); i++){
                    ground_name.add(ground_array.getString(i));
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
            //팀 스피너
            spinner3 = (Spinner) findViewById(R.id.spinner3);
            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, ground_name);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner3.setAdapter(adapter3);
            spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    stadium = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

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