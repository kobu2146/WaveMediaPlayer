package com.wavemediaplayer.fragments;

import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.playlist.CreatePlayList;

import java.util.ArrayList;
import java.util.Map;

public class PlayListsFragment extends DialogFragment {


    /**
     *
     * option menudeki add playlist kısmı icin
     * AÇılır bir dialog fragment acılacak ve play listeleri gosterilecek
     * */
    ListView playListView;
    ArrayList<String> playLists = new ArrayList<>();

    ArrayList<Integer> music_position = new ArrayList<>();

    Button btn_add;
    EditText edit_playlist_name;

    Context context;

    public PlayListsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_lists,
                container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

       playListView = view.findViewById(R.id.playListView);
       btn_add = view.findViewById(R.id.btn_add);
       edit_playlist_name = view.findViewById(R.id.edit_playlist_name);
       context = view.getContext();

        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences( "WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);


        /** tum playlistleri ve iceriklerini cekiyor cekiyor */
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.e("map values", entry.getKey() + ": " + entry.getValue().toString());
            playLists.add(entry.getKey());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, playLists);
        playListView.setAdapter(adapter);

        clickEvent();
    }

    private void clickEvent(){
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String list_name = edit_playlist_name.getText().toString();
                if (list_name != null || list_name.equals("")){
                    new CreatePlayList(MainActivity.context).createAndAddList(list_name,music_position);
                    SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences( "WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);
                    String s =  sharedPreferences.getString(list_name,null);
                }
            }
        });

        /** Listviiew deki elemana tıklandıgında yen geleln itemlleri o listview iceriisine atıyor */
        playListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new CreatePlayList(MainActivity.context).createAndAddList(playLists.get(position),music_position);
            }
        });
    }

    public void setList(ArrayList<Integer> tempList){

        music_position = tempList;
    }



}
