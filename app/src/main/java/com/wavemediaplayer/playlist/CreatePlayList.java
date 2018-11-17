package com.wavemediaplayer.playlist;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.adapter.MusicData;
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
    ArrayList<PlayList> plList;
    public CreatePlayList(Context context){
        this.context = context;
        plList = new ArrayList<>();
        sharedPreferences  = context.getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE);


    }


    public void createAndAddList(String listName, ArrayList<Integer> playLists){


        for (int i : playLists){
            String title = MusicList.musicData.get(i).getTitles();
            String artirst = MusicList.musicData.get(i).getArtist();
            Integer thumbnail = MusicList.musicData.get(i).getImages();
            String duration = MusicList.musicData.get(i).getDuration();
            String location = MusicList.musicData.get(i).getLocation();
            String id = MusicList.musicData.get(i).getIds();

            plList.add(new PlayList(title,artirst,thumbnail,duration,location,id));

        }

        eskiMuzikleriYukle(listName);

        Gson gson = new Gson();
        String json = gson.toJson(plList);
        set(listName,json);
    }

    public void eskiMuzikleriYukle(String listName){
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
                        String duration = jsonObject.getString("duration");
                        String location = jsonObject.getString("location");
                        String id = jsonObject.getString("id");


                        plList.add(new PlayList(title,artist,thumbnail,duration,location,id));

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void muzikleriKaldır(ArrayList<MusicData> md,String listName){
        plList.clear();
        for (MusicData m : md){
            String title = m.getTitles();
            String artirst = m.getArtist();
            Integer thumbnail = m.getImages();
            String duration = m.getDuration();
            String location = m.getLocation();
            String id = m.getIds();

            plList.add(new PlayList(title,artirst,thumbnail,duration,location,id));

         }
        Gson gson = new Gson();
        String json = gson.toJson(plList);
        editor  = sharedPreferences.edit();
        editor.remove(listName);
        editor.putString(listName,json);

        editor.apply();
    }



    private void set(String key, String value){
        editor  = sharedPreferences.edit();
        editor.putString(key,value);

        editor.apply();

        MainActivity.playList_Ekleme_Yapildi = true;



    }
}
