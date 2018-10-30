package com.wavemediaplayer.playlist;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wavemediaplayer.adapter.MusicList;

import java.util.ArrayList;

public class CreatePlayList {

    private Context context;
     private String STORE_FILE_NAME = "WAVE MUSIC PLAYLIST";
    private  SharedPreferences sharedPreferences;
    private  SharedPreferences.Editor editor;
    public CreatePlayList(Context context){
        this.context = context;
        sharedPreferences  = context.getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE);


    }


    public void createAndAddList(String listName, ArrayList<Integer> playLists){

        ArrayList<PlayList> plList = new ArrayList<>();
        for (int i : playLists){
            String title = MusicList.titleList.get(i);
            String artirst = MusicList.artistList.get(i);
            Integer thumbnail = MusicList.imageList.get(i);
            String location = MusicList.locationList.get(i);

            plList.add(new PlayList(title,artirst,thumbnail,location));

        }


        Gson gson = new Gson();
        String json = gson.toJson(plList);
        set(listName,json);
    }



    private void set(String key, String value){
        editor  = sharedPreferences.edit();
        editor.putString(key,value);

        editor.apply();



    }
}
