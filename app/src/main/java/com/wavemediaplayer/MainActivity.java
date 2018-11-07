package com.wavemediaplayer;


import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wavemediaplayer.adapter.MusicData;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.EqualizerFragment;
import com.wavemediaplayer.fragments.FragmentListener;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;
import com.wavemediaplayer.fragments.PlayListsFragment;
import com.wavemediaplayer.main.FPlayListener;
import com.wavemediaplayer.mfcontroller.MainManager;
import com.wavemediaplayer.play.PlayMusic;
import com.wavemediaplayer.settings.FolderFragment;
import com.wavemediaplayer.settings.InitilationMediaPlayer;
import com.wavemediaplayer.settings.MusicListSettingsFragment;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.List;

import static com.wavemediaplayer.play.PlayMusic.mediaPlayer;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener,
        SlideAndDragListView.OnDragDropListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnItemDeleteListener {

    /** Diger sınıflara context ve view gondermek icin */
    public static Context context;
    public static View mainView;
    private boolean isMulti = false;

    public static int tabsHeigh;

    private MainMenu mainMenu;


    /** Main musiclistview */
    public static SlideAndDragListView musicListView;
    List<View> tempListLayout = new ArrayList<>();
    private MusicData mDraggedEntity;

    public EditText edit_search;
    ArrayList<ArrayList<MusicData>> geciciAramaSonuclari = new ArrayList<>();
    ArrayList<MusicData> tempData = new ArrayList<>();
     /** Templist'te multi choise ile secilen coklu secimlerin pozisyonları tutuluyor */
    ArrayList<Integer> tempList = new ArrayList<>();

    /** listview de secilen item sayısı multichoise icin */
    int list_selected_count = 0;

    private Button mainEqualizer;
    private EqualizerFragment equalizerFragment;
    public FrameLayout mainFrame;
    /** Calma listelerinin goorunecegi listi  oynatmaListesiFragment inde gosterilecek*/
    private OynatmaListesiFragment oynatmaListesiFragment;

    public FolderFragment folderFragment;
    /** fat linstener event knk */
    public static FPlayListener fPlayListener;
    public MusicList musicList;
    /** default olarak ilk sıradaki muzigi calar eger listede herhangi bir yere tıklanmıssa ordaki muzigin positionunu alır */
    static int pos = 0;
    private ArrayList<MusicData> denememusicdata;

    public static final String KARISIK_CAL = "KARISIK CAL";
    public static final String SARKIYI_TEKRARLA = "SARKIYI TEKRAR";

    SlidingUpPanelLayout mLayout;
    public FragmentListener fragmentListener;
    public MusicListSettingsFragment musicListSettingsFragment;

    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;

    public static boolean playList_Ekleme_Yapildi = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabsHeigh=(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,getResources().getDisplayMetrics());
        context = this;
        mainView = getWindow().getDecorView().findViewById(android.R.id.content);
        musicListView = findViewById(R.id.main_musicListView);
        edit_search = findViewById(R.id.edit_search);

        sharedPreferences  = context.getSharedPreferences(KARISIK_CAL, Context.MODE_PRIVATE);
        sharedPreferences2  = context.getSharedPreferences(SARKIYI_TEKRARLA, Context.MODE_PRIVATE);
        muzikCalmaBicimleri();


        fragmentListener=new FragmentListener(this);
        musicListSettingsFragment=new MusicListSettingsFragment();
        folderFragment=new FolderFragment();
        new MainManager(this);

        musicList = new MusicList(musicListView,this);
        musicList.getMusic("notification","ringtone");
        denememusicdata=new ArrayList<>();
        denememusicdata.addAll(MusicList.musicData);
        musicListView.setOnDragDropListener(this);
        musicListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        musicListView.setOnSlideListener(this);
        musicListView.setOnMenuItemClickListener(this);
        musicListView.setOnItemDeleteListener(this);
        musicListView.setOnScrollListener(this);

        m_createListener();
        f_createListener();
        editTextDegisiklikKontrol();
        mainMenu=new MainMenu(this);

        //  PlayerFragment fragmentS1 = new PlayerFragment();
        //    getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragmentS1).commit();
    }

    private void muzikCalmaBicimleri(){
        PlayMusic.karisikCal = sharedPreferences.getBoolean("karisik",true);
        PlayMusic.tekrarla = sharedPreferences2.getInt("tekrarla",0);


    }


    private void editTextDegisiklikKontrol(){
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    musicList.getMusic("notification","ringtone");
                    geciciAramaSonuclari.clear();
                }
                else {

                    searchItem(s.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void listsettingMusicDataDegistir(){
        denememusicdata.clear();
        denememusicdata.addAll(MusicList.musicData);
    }

    private void searchItem(String text){
        MusicList.musicData.clear();
        for(int i=0;i<denememusicdata.size();i++){
            if(denememusicdata.get(i).getTitles().toLowerCase().contains(text.toString().toLowerCase())){
                MusicList.musicData.add(denememusicdata.get(i));
                MusicList.adapter.notifyDataSetChanged();

            }
        }
        MusicList.adapter.notifyDataSetChanged();
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
                    fragmentListener.addFragment(equalizerFragment);
                }
            }
        });
    }

    /** Fatihin olusturdugu listener fonksiyonu */
    private void f_createListener(){
        mLayout =  findViewById(R.id.activity_main);
        fPlayListener = new FPlayListener(this,mainView);
        /** burada equalizeri başlangıçta çalıştırıyorum ki sonradan equalizere tıkladığında ses değişmesin ayarlar önceden yapılsın diye*/
        if(mediaPlayer!=null){
            fragmentListener.addFragment(equalizerFragment);
        }
        /** Herhangi bit posizyon yok ise default 0'dır */
        FPlayListener.currentMusicPosition = pos;
        PlayMusic.prevMusicDAta = MusicList.musicData.get(pos);
        fPlayListener.f_ListenerEvent(pos);

        /** Listviewde coklu secim yapmak icin */
        multipleChoise();
        listviewOneClickListener();
    }

    private void listviewOneClickListener(){
            musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (!isMulti){
                        Log.e("tiklandi",position+" "+"multi "+isMulti);
                        // pl.play(MusicList.locationList.get(position));
                        FPlayListener.calmaListesiMuzik = false;
                        FPlayListener.currentMusicPosition = position;
                        PlayMusic.prevMusicDAta = MusicList.musicData.get(position);
                        pos = position;
                        fPlayListener.playMusic(position);



                        fPlayListener.f_ListenerEvent(position);
                        eventClick(view);
                    }
                }
            });
    }

    /** Listview multi choise event fonksiyonu */
    public void multipleChoise(){
        musicListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            /** Multichoise islemi yapıldıgında secilen itemleri liste ata ve secilen sayısını belirt */
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                isMulti = true;
                if (!tempList.contains(position)){
                    list_selected_count = list_selected_count + 1;
                    mode.setTitle(list_selected_count + "item selected");
                    musicListView.getChildAt(position).findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.background_grey));
                    tempListLayout.add( musicListView.getChildAt(position).findViewById(R.id.listview_layout));
                    tempList.add(position);
                }
                else {
                    list_selected_count = list_selected_count - 1;
                    mode.setTitle(list_selected_count + "item selected");
                    musicListView.getChildAt(position).findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
                    tempListLayout.remove(musicListView.getChildAt(position).findViewById(R.id.listview_layout));
                    tempList.remove((Object)position);
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
                MenuInflater inflater = mode.getMenuInflater();
                switch (item.getItemId()){
                    case R.id.itemSil:
                        layoutListClear(tempListLayout);
                        for (Integer s: tempList){
                            musicList.removeFromAdapter(s);
                        }
                        list_selected_count = 0;
                        mode.finish();
                        tempList.clear();
                        return true;

                    case R.id.itemPlayList:
                        playlistInfo(tempList);
                        list_selected_count = 0;
                        layoutListClear(tempListLayout);
                        mode.finish();

                    default:
                        return false;
                }

            }

            /** Multichoise islemi iptal edildiginde secilen tum itemler sıfırlanacak  */
            @Override
            public void onDestroyActionMode(ActionMode mode) {

                tempList = new ArrayList<>();
                layoutListClear(tempListLayout);
                list_selected_count = 0;
                isMulti = false;
            }
        });
    }

    private void layoutListClear(List<View> layoutList){
        for (View v :layoutList){
            v.findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
        }
        tempListLayout.clear();
        tempListLayout =  new ArrayList<>();

    }

    /** Add Play list'e tıklandıgında Dialog fragment acılacak ve olusturulan playlistler gosterilecek */
    private void playlistInfo(ArrayList<Integer> tempLists){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
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

        if(fragmentListener.removeFragment(folderFragment,equalizerFragment)){
            return;
        }

        if( oynatmaListesiFragment!= null && !oynatmaListesiFragment.isHidden()){
            if (oynatmaListesiFragment.isList){
                getSupportFragmentManager().beginTransaction().hide(oynatmaListesiFragment).commit();
                return;
            }
            else {
                oynatmaListesiFragment.getCalmaListeleri();
                OynatmaListesiFragment.isList = true;
                return;
            }
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
            case R.id.menu_search: mainMenu.search(); break;
            case R.id.menu_folder: mainMenu.folder(); break;
            case R.id.menu_musiclist: mainMenu.musiclist(); break;
        }
        return true;
    }

    @Override
    public void onDragViewStart(int beginPosition) {
        mDraggedEntity = MusicList.musicData.get(beginPosition);
    }

    @Override
    public void onDragDropViewMoved(int fromPosition, int toPosition) {
        MusicData applicationInfo = MusicList.musicData.remove(fromPosition);
        MusicList.musicData.add(toPosition, applicationInfo);
    }

    @Override
    public void onDragViewDown(int finalPosition) {
        MusicList.musicData.set(finalPosition, mDraggedEntity);
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case com.yydcdut.sdlv.MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return com.yydcdut.sdlv.Menu.ITEM_NOTHING;
                    case 1:
                        return com.yydcdut.sdlv.Menu.ITEM_SCROLL_BACK;
                }
                break;
            case com.yydcdut.sdlv.MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return com.yydcdut.sdlv.Menu.ITEM_SCROLL_BACK;
                    case 1:
                        return com.yydcdut.sdlv.Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                }
        }
        return com.yydcdut.sdlv.Menu.ITEM_NOTHING;
    }

    @Override
    public void onItemDeleteAnimationFinished(View view, int position) {
        MusicList.musicData.remove(position - musicListView.getHeaderViewsCount());
        MusicList.adapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }




}
