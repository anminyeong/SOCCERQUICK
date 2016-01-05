package com.example.soccerquick2.Fragment_Club;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.soccerquick2.Board.board_main;
import com.example.soccerquick2.R;
import com.example.soccerquick2.Setting;
import com.example.soccerquick2.ground.Ground;
import com.example.soccerquick2.Match.match_list;
import com.example.soccerquick2.login;
import com.example.soccerquick2.user_info;


public class club_tab extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    Intent intent;


    private ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private ActionBar actionBar;
    private String[] tabs = { "My Club","Club List","Make Club"};

    private String[] navItems = {"매치현황", "구장현황", "MyClub",
            "게시판", "회원정보 수정","로그아웃"};
    private ListView lvNavList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_tab);

        //툴바 + 메뉴

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.drawer_item_1:
                        intent = new Intent(getApplicationContext(), match_list.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_2:
                        intent = new Intent(getApplicationContext(), Ground.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_3:
                        intent = new Intent(getApplicationContext(), club_tab.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_4:
                        intent = new Intent(getApplicationContext(), board_main.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_5:
                        intent = new Intent(getApplicationContext(), user_info.class);
                        startActivity(intent);
                        break;
                    case R.id.drawer_item_6:
                        intent = new Intent(getApplicationContext(), login.class);
                        startActivity(intent);
                        break;
                    default:
                        return false;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

//툴바 + 메뉴 끝

        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

//        for (String tab_name : tabs) {
//            actionBar.addTab(actionBar.newTab().setIcon(android.R.drawable.star_on).setText(tab_name).setTabListener(this));
//        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //메뉴바
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);

                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(),Setting.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
//메뉴바 끝



}