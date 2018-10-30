package com.wavemediaplayer.main;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.play.PlayMusic;

/**
 * Fatihin olusturdugu listener
 *
 * Mainactivity deki tanımlamlamalar ve event islemleri yapılacak
 *
 *
 * */

public class FPlayListener {

    private  PlayMusic pl;

    private ImageButton like;
    private ImageButton notlike;
    private ImageButton dislike;
    private ImageButton notdislike;
    private ImageButton play;
    private ImageButton pause;
    private ImageButton play_main;
    private ImageButton pause_main;

    private SeekBar myseekbar;
    private TextView mytext1;
    private TextView mytext2;
    private  Runnable runnable;
    private Handler handler;



    private Context context;
    private View view;

    public FPlayListener(Context context, View view){
        this.context = context;
        this.view = view;
        init();
    }



    private void init(){
        like = (ImageButton) view.findViewById(R.id.sample_main_imageButton2);
        notlike = (ImageButton) view.findViewById(R.id.sample_main_imageButton2new);
        dislike = (ImageButton) view.findViewById(R.id.sample_main_button);
        notdislike = (ImageButton) view.findViewById(R.id.sample_main_buttontwo);
        play = (ImageButton) view.findViewById(R.id.sample_main_play_button);
        pause = (ImageButton) view.findViewById(R.id.sample_main_pause_button);
        play_main = (ImageButton) view.findViewById(R.id.sample_main_play_button_main);
        pause_main = (ImageButton) view.findViewById(R.id.sample_main_pause_button_main);

        mytext1= view.findViewById(R.id.sample_main_StartTime);
        mytext2= view.findViewById(R.id.sample_main_endTime);
        myseekbar= view.findViewById(R.id.sample_main_seekBar3);
        handler = new Handler();

        pl = new PlayMusic(context,myseekbar,mytext1,mytext2,play,handler);
    }


    public void playFromPlayList(String link){
        /** Music play */
        pl.playMusic(link);

        // play tab on screen
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        Toast.makeText(context,"Song Is now Playing",Toast.LENGTH_SHORT).show();
        if (play_main.getVisibility() == View.VISIBLE){
            play_main.setVisibility(View.GONE);
            pause_main.setVisibility(View.VISIBLE);
        }

        // main play button
        play_main.setVisibility(View.GONE);
        pause_main.setVisibility(View.VISIBLE);
        Toast.makeText(context,"Song Is now Playing",Toast.LENGTH_SHORT).show();
        if (play.getVisibility() == View.VISIBLE){
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    }

    public void playMusic(int position){
        /** Music play */
        pl.playMusic(MusicList.locationList.get(position));

        // play tab on screen
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        Toast.makeText(context,"Song Is now Playing",Toast.LENGTH_SHORT).show();
        if (play_main.getVisibility() == View.VISIBLE){
            play_main.setVisibility(View.GONE);
            pause_main.setVisibility(View.VISIBLE);
        }

        // main play button
        play_main.setVisibility(View.GONE);
        pause_main.setVisibility(View.VISIBLE);
        Toast.makeText(context,"Song Is now Playing",Toast.LENGTH_SHORT).show();
        if (play.getVisibility() == View.VISIBLE){
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    }



    public void f_ListenerEvent(final int position){

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notlike.setVisibility(View.VISIBLE);
                Toast.makeText(context,"You Like the Song",Toast.LENGTH_SHORT).show();
                if (notdislike.getVisibility() == View.VISIBLE){
                    notdislike.setVisibility(View.GONE);
                }
            }
        });

        notlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notlike.setVisibility(View.GONE);
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notdislike.setVisibility(View.VISIBLE);
                Toast.makeText(context,"You DisLike the Song",Toast.LENGTH_SHORT).show();
                if (notlike.getVisibility() == View.VISIBLE){
                    notlike.setVisibility(View.GONE);
                }
            }
        });

        notdislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notdislike.setVisibility(View.GONE);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pl.playMusic(MusicList.locationList.get(position));
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                Toast.makeText(context,"Song Is now Playing",Toast.LENGTH_SHORT).show();
                if (play_main.getVisibility() == View.VISIBLE){
                    play_main.setVisibility(View.GONE);
                    pause_main.setVisibility(View.VISIBLE);
                }

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pl.pauseMusic();
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                Toast.makeText(context,"Song is Pause",Toast.LENGTH_SHORT).show();
                if (pause_main.getVisibility() == View.VISIBLE){
                    pause_main.setVisibility(View.GONE);
                    play_main.setVisibility(View.VISIBLE);
                }
            }
        });

        play_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pl.playMusic(MusicList.locationList.get(position));
                play_main.setVisibility(View.GONE);
                pause_main.setVisibility(View.VISIBLE);
                Toast.makeText(context,"Song Is now Playing",Toast.LENGTH_SHORT).show();
                if (play.getVisibility() == View.VISIBLE){
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }
            }
        });

        pause_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pl.pauseMusic();
                pause_main.setVisibility(View.GONE);
                play_main.setVisibility(View.VISIBLE);
                Toast.makeText(context,"Song is Pause",Toast.LENGTH_SHORT).show();
                if (pause.getVisibility() == View.VISIBLE){
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                }
            }
        });



    }
}
