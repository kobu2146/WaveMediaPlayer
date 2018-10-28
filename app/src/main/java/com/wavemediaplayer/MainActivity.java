package com.wavemediaplayer;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.EqualizerFragment;


public class MainActivity extends AppCompatActivity {



     ListView musicListView;
     private Button mainEqualizer;
     public static MediaPlayer mediaPlayer;
     private EqualizerFragment equalizerFragment;
     public FrameLayout mainFrame;


     // Sliding up panel for music player
    ImageButton like, notlike,dislike,notdislike;
    ImageButton play,pause,play_main,pause_main;
    private SlidingUpPanelLayout mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        musicListView = findViewById(R.id.main_musicListView);




        MusicList musicList = new MusicList(musicListView,this);
        musicList.getMusic("notification","ringtone");



        m_createListener();


      //  PlayerFragment fragmentS1 = new PlayerFragment();
    //    getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentS1).commit();


    }

    private void m_createListener(){
        equalizerFragment=new EqualizerFragment();
        mainEqualizer=findViewById(R.id.mainEqualizer);
        mainFrame=findViewById(R.id.mainFrame);




        mainEqualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                //fat burası equalizeri açmak için
                if(mediaPlayer!=null){
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    if(!equalizerFragment.isAdded()){
                        ft.add(android.R.id.content, equalizerFragment);
                        mainFrame.setBackgroundColor(Color.BLUE);
                    }else{
                        if(equalizerFragment.isHidden()){
                            ft.show(equalizerFragment);
                            mainFrame.setBackgroundColor(Color.BLUE);

                        }else{
                            ft.hide(equalizerFragment);
                            mainFrame.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    ft.commit();

                }


//                if(mediaPlayer!=null){
//                    FragmentManager manager = getFragmentManager();
//                    FragmentTransaction ft = manager.beginTransaction();
//                    ft.add(android.R.id.content, equalizerFragment);
//
//                    if(equalizerFragment.isHidden()){
//                        ft.show(equalizerFragment);
//                    }else{
//                        ft.hide(equalizerFragment);
//                    }
//                    ft.commit();
//
//                }

            }
        });
    }

    private void f_createListener(){

        like = (ImageButton) findViewById(R.id.sample_main_imageButton2);
        notlike = (ImageButton) findViewById(R.id.sample_main_imageButton2new);
        dislike = (ImageButton) findViewById(R.id.sample_main_button);
        notdislike = (ImageButton) findViewById(R.id.sample_main_buttontwo);
        play = (ImageButton) findViewById(R.id.sample_main_play_button);
        pause = (ImageButton) findViewById(R.id.sample_main_pause_button);
        play_main = (ImageButton) findViewById(R.id.sample_main_play_button_main);
        pause_main = (ImageButton) findViewById(R.id.sample_main_pause_button_main);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_main);
    }





}
