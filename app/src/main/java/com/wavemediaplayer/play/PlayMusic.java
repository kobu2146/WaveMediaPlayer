package com.wavemediaplayer.play;

import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wavemediaplayer.mservices.NotificationService;
import com.wavemediaplayer.settings.InitilationMediaPlayer;

import java.io.File;

/**
 *
 * Gonderilen dosya linkine gore muzik calma islemi
 * */
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
    public InitilationMediaPlayer initilationMediaPlayer;

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


    /** Dosya var mı yok mu belirtilecek varsa calmaya baslar */
    public void playMusic(String link){

        File file =  new File(link);
        try {
            if(isMyServiceRunning(NotificationService.class)){
                mediaPlayer=NotificationService.mediaPlayer;
            }



                if (file.exists()){
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
                        mediaPlayer.setDataSource(context,Uri.parse(link));
                        mediaPlayer.prepareAsync();
//                    mediaPlayer=MediaPlayer.create(context,Uri.parse(link));

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
                    if(isMyServiceRunning(NotificationService.class)){
                        NotificationService.mediaPlayer=mediaPlayer;
                    }
                }
                else {
                    Log.e("Dosya","Belirtilen dosya yok");
                    Toast.makeText(context,"Hata",Toast.LENGTH_LONG).show();
                }

        }
        catch (Exception ex){
            Log.e("HATA",ex.getMessage());
        }



        if(mediaPlayer!=null && initilationMediaPlayer==null){
            initilationMediaPlayer=new InitilationMediaPlayer(context).init(mediaPlayer);
        }



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;
            }
        }
        return false;
    }



    private void stopPlaying() {
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
