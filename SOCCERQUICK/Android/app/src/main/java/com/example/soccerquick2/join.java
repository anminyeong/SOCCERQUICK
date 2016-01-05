package com.example.soccerquick2;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

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
import android.view.View.OnClickListener;
import android.widget.Toast;


public class join extends Activity implements OnClickListener{

    RadioGroup type;
    EditText email, password, name, phone, kakao, pwdconfirm;
    int checked;
    int idx;

    String email_str, password_str, name_str, phone_str, kakao_str, type_str, pwdconfirm_str;
    Button btnSave, btnCancle;
    View radioButton;
    int responseCode;
    BackgroundTask task;
    String token;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        token = pref.getString("token", "");

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        pwdconfirm = (EditText)findViewById(R.id.editText3);
        name = (EditText)findViewById(R.id.name);
        phone = (EditText)findViewById(R.id.phone);
        kakao = (EditText)findViewById(R.id.kakao);
        type = (RadioGroup)findViewById(R.id.radioGroup);

        btnSave = (Button)findViewById(R.id.btnSave);
        btnCancle = (Button)findViewById(R.id.btnCancle);

        btnSave.setOnClickListener(this);

    }

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            email_str = email.getText().toString();
            password_str =  password.getText().toString();
            pwdconfirm_str = pwdconfirm.getText().toString();
            name_str =  name.getText().toString();
            phone_str =  phone.getText().toString();
            kakao_str =  kakao.getText().toString();
            checked = type.getCheckedRadioButtonId();
            radioButton = type.findViewById(checked);
            idx = type.indexOfChild(radioButton);
        }

        @Override
        protected Integer doInBackground(Integer... arg0) { //서버에 가입 정보 저장
            // TODO Auto-generated method stub
            Log.e("test","@");
            HttpURLConnection urlConn = null;
            OutputStream outStream = null;

            BufferedWriter writer = null;
            Log.e("test", "@");

            try {
                //jsonText/
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("member_id", email_str);
                jsonBody.put("password", password_str);
                jsonBody.put("nickname", name_str);
                jsonBody.put("phone", phone_str);
                jsonBody.put("kakao", kakao_str);
                jsonBody.put("type", idx);
                jsonBody.put("token", token);   //device 고유 토큰값 (푸시를 위해)

                Log.i("Device token", token);
                Log.i("jsonBody", jsonBody.toString());
                Log.e("test", "@");
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/join", "POST", getApplicationContext());

                outStream = urlConn.getOutputStream();
                Log.e("test","@");
                writer = new BufferedWriter(new OutputStreamWriter(outStream,"UTF-8"));
                StringBuilder buf = new StringBuilder();

                buf.append(jsonBody.toString());
                writer.write(buf.toString());

                Log.i("OutStream", outStream.toString());
                writer.flush();
                Log.i("inputStream", urlConn.getResponseCode() + "");

                if(urlConn.getResponseCode() == 200){
                    responseCode = 1;
                    Log.i("responseCode", "1");
                }else{
                    responseCode = 0;
                    Log.i("responseCode", "0");
                }
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
            if(responseCode == 1){
                Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }else{
                email.setError("중복된 회원이 존재합니다.");
            }
        }
    }

    public void onClick(View view) {
        email_str = email.getText().toString();
        password_str =  password.getText().toString();
        pwdconfirm_str = pwdconfirm.getText().toString();
        name_str =  name.getText().toString();
        phone_str =  phone.getText().toString();
        kakao_str =  kakao.getText().toString();
        checked = type.getCheckedRadioButtonId();
        radioButton = type.findViewById(checked);
        idx = type.indexOfChild(radioButton);

        Log.i("email",email_str);
        if(email_str.length() == 0){
            email.setError( "아이디를 입력해주세요." );
        }else if(password_str.length() == 0) {
            password.setError(" 비밀번호를 입력해주세요.");
        }else if(!password_str.equals(pwdconfirm_str)){
            pwdconfirm.setError(password_str + "/" + pwdconfirm_str);
        }else if(name_str.length() == 0){
            name.setError("이름을 입력해주세요.");
        }else if(phone_str.length() == 0) {
            phone.setError("전화번호를 입력해주세요.");
        }else{

            task = new BackgroundTask();
            task.execute(null, null, null);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
