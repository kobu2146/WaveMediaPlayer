package com.wavemediaplayer;


import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.EqualizerFragment;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;
import com.wavemediaplayer.fragments.PlayListsFragment;
import com.wavemediaplayer.main.FPlayListener;

import java.util.ArrayList;

import static com.wavemediaplayer.play.PlayMusic.mediaPlayer;


public class MainActivity extends AppCompatActivity {

    /** Diger sınıflara context ve view gondermek icin */
    public static Context context;
    public static View mainView;


    /** Main musiclistview */
    ListView musicListView;

    /**
     * Templist'te multi choise ile secilen coklu secimlerin pozisyonları tutuluyor
     * */
    ArrayList<Integer> tempList = new ArrayList<>();

    /** listview de secilen item sayısı multichoise icin */
    int list_selected_count = 0;

    private Button mainEqualizer;
    private EqualizerFragment equalizerFragment;
    public FrameLayout mainFrame;
    /** Calma listelerinin goorunecegi listi  oynatmaListesiFragment inde gosterilecek*/
    private OynatmaListesiFragment oynatmaListesiFragment;

    /** fat linstener event knk */
    FPlayListener fPlayListener;
    MusicList musicList;

    /** default olarak ilk sıradaki muzigi calar eger listede herhangi bir yere tıklanmıssa ordaki muzigin positionunu alır */
    static int pos = 0;


    SlidingUpPanelLayout mLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        mainView = getWindow().getDecorView().findViewById(android.R.id.content);
        musicListView = findViewById(R.id.main_musicListView);
        musicListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        musicList = new MusicList(musicListView,this);
        musicList.getMusic("notification","ringtone");

        m_createListener();
        f_createListener();




        //  PlayerFragment fragmentS1 = new PlayerFragment();
        //    getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentS1).commit();


    }

    /** Uygulama arka plana dusup tekrar acıldıgında musicleri yeniden secme */
    @Override
    protected void onResume() {
        super.onResume();
        musicList.getMusic("notification","ringtone");
    }

    /** Musanın olusturdugu listener fonksyonu */
    private void m_createListener(){
        equalizerFragment=new EqualizerFragment();
        oynatmaListesiFragment = new OynatmaListesiFragment();
        mainEqualizer=findViewById(R.id.mainEqualizer);
        mainFrame=findViewById(R.id.mainFrame);

        mainEqualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


            }
        });
    }


    /** Fatihin olusturdugu listener fonksiyonu */
    private void f_createListener(){

        mLayout =  findViewById(R.id.activity_main);
        fPlayListener = new FPlayListener(this,mainView);

        /** Herhangi bit posizyon yok ise default 0'dır */
        fPlayListener.f_ListenerEvent(pos);


        /** Listviewde coklu secim yapmak icin */
        multipleChoise();


        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // pl.play(MusicList.locationList.get(position));
                fPlayListener.playMusic(position);
                pos = position;
                fPlayListener.f_ListenerEvent(position);
                eventClick(view);
            }
        });
    }

    /** Listview multi choise event fonksiyonu */
    public void multipleChoise(){
        musicListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            /** Multichoise islemi yapıldıgında secilen itemleri liste ata ve secilen sayısını belirt */
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {


                if (!tempList.contains(position)){
                    list_selected_count = list_selected_count + 1;
                    mode.setTitle(list_selected_count + "item selected");
                    tempList.add(position);
                }


            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.custom_tools,menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()){
                    case R.id.itemSil:
                        for (Integer s: tempList){
                            // silme islemi
                            musicList.removeFromAdapter(s);
                        }
                        list_selected_count = 0;
                        mode.finish();
                        tempList.clear();

                        return true;
                    case R.id.itemPlayList:
                        // select Playlist
                        playlistInfo(tempList);
//                        if (!oynatmaListesiFragment.isAdded()){
//                            getFragmentManager().beginTransaction().add(android.R.id.content, oynatmaListesiFragment).commit();
//                            mainFrame.setBackgroundColor(Color.WHITE);
//                        }
//                        else {
//                            if(equalizerFragment.isHidden()){
//                                getFragmentManager().beginTransaction().show(oynatmaListesiFragment).commit();
//                                mainFrame.setBackgroundColor(Color.WHITE);
//
//                            }else{
//                                getFragmentManager().beginTransaction().hide(oynatmaListesiFragment).commit();
//                                mainFrame.setBackgroundColor(Color.WHITE);
//                            }
//                        }
                    default:
                        return false;
                }

            }

            /** Multichoise islemi iptal edildiginde secilen tum itemler sıfırlanacak  */
            @Override
            public void onDestroyActionMode(ActionMode mode) {

                tempList = new ArrayList<>();
                list_selected_count = 0;
            }
        });
    }

    /** Add Play list'e tıklandıgında Dialog fragment acılacak ve olusturulan playlistler gosterilecek */
    private void playlistInfo(ArrayList<Integer> tempLists){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null){
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment dialogFragment = new PlayListsFragment();
        ((PlayListsFragment) dialogFragment).setList(tempLists);
        dialogFragment.show(fragmentTransaction,"dialog");


    }

    /** Mini music playera herhangi bir tiklama isleminde büyültme veya kucultme islemi calisacak*/
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
        if( equalizerFragment!=null && !equalizerFragment.isHidden()){
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.hide(equalizerFragment);
            mainFrame.setBackgroundColor(Color.TRANSPARENT);
            ft.commit();
            return;
        }

        /**  */
        if( oynatmaListesiFragment!= null && !oynatmaListesiFragment.isHidden()){
            Log.e("hidden","deil");
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.hide(oynatmaListesiFragment);
            mainFrame.setBackgroundColor(Color.TRANSPARENT);
            ft.commit();
            return;
        }


        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    /** item menu islemleri */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {


        }
        return true;
    }




}
