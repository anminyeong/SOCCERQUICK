package com.example.soccerquick2.ground;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.soccerquick2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YSH on 2015-11-19.
 */
public class GroundAdapter extends BaseAdapter {
    List<String> result_name = new ArrayList<String>();
    List<String> result_local = new ArrayList<String>();
    List<Integer> result_court = new ArrayList<Integer>();
    List<Integer> result_id = new ArrayList<Integer>();
    List<String> result_num = new ArrayList<String>();
    List<Integer> result_img = new ArrayList<Integer>();
    Context context;
    private static LayoutInflater inflater = null;

    public GroundAdapter(Context context, List<String> name, List<String> local, List<Integer> court, int[] prgmImages, List<String> num, List<Integer> id, List<Integer>img) {
        // TODO Auto-generated constructor stub
        result_name = name;
        result_local = local;
        result_court = court;
        this.context=context;
        result_img = img;
        result_id = id;
        result_num = num;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result_name.size();
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.ground_list, null);
        holder.tv = (TextView) rowView.findViewById(R.id.grd_local);
        holder.tv2 = (TextView) rowView.findViewById(R.id.grd_name);
        holder.tv3 = (TextView) rowView.findViewById(R.id.grd_court);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView);
        holder.tv.setText(result_name.get(position));
        holder.tv2.setText(result_local.get(position));
        holder.tv3.setText(result_court.get(position).toString());
        String image = "ground"+ result_img.get(position).toString();
        String packName = context.getPackageName();
        int resID = context.getResources().getIdentifier(image, "drawable", packName);
        Log.e("club_list", image + "::" + resID);
        holder.img.setImageResource(resID);
        Button btn = (Button)rowView.findViewById(R.id.grdBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context,MapsActivity.class);
                intent.putExtra("ground_id", result_id.get(position).toString());
                context.startActivity(intent);
            }
        });

        Button btn1 = (Button)rowView.findViewById(R.id.callBtn);
        btn1.setOnClickListener(new View.OnClickListener() {
            String a = result_num.get(position);
            @Override
            public void onClick(View v) {
                Uri n = Uri.parse("tel: "+a);
                context.startActivity(new Intent(Intent.ACTION_DIAL,n));
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ground_info.class);
                intent.putExtra("ground_id", result_id.get(position).toString());
                intent.putExtra("image", result_img.get(position).toString());
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}