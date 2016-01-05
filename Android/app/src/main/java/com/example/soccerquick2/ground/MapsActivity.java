package com.example.soccerquick2.ground;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.soccerquick2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    Intent intent;
    String ground_id, user_id,name;
    Double row,column;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        intent = getIntent();
        ground_id = intent.getExtras().getString("ground_id");

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        user_id = pref.getString("id", "");

        BackgroundTask task = new BackgroundTask();
        task.execute(null, null, null);
    }


    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //서버로부터 위도 경도 받아와 화면에 지도 출력
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
                row = task.getDouble("mapwi");
                column = task.getDouble("mapkyung");

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



            setUpMapIfNeeded();
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


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(row, column)).title(name)).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(row, column)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

}
