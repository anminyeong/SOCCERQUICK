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
import android.view.MenuInflater;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class match_list extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Intent intent;
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    Context context;
    ListView lv;
    int[] prgmImages = {R.drawable.images, R.drawable.images1, R.drawable.images, R.drawable.images1, R.drawable.images, R.drawable.images1, R.drawable.images, R.drawable.images, R.drawable.images1};
    //List<Integer> logo = new ArrayList<Integer>();
    List<Integer> id = new ArrayList<Integer>();
    List<String> club_name = new ArrayList<String>();
    List<String> title = new ArrayList<String>();
    List<String> local = new ArrayList<String>();
    List<String> people = new ArrayList<String>();
    List<String> date = new ArrayList<String>();
    List<Integer> start = new ArrayList<Integer>();
    List<Integer> end = new ArrayList<Integer>();
    List<Integer> match_id = new ArrayList<Integer>();
    List<Integer> logo = new ArrayList<Integer>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat startFormat = new SimpleDateFormat("kk");
    SimpleDateFormat endFormat = new SimpleDateFormat("kk");
    Calendar dateTime = Calendar.getInstance();
    Calendar startTime = Calendar.getInstance();
    Calendar endTime = Calendar.getInstance();
    Button btn;
    Spinner spinner0;
    Spinner spinner1;
    Spinner spinner2;
    String match_date = "1", match_start = "1", match_end = "1";
    int match_local = 0, match_people = 0;
    String myteam;

    String user_info;   //로그인 key
    //네비게이션바

    BackgroundTask1 task1;
    //여기까지
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Toast.makeText(context, "날짜대로 리스트 변경", Toast.LENGTH_SHORT).show();
            btn = (Button) findViewById(R.id.dateBtn);
            btn.setText(dateFormat.format(dateTime.getTime()));
            match_date = dateFormat.format(dateTime.getTime());
            task1 = new BackgroundTask1();
            task1.execute(null, null, null);
        }
    };

    TimePickerDialog.OnTimeSetListener st = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startTime.set(Calendar.MINUTE, minute);
            Toast.makeText(context, "시작시간대로 리스트 변경", Toast.LENGTH_SHORT).show();
            btn = (Button) findViewById(R.id.startBtn);
            btn.setText(startFormat.format(startTime.getTime()) + ":00");
            match_start = startFormat.format(startTime.getTime());
            task1 = new BackgroundTask1();
            task1.execute(null, null, null);
        }
    };
    TimePickerDialog.OnTimeSetListener et = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endTime.set(Calendar.MINUTE, minute);
            Toast.makeText(context, "끝 시간대로 리스트 변경", Toast.LENGTH_SHORT).show();
            btn = (Button) findViewById(R.id.endBtn);
            btn.setText(endFormat.format(endTime.getTime()) + ":00");
            match_end = endFormat.format(endTime.getTime());
            task1 = new BackgroundTask1();
            task1.execute(null, null, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);


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


        //id
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        user_info = pref.getString("id", "");

        context = this;

        //날짜버튼
        btn = (Button) findViewById(R.id.dateBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(match_list.this, d, dateTime.get(Calendar.YEAR),
                        dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //시작시간
        btn = (Button) findViewById(R.id.startBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(match_list.this, st, startTime.get(Calendar.HOUR_OF_DAY),
                        startTime.get(Calendar.MINUTE), true).show();
                match_start = startFormat.format(startTime.getTime());
            }
        });
        //끝시간
        btn = (Button) findViewById(R.id.endBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(match_list.this, et, endTime.get(Calendar.HOUR_OF_DAY),
                        endTime.get(Calendar.MINUTE), true).show();
                match_end = endFormat.format(endTime.getTime());
            }
        });

        //인원수 스피너
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.people_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).toString().equals("전체보기")) {
                    if (match_local == 0 && match_people == 0) {
                    } else {
                        task1 = new BackgroundTask1();
                        task1.execute(null, null, null);
                    }


                    match_people = 0;
                } else {
                    match_people = position;
                    task1 = new BackgroundTask1();
                    task1.execute(null, null, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //지역 스피너
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.local_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).toString().equals("전체보기")) {
                    match_local = 0;
                    task1 = new BackgroundTask1();
                    task1.execute(null, null, null);
                } else {
                    match_local = position;
                    task1 = new BackgroundTask1();
                    task1.execute(null, null, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //글쓰기 버튼
        btn = (Button) findViewById(R.id.create);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateMatch.class);
                startActivity(intent);
            }
        });


        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);
    }

    class BackgroundTask1 extends AsyncTask<Integer, Integer, Integer> { //서버로부터 매치방 목록 받아와 화면에 출력
        JSONObject list = null;

        protected void onPreExecute() {
            id.clear();
            title.clear();
            people.clear();
            local.clear();
            date.clear();
            start.clear();
            end.clear();
            logo.clear();
            match_id.clear();

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
                String query = Integer.toString(match_local);
                String query1 = Integer.toString(match_people);
                //첫번째 부분
                //  Log.i("test",match_people);
                Log.i("what is id:", user_info);
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/matchlist/" + user_info + "/" + query1 + "/" + query + "/" + match_date + "/" + match_start + "/" + match_end, "GET", getApplicationContext());
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

                //JSONArray logo_array = new JSONArray(list.getString("logo"));
                JSONArray id_array = new JSONArray(list.getString("member_id"));
                JSONArray title_array = new JSONArray(list.getString("title")); // 매치게시글 제목
                JSONArray local_array = new JSONArray(list.getString("local")); // 지역
                JSONArray people_array = new JSONArray(list.getString("people")); //인원
                JSONArray date_array = new JSONArray(list.getString("date")); //날짜
                JSONArray start_array = new JSONArray(list.getString("start")); //시작시간
                JSONArray end_array = new JSONArray(list.getString("end")); //끝시간
                JSONArray match_array = new JSONArray(list.getString("match_id"));
                JSONArray logo_array = new JSONArray(list.getString("logo"));

                for (int i = 0; i < title_array.length(); i++) {
                    logo.add(logo_array.getInt(i));
                    match_id.add(match_array.getInt(i));
                    id.add(id_array.getInt(i));
                    title.add(title_array.getString(i));
                    local.add(local_array.getString(i));
                    people.add(people_array.getString(i));
                    date.add(date_array.getString(i));
                    start.add(start_array.getInt(i));
                    end.add(end_array.getInt(i));
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
            lv = (ListView) findViewById(R.id.listView);
            lv.setAdapter(new MatchAdapter(context, match_id, id, title, people, local, date, start, end, logo));
        }
    }

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
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/clubinfo/" + user_info, "GET", getApplicationContext());
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
                Log.i("tit99999&&&&&&", clubname_array.toString());

//                JSONArray content_array = new JSONArray(list.getString("content"));

                for (int i = 0; i < clubname_array.length(); i++) {
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
            ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, club_name);
            adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner0.setAdapter(adapter0);
            spinner0.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    myteam = parent.getItemAtPosition(position).toString();
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("team_info", myteam);
                    editor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
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
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),
                    "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            System.exit(0);
        }

//메뉴바 끝
    }
}

