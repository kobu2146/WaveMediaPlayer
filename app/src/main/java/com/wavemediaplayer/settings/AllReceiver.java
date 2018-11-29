package com.wavemediaplayer.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.play.PlayMusic;

public class AllReceiver{
    private MainActivity mainActivity;
    private IntentFilter actionheadsetplug;
    private JackListener jackListener;

    public AllReceiver(MainActivity mainActivity){
        this.mainActivity=mainActivity;
        jackListener=new JackListener();
        actionheadsetplug = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    }


    public void registerReceiver(){
        mainActivity.registerReceiver(jackListener,actionheadsetplug);
    }

    public void unRegisterReceiver(){
        mainActivity.unregisterReceiver(jackListener);
    }





    class JackListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("state", 0) == 0) {
                if (PlayMusic.mediaPlayer != null){
                    if (PlayMusic.mediaPlayer.isPlaying()){
                        PlayMusic.mediaPlayer.pause();
                        if(mainActivity.s!=null) mainActivity.s.activityPause();
                        mainActivity.fPlayListener.pl.iconKapat(false);

                    }
                }
            }

        }
    }

}


