package com.wavemediaplayer.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;

import com.wavemediaplayer.R;

import java.io.File;
import java.util.ArrayList;

public class MusicList {

    public static ArrayList<String> titleList;
    public static ArrayList<String> artistList;
    public static ArrayList<Integer> imageList;
    public static ArrayList<String> durationList;
    public static ArrayList<String> locationList;
    public static ArrayList<String> idList;
    private Adapter adapter;

    private ListView musicListView;
    private Context context;

    private boolean isAdd = true;

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
        idList = new ArrayList<>();
    }




    // Eger herhangi bir bouyt belirtilmezse 0 kb den buyuk butun dosyaları alacak
    //  Eger engellenmesini istedigimiz pathler varsa paths ile belirtiyoruz
    public void getMusic(String... paths){
        getMusic(0,paths);
    }

    public void getMusic(int Size, String... paths){

        titleList = new ArrayList<>();
        artistList = new ArrayList<>();
        imageList = new ArrayList<>();
        durationList = new ArrayList<>();
        locationList = new ArrayList<>();
        idList = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri,null,null,null,null);

        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int location = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int size = songCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int id = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int duration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentSize = songCursor.getString(size);
                String currentLocation = songCursor.getString(location);
                String currentId = songCursor.getString(id);
                String currentDuration = songCursor.getString(duration);

                if (Integer.valueOf(currentSize) >= Size){
                    if (paths.length>0){
                        isAdd = true;
                        for (int i = 0;i<paths.length;i++){
                            if (currentLocation.toLowerCase().contains(paths[i])){
                                isAdd = false;

                            }

                        }

                        if (isAdd){
                            Log.e("curremtLoc",currentLocation);
                            Log.e("Size",currentSize);

                            if (!idList.contains(currentId)){
                                titleList.add(currentTitle);
                                artistList.add(currentArtist);
                                imageList.add(R.drawable.music);
                                durationList.add(currentDuration);
                                locationList.add(currentLocation);
                                idList.add(currentId);
                            }

                        }
                    }
                    else {
                        if (!idList.contains(currentId)){
                            titleList.add(currentTitle);
                            artistList.add(currentArtist);
                            imageList.add(R.drawable.music);
                            durationList.add(currentDuration);
                            locationList.add(currentLocation);
                            idList.add(currentId);
                        }
                    }
                }




            }
            while (songCursor.moveToNext());
        }

        adapter = new Adapter(context,R.layout.custom_list_item,titleList,artistList,imageList,durationList);
        musicListView.setAdapter(adapter);
    //   getSDCardMusic(Size,paths);


    }

    private void getSDCardMusic(int Size, String... paths){
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri,null,null,null,null);

        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int location = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int size = songCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int id = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int duration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentSize = songCursor.getString(size);
                String currentLocation = songCursor.getString(location);
                String currentId = songCursor.getString(id);
                String currentDuration = songCursor.getString(duration);

                if (Integer.valueOf(currentSize) >= Size){
                    if (paths.length>0){
                        isAdd = true;
                        for (int i = 0;i<paths.length;i++){
                            if (currentLocation.toLowerCase().contains(paths[i])){
                                isAdd = false;

                            }

                        }

                        if (isAdd){
                            Log.e("curremtLoc",currentLocation);
                            Log.e("Size",currentSize);

                            if (!idList.contains(currentId)){
                                titleList.add(currentTitle);
                                artistList.add(currentArtist);
                                imageList.add(R.drawable.music);
                                durationList.add(currentDuration);
                                locationList.add(currentLocation);
                                idList.add(currentId);
                            }

                        }
                    }
                    else {
                        if (!idList.contains(currentId)){
                            titleList.add(currentTitle);
                            artistList.add(currentArtist);
                            imageList.add(R.drawable.music);
                            durationList.add(currentDuration);
                            locationList.add(currentLocation);
                            idList.add(currentId);
                        }
                    }
                }




            }
            while (songCursor.moveToNext());
            songCursor.close();

        }

        adapter = new Adapter(context,R.layout.custom_list_item,titleList,artistList,imageList,durationList);
        musicListView.setAdapter(adapter);

    }

    public void removeFromAdapter(int s){
        File file = new File(MusicList.locationList.get(s));
        Log.e("FILE",MusicList.locationList.get(s));

        if (file.exists()){
            Log.e("File","var");
            if (file.delete()){
                Log.e("silindi","ever");
                Log.e(MusicList.locationList.remove(s),"silindi");

                MusicList.titleList.remove(s);
                MusicList.artistList.remove(s);
                MusicList.durationList.remove(s);
                MusicList.imageList.remove(s);
                MusicList.idList.remove(s);


                // burdaki parametreler simdilik boyle
                getMusic("notification","ringtone");
            }
            else {
                Log.e("silindi","hayır");
            }
        }
        else {
            Log.e("File","yok");
        }


    }
}
