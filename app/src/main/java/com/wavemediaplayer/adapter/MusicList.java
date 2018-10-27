package com.wavemediaplayer.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;

import java.util.ArrayList;

public class MusicList {

    public static ArrayList<String> titleList;
    public static ArrayList<String> artistList;
    public static ArrayList<Integer> imageList;
    public static ArrayList<String> durationList;
    public static ArrayList<String> locationList;

    private ListView musicListView;
    private Context context;

    public MusicList(ListView musicListView, Context context) {
        this.musicListView = musicListView;
        this.context = context;
        init();
    }
    public MusicList(){}

    private void init(){
        titleList = new ArrayList<>();
        artistList = new ArrayList<>();
        imageList = new ArrayList<>();
        durationList = new ArrayList<>();
        locationList = new ArrayList<>();
    }




    // Eger herhangi bir bouyt belirtilmezse 0 kb den buyuk butun dosyaları alacak
    //  Eger engellenmesini istedigimiz pathler varsa paths ile belirtiyoruz
    public void getMusic(String... paths){
        getMusic(500*1024*8,paths);
    }

    public void getMusic(int Size, String... paths){
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri,null,null,null,null);

        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int location = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int size = songCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int duration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentSize = songCursor.getString(size);
                String currentLocation = songCursor.getString(location);
                String currentDuration = songCursor.getString(duration);

                if (Integer.valueOf(currentSize) >= Size){
                    if (paths.length>0){
                        for (int i = 0;i<paths.length;i++){
                            if (!currentLocation.toLowerCase().contains(paths[i])){

                                Log.e("curremtLoc",currentLocation);
                                Log.e("Size",currentSize);

                                titleList.add(currentTitle);
                                artistList.add(currentArtist);
                                imageList.add(R.drawable.music);
                                durationList.add(currentDuration);
                                locationList.add(currentLocation);
                            }
                        }
                    }
                    else {
                        titleList.add(currentTitle);
                        artistList.add(currentArtist);
                        imageList.add(R.drawable.music);
                        durationList.add(currentDuration);
                        locationList.add(currentLocation);
                    }
                }




            }
            while (songCursor.moveToNext());
        }

        Adapter adapter = new Adapter(context,R.layout.custom_list_item,titleList,artistList,imageList,durationList);
        musicListView.setAdapter(adapter);

    }
}
