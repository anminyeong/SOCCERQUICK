package com.example.soccerquick2.Board;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.soccerquick2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YSH on 2015-12-09.
 */
public class replyAdapter extends BaseAdapter {

    Context context;
    List<String> result_id = new ArrayList<String>();
    List<String> result_content = new ArrayList<String>();
    List<String> result_date = new ArrayList<String>();
    private static LayoutInflater inflater=null;
    public replyAdapter(Context context, List<String> id, List<String> content, List<String> date ) {
        // TODO Auto-generated constructor stub
        this.context=context;
        result_id = id;
        result_content = content;
        result_date = date;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.i("@@@@@@@@","이동하냐");
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
        TextView content, date;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.reply_list, null);
        holder.nick=(TextView) rowView.findViewById(R.id.re_id);
        holder.nick.setText(result_id.get(position));
        holder.content =(TextView) rowView.findViewById(R.id.content);
        holder.content.setText(result_content.get(position));
        holder.date = (TextView) rowView.findViewById(R.id.re_date);
        holder.date.setText(result_date.get(position));
        return rowView;
    }


}