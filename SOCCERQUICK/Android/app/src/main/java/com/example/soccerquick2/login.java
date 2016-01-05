package com.example.soccerquick2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.soccerquick2.Match.Match_info;
import com.example.soccerquick2.Match.match_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class login extends Activity {

    public static boolean onPass = true;

    EditText email, password;
    String email_str, password_str;
    int checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aaa);

        if (onPass) {
            startActivity(new Intent(this, Loading.class));
        }

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pwd);

        if (MusicService.player == null) {
            MusicService.player = MediaPlayer.create(this, R.raw.music1);
            MusicService.player.setLooping(false);
            Uri music = Uri.parse("android.resource://" + getPackageName() + "/raw/music1");
            playSong(music);
        }

        Intent intent = new Intent(this, com.example.soccerquick2.RegistrationIntentService.class);
        startService(intent);
    }

    public void playSong(Uri songPath) {
        try {
            MusicService.selectedUri = songPath;
            Intent service = new Intent();
            service.putExtra("songPath", songPath);
            startService(service);
            play(null);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void play(View view) {
        if (MusicService.selectedUri != null) {
            MusicService.player.reset();
            MusicService.player = MediaPlayer.create(this, MusicService.selectedUri);
        }
        MusicService.player.start();

        startService(new Intent(this, MusicService.class));

    }

    public void user_join(View v) { //회원가입 화면 이동
        Intent intent = new Intent(getApplicationContext(), join.class);
        startActivity(intent);

    }

    public void user_login(View v) {
        email_str = email.getText().toString();
        password_str = password.getText().toString();

        class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //서버로 값 받아와 아이디 비밀번호 비교 후 로그인
            JSONObject list = null;

            protected void onPreExecute() {
            }

            @Override
            protected Integer doInBackground(Integer... arg0) {
                // TODO Auto-generated method stub
                HttpURLConnection urlConn = null;
                OutputStream outStream = null;
                BufferedReader jsonStreamData = null;
                BufferedWriter writer = null;

                try {
                    urlConn = getHttpURLConnection("http://52.193.2.122:3001/login/" + email_str + "/" + password_str, "GET", getApplicationContext());
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
                    jsonStreamData.close();
                    list = new JSONObject(buf.toString());            //json형태로 가져와서 값을 정리
                    Log.i("requset", buf.toString());
                    checked = list.getInt("check");

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

                if (checked == 1) {
                    Toast.makeText(getApplicationContext(), email_str + "님 환영합니다.", Toast.LENGTH_LONG).show();
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("id", email_str);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), match_list.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        }
        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);
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

    public void onBackPressed() {
        finish();
    }

}
