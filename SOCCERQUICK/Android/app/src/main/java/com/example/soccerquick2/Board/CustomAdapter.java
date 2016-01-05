package com.example.soccerquick2.Board;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.R;

import java.util.List;

public class CustomAdapter extends BaseAdapter{
    List<String> result_title;
    List<String> result_subtitle;
    List<String> result_date;
    List<String> result_header;
    List<Integer> result_id;
    List<Integer> result_logo;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapter(Context context, List<String> title, List<String> subtitle, List<String> date, List<String> header, List<Integer> id,List<Integer> logo) {
        // TODO Auto-generated constructor stub
        result_title=title;
        result_subtitle = subtitle;
        result_date = date;
        result_header = header;
        result_id = id;
        result_logo = logo;
        this.context=context;
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
        TextView tv;
        TextView tv2;
        TextView tv3,tv4;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.activity_board_list2, null);
        holder.tv=(TextView) rowView.findViewById(R.id.title);
        holder.tv2=(TextView) rowView.findViewById(R.id.textView2);
        holder.tv3=(TextView) rowView.findViewById(R.id.textDate);
        holder.tv4=(TextView) rowView.findViewById(R.id.header);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.tv.setText(result_title.get(position).toString());
        holder.tv2.setText(result_subtitle.get(position).toString());
        holder.tv3.setText(result_date.get(position).toString());
        holder.tv4.setText(result_header.get(position).toString());
        String image = "images"+ result_logo.get(position);
        String packName = context.getPackageName();
        int resID = context.getResources().getIdentifier(image, "drawable", packName);
        Log.e("club_list", image + "::" + resID);
        holder.img.setImageResource(resID);

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("board_id", result_id.get(position).toString() );
                editor.commit();
                Intent intent = new Intent(context,board_info.class);
                context.startActivity(intent);
            }
        });
        return rowView;
    }

}