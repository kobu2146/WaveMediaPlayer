package com.wavemediaplayer.play;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wavemediaplayer.R;

import java.io.IOException;

import static com.wavemediaplayer.MainActivity.mediaPlayer;

public class PlayMusic {

    private Context context;
    private SeekBar myseekbar;
    private TextView mytext1;
    private TextView mytext2;
    private ImageView myimageview;
    private static Runnable runnable;
    private static Handler handler;

    /***/



    public PlayMusic(Context context, MediaPlayer mediaPlayer, SeekBar myseekbar,TextView mytext1, TextView mytext2, ImageView myimageview,Handler handler,Runnable runnable){
        this.context = context;
        this.myseekbar = myseekbar;
        this.mytext1 = mytext1;
        this.mytext2 = mytext2;
        this.myimageview = myimageview;

    }


    public void play(String link){

        if(handler==null){
            handler=new Handler();
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


        try {
            if(mediaPlayer == null){
                mediaPlayer=new MediaPlayer();
                mediaPlayer.setDataSource(context, Uri.parse(link));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mediaPlayer.prepareAsync();
            }

            else {

                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(runnable);
                    runnable = null;

                    mediaPlayer.pause();
                    myimageview.setImageResource(R.drawable.play2);
                }

                mediaPlayer=new MediaPlayer();
                mediaPlayer.setDataSource(context, Uri.parse(link));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
            }



            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaPlayer != null){
            if(!mediaPlayer.isPlaying()){

                handler=new Handler();
                runnable=new Runnable() {
                    @Override
                    public void run()
                    {
                        myseekbar.setProgress(mediaPlayer.getCurrentPosition());
                        mytext1.setText(String.valueOf(android.text.format.DateFormat.format("mm:ss", mediaPlayer.getCurrentPosition())));
                        handler.postDelayed(runnable,1000);
                    }
                };
                runnable.run();

                mediaPlayer.start();
                myimageview.setImageResource(R.drawable.stop2);
            }else {
                handler.removeCallbacks(runnable);
                runnable = null;

                mediaPlayer.pause();
                myimageview.setImageResource(R.drawable.play2);
            }
        }


    }


}
