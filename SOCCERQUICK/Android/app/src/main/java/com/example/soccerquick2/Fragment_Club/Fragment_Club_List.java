package com.example.soccerquick2.Fragment_Club;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.soccerquick2.Board.CustomAdapter;
import com.example.soccerquick2.R;

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

public class Fragment_Club_List extends Fragment {
    ListView lv;
    Context context;
    List<Integer> prgmImages = new ArrayList<Integer>();
    List<String> prgmNameList = new ArrayList<String>();
    List<String> subtitle = new ArrayList<String>();
    List<Integer> subtitle2 = new ArrayList<Integer>();
    List<Integer> accept = new ArrayList<Integer>();
    List<Integer> clubId = new ArrayList<Integer>();
    String user_id;
    List<String> master = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_join_club, container, false);

        SharedPreferences pref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        user_id = pref.getString("id", "");

        context=getActivity();

        class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //서버로부터 창설된 클럽 목록 받아와 화면에 출력
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
                    urlConn = getHttpURLConnection("http://52.193.2.122:3001/clublist", "GET", getActivity());
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
                    JSONArray people_array = new JSONArray(list.getString("members"));
                    JSONArray id_array = new JSONArray(list.getString("club_id"));
                    JSONArray recruit_array = new JSONArray(list.getString("recruit"));


                    Log.i("bbq",img_array.toString());
                    Log.i("bbq2",title_array.toString());
                    for(int i=0; i<title_array.length(); i++) {
                        prgmImages.add(img_array.getInt(i));
                        prgmNameList.add(title_array.getString(i));
                        subtitle.add(content_array.getString(i));
                        subtitle2.add(people_array.getInt(i));
                        clubId.add(id_array.getInt(i));
                        accept.add(recruit_array.getInt(i));
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
                lv=(ListView) v.findViewById(R.id.listView2);
                lv.setAdapter(new club_list( getActivity(), prgmNameList, prgmImages, subtitle, subtitle2,accept,clubId));
            }
        }
        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);
        return v;
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
}