package com.wavemediaplayer.playlist;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wavemediaplayer.adapter.MusicList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

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
            String title = MusicList.musicData.get(i).getTitles();
            String artirst = MusicList.musicData.get(i).getArtist();
            Integer thumbnail = MusicList.musicData.get(i).getImages();
            String location = MusicList.musicData.get(i).getLocation();

            plList.add(new PlayList(title,artirst,thumbnail,location));

        }



        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                if (entry.getKey().equals(listName)){
                    JSONArray jsonArray = new JSONArray(entry.getValue().toString());
                    for (int i = 0;i<jsonArray.length();i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String artist = jsonObject.getString("artist");
                        int thumbnail = jsonObject.getInt("thumbnail");
                        String location = jsonObject.getString("location");

                        plList.add(new PlayList(title,artist,thumbnail,location));

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
