package com.wavemediaplayer.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MusicList {
    public static ArrayList<MusicData> musicData;
    public static Adapter adapter;
    private static boolean atamaYapildimi = false;
    private SlideAndDragListView musicListView;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences1;
    private SharedPreferences.Editor editor;
    private Set<String> folderControl;

    public MusicList(SlideAndDragListView musicListView, Context context) {
        this.musicListView = musicListView;
        this.context = context;
        sharedPreferences = MainActivity.context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        init();
    }

    public MusicList() {
    }

    private void init() {
        musicData = new ArrayList<>();
    }
    private void duzenlenmisListeyiCek() {

        sharedPreferences1 = context.getSharedPreferences(MainActivity.DUZENLENMIS_LISTE, Context.MODE_PRIVATE);

        if (sharedPreferences1.getString("main_liste", null) != null) {
            Map<String, ?> allEntries = sharedPreferences1.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                try {
                    if (entry.getKey().equals("main_liste")) {
                        JSONArray jsonArray = new JSONArray(entry.getValue().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String title = jsonObject.getString("title");
                            String artist = jsonObject.getString("artist");
                            int thumbnail = jsonObject.getInt("image");
                            String duration = jsonObject.getString("duration");
                            String location = jsonObject.getString("location");
                            String ids = jsonObject.getString("id");

                            musicData.add(new MusicData(title, artist, thumbnail, duration, location, ids));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter = new Adapter(context, R.layout.custom_list_item, musicData, 0);
            musicListView.setMenu(new Menu(false));
            musicListView.setAdapter(adapter);
        }

    }
    // Eger herhangi bir bouyt belirtilmezse 0 kb den buyuk butun dosyaları alacak
    //  Eger engellenmesini istedigimiz pathler varsa paths ile belirtiyoruz
    public void getMusic(String... paths) {
        musicData.clear();
        getMusic(0, paths);
    }

    public void getMusic(int Size, String... paths) {
        duzenlenmisListeyiCek();

        String durationMusicSettings=sharedPreferences.getString("musicDuration","ignore");
        int musicDurationSettings=0;
        if(!durationMusicSettings.contains("ignore")) musicDurationSettings=Integer.valueOf(durationMusicSettings);


        if (sharedPreferences.contains("listsettings")) atamaYapildimi = true;
        folderControl = sharedPreferences.getStringSet("listsettings", new HashSet<String>());
        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
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
                String loc = "/storage/emulated/0/";
                String[] split = currentLocation.substring(loc.length(), currentLocation.length()).split("/");
                if (!atamaYapildimi || folderControl.contains(split[0])) {
                    if (Integer.valueOf(currentDuration) >= musicDurationSettings*1000) {

                        if (musicData.size() > 0) {
                            boolean ekle = false;
                            for (MusicData md : musicData) {
                                if (!md.getIds().equals(currentId)) {
                                    ekle = true;
                                } else {
                                    ekle = false;
                                    break;
                                }
                            }
                            if (ekle) {
                                musicData.add(new MusicData(currentTitle, currentArtist, R.drawable.ic_music_nota_1, currentDuration, currentLocation, currentId));
                            }
                        } else {
                            musicData.add(new MusicData(currentTitle, currentArtist, R.drawable.ic_music_nota_1, currentDuration, currentLocation, currentId));

                        }
                    }
                }
            }

            while (songCursor.moveToNext());
            if (!sharedPreferences.contains("listsettings")) {
                if (musicData.size() > 0) {
                    editor = sharedPreferences.edit();
                    Set<String> set = new HashSet<String>();
                    for (int i = 0; i < musicData.size(); i++) {

                        String myLocation = musicData.get(i).getLocation();
                        String loc = "/storage/emulated/0/";
                        String[] split = myLocation.substring(loc.length(), myLocation.length()).split("/");
                        set.add(split[0]);
                    }
                    atamaYapildimi = true;
                    editor.clear();
                    editor.putStringSet("listsettings", set);
                    editor.apply();
                    editor.commit();
                }
            }
            songCursor.close();
        }

        adapter = new Adapter(context, R.layout.custom_list_item, musicData, 0);
        musicListView.setMenu(new Menu(false));
        musicListView.setAdapter(adapter);
    }

    public void removeFromAdapter(int s) {
        Uri uri = Uri.parse(MusicList.musicData.get(s).getLocation());
        File file = new File(uri.getPath());

        if (file.exists()) {
            if (file.delete()) {
                if (file.exists()) {
                    context.deleteFile(file.getName());
                }
                scanaddedFile(MusicList.musicData.get(s).getLocation()); // bu mediadanda siliyor
                // burdaki parametreler simdilik boyle
                getMusic("notification", "ringtone");
            } else {
            }
        } else {

        }

    }

    private void scanaddedFile(String path) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{path},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            context.getContentResolver()
                                    .delete(uri, null, null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
