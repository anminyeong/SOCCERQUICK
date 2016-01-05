package com.example.soccerquick2.Fragment_Club;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.CustomDialog;
import com.example.soccerquick2.Match.Match_info;
import com.example.soccerquick2.MyClub.club_info;
import com.example.soccerquick2.R;

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

import static com.example.soccerquick2.Fragment_Club.Fragment_Club_List.getHttpURLConnection;

public class club_list extends BaseAdapter {
    String member_id;

    List<String> result;
    List<String> result2;
    List<Integer> result3;
    Context context;
    List<Integer> imageId;
    List<Integer> result_club;
    List<Integer> result_accept;
    String club_id, user_id,a;
    List<String> master_id;
    private static LayoutInflater inflater=null;
    public club_list(Context context, List<String> prgmNameList, List<Integer> prgmImages, List<String> subtitle, List<Integer> subtitle2, List<Integer> accept, List<Integer> club_id) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        result2 = subtitle;
        result3 = subtitle2;
        this.context=context;
        imageId=prgmImages;
        result_club = club_id;
        result_accept = accept;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();

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
        TextView tv;
        TextView tv2;
        TextView tv3;
        ImageView img;
        Button joinButton;
    }

    public CustomDialog mCustomDialog, mCustomDialog1;


    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(context, "가입 신청 취소", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
        }
    };

    int postValue ;

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {

            BackgroundTask task = new BackgroundTask();
            task.execute(null, null, null);
            Intent intent = new Intent(context,club_tab.class);
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

                urlConn = getHttpURLConnection("http://52.193.2.122:3001/club/apply/"+member_id+"/"+club_id,"POST", context);
                //int response = urlConn.getResponseCode()
                //String reString = urlConn.getRequestMethod();
                //urlConn.getRequestProperty("application/json");


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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.activity_club_list, null);
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        member_id = pref.getString("id", "");
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.tv2=(TextView) rowView.findViewById(R.id.textView11);
        holder.tv3=(TextView) rowView.findViewById(R.id.textView12);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.joinButton = (Button) rowView.findViewById(R.id.button8);
        holder.tv.setText(result.get(position));
        holder.tv2.setText(result2.get(position));
        holder.tv3.setText(result3.get(position).toString());
        if(Integer.parseInt(result_accept.get(position).toString())==1){
            holder.joinButton.setVisibility(View.GONE);
        }
        String image = "images"+ imageId.get(position);
        String packName = context.getPackageName();
        int resID = context.getResources().getIdentifier(image, "drawable", packName);

        Log.e("club_list", image + "::" + resID);
        holder.img.setImageResource(resID);
        holder.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, a +": 님께 클럽승인 푸시" ,Toast.LENGTH_SHORT).show();
                club_id = result_club.get(position).toString();
                switch (v.getId()) {
                    case R.id.button8:
                        mCustomDialog = new CustomDialog(context, "가입신청", "가입신청을 신청 하시겠습니까?", rightListener, leftListener); // 오른쪽 버튼 이벤트leftListener
                        mCustomDialog.show();
                        break;
                }
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                club_id = result_club.get(position).toString();
                Intent intent = new Intent(context,club_info.class);
                intent.putExtra("club_id",club_id);
                context.startActivity(intent);
            }
        });
        return rowView;
    }
}