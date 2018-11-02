package com.wavemediaplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.AdapterPlayList;
import com.wavemediaplayer.adapter.MusicData;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.main.FPlayListener;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class OynatmaListesiFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener,
        SlideAndDragListView.OnDragDropListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnItemDeleteListener {


    public static SlideAndDragListView oynatma_listesi;
    ArrayList<String> oynat_list = new ArrayList<>();
    ArrayList<MusicData> music_oynat_list = new ArrayList<>();


    FPlayListener fPlayListener;
    private MusicData mDraggedEntity;
    AdapterPlayList adapterPlayList;
    Context context;
    // Tum oynatma listesini gpruntulemek icin
    private boolean isList = true;


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
        View view=inflater.inflate(R.layout.fragment_oynatma_listesi, container, false);
        oynatma_listesi = view.findViewById(R.id.oynatma_listesi);

        oynatma_listesi.setOnDragDropListener(this);
        oynatma_listesi.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        oynatma_listesi.setOnSlideListener(this);
        oynatma_listesi.setOnMenuItemClickListener(this);
        oynatma_listesi.setOnItemDeleteListener(this);
        oynatma_listesi.setOnScrollListener(this);


        context = getActivity();
        fPlayListener = new FPlayListener(MainActivity.context,MainActivity.mainView);

        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences( "WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);

        /** tum playlistleri ve iceriklerini cekiyor cekiyor */
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.e("map values", entry.getKey() + ": " + entry.getValue().toString());
            oynat_list.add(entry.getKey());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, oynat_list);
        oynatma_listesi.setMenu(new Menu(false));
        oynatma_listesi.setAdapter(adapter);

        clickEvent();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){




    }

    private void clickEvent(){

        oynatma_listesi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isList){
                    SharedPreferences sharedPreferences  = context.getSharedPreferences("WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);
                    Map<String, ?> allEntries = sharedPreferences.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        try {
                            Log.e("map values", entry.getKey() + ": " + entry.getValue().toString());
                            if (entry.getKey().equals(oynat_list.get(position))){
                                JSONArray jsonArray = new JSONArray(entry.getValue().toString());
                                Log.e("xxxxx",jsonArray.toString());
                                for (int i = 0;i<jsonArray.length();i++){


                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String title = jsonObject.getString("title");
                                    String artist = jsonObject.getString("artist");
                                    int thumbnail = jsonObject.getInt("thumbnail");
                                    String duration = jsonObject.getString("duration");
                                    String location = jsonObject.getString("location");
                                    String ids = jsonObject.getString("id");

                                    music_oynat_list.add(new MusicData(title,artist,thumbnail,duration,location,ids));


                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapterPlayList = new AdapterPlayList(context,R.layout.custom_list_item,music_oynat_list);
                    oynatma_listesi.setMenu(new Menu(false));
                    oynatma_listesi.setAdapter(adapterPlayList);

                    isList = false;
                }

                else {
                    Log.e("music","caliyor");
                    fPlayListener.playFromPlayList(music_oynat_list.get(position).getLocation());
                }

            }
        });
    }

    @Override
    public void onDragViewStart(int beginPosition) {
        mDraggedEntity = music_oynat_list.get(beginPosition);
    }

    @Override
    public void onDragDropViewMoved(int fromPosition, int toPosition) {
        MusicData applicationInfo = music_oynat_list.remove(fromPosition);
        music_oynat_list.add(toPosition, applicationInfo);
    }

    @Override
    public void onDragViewDown(int finalPosition) {
        music_oynat_list.set(finalPosition, mDraggedEntity);
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {

        return com.yydcdut.sdlv.Menu.ITEM_NOTHING;
    }

    @Override
    public void onItemDeleteAnimationFinished(View view, int position) {
        music_oynat_list.remove(position - oynatma_listesi.getHeaderViewsCount());
        adapterPlayList.notifyDataSetChanged();
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
