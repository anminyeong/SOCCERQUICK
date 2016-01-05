package com.example.soccerquick2.MyClub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soccerquick2.R;

import java.util.ArrayList;
import java.util.List;

public class club_info_list extends BaseAdapter{
    Context context;
    List<String> result_id = new ArrayList<String>();
    List<String> result_kakao = new ArrayList<String>();
    private static LayoutInflater inflater=null;
    public club_info_list(Context context, List<String> id, List<String>kakao ) {
        // TODO Auto-generated constructor stub
        this.context=context;

        result_id = id;
        result_kakao = kakao;
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
        TextView nick;
        TextView kakao;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { //클럽 가입자 목록 화면에 출력
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.activity_club_info_list, null);
        holder.nick=(TextView) rowView.findViewById(R.id.nick);
        holder.kakao =(TextView) rowView.findViewById(R.id.kakao);

        holder.nick.setText(result_id.get(position));
        holder.kakao.setText(result_kakao.get(position));


        return rowView;
    }

}