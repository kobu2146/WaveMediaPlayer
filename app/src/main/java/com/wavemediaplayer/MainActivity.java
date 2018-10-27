package com.wavemediaplayer;


import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.play.PlayMusic;


public class MainActivity extends AppCompatActivity {



     ListView musicListView;
     public static MediaPlayer mediaPlayer;

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

        String musa="mal fatih";

         MusicList musicList = new MusicList(musicListView,this);
        musicList.getMusic("notification","ringtone");

       final PlayMusic pl = new PlayMusic(MainActivity.this,mediaPlayer,myseekbar,mytext1,mytext2,myimageview,handler,runnable);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pl.play(MusicList.locationList.get(position));


            }
        });

      //  PlayerFragment fragmentS1 = new PlayerFragment();
    //    getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentS1).commit();


    }






}
