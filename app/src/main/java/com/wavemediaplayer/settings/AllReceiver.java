package com.wavemediaplayer.settings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.wavemediaplayer.mservices.NotificationService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AllReceiver{
    private IntentFilter actionheadsetplug,heatsetFilter;
    private JackListener jackListener;
    private static NotificationService notificationService;
    private HeatsetButton heatsetButton;
    private AudioManager mAudioManager;
    private ComponentName mRemoteControlResponder;
    private static Method mRegisterMediaButtonEventReceiver;
    private static Method mUnregisterMediaButtonEventReceiver;

    public AllReceiver(NotificationService notificationService){
        this.notificationService=notificationService;
        jackListener=new JackListener();
        actionheadsetplug = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

        mAudioManager = (AudioManager)notificationService.getSystemService(Context.AUDIO_SERVICE);
        mRemoteControlResponder = new ComponentName(notificationService.getPackageName(),
                HeatsetButton.class.getName());


    }



    public void registerReceiver(){
        notificationService.registerReceiver(jackListener,actionheadsetplug);
        mAudioManager.registerMediaButtonEventReceiver(
                mRemoteControlResponder);
    }

    public void unRegisterReceiver(){
        notificationService.unregisterReceiver(jackListener);
//        mAudioManager.unregisterMediaButtonEventReceiver(
//                mRemoteControlResponder);
    }




    public static class HeatsetButton extends BroadcastReceiver{
        final long CLICK_DELAY = 500;
        static  long lastClick = 0; // oldValue
        static long currentClick = 0;
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
//                Log.e(intent.getAction(),"keykeodoeod");
            }
//            Log.e("qqqqqqqqqqqqq","keykeodoeod");
            String intentAction = intent.getAction();
            if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction))
                return;
            KeyEvent event = (KeyEvent) intent
                    .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            int keycode = event.getKeyCode();
            int action = event.getAction();
//            Log.e("keycode", String.valueOf(keycode));
//            Log.e("action", String.valueOf(action));

            if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || keycode == KeyEvent.KEYCODE_HEADSETHOOK)
                if (action == KeyEvent.ACTION_DOWN){
//                    notificationService.pauseSong();
                    lastClick = currentClick ;

                    currentClick = System.currentTimeMillis();
                    Log.e(String.valueOf(lastClick),String.valueOf(currentClick));

                    if(currentClick - lastClick < CLICK_DELAY ){
                        Log.e("musa", "pause double");
                        notificationService.nextSong();
                        //This is double click

                    } else {
                        Log.e("musa", "pause single");
                        notificationService.pauseSong();
                        //This is single click

                    }

                }
            if (keycode == KeyEvent.KEYCODE_MEDIA_NEXT)
                if (action == KeyEvent.ACTION_DOWN){
                    notificationService.nextSong();
                    Log.e("musa", "next down");
                }


            if (keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                if (action == KeyEvent.ACTION_DOWN){
                    notificationService.previousSong();
                    Log.e("musa", "previous down");
                }




        }

    }



    class JackListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra("state", 0) == 0) {
                notificationService.pauseSong();

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


