package com.example.soccerquick2.MyClub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.Board.board_info;
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
import java.util.List;

import static com.example.soccerquick2.Fragment_Club.Fragment_Club_List.getHttpURLConnection;

public class club_info_list_custom extends BaseAdapter{
    List<String> title;
    List<String> sub;
    List<Integer> imageId;
    Context context;

    String image;
    String packName;
    int id;
    List<Integer> club_id;
    private static LayoutInflater inflater=null;
    public club_info_list_custom(Context context, List<String> prgmNameList, List<Integer> prgmImages, List<String> subtitle, List<Integer> id) {
        // TODO Auto-generated constructor stub
        imageId = prgmImages;
        title=prgmNameList;
        sub = subtitle;
        club_id = id;
        this.context=context;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return title.size();
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
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { //클럽 상세정보 화면에 출력
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.club_info_list_custom, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.tv2=(TextView) rowView.findViewById(R.id.textView2);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);

        holder.tv.setText(title.get(position));
        holder.tv2.setText(sub.get(position));
        // holder.img.setImageResource(imageId.get(position));

        image = "images"+ imageId.get(position);
        packName = context.getPackageName();
        int resID = context.getResources().getIdentifier(image, "drawable", packName);


        Log.e("club_list", image + "::" + resID);
        holder.img.setImageResource(resID);

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context, club_info.class);
                id = club_id.get(position);
                intent.putExtra("club_id", Integer.toString(id));
                Log.i("what is id", Integer.toString(id));
                context.startActivity(intent);
            }
        });
        return rowView;
    }
}