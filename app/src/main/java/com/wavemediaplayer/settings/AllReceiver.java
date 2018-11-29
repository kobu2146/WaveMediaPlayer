package com.wavemediaplayer.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.wavemediaplayer.mservices.NotificationService;

public class AllReceiver{
    private IntentFilter actionheadsetplug;
    private JackListener jackListener;
    private NotificationService notificationService;

    public AllReceiver(NotificationService notificationService){
        this.notificationService=notificationService;
        jackListener=new JackListener();
        actionheadsetplug = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    }


    public void registerReceiver(){
        notificationService.registerReceiver(jackListener,actionheadsetplug);
    }

    public void unRegisterReceiver(){
        notificationService.unregisterReceiver(jackListener);
    }





    class JackListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("state", 0) == 0) {
                notificationService.pauseSong();
                Log.e("receiver","receiiiver");

//                if (PlayMusic.mediaPlayer != null){
//                    if (PlayMusic.mediaPlayer.isPlaying()){
//                        PlayMusic.mediaPlayer.pause();
//                        if(mainActivity.s!=null) mainActivity.s.activityPause();
//                        mainActivity.fPlayListener.pl.iconKapat(false);
//
//                    }
//                }
            }

        }
    }

}


