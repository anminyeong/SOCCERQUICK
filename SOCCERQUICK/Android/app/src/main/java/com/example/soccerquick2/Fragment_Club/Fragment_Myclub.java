package com.example.soccerquick2.Fragment_Club;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.soccerquick2.Board.CustomAdapter;
import com.example.soccerquick2.MyClub.club_info_list_custom;
import com.example.soccerquick2.R;
import com.example.soccerquick2.MyClub.club_board;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import static com.example.soccerquick2.Fragment_Club.Fragment_Make_Club.getHttpURLConnection;

public class Fragment_Myclub extends Fragment {

    String member_id;
    ListView lv;
    Context context;

    List<Integer> clubImages = new ArrayList<Integer>();
    List<String> clubNameList = new ArrayList<String>();
    List<String> clubsubtitle = new ArrayList<String>();
    List<Integer> clubId = new ArrayList<Integer>();
    Button clubButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_my_club, container, false);

        SharedPreferences pref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        member_id = pref.getString("id", "");
        context=getActivity();

        class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //서버로부터 가입한 클럽 목록 받아와 화면에 출력
            JSONObject list = null;

            protected void onPreExecute() {
                clubImages.clear();
                clubsubtitle.clear();
                clubNameList.clear();
                clubId.clear();
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
                    urlConn = getHttpURLConnection("http://52.193.2.122:3001/clublist/"+member_id, "GET", getActivity());
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
                    //JSONObject title = list.getJSONObject("title");
                    //여기서 값을 처리


                    JSONArray img_array = new JSONArray(list.getString("img"));
                    JSONArray title_array = new JSONArray(list.getString("title"));
                    JSONArray content_array = new JSONArray(list.getString("content"));
                    JSONArray id_array = new JSONArray((list.getString("club_id")));

                    for(int i=0; i<title_array.length(); i++){
                        clubImages.add(img_array.getInt(i));
                        clubNameList.add(title_array.getString(i));
                        clubsubtitle.add(content_array.getString(i));
                        clubId.add(id_array.getInt(i));
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
                lv=(ListView) v.findViewById(R.id.listView3);
                lv.setAdapter(new club_info_list_custom(getActivity(),  clubNameList,clubImages, clubsubtitle, clubId));
                Log.e("test", "@");
            }
        }
        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);


        clubButton = (Button)v.findViewById(R.id.button11);

        clubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), club_board.class);
                startActivity(intent);
            }
        });


        return v;
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