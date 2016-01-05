package com.example.soccerquick2.ground;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.Board.board_main;
import com.example.soccerquick2.Fragment_Club.club_tab;
import com.example.soccerquick2.R;
import com.example.soccerquick2.Match.match_list;
import com.example.soccerquick2.Setting;
import com.example.soccerquick2.login;
import com.example.soccerquick2.user_info;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class ground_info extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;


    Intent intent;
    String user_id, name, address, local, num, time, holiday ,fee,etc="",ground_id,ground_img;
    int court, parking, shower,market;
    TextView grd_name, grd_address, grd_local, grd_num, grd_time, grd_holiday, grd_fee, grd_etc, grd_court;
    CheckBox grd_parking, grd_shower, grd_market;
    //Double row,column;

    // private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ground_info);

        intent = getIntent();
        ground_id = intent.getExtras().getString("ground_id");
        ground_img = intent.getExtras().getString("image");

        ImageView image = (ImageView)findViewById(R.id.image);

        String img =  "ground"+ground_img.toString();
        String packName = getApplicationContext().getPackageName();
        int resID = getApplicationContext().getResources().getIdentifier(img, "drawable", packName);
        image.setImageResource(resID);


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
        user_id = pref.getString("id", "");

        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);

    }

// 구장 마다 구글맵 따로 받아와야 하는데 그럼 구장마다 위도 경도 저장하던가 주소에 따라 구글맵 띄우는거 해야 함 일단 구글맵은 남겨두고 나머지 진행

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //서버로부터 구장정보 받아와 화면에 출력
        JSONObject list;
        @Override
        protected Integer doInBackground(Integer... params) {
            HttpURLConnection urlConn = null;
            OutputStream outStream = null;
            BufferedReader jsonStreamData = null;
            BufferedWriter writer = null;

            try {
                //첫번째 부분
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/ground/detail/"+ground_id, "GET", getApplicationContext());
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
//                JSONArray array = new JSONArray(list.getString("user_info"));
                JSONObject task = new JSONObject(list.getString("user_info"));
                name = task.getString("name");
                address = task.getString("address");
                local = task.getString("local");
                num = task.getString("num");
                time = task.getString("time");
                holiday = task.getString("holiday");
                fee = task.getString("fee");
                court = task.getInt("court");
                parking = task.getInt("parking");
                shower = task.getInt("shower");
                market = task.getInt("market");

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

            grd_name = (TextView) findViewById(R.id.ground_name);
            grd_name.setText(name);
            grd_address = (TextView) findViewById(R.id.ground_address);
            grd_address.setText(address);
            grd_local = (TextView) findViewById(R.id.grd_local);
            grd_local.setText(local);
            grd_num = (TextView) findViewById(R.id.ground_num);
            grd_num.setText(num);
            grd_num.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri n = Uri.parse("tel: " + num);
                    startActivity(new Intent(Intent.ACTION_DIAL, n));
                }
            });
            grd_court = (TextView) findViewById(R.id.ground_court);
            grd_court.setText(Integer.toString(court));
            grd_time = (TextView) findViewById(R.id.ground_time);
            grd_time.setText(time);
            grd_holiday = (TextView) findViewById(R.id.ground_holiday);
            grd_holiday.setText(holiday);
            grd_fee = (TextView) findViewById(R.id.ground_fee);
            grd_fee.setText(fee);
            grd_etc = (TextView) findViewById(R.id.ground_etc);
            grd_etc.setText(etc);
            grd_parking = (CheckBox) findViewById(R.id.ground_parking);
            if (parking == 1) {
                grd_parking.setChecked(true);
            } else {
                grd_parking.setChecked(false);
            }
            grd_shower = (CheckBox) findViewById(R.id.ground_shower);
            if (shower == 1) {
                grd_shower.setChecked(true);
            } else {
                grd_shower.setChecked(false);
            }
            grd_market = (CheckBox) findViewById(R.id.ground_market);
            if (market == 1) {
                grd_market.setChecked(true);
            } else {
                grd_market.setChecked(false);
            }
            Button btn = (Button)findViewById(R.id.abc);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getApplicationContext(), ground_schedule.class);
                    startActivity(intent);
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