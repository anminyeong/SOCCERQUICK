package com.example.soccerquick2.Match;

import android.app.Activity;
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

public class MatchAdapter extends BaseAdapter {

    List<Integer> result_logo = new ArrayList<Integer>();
    List<Integer> result_id = new ArrayList<Integer>();
    List<String> result_title = new ArrayList<String>();
    List<String> result_local = new ArrayList<String>();
    List<String> result_people = new ArrayList<String>();
    List<String> result_date = new ArrayList<String>();
    List<Integer> result_start = new ArrayList<Integer>();
    List<Integer> result_end = new ArrayList<Integer>();
    List<Integer> result_match = new ArrayList<Integer>();
    List<Integer> result = new ArrayList<Integer>();
    List<Integer> result1 = new ArrayList<Integer>();
    Context context;
    private static LayoutInflater inflater = null;



    public int a;       //글등록자
    public int b;       //등록된 글 id;
    String team_info;
    static Context mContext;
    String user_info;

    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //서버로부터 매치 정보 받아와 출력
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
                urlConn = getHttpURLConnection("http://52.193.2.122:3001/match_apply/"+a+"/"+user_info+"/"+b, "POST", mContext);
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


    public MatchAdapter(Context Activity, List<Integer> match_id, List<Integer> user_id, List<String> title, List<String> people, List<String> local, List<String> date, List<Integer> start, List<Integer> end,List<Integer> logo) {
        // TODO Auto-generated constructor stub int[
        result_match = match_id;
        result_logo = logo;
        result_id = user_id;
        result_title = title;
        result_local = local;
        result_people = people;
        result_date = date;
        result_start = start;
        result_end = end;
        context = Activity;
        //imageId = prgmImages;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SharedPreferences teamm = context.getSharedPreferences("pref", 0);
        team_info = teamm.getString("team_info", "");
        user_info = teamm.getString("id","");
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
        TextView tv, tv2, tv3, tv4, tv5, tv6, tv7;
        ImageView img;
    }

    public CustomDialog mCustomDialog;


    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(context, "매치 신청 취소", Toast.LENGTH_SHORT).show();
            mCustomDialog.dismiss();
        }
    };

    int postValue ;

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(context, a+"매치 신청이 되었습니다.",
                    Toast.LENGTH_SHORT).show();
            BackgroundTask task = new BackgroundTask();
            task.execute(null, null, null);
        }
    };

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.match_list, null);
        holder.tv = (TextView) rowView.findViewById(R.id.title);

        holder.tv3 = (TextView) rowView.findViewById(R.id.people);
        holder.tv4 = (TextView) rowView.findViewById(R.id.local);
        holder.tv5 = (TextView) rowView.findViewById(R.id.date);
        holder.tv6 = (TextView) rowView.findViewById(R.id.start);
        holder.tv7 = (TextView) rowView.findViewById(R.id.end);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.tv.setText(result_title.get(position));
//        holder.tv2.setText(result_id.get(position).toString());
        result.add(result_id.get(position));
        result1.add(result_match.get(position));
        holder.tv3.setText(result_people.get(position)+" ");
        holder.tv4.setText(result_local.get(position)+" ");
        holder.tv5.setText(result_date.get(position)+" ");
        holder.tv6.setText(result_start.get(position).toString()+" ");
        holder.tv7.setText(result_end.get(position).toString());
        Log.e("###", result_logo.toString());

//        holder.img.setImageResource(result_logo.get(position));
        Button btn = (Button)rowView.findViewById(R.id.matchBtn);

        String image = "images"+ result_logo.get(position).toString();
        String packName = context.getPackageName();
        int resID = context.getResources().getIdentifier(image, "drawable", packName);
        Log.e("club_list", image + "::" + resID);
        holder.img.setImageResource(resID);

        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                Intent intent = new Intent(context,Match_info.class);
                intent.putExtra("match_id",result_match.get(position).toString());
                context.startActivity(intent);
            }
        });


        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.matchBtn:
                        a = result.get(position);
                        b = result1.get(position);
                        mCustomDialog = new CustomDialog(context, "퀵 매치(바로 매치신청)", a +" 님께 매치신청을 하시겠습니까?", rightListener, leftListener); // 오른쪽 버튼 이벤트leftListener
                        mCustomDialog.show();
                        break;
                }
            }
        });
        return rowView;
    }
}