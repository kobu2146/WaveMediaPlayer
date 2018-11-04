package com.wavemediaplayer.settings;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class MusicListSettingsFragment extends Fragment {
    private View view;
    private LinearLayout linearLayout;
    private MyAdapter myAdapter;
    private ArrayList<String> arrayList;
    private Activity activity;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity=getActivity();
        linearLayout=new LinearLayout(activity);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);

        listView=new ListView(getActivity());
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        linearLayout.addView(listView);
        view=linearLayout;

        getMusic();



        return view;
    }


    public void getMusic(){


        arrayList=new ArrayList<>();



        ContentResolver contentResolver = getContext().getContentResolver();
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
        }

    }

    /***************************************************************************************************/

    public class MyAdapter extends ArrayAdapter {
        private ArrayList<String> list;
        private Context context;
        private int resource;


        public MyAdapter(@NonNull Context context, int resource,ArrayList<String> list) {
            super(context, resource,list);
            this.context=context;
            this.resource=resource;
            this.list=list;
        }

        @NonNull
        @Override
        public View getView(int position,@Nullable View convertView,@NonNull ViewGroup parent) {
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


            viewHolder.holderCheckBox.setChecked(viewHolder.holderCheckBox.isChecked());
            viewHolder.holderCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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


