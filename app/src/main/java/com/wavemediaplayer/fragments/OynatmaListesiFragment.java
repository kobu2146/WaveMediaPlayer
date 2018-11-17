package com.wavemediaplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.Adapter;
import com.wavemediaplayer.adapter.ListAdapter;
import com.wavemediaplayer.adapter.MusicData;
import com.wavemediaplayer.main.FPlayListener;
import com.wavemediaplayer.mservices.NotificationService;
import com.wavemediaplayer.play.PlayMusic;
import com.wavemediaplayer.playlist.CreatePlayList;
import com.wavemediaplayer.playlist.PlayListData;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OynatmaListesiFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener,
        SlideAndDragListView.OnDragDropListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnItemDeleteListener {


    public static SlideAndDragListView oynatma_listesi;
    public static ArrayList<PlayListData> oynat_list = new ArrayList<>();
    public static ArrayList<MusicData> music_oynat_list = new ArrayList<>();


    private MusicData mDraggedEntity;
    Adapter adapterPlayList;
    public static Context context;

    private int calma_listesi_pos = 0;

    private ArrayList<Integer> temp_position_list = new ArrayList<>();

    private boolean isMulti = false;
    int list_selected_count = 0;
    List<View> tempListLayout = new ArrayList<>();
    // Tum oynatma listesini gpruntulemek icin
    public static boolean isList = true;


    public OynatmaListesiFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oynatma_listesi, container, false);
        oynatma_listesi = view.findViewById(R.id.oynatma_listesi);

        oynatma_listesi.setOnDragDropListener(this);
        oynatma_listesi.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        oynatma_listesi.setOnSlideListener(this);
        oynatma_listesi.setOnMenuItemClickListener(this);
        oynatma_listesi.setOnItemDeleteListener(this);
        oynatma_listesi.setOnScrollListener(this);


        context = view.getContext();


        listviewMultiChoise();
        clickEvent();

        getCalmaListeleri();

        return view;
    }

    public void getCalmaListeleri() {
        temp_position_list.clear();
        oynat_list.clear();
        isList = true;
        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences("WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);
        /** tum playlistleri ve iceriklerini cekiyor cekiyor */
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            oynat_list.add(new PlayListData(entry.getKey()));
        }

        ListAdapter adapter2 = new ListAdapter(context,
                R.layout.basic_list, oynat_list);
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context,
//                android.R.layout.simple_list_item_1, android.R.id.text1, oynat_list);

        oynatma_listesi.setMenu(new Menu(false));
        oynatma_listesi.setAdapter(adapter2);


    }

    private void listviewMultiChoise() {
        oynatma_listesi.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                isMulti = true;
                itemCheckedState(mode, position, id, checked);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                if (isList) {
                    inflater.inflate(R.menu.playlist_menu, menu);
                } else {
                    inflater.inflate(R.menu.calma_listesi_menu, menu);
                }

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                MenuInflater inflater = mode.getMenuInflater();
                switch (item.getItemId()) {
                    case R.id.item_sil:
                        layoutListClear(tempListLayout);
                        calmaListeleriniSil();
                        list_selected_count = 0;
                        mode.finish();
                        temp_position_list.clear();
                        return true;

                    case R.id.item_kaldır:
                        layoutListClear(tempListLayout);
                        calmaListesiMuzikleriniSil();
                        list_selected_count = 0;
                        temp_position_list.clear();
                        mode.finish();
                    case R.id.item_paylas:
                        list_selected_count = 0;
                        layoutListClear(tempListLayout);
                        mode.finish();

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (!isList) {
                    for (Integer pos : temp_position_list) {
                        music_oynat_list.get(pos).setIsaretlendi(false);
                    }
                } else {
                    for (Integer pos : temp_position_list) {
                        oynat_list.get(pos).setIsaretlendi(false);
                    }

                }
                layoutListClear(tempListLayout);
                temp_position_list.clear();

                list_selected_count = 0;
                isMulti = false;

            }
        });
    }

    private void calmaListeleriniSil() {
        for (Integer s : temp_position_list) {
            String key = oynat_list.get(s).getListBaslik();
            SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences("WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
        getCalmaListeleri();

    }

    private void calmaListesiMuzikleriniSil() {
        ArrayList<MusicData> data = new ArrayList<>();
        for (int s : temp_position_list) {
            data.add(music_oynat_list.get(s));
        }
        for (MusicData d : data) {
            music_oynat_list.remove(d);
        }

        new CreatePlayList(MainActivity.context).muzikleriKaldır(music_oynat_list, oynat_list.get(calma_listesi_pos).getListBaslik());

        calmaListesiMuzikleriniGetir(oynat_list.get(calma_listesi_pos).getListBaslik());
        getCalmaListeleriSarkilari();


    }


    private void itemCheckedState(ActionMode mode, int position, long id, boolean checked) {


        // Oynatma listelerini gosterecek liste ise
        try {
            if (isList) {
                if (!temp_position_list.contains(position)) {
                    list_selected_count = list_selected_count + 1;
                    mode.setTitle(list_selected_count + "item selected");
                    oynat_list.get(position).setIsaretlendi(true);
                    if (oynatma_listesi.getChildAt(position) != null) {
                        oynatma_listesi.getChildAt(position).findViewById(R.id.basic_listview_layout).setBackgroundColor(getResources().getColor(R.color.holo_gray_light));
                        tempListLayout.add(oynatma_listesi.getChildAt(position).findViewById(R.id.basic_listview_layout));
                    }

                    temp_position_list.add(position);
                    adapterPlayList.notifyDataSetChanged();
                } else {
                    list_selected_count = list_selected_count - 1;
                    mode.setTitle(list_selected_count + "item selected");
                    oynat_list.get(position).setIsaretlendi(false);
                    if (oynatma_listesi.getChildAt(position) != null) {
                        oynatma_listesi.getChildAt(position).findViewById(R.id.basic_listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
                        tempListLayout.remove(oynatma_listesi.getChildAt(position).findViewById(R.id.basic_listview_layout));

                    }
                    temp_position_list.remove((Object) position);
                    adapterPlayList.notifyDataSetChanged();

                }
            } else {
                if (!temp_position_list.contains(position)) {
                    list_selected_count = list_selected_count + 1;
                    mode.setTitle(list_selected_count + "item selected");
                    music_oynat_list.get(position).setIsaretlendi(true);
                    if (oynatma_listesi.getChildAt(position) != null) {
                        oynatma_listesi.getChildAt(position).findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.holo_gray_light));
                        tempListLayout.add(oynatma_listesi.getChildAt(position).findViewById(R.id.listview_layout));

                    }
                    temp_position_list.add(position);
                    adapterPlayList.notifyDataSetChanged();

                } else {
                    list_selected_count = list_selected_count - 1;
                    mode.setTitle(list_selected_count + "item selected");
                    music_oynat_list.get(position).setIsaretlendi(false);
                    if (oynatma_listesi.getChildAt(position) != null) {
                        oynatma_listesi.getChildAt(position).findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
                        tempListLayout.remove(oynatma_listesi.getChildAt(position).findViewById(R.id.listview_layout));
                        temp_position_list.remove((Object) position);
                    }
                    adapterPlayList.notifyDataSetChanged();

                }
            }
        } catch (Exception ex) {
        } finally {

        }

    }

    private void layoutListClear(List<View> layoutList) {
//        for (View v : layoutList) {
//            if (isList) {
//                v.findViewById(R.id.basic_listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
//            } else {
//                v.findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
//            }
//        }
        for (int pos : temp_position_list) {
            if (isList) {
                if (oynatma_listesi.getChildAt(pos) != null) {
                    oynatma_listesi.getChildAt(pos).findViewById(R.id.basic_listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            } else {
                if (oynatma_listesi.getChildAt(pos) != null) {
                    oynatma_listesi.getChildAt(pos).findViewById(R.id.listview_layout).setBackgroundColor(getResources().getColor(R.color.transparent));
                    adapterPlayList.notifyDataSetChanged();
                }
            }
        }
        tempListLayout.clear();
        tempListLayout = new ArrayList<>();

    }


    public void getCalmaListeleriSarkilari() {
        adapterPlayList = new Adapter(context, R.layout.custom_list_item, music_oynat_list, 1);
        oynatma_listesi.setMenu(new Menu(false));
        oynatma_listesi.setAdapter(adapterPlayList);
        adapterPlayList.notifyDataSetChanged();
    }

    private void calmaListesiMuzikleriniGetir(String liste_key) {
        music_oynat_list.clear();
        SharedPreferences sharedPreferences = context.getSharedPreferences("WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                if (entry.getKey().equals(liste_key)) {
                    JSONArray jsonArray = new JSONArray(entry.getValue().toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String artist = jsonObject.getString("artist");
                        int thumbnail = jsonObject.getInt("thumbnail");
                        String duration = jsonObject.getString("duration");
                        String location = jsonObject.getString("location");
                        String ids = jsonObject.getString("id");

                        music_oynat_list.add(new MusicData(title, artist, thumbnail, duration, location, ids));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    private void clickEvent() {
        oynatma_listesi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!isMulti) {
                    if (isList) {
                        music_oynat_list.clear();

                        calma_listesi_pos = position;
                        calmaListesiMuzikleriniGetir(oynat_list.get(position).getListBaslik());
                        isList = false;
                        getCalmaListeleriSarkilari();

                    } else {
                        if (music_oynat_list.size() > 0) {
                            NotificationService.calimaListesiOncekiPos.clear();
                            FPlayListener.currentMusicPosition = position;
                            NotificationService.calimaListesiOncekiPos.add(position);
                            PlayMusic.prevMusicDAta = music_oynat_list.get(position);
                            MainActivity.fPlayListener.song_title.setText(music_oynat_list.get(position).getTitles());
                            MainActivity.fPlayListener.song_artis.setText(music_oynat_list.get(position).getArtist());
                            MainActivity.fPlayListener.playFromPlayList(music_oynat_list.get(position).getLocation());
                            if (((MainActivity) MainActivity.context).s != null)
                                ((MainActivity) MainActivity.context).s.listeDegistir(OynatmaListesiFragment.music_oynat_list, FPlayListener.currentMusicPosition);
                        }

                    }
                }


            }
        });
    }


    @Override
    public void onDragViewStart(int beginPosition) {
        if (!isList) {
            mDraggedEntity = music_oynat_list.get(beginPosition);
        }

    }

    @Override
    public void onDragDropViewMoved(int fromPosition, int toPosition) {
        if (!isList) {
            MusicData applicationInfo = music_oynat_list.remove(fromPosition);
            music_oynat_list.add(toPosition, applicationInfo);
        }

    }

    @Override
    public void onDragViewDown(int finalPosition) {
        if (!isList) {
            music_oynat_list.set(finalPosition, mDraggedEntity);
            new CreatePlayList(MainActivity.context).muzikleriKaldır(music_oynat_list, oynat_list.get(calma_listesi_pos).getListBaslik());
        }

    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        if (!isList) {
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
        }

        return com.yydcdut.sdlv.Menu.ITEM_NOTHING;
    }

    @Override
    public void onItemDeleteAnimationFinished(View view, int position) {
        if (!isList) {
            music_oynat_list.remove(position - oynatma_listesi.getHeaderViewsCount());
            adapterPlayList.notifyDataSetChanged();
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!isList) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    break;
            }
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
