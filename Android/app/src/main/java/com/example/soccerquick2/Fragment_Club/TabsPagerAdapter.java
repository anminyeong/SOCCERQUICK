package com.example.soccerquick2.Fragment_Club;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.soccerquick2.Fragment_Club.Fragment_Club_List;
import com.example.soccerquick2.Fragment_Club.Fragment_Make_Club;
import com.example.soccerquick2.Fragment_Club.Fragment_Myclub;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int index){ //fragment 탭

        switch (index){
            case 0:
                return new Fragment_Myclub();
            case 1:
                return new Fragment_Club_List();
            case 2:
                return new Fragment_Make_Club();
        }
        return null;
    }


    @Override
    public int getCount(){
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "My Club";
            case 1:
                return "Club List";
            case 2:
                return "Make Club";
        }
        return null;
    }
}
