package com.example.soccerquick2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 주용 on 2015-11-15.
 */
public class MusicService extends Service
{

    private static final String TAG ="MusicService";
    public static MediaPlayer player;
    public static Uri selectedUri;

    public void onCreate(){
        if(player == null) {
            player = new MediaPlayer();
            player.setLooping(false);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId){ //배경음악 재생

        Toast.makeText(this, "Music Service가 시작되었습니다.",
                Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStrat()");
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent){
        return null;
    }

    public void onDestroy(){ //배경음악 종료
        Toast.makeText(this, "Music Service가 중지되었습니다.",
                Toast.LENGTH_LONG).show();
        Log.d(TAG,"onDestroy()");
        player.stop();
    }
}
