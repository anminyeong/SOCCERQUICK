package com.example.soccerquick2.Fragment_Club;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.MainActivity;
import com.example.soccerquick2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.soccerquick2.login.getHttpURLConnection;

public class Fragment_Make_Club extends Fragment {



    private static final String TEMP_PHOTO_FILE = "temp.jpg";       // 임시 저장파일
    private static final int REQ_CODE_PICK_IMAGE = 0;

     String[] strings = {"로고를 선택하세요.","사진첩에서 직접 선택","AC Milan",
            "Manchester City", "Barcelona", "Valencia","Juventus"};

    int arr_images[] = {R.drawable.temp, R.drawable.sajin,
            R.drawable.images, R.drawable.images1,
            R.drawable.images2, R.drawable.images3, R.drawable.images4};


    Context context;
    ImageView imageView; //구단로고
    Spinner spinner3; // 로고선택
    Spinner spinner; // 홈구장 선택

    Button makeclub;

    String myclub_name, myclub_content, myclub_home;
    int myclub_img;
    String member_id;

    int resName;

    String packName;
    int resID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_make_club, container, false);


        SharedPreferences pref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        member_id = pref.getString("id", "");


        final EditText edit_Text3 = (EditText)v.findViewById(R.id.editText3);
        final EditText edit_Text4 = (EditText)v.findViewById(R.id.editText4);

        context=getActivity();
        imageView = (ImageView) v.findViewById(R.id.imageView);

        class BackgroundTask extends AsyncTask<Integer, Integer, Integer> { //작성한 클럽 정보 서버에 저장
            protected void onPreExecute() {
                myclub_name = edit_Text3.getText().toString();
                myclub_content = edit_Text4.getText().toString();
                myclub_img = resName;
            }

            @Override
            protected Integer doInBackground(Integer... arg0) {
                // TODO Auto-generated method stub
                Log.e("test","@");
                HttpURLConnection urlConn = null;
                OutputStream outStream = null;

                BufferedWriter writer = null;
                Log.e("test", "@");

                try {
                    //jsonText/
                    JSONObject jsonBody = new JSONObject();


                    jsonBody.put("logo", myclub_img);
                    jsonBody.put("club_name", myclub_name);
                    jsonBody.put("club_content", myclub_content);
                    jsonBody.put("stadium_id",myclub_home);

                    Log.i("jsonBody", jsonBody.toString());
                    Log.e("test", "@");
                    Log.e("member_id", member_id);
                    urlConn = getHttpURLConnection("http://52.193.2.122:3001/club/setup/"+member_id, "POST", getActivity());
                    //int response = urlConn.getResponseCode();
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

                Intent intent = new Intent(context, club_tab.class);
                startActivity(intent);
            }
        }

        final BackgroundTask task = new BackgroundTask();

        makeclub = (Button)v.findViewById(R.id.button7);
        makeclub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                task.execute(null, null, null);
                edit_Text3.setText("");
                edit_Text4.setText("");
                spinner3.setSelection(0);
                spinner.setSelection(0);
            }
        });

        spinner3 = (Spinner)v.findViewById(R.id.spinner3);
        spinner3.setAdapter(new MyAdapter(getActivity(), R.layout.activity_club_logo_spinner, strings));
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                if (position == 1) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
                    intent.putExtra("outputFormat",         // 포맷방식
                            Bitmap.CompressFormat.JPEG.toString());

                    startActivityForResult(intent, REQ_CODE_PICK_IMAGE);
                }
                //ImageView image=(ImageView)v.findViewById(R.id.imageView);
                imageView.setImageResource(arr_images[position]);

                resName = position;

//                resName = "image"+position;
//                packName = context.getPackageName();
//                resID = getResources().getIdentifier(resName, "drawable", packName);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner = (Spinner) v.findViewById(R.id.spinner2);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.ground_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                myclub_home = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return v;
    }


    public class MyAdapter extends ArrayAdapter<String>{

        public MyAdapter(Context context, int textViewResourceId,   String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getActivity().getLayoutInflater();
            View row=inflater.inflate(R.layout.activity_club_logo_spinner, parent, false);
            TextView label=(TextView)row.findViewById(R.id.company);
            label.setText(strings[position]);

            ImageView image=(ImageView)row.findViewById(R.id.image);
            image.setImageResource(arr_images[position]);
            return row;
        }
    }




    /** 임시 저장 파일의 경로를 반환 */
    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    /** 외장메모리에 임시 이미지 파일을 생성하여 그 파일의 경로를 반환  */
    private File getTempFile() {
        if (isSDCARDMOUNTED()) {
            File f = new File(Environment.getExternalStorageDirectory(), // 외장메모리 경로
                    TEMP_PHOTO_FILE);
            try {
                f.createNewFile();      // 외장메모리에 temp.jpg 파일 생성
            } catch (IOException e) {
            }

            return f;
        } else
            return null;
    }

    /** SD카드가 마운트 되어 있는지 확인 */
    private boolean isSDCARDMOUNTED() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;

        return false;
    }

    /** 다시 액티비티로 복귀하였을때 이미지를 셋팅 */
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == getActivity().RESULT_OK) {
                    if (imageData != null) {
                        String filePath = Environment.getExternalStorageDirectory()
                                + "/temp.jpg";

                        System.out.println("path" + filePath); // logCat으로 경로확인.

                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        // temp.jpg파일을 Bitmap으로 디코딩한다.

                        imageView.setImageBitmap(selectedImage);
                        // temp.jpg파일을 이미지뷰에 씌운다.
                    }
                }
                break;
        }
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