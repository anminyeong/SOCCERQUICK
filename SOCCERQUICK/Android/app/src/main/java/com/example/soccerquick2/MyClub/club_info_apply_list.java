package com.example.soccerquick2.MyClub;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.CustomDialog;
import com.example.soccerquick2.Match.Match_info;
import com.example.soccerquick2.R;

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
import java.util.ArrayList;
import java.util.List;

public class club_info_apply_list extends BaseAdapter{
    List<String> result_nick = new ArrayList<String>();
    List<String> result_kakao = new ArrayList<String>();
    List<String> result_id = new ArrayList<String>();
    Context context;
    String master_id, user_id,a,club_id;
    private static LayoutInflater inflater=null;
    public club_info_apply_list(Context context, List<String> id, List<String>kakao, String user_id, List<String> member_id, String club) {
        // TODO Auto-generated constructor stub
        this.context=context;
        result_id = member_id;
        result_nick = id;
        result_kakao = kakao;
        master_id = user_id;
        club_id = club;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result_id.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView num;
        TextView nick;
        TextView kakao;
        Button agree;
        Button deny;

    }

    public CustomDialog mCustomDialog, mCustomDialog1;

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            mCustomDialog.dismiss();
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            BackgroundTask task = new BackgroundTask();
            task.execute(null, null, null);
            Intent intent = new Intent(context,club_info.class);
            intent.putExtra("club_id", club_id);
            context.startActivity(intent);
        }
    };

    private View.OnClickListener leftListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            mCustomDialog1.dismiss();
        }
    };

    private View.OnClickListener rightListener1 = new View.OnClickListener() {
        public void onClick(View v) {
            BackgroundTask1 task1 = new BackgroundTask1();
            task1.execute(null, null, null);
            Intent intent = new Intent(context,Match_info.class);
            intent.putExtra("club_id",club_id);
            context.startActivity(intent);
        }
    };


    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
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

            try {
                //jsonText/

                urlConn = getHttpURLConnection("http://52.193.2.122:3001/join_true/"+master_id+"/"+a+"/"+club_id,"POST", context);

                outStream = urlConn.getOutputStream();
                Log.e("test","@");
                writer = new BufferedWriter(new OutputStreamWriter(outStream,"UTF-8"));
                StringBuilder buf = new StringBuilder();

                JSONObject jsonBody = new JSONObject();
                buf.append(jsonBody.toString());
                writer.write(buf.toString());

                Log.i("OutStream", outStream.toString());
                writer.flush();
                Log.i("inputStream", urlConn.getResponseCode() + "");


                InputStream inputstream = urlConn.getInputStream();

                writer.close();
                outStream.close();

            }catch (IOException e) {
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

    class BackgroundTask1 extends AsyncTask<Integer, Integer, Integer> {
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

            try {
                //jsonText/

                urlConn = getHttpURLConnection("http://52.193.2.122:3001/join_false/"+master_id+"/"+a+"/"+club_id,"POST", context);

                outStream = urlConn.getOutputStream();
                Log.e("test","@");
                writer = new BufferedWriter(new OutputStreamWriter(outStream,"UTF-8"));
                StringBuilder buf = new StringBuilder();

                JSONObject jsonBody = new JSONObject();
                buf.append(jsonBody.toString());
                writer.write(buf.toString());

                Log.i("OutStream", outStream.toString());
                writer.flush();
                Log.i("inputStream", urlConn.getResponseCode() + "");

                InputStream inputstream = urlConn.getInputStream();

                writer.close();
                outStream.close();

            }catch (IOException e) {
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
            mCustomDialog1.dismiss();
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


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { //가입신청 리스트 화면에 출력
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.activity_club_info_list, null);
        SharedPreferences pref =  context.getSharedPreferences("pref", context.MODE_PRIVATE);
        user_id = pref.getString("id", "");
        holder.nick=(TextView) rowView.findViewById(R.id.nick);
        holder.kakao =(TextView) rowView.findViewById(R.id.kakao);

        holder.agree = (Button) rowView.findViewById(R.id.button6);
        holder.deny = (Button) rowView.findViewById(R.id.button9);

        holder.nick.setText(result_nick.get(position));
        holder.kakao.setText(result_kakao.get(position));

        if(user_id.equals(master_id)){
            holder.agree.setVisibility(View.VISIBLE);
            holder.deny.setVisibility(View.VISIBLE);
        }
        holder.agree.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                a = result_id.get(position);
                mCustomDialog = new CustomDialog(context, "가입수락", a +" 님의 가입신청을 수락 하시겠습니까?", rightListener, leftListener); // 오른쪽 버튼 이벤트leftListener
                mCustomDialog.show();
            }
        });

        holder.deny.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                a = result_id.get(position);
                mCustomDialog1 = new CustomDialog(context, "가입거절", a +" 님의 가입신청을 거절 하시겠습니까?", rightListener1, leftListener1); // 오른쪽 버튼 이벤트leftListener
                mCustomDialog1.show();
            }
        });

        return rowView;
    }

}