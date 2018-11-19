package com.wavemediaplayer;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wavemediaplayer.adapter.MusicData;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.EqualizerFragment;
import com.wavemediaplayer.fragments.FragmentListener;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;
import com.wavemediaplayer.fragments.PlayListsFragment;
import com.wavemediaplayer.main.FPlayListener;
import com.wavemediaplayer.main.GestureListener;
import com.wavemediaplayer.mfcontroller.MainManager;
import com.wavemediaplayer.mservices.Constants;
import com.wavemediaplayer.mservices.NotificationService;
import com.wavemediaplayer.play.PlayMusic;
import com.wavemediaplayer.settings.FolderFragment;
import com.wavemediaplayer.settings.MusicListSettingsFragment;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener,
        SlideAndDragListView.OnDragDropListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnItemDeleteListener, ServiceConnection {

    public static final String KARISIK_CAL = "KARISIK CAL";
    public static final String SARKIYI_TEKRARLA = "SARKIYI TEKRAR";
    public static final String DUZENLENMIS_LISTE = "DUZENLENMIS LISTE";
    public static Context context;
    public static View mainView;
    public static int tabsHeigh;
    public static SlideAndDragListView musicListView;
    public static FPlayListener fPlayListener;
    public static boolean playList_Ekleme_Yapildi = false;
    public static boolean allPermGrand = false;
    public static boolean isMulti = false;
    static int pos = 0;
    public NotificationService s;
    public RelativeLayout mainSearchLayout;
    public MainMenu mainMenu;
    public EditText edit_search;
    public EqualizerFragment equalizerFragment;
    public FrameLayout mainFrame;
    public FolderFragment folderFragment;
    public MusicList musicList;
    public FragmentListener fragmentListener;
    public MusicListSettingsFragment musicListSettingsFragment;
    public boolean isSwipeOpen = false;
    List<View> tempListLayout = new ArrayList<>();
    ArrayList<Integer> tempList = new ArrayList<>();
    int list_selected_count = 0;
    SlidingUpPanelLayout mLayout;
    GestureDetector gestureDetector;
    GestureListener gestureListener;
    SharedPreferences sharedPreferences2;
    MainManager mainManager;
    View HeaderView;
    View adHeader;
    AdView mAdView;
    NativeExpressAdView nativeExpressAdView;
    private boolean isDrag = false;
    private MusicData mDraggedEntity;
    private OynatmaListesiFragment oynatmaListesiFragment;
    private ArrayList<MusicData> denememusicdata;
    private SharedPreferences sharedPreferences;
    private IntentFilter intentFilter;
    private ImageView mainsearchButton;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("servicePause") != null) {
                PlayMusic.mediaPlayer = NotificationService.mediaPlayer;
                fPlayListener.pl.iconKapat(false);

            } else if (intent.getStringExtra("servicePlay") != null) {
                PlayMusic.mediaPlayer = NotificationService.mediaPlayer;
                fPlayListener.pl.iconKapat(true);
            } else if (intent.getStringExtra("serviceNext") != null) {
                PlayMusic.mediaPlayer = NotificationService.mediaPlayer;
                fPlayListener.icerikDegistirme();

            } else if (intent.getStringExtra("serviceBefore") != null) {
                PlayMusic.mediaPlayer = NotificationService.mediaPlayer;
                fPlayListener.icerikDegistirme();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        tabsHeigh = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mainView = getWindow().getDecorView().findViewById(android.R.id.content);
        musicListView = findViewById(R.id.main_musicListView);
        HeaderView = LayoutInflater.from(this).inflate(R.layout.layout_header, null);
        edit_search = HeaderView.findViewById(R.id.edit_search);
        mainSearchLayout = HeaderView.findViewById(R.id.mainSearchLayout);
        mainsearchButton = HeaderView.findViewById(R.id.mainsearchButton);

        adHeader = LayoutInflater.from(this).inflate(R.layout.ad_layout, null);


        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        muzikCalmaBicimleri();


        fragmentListener = new FragmentListener(this);
        musicListSettingsFragment = new MusicListSettingsFragment();
        folderFragment = new FolderFragment();
        mainManager = new MainManager(this);
        gestureListener = new GestureListener(mainManager);
        gestureDetector = new GestureDetector(context, gestureListener);

        ///////////////////////

        denememusicdata = new ArrayList<>();
        musicListView.setOnDragDropListener(this);
        musicListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        musicListView.setOnSlideListener(this);
        musicListView.setOnMenuItemClickListener(this);
        musicListView.setOnItemDeleteListener(this);
        musicListView.setOnScrollListener(this);
        musicListView.setNotDragHeaderCount(1);
        musicListView.addHeaderView(HeaderView);
        musicListView.addHeaderView(adHeader);
        intentFilter = new IntentFilter("speedExceeded");


        DexterPermission dex = new DexterPermission(this);
        dex.girisControl();
        /**servis çalışır ama create edilmediği için iconu yukarda gözükmez taki bi müzik açılana kadar*/
    }

    /**
     * Uygulama arka plana dusup tekrar acıldıgında musicleri yeniden secme
     */

    public void createStart() {
        if (!isMyServiceRunning(NotificationService.class)) {
            Intent serviceIntent = new Intent(context, NotificationService.class);
            serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            context.startService(serviceIntent);
        }
        musicList = new MusicList(musicListView, this);
        musicList.getMusic();
        denememusicdata.addAll(MusicList.musicData);

        m_createListener();
        f_createListener();
        editTextDegisiklikKontrol();
        mainMenu = new MainMenu(this);

        fPlayListener.pl.stopRunable();
        fPlayListener.pl.startRunable();

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

    private void muzikCalmaBicimleri() {
        sharedPreferences = getSharedPreferences(KARISIK_CAL, Context.MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences(SARKIYI_TEKRARLA, Context.MODE_PRIVATE);
        PlayMusic.karisikCal = sharedPreferences.getBoolean("karisik", true);
        PlayMusic.tekrarla = sharedPreferences2.getInt("tekrarla", 0);
    }

    /**
     * Uygulama arka plana dusup tekrar acıldıgında musicleri yeniden secme
     */

    private void editTextDegisiklikKontrol() {
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    musicList.getMusic("notification", "ringtone");
                } else {

                    searchItem(s.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void listsettingMusicDataDegistir() {
        denememusicdata.clear();
        denememusicdata.addAll(MusicList.musicData);
    }

    private void searchItem(String text) {
        MusicList.musicData.clear();
        for (int i = 0; i < denememusicdata.size(); i++) {
            if (denememusicdata.get(i).getTitles().toLowerCase().contains(text.toLowerCase())) {
                MusicList.musicData.add(denememusicdata.get(i));
                MusicList.adapter.notifyDataSetChanged();

            }
        }
        MusicList.adapter.notifyDataSetChanged();
    }

    private void m_createListener() {
        equalizerFragment = new EqualizerFragment();
        oynatmaListesiFragment = new OynatmaListesiFragment();
        mainFrame = findViewById(R.id.mainFrame);


        mainsearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animationSet = AnimationUtils.loadAnimation(context, R.anim.slideoutedittext);
                Animation animationSet2 = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_left);
                if (edit_search.getVisibility() == View.INVISIBLE) {
                    edit_search.setAnimation(animationSet2);
                    animationSet2.setDuration(500);
                    edit_search.setVisibility(View.VISIBLE);
                    edit_search.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edit_search, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    edit_search.setText("");
                    edit_search.setAnimation(animationSet);
                    animationSet.setDuration(500);
                    edit_search.setVisibility(View.INVISIBLE);
                    edit_search.setText("");
                    klavyeDisable();
                }
            }
        });
    }

    private void f_createListener() {
        mLayout = findViewById(R.id.activity_main);
        fPlayListener = new FPlayListener(this, mainView);
        fPlayListener.f_ListenerEvent();
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == (SlidingUpPanelLayout.PanelState.EXPANDED)) {
                    isSwipeOpen = true;
                } else if (newState == (SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                    isSwipeOpen = false;
                }
            }
        });

        if (MusicList.musicData.size() > 0) {
            FPlayListener.currentMusicPosition = pos;
            PlayMusic.prevMusicDAta = MusicList.musicData.get(pos);

        }


        multipleChoise();
        listviewOneClickListener();
    }

    private void listviewOneClickListener() {
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                position = position - 2;
                klavyeDisable();
                if (!isMulti) {
                    FPlayListener.calmaListesiMuzik = false;
                    FPlayListener.currentMusicPosition = position;
                    NotificationService.mainListeOncekiPos.clear();
                    NotificationService.kaldirilanPos.clear();
                    NotificationService.mainListeOncekiPos.add(position);
                    pos = position;
                    fPlayListener.playMusic(position);

                    if (s != null)
                        s.listeDegistir(MusicList.musicData, FPlayListener.currentMusicPosition);
                    eventClick(view);
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isDrag) {
            gestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void multipleChoise() {

        musicListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                position = position - 2;
                isMulti = true;
                if (!tempList.contains(position)) {
                    list_selected_count = list_selected_count + 1;
                    mode.setTitle(list_selected_count + "item selected");
                    MusicList.musicData.get(position).setIsaretlendi(true);
                    if (musicListView.getChildAt(position) != null) {
                        if (musicListView.getChildAt(position).findViewById(R.id.listview_layout) != null) {
                            musicListView.getChildAt(position).findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.holo_gray_light));
                            tempListLayout.add(musicListView.getChildAt(position).findViewById(R.id.listview_layout));
                        }
                    }
                    tempList.add(position);
                    MusicList.adapter.notifyDataSetChanged();

                } else {
                    list_selected_count = list_selected_count - 1;
                    mode.setTitle(list_selected_count + "item selected");
                    MusicList.musicData.get(position).setIsaretlendi(false);
                    if (musicListView.getChildAt(position) != null) {
                        if (musicListView.getChildAt(position).findViewById(R.id.listview_layout) != null) {
                            musicListView.getChildAt(position).findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
                            tempListLayout.remove(musicListView.getChildAt(position).findViewById(R.id.listview_layout));
                        }


                    }
                    tempList.remove((Object) position);
                    MusicList.adapter.notifyDataSetChanged();
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.custom_tools, menu);
                klavyeDisable();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemSil:
                        layoutListClear();
                        ArrayList<MusicData> tmpData = new ArrayList<>();

                        for (Integer s : tempList) {
                            tmpData.add(MusicList.musicData.get(s));
                            musicList.removeFromAdapter(s);

                        }
                        for (MusicData data : tmpData) {
                            MusicList.musicData.remove(data);
                        }
                        denememusicdata.clear();
                        denememusicdata.addAll(MusicList.musicData);
                        MusicList.adapter.notifyDataSetChanged();
                        musicList.getMusic();


                        list_selected_count = 0;
                        mode.finish();
                        tempList.clear();
                        return true;

                    case R.id.itemPlayList:
                        playlistInfo(tempList);
                        list_selected_count = 0;
                        layoutListClear();
                        mode.finish();
                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                for (int pos : tempList) {
                    MusicList.musicData.get(pos).setIsaretlendi(false);
                }
                Log.e("destroya girdik","ssssssssssssssss");
                layoutListClear();
                tempList.clear();
                list_selected_count = 0;
                isMulti = false;
            }
        });
    }

    private void klavyeDisable() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_search.getApplicationWindowToken(), 0);
        //edit_search.setVisibility(View.INVISIBLE);
    }

    private void duzenlenmisListeKaydet() {

        sharedPreferences = getSharedPreferences(DUZENLENMIS_LISTE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(MusicList.musicData);
        if (sharedPreferences.getString("main_liste", null) != null) {
            editor.remove("main_liste");
        }

        editor.putString("main_liste", json);
        editor.apply();
    }

    private void layoutListClear() {

        for (int pos : tempList) {
            if (musicListView.getChildAt(pos) != null) {
                if (musicListView.getChildAt(pos).findViewById(R.id.listview_layout) != null) {
                    musicListView.getChildAt(pos).findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }

        }
        tempListLayout.clear();
        tempListLayout = new ArrayList<>();
        MusicList.adapter.notifyDataSetChanged();
    }

    /**
     * Add Play list'e tıklandıgında Dialog fragment acılacak ve olusturulan playlistler gosterilecek
     */
    private void playlistInfo(ArrayList<Integer> tempLists) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment dialogFragment = new PlayListsFragment();
        ((PlayListsFragment) dialogFragment).setList(tempLists);
        dialogFragment.show(fragmentTransaction, "dialog");
    }

    public void eventClick(View view) {
        if (mLayout != null) {
            if ((mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                isSwipeOpen = false;
            } else {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                isSwipeOpen = true;
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (fragmentListener.removeFragment(folderFragment, equalizerFragment, oynatmaListesiFragment, musicListSettingsFragment)) {
            return;
        }

        if (oynatmaListesiFragment != null && !oynatmaListesiFragment.isHidden()) {
            if (OynatmaListesiFragment.isList) {
                getSupportFragmentManager().beginTransaction().hide(oynatmaListesiFragment).commit();
                return;
            } else {
                oynatmaListesiFragment.getCalmaListeleri();
                OynatmaListesiFragment.isList = true;
                return;
            }
        }

        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            isSwipeOpen = false;
        } else {
            isSwipeOpen = true;
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                mainMenu.search();
                break;
            case R.id.menu_folder:
                mainMenu.folder();
                break;
            case R.id.menu_musiclist:
                mainMenu.musiclist();
                break;
        }
        return true;
    }

    @Override
    public void onDragViewStart(int beginPosition) {
        mDraggedEntity = MusicList.musicData.get(beginPosition);
        isDrag = true;
    }

    @Override
    public void onDragDropViewMoved(int fromPosition, int toPosition) {
        MusicData applicationInfo = MusicList.musicData.remove(fromPosition);
        MusicList.musicData.add(toPosition, applicationInfo);
    }

    @Override
    public void onDragViewDown(int finalPosition) {
        isDrag = false;
        MusicList.musicData.set(finalPosition, mDraggedEntity);
        duzenlenmisListeKaydet();
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        NotificationService.MyBinder b = (NotificationService.MyBinder) service;
        s = b.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        s = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdView = adHeader.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        nativeExpressAdView = findViewById(R.id.nativeAds);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        nativeExpressAdView.loadAd(adRequest2);

        Intent intent = new Intent(this, NotificationService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, intentFilter);
        if (allPermGrand) {
            if (!isMulti){
                musicList.getMusic("notification", "ringtone");
            }
            fPlayListener.pl.stopRunable();
            fPlayListener.pl.startRunable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (allPermGrand) {
            fPlayListener.pl.stopRunable();
        }
        unbindService(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (allPermGrand) {
            {
                fPlayListener.pl.startRunable();
                s.setSettings();
            }
        }
        //buradaki amaç runable activity sonlandırldığında dahi calısıyor o yüzden açık bırakıyorum servisteki işlemler için
    }


}

