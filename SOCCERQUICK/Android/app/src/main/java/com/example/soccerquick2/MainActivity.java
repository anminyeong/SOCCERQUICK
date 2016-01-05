package com.example.soccerquick2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.soccerquick2.Board.board_main;
import com.example.soccerquick2.Fragment_Club.club_tab;
import com.example.soccerquick2.Match.match_list;
import com.example.soccerquick2.ground.Ground;



public class MainActivity extends Activity {

    public static boolean onPass;

    //네비게이션바
    private String[] navItems = {"매치현황", "구장현황", "MyClub",
            "게시판", "회원정보 수정","로그아웃"};
    private ListView lvNavList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //메뉴 리스트뷰
        lvNavList = (ListView) findViewById(R.id.lv_activity_main_nav_list);
        lvNavList.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navItems));
        lvNavList.setOnItemClickListener(new DrawerItemClickListener());

        if(onPass){
            startActivity(new Intent(this,Loading.class));
        }
    }

    public void onSetting(View v){
        Intent intent = new Intent(getApplicationContext(),Setting.class);
        startActivity(intent);
    }


    public void onClub(View v){
        Intent intent = new Intent(getApplicationContext(),club_tab.class);
        startActivity(intent);
    }


    public void onBoard(View v){

        Intent intent = new Intent(getApplicationContext(),board_main.class);
        startActivity(intent);

    }

    public void onGroundInfo(View v){
        Intent intent = new Intent(getApplicationContext(), Ground.class);
        startActivity(intent);
    }

    //메뉴 클래스
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            Intent intent;
            switch (position) {
                case 0:
                    intent = new Intent(getApplicationContext(), match_list.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(getApplicationContext(), Ground.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(getApplicationContext(), club_tab.class);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(getApplicationContext(), board_main.class);
                    startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(getApplicationContext(), user_info.class);
                    startActivity(intent);
                    break;
                case 5:
                    intent = new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
