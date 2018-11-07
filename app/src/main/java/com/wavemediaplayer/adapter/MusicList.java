package com.wavemediaplayer.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;

import com.wavemediaplayer.R;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MusicList {



    public static ArrayList<MusicData> musicData;
    public static Adapter adapter;

    private SlideAndDragListView musicListView;
    private Context context;

    private boolean isAdd = true;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Set<String> folderControl;
    private static boolean atamaYapildimi=false;

    public MusicList(SlideAndDragListView musicListView, Context context) {
        this.musicListView = musicListView;
        this.context = context;
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);

        init();
    }
    public MusicList(){}

    private void init(){
        musicData = new ArrayList<>();


    }




    // Eger herhangi bir bouyt belirtilmezse 0 kb den buyuk butun dosyaları alacak
    //  Eger engellenmesini istedigimiz pathler varsa paths ile belirtiyoruz
    public void getMusic(String... paths){
        getMusic(0,paths);
       // getAllMusic();
    }

    public void getMusic(int Size, String... paths){

        musicData = new ArrayList<>();
        int count = 0;


        if(sharedPreferences.contains("listsettings")) atamaYapildimi=true;
        folderControl=sharedPreferences.getStringSet("listsettings",new HashSet<String>());


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

                String loc="/storage/emulated/0/";
                String[] split= currentLocation.substring(loc.length(),currentLocation.length()).split("/");
                Log.e(String.valueOf(!sharedPreferences.contains("listsettings")),String.valueOf(folderControl.contains(split[0])));

                if(!atamaYapildimi || folderControl.contains(split[0])){
                    if (Integer.valueOf(currentSize) >= Size){
                        musicData.add(new MusicData(currentTitle,currentArtist,R.drawable.music,currentDuration,currentLocation,currentId));
                        count++;
                }


                }
            }
            while (songCursor.moveToNext());

            Log.e("while,","cikti");

            if(!sharedPreferences.contains("listsettings")){
                if(musicData.size()>0){
                    editor=sharedPreferences.edit();
                    Set<String> set=new HashSet<String>();
                    for(int i=0;i<musicData.size();i++){

                        String myLocation=musicData.get(i).getLocation();
                        String loc="/storage/emulated/0/";
                        String[] split= myLocation.substring(loc.length(),myLocation.length()).split("/");
                        set.add(split[0]);
                    }

                    atamaYapildimi=true;
                    editor.clear();
                    editor.putStringSet("listsettings",set);
                    editor.apply();
                    editor.commit();
                }
            }
            songCursor.close();
        }





        // getSDCardMusic(Size,paths);
        adapter = new Adapter(context,R.layout.custom_list_item,musicData,0);
        musicListView.setMenu(new Menu(false));
        musicListView.setAdapter(adapter);

    }

    private void getSDCardMusic(int Size, String... paths){
        int count = 0;
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
                Log.e("qqqqq",currentLocation);

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

                            if (!musicData.get(count).getIds().contains(currentId)){
                                musicData.add(new MusicData(currentTitle,currentArtist,R.drawable.music,currentDuration,currentLocation,currentId));
                                count++;
                            }
                        }
                    }
                    else {
                        if (!musicData.get(count).getIds().contains(currentId)){
                            musicData.add(new MusicData(currentTitle,currentArtist,R.drawable.music,currentDuration,currentLocation,currentId));
                            count++;
                        }
                    }
                }
            }
            while (songCursor.moveToNext());
            songCursor.close();

        }

        adapter = new Adapter(context,R.layout.custom_list_item,musicData,0);
        musicListView.setMenu(new Menu(false));
        musicListView.setAdapter(adapter);

    }

    public void removeFromAdapter(int s){
        File file = new File(MusicList.musicData.get(s).getLocation());
        Log.e("FILE",MusicList.musicData.get(s).getLocation());

        if (file.exists()){
            Log.e("File","var");
            if (file.delete()){
                Log.e(MusicList.musicData.remove(s).toString(),"silindi");
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
