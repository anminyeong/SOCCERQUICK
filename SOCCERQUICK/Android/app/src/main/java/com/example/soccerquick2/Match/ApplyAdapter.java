package com.example.soccerquick2.Match;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.CustomDialog;
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

public class ApplyAdapter extends BaseAdapter {
    Context context;
    private static LayoutInflater inflater = null;
    List<String> result_id = new ArrayList<String>();
    List<String> result_kakao = new ArrayList<String>();
    List<String> result_team = new ArrayList<String>();
    List<Integer> result_logo = new ArrayList<Integer>();
    String match_id;
    String a,b;
    String user_info, team_info, writer_id;
    int match_accept, board_id;
    public ApplyAdapter(Context context, List<String> id, List<String> kakao, List<Integer> logo, List<String> team, String match, String club, int accept, String writer, int board) {
        // TODO Auto-generated constructor stub
        result_id = id;
        result_kakao = kakao;
        this.context=context;
        result_logo = logo;
        result_team = team;
        match_id = match;
        team_info = club;
        match_accept = accept;
        writer_id = writer;
        board_id = board;
        inflater = (LayoutInflater) context.
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

    public class Holder {
        TextView tv;
        TextView tv2;
        TextView tv3;
        ImageView img;
    }

    public CustomDialog mCustomDialog;


    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(context, "매치 신청 취소", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            BackgroundTask task = new BackgroundTask();
            task.execute(null, null, null);
            Intent intent = new Intent(context,Match_info.class);
            intent.putExtra("match_id",Integer.toString(board_id));
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
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("team", team_info);   //device 고유 토큰값 (푸시를 위해)

                Log.i("jsonBody", jsonBody.toString());
                Log.e("test", "@");
                String query = Integer.toString(board_id);
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/match_complete/"+a+"/"+user_info+"/"+query, "POST", context);
                //int response = urlConn.getResponseCode()
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { //매치 신청자 목록 화면에 출력
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.apply_list, null);
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        user_info = pref.getString("id", "");
        holder.tv = (TextView) rowView.findViewById(R.id.user_id);
        holder.tv2 = (TextView) rowView.findViewById(R.id.user_kakao);
        holder.tv3 = (TextView) rowView.findViewById(R.id.user_team);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView);
        holder.tv.setText(result_id.get(position));
        holder.tv2.setText(result_kakao.get(position));
        holder.tv3.setText(result_team.get(position));
        String image = "images"+ result_logo.get(position);
        String packName = context.getPackageName();
        int resID = context.getResources().getIdentifier(image, "drawable", packName);
        Log.e("club_list", image + "::" + resID);
        holder.img.setImageResource(resID);

        final Button btn = (Button) rowView.findViewById(R.id.matchBtn);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.matchBtn:
                        a = result_id.get(position);
                        mCustomDialog = new CustomDialog(context, "매치수락(바로 매치성사)", a + " 님의 매치신청을 수락 하시겠습니까?", rightListener, leftListener); // 오른쪽 버튼 이벤트leftListener
                        mCustomDialog.show();
                        break;
                }
            }
        });

        if(writer_id.equals(user_info)) {
            btn.setVisibility(View.VISIBLE);
        }

        if(match_accept==1) {
            btn.setClickable(false);
            btn.setText("매치완료");
        }

        return rowView;
    }
}
