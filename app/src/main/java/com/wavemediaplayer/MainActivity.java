package com.wavemediaplayer;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.EqualizerFragment;
import com.wavemediaplayer.main.FPlayListener;

import static com.wavemediaplayer.play.PlayMusic.mediaPlayer;


public class MainActivity extends AppCompatActivity {

     Context context;
     ListView musicListView;
     private Button mainEqualizer;
     private EqualizerFragment equalizerFragment;
     public FrameLayout mainFrame;
     // fat linstener event knk
    FPlayListener fPlayListener;

    // default olarak ilk sıradaki muzigi calar eger listede herhangi bir yere tıklanmıssa ordaki muzigin positionunu alır
     static int pos = 0;


     SlidingUpPanelLayout mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        musicListView = findViewById(R.id.main_musicListView);

        MusicList musicList = new MusicList(musicListView,this);
        musicList.getMusic("notification","ringtone");

        m_createListener();
        f_createListener();




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


        mLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_main);

        fPlayListener = new FPlayListener(this,getWindow().getDecorView().findViewById(android.R.id.content));

        // Herhangi bit posizyon yok ise default 0'dır
        fPlayListener.f_ListenerEvent(pos);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // pl.play(MusicList.locationList.get(position));
                fPlayListener.playMusic(position);
                pos = position;
               fPlayListener.f_ListenerEvent(position);


            }
        });
    }

    // Layouttaki herhangi bir clik button click haric bu islem calisacak
    public  void eventClick(View view){
        if (mLayout != null){
            if ((mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
            else {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }





}
