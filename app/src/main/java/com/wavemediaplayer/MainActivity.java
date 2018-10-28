package com.wavemediaplayer;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.EqualizerFragment;
import com.wavemediaplayer.play.PlayMusic;


public class MainActivity extends AppCompatActivity {



     ListView musicListView;
     private Button mainEqualizer;
     public static MediaPlayer mediaPlayer;
     private EqualizerFragment equalizerFragment;
     public FrameLayout mainFrame;

    //Media Player
      ImageView myimageview;
      TextView mytext1,mytext2;
      SeekBar myseekbar;
      Handler handler;
      Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        musicListView = findViewById(R.id.musicListView);

        mytext1= findViewById(R.id.baslangicText);
        mytext2=findViewById(R.id.bitisText);
        myimageview= findViewById(R.id.imageview);
        myseekbar= findViewById(R.id.seekBar);



        MusicList musicList = new MusicList(musicListView,this);
        musicList.getMusic("notification","ringtone");

       final PlayMusic pl = new PlayMusic(MainActivity.this,mediaPlayer,myseekbar,mytext1,mytext2,myimageview,handler,runnable);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pl.play(MusicList.locationList.get(position));


            }
        });

        m_createListener();


        //  PlayerFragment fragmentS1 = new PlayerFragment();
        //  getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentS1).commit();


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






}
