package com.wavemediaplayer.play;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import static android.media.audiofx.PresetReverb.PRESET_LARGEHALL;

public class PlayMusic {

    private Context context;
    public static MediaPlayer mediaPlayer;
    private SeekBar myseekbar;
    private TextView mytext1;
    private TextView mytext2;
    private ImageView myimageview;
    private  Runnable runnable;
    private  Handler handler;
    private BassBoost bassBoost;

    private static String playPrev = "";
    /***/



    public PlayMusic(Context context, SeekBar myseekbar,TextView mytext1, TextView mytext2, ImageView myimageview,Handler handler){
        this.context = context;
        this.myseekbar = myseekbar;
        this.mytext1 = mytext1;
        this.mytext2 = mytext2;
        this.myimageview = myimageview;
        this.handler = handler;
    }


    public void playMusic(String link){

        if (!playPrev.equals(link)){
            playPrev = link;
            stopPlaying();
            mediaPlayer = new MediaPlayer();
            bassBoost = new BassBoost(1, mediaPlayer.getAudioSessionId());
            bassBoost.setEnabled(true);
            BassBoost.Settings bassBoostSettingTemp =  bassBoost.getProperties();
            BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
            bassBoostSetting.strength=1000;
            bassBoost.setStrength((short)1000);
            bassBoost.setProperties(bassBoostSetting);
            mediaPlayer.setAuxEffectSendLevel(1.0f);
            mediaPlayer.attachAuxEffect(bassBoost.getId());
            mediaPlayer=MediaPlayer.create(context,Uri.parse(link));
//            try {
//                mediaPlayer.setDataSource(context, Uri.parse(link));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Log.e("link",link);
//




//
//            try {
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//            try {
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
        else {
            if (mediaPlayer != null){
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
            }
        }


        seekBarChange();
        setChangeSeconds();



    }
    public void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        }

    }

    private void seekBarChange(){
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e("qqqq","qqqqqqq");
                myseekbar.setMax(mediaPlayer.getDuration());
                myseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mediaPlayer!=null && fromUser){

                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                mytext2.setText(String.valueOf(android.text.format.DateFormat.format("mm:ss", mediaPlayer.getDuration())));


                mediaPlayer.start();
            }
        });
    }

    public void pauseMusic(){
        if (mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                handler.removeCallbacks(runnable);
                runnable = null;

                mediaPlayer.pause();
            }
        }
    }

     private void getAudioStats(){
        int duration  = mediaPlayer.getDuration()/1000; // In milliseconds
        int due = (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition())/1000;
        int pass = duration - due;

        mytext1.setText(""+due);

        mytext2.setText( ""+duration );
    }



    private void setChangeSeconds(){


            runnable=new Runnable() {
                @Override
                public void run()
                {
                    if(mediaPlayer!=null){
                        myseekbar.setProgress(mediaPlayer.getCurrentPosition());
                        mytext1.setText(String.valueOf(android.text.format.DateFormat.format("mm:ss", mediaPlayer.getCurrentPosition())));
                    }

                    handler.postDelayed(runnable,1000);
                }
            };
            runnable.run();

    }



}
