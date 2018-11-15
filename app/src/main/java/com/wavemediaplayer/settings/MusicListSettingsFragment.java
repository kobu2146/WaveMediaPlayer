package com.wavemediaplayer.settings;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wavemediaplayer.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MusicListSettingsFragment extends Fragment {
    private View view;
    private LinearLayout linearLayout;
    private MyAdapter myAdapter;
    private ArrayList<String> arrayList;
    private Activity activity;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity=getActivity();
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(activity);
        linearLayout=new LinearLayout(activity);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.bar7));

        TextView textView=new TextView(activity);
        textView.setText("Folder Settings");
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(20f);
        LinearLayout.LayoutParams paramstext=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paramstext.gravity=Gravity.CENTER_HORIZONTAL;
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setLayoutParams(paramstext);
        paramstext.topMargin=20;
        linearLayout.addView(textView);

        listView=new ListView(getActivity());
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.addView(listView);



        view=linearLayout;
        getMusic();



        return view;
    }


    public void getMusic(){


        arrayList=new ArrayList<>();


        /**buradaki amaç müzik olan klasörleri elde tutmak bunları kapatarak o klasöre ait müzikleri listenden çıkarabilirsin*/

        ContentResolver contentResolver = view.getContext().getContentResolver();
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
                Log.e("qqqq",currentLocation);
                String loc="/storage/emulated/0/";
                String[] split= currentLocation.substring(loc.length(),currentLocation.length()).split("/");
                if(!arrayList.contains(split[0])){
                    arrayList.add(split[0]);
                }



            }
            while (songCursor.moveToNext());









            myAdapter=new MyAdapter(activity, R.layout.fragment_settings_musiclist_item,arrayList);
            listView.setAdapter(myAdapter);
            songCursor.close();
        }

    }

    /***************************************************************************************************/

    public class MyAdapter extends ArrayAdapter {
        private ArrayList<String> list;
        private Context context;
        private int resource;
        private Set<String> adapterSet;


        public MyAdapter(@NonNull Context context, int resource,ArrayList<String> list) {
            super(context, resource,list);
            this.context=context;
            this.resource=resource;
            this.list=list;
            adapterSet=sharedPreferences.getStringSet("listsettings",new HashSet<String>());
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                viewHolder=new ViewHolder();
                convertView=LayoutInflater.from(context).inflate(resource,parent,false);
                viewHolder.holderTextView=convertView.findViewById(R.id.settingsListTextView);
                convertView.setTag(viewHolder);
                viewHolder.holderCheckBox=convertView.findViewById(R.id.settingsListCheckBox);
            }else{
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.holderTextView.setText(list.get(position));

            for (String veri:adapterSet){
                Log.e("oncreate",veri);
            }

            if(adapterSet.contains(arrayList.get(position))) viewHolder.holderCheckBox.setChecked(true);
            else viewHolder.holderCheckBox.setChecked(false);

            viewHolder.holderCheckBox.setChecked(viewHolder.holderCheckBox.isChecked());
            viewHolder.holderCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor=sharedPreferences.edit();
                    Set<String> set;
                    set = sharedPreferences.getStringSet("listsettings",new HashSet<String>());
                    if(isChecked){
                        if(!set.contains(arrayList.get(position))) {
                            set.add(arrayList.get(position));
                        }

                    }else{
                        if(set.contains(arrayList.get(position))) {
                            for (String veri:set){
                                Log.e("checkremove oldu",veri);
                            }
                            set.remove(arrayList.get(position));

                        }
                    }

                    editor.clear();
                    editor.putStringSet("listsettings",set);
                    editor.apply();
                    editor.commit();

                    for (String veri:sharedPreferences.getStringSet("listsettings",new HashSet<String>())){
                        Log.e("son aşama",veri);
                    }

                }
            });





            return convertView;
        }

        public class ViewHolder{
            public TextView holderTextView;
            public CheckBox holderCheckBox;
        }
    }

    /***************************************************************************************************/


}


