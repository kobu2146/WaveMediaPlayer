package com.wavemediaplayer.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
            Log.e("title1",title);
            String artirst = MusicList.musicData.get(i).getArtist();
            Log.e("artirst1",artirst);
            Integer thumbnail = MusicList.musicData.get(i).getImages();
            Log.e("thumbnail1",""+thumbnail);
            String duration = MusicList.musicData.get(i).getDuration();
            Log.e("duration1",duration);
            String location = MusicList.musicData.get(i).getLocation();
            Log.e("location1",location);
            String id = MusicList.musicData.get(i).getIds();
            Log.e("id1",id);

            plList.add(new PlayList(title,artirst,thumbnail,duration,location,id));

        }



        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                if (entry.getKey().equals(listName)){
                    JSONArray jsonArray = new JSONArray(entry.getValue().toString());
                    for (int i = 0;i<jsonArray.length();i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        Log.e("title2",title);
                        String artist = jsonObject.getString("artist");
                        Log.e("artis2",artist);
                        int thumbnail = jsonObject.getInt("thumbnail");
                        Log.e("thumbnail2",""+thumbnail);
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
