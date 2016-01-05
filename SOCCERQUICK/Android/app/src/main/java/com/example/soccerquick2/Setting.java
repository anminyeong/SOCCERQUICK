package com.example.soccerquick2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

public class Setting extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setting);

        if(MusicService.player == null) {
            Field[] fields = R.raw.class.getFields();
            MusicService.selectedUri =  Uri.parse("android.resource://" + getPackageName() + "/raw/music1");
            MusicService.player = MediaPlayer.create(this, R.raw.music1);
            MusicService.player.setLooping(false);
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public class MyPreferenceFragment extends PreferenceFragment {

        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_activity);

            CheckBoxPreference loading;
            final CheckBoxPreference bgm;

            loading = (CheckBoxPreference)findPreference("loading");
            bgm = (CheckBoxPreference)findPreference("bgm");
            bgm.setChecked(true);

            loading.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) { //로딩 체크박스
                    if (preference.getKey().equals("loading")) {
                        boolean loading = preference.getSharedPreferences().getBoolean("loading", false);
                        MainActivity.onPass = loading;
                    }

                    return false;
                }
            });

            bgm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) { //bgm 체크박스

                    if(bgm.isChecked()){
                        Uri music = Uri.parse("android.resource://" + getPackageName() + "/raw/music1");
                        playSong(music);
                    }
                    else{

                        if(MusicService.player.isPlaying()){
                            System.out.println(MusicService.player);
                            MusicService.player.stop();
                            MusicService.player.seekTo(0);

                        }
                    }
                    return false;
                }
            });
        }
    }


    public void playSong(Uri songPath)
    {
        try{
            MusicService.selectedUri = songPath;
            Intent service = new Intent();
            service.putExtra("songPath",songPath);
            startService(service);
            play(null);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void play(View view)
    {
        if(MusicService.selectedUri != null) {
            MusicService.player.reset();
            MusicService.player = MediaPlayer.create(this, MusicService.selectedUri);
        }
        MusicService.player.start();

        startService(new Intent(this, MusicService.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
