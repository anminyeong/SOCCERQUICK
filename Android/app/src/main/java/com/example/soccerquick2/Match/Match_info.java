package com.example.soccerquick2.Match;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.Board.board_main;
import com.example.soccerquick2.Board.board_modify;
import com.example.soccerquick2.CustomDialog;
import com.example.soccerquick2.Fragment_Club.club_tab;
import com.example.soccerquick2.R;
import com.example.soccerquick2.Setting;
import com.example.soccerquick2.ground.Ground;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YSH on 2015-11-19.
 */
public class Match_info extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Intent intent;

    ListView lv;

    List<String> kakao = new ArrayList<String>();
    List<String> user_id_array = new ArrayList<String>();
    List<String> team = new ArrayList<String>();
    List<Integer> team_logo = new ArrayList<Integer>();

    Context context;
    private String[] navItems = {"매치현황", "구장현황", "MyClub",
            "게시판", "회원정보 수정","로그아웃"};
    private ListView lvNavList;
    String member_id, title, date, match_date,stadium, person, club, local, content, match_id;
    int start_time, end_time,logo, accept,board_id;
    TextView match_title, cur_date, date_match, match_stadium, match_person, user_id, match_club, match_local, match_start, match_end, match_content;
    ImageView match_logo;

    public CustomDialog mCustomDialog;

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(context, "매치 신청 취소", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
        }
    };

    String login_id;

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(context, member_id+"님에게 매치 신청이 되었습니다.",
                    Toast.LENGTH_SHORT).show();
            BackgroundTask2 task2 = new BackgroundTask2();
            task2.execute(null, null, null);
            Intent intent = new Intent(context,Match_info.class);
            intent.putExtra("match_id",match_id);
            context.startActivity(intent);
            finish();
        }
    };

    String team_info;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_info);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        login_id = pref.getString("id", "");
        team_info = pref.getString("team_info", "");


        Button btn = (Button)findViewById(R.id.matchBtn); //매치신청 버튼
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.matchBtn:
                        mCustomDialog = new CustomDialog(context, "퀵 매치(바로 매치신청)", member_id + " 님께 매치신청을 하시겠습니까?", rightListener, leftListener); // 오른쪽 버튼 이벤트leftListener
                        mCustomDialog.show();
                        break;
                }

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
                        //로그아웃
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


        intent = getIntent();
        match_id = intent.getExtras().getString("match_id");


        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);

        context = this;
    }

    class BackgroundTask3 extends AsyncTask<Integer, Integer, Integer> {
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

                Log.i("jsonBody", jsonBody.toString());
                Log.e("test", "@");
                //url 수정 해야 한다.
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/matchboard/remove/"+match_id, "POST", getApplicationContext());
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
            Intent intent = new Intent(getApplicationContext(), match_list.class);
            startActivity(intent);
        }
    }

    //post
    class BackgroundTask2 extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            // TODO Auto-generated method stub
            Log.e("test", "@");
            HttpURLConnection urlConn = null;
            OutputStream outStream = null;

            BufferedWriter writer = null;
            Log.e("test", "@");
            int query = Integer.parseInt(match_id);
            try {
                //jsonText/
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("team", team_info);

                Log.i("jsonBody", jsonBody.toString());
                Log.e("test", "@");
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/match_apply/" + member_id + "/" + login_id + "/" + query, "POST", context);
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
            mCustomDialog.dismiss();
        }
    }



    public static HttpURLConnection getHttpURLConnection(String targetURL, String reqMethod,Context context) {
        HttpURLConnection httpConnetion = null;
        try {
            URL url = new URL(targetURL);
            httpConnetion = (HttpURLConnection) url.openConnection();

            if(reqMethod.equals("POST")){
                httpConnetion.setRequestMethod(reqMethod);
                httpConnetion.setDoOutput(true);
                Log.i("Post", "post");
            }if(reqMethod.equals("GET")){
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
            /*new AlertDialog.Builder(context).setTitle("서버 연결 오류").setMessage("서버가 불안정 하여 잠시 후에 접속하시기 바랍니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    }).show();*/
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return httpConnetion;
    }

    //get
    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //매치방 정보 화면에 출력
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
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/match/detail/"+match_id, "GET", getApplicationContext());
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
                JSONObject task = list;
                member_id = task.getString("writer");
                title = task.getString("title");
                date = task.getString("date");
                match_date = task.getString("match_date");
                start_time = task.getInt("start_time");
                end_time = task.getInt("end_time");
                person = task.getString("person");
                club = task.getString("club");
                local = task.getString("local");
                stadium = task.getString("stadium");
                content = task.getString("content");
                logo = task.getInt("img");
                accept = task.getInt("accept");
                board_id = task.getInt("board_id");
                JSONArray id_array = new JSONArray(list.getString("member_id"));
                JSONArray team_array = new JSONArray(list.getString("member_team"));
                JSONArray kakao_array = new JSONArray(list.getString("kakao"));
                JSONArray team_logo_array = new JSONArray(list.getString("member_logo"));
                for(int i =0; i<id_array.length();i++){
                    kakao.add(kakao_array.getString(i));
                    user_id_array.add(id_array.getString(i));
                    team.add(team_array.getString(i));
                    team_logo.add(team_logo_array.getInt(i));
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
            //match_title, cur_date, date_match, match_stadium, match_person, user_id, match_club, match_local, match_start, match_end, match_content;
            match_title = (TextView)findViewById(R.id.title);
            match_title.setText(title);
            cur_date = (TextView)findViewById(R.id.cur_date);
            cur_date.setText(date);
            date_match = (TextView)findViewById(R.id.date);
            date_match.setText(match_date);
            match_stadium = (TextView)findViewById(R.id.stadium);
            match_stadium.setText(stadium);
            match_person = (TextView)findViewById(R.id.person);
            match_person.setText(person);
            user_id = (TextView)findViewById(R.id.member_id);
            user_id.setText(member_id);
            match_club = (TextView)findViewById(R.id.club);
            match_club.setText(club);
            match_local = (TextView)findViewById(R.id.local);
            match_local.setText(local);
            match_start = (TextView)findViewById(R.id.start);
            match_start.setText(Integer.toString(start_time));
            match_end = (TextView)findViewById(R.id.end);
            match_end.setText(Integer.toString(end_time));
            match_content = (TextView)findViewById(R.id.content1);
            match_content.setText(content);
            match_logo = (ImageView)findViewById(R.id.image);
            String image = "images"+ logo;
            String packName = context.getPackageName();
            int resID = context.getResources().getIdentifier(image, "drawable", packName);
            Log.e("club_list", image + "::" + resID);
            match_logo.setImageResource(resID);

            lv = (ListView) findViewById(R.id.listView);
            lv.setAdapter(new ApplyAdapter(context, user_id_array ,kakao, team_logo,team,match_id,club, accept, member_id, board_id));
            Button del = (Button)findViewById(R.id.delBtn);
            Button mod = (Button)findViewById(R.id.modBtn);
            if(login_id.equals(member_id)) {
                del.setVisibility(View.VISIBLE);
                mod.setVisibility(View.VISIBLE);
            }
            mod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getApplicationContext(), match_modify.class);
                    intent.putExtra("match_id", match_id).toString();
                    Log.i("@@@@@@@@@@@@",match_id);
                    startActivity(intent);
                }
            });
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BackgroundTask3 task3 = new BackgroundTask3();
                    task3.execute(null, null, null);
                }
            });
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


}