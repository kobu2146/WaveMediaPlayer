package com.wavemediaplayer.settings;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MusicListSettingsFragment extends Fragment {
    private View view;
    private MyAdapter myAdapter;
    private ArrayList<String> arrayList;
    private Activity activity;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Spinner musiclistSpinner;
    private ArrayList<String> spinnerList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        sharedPreferences = MainActivity.context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        view = inflater.inflate(R.layout.fragment_musiclistsettings, container, false);
        listView = view.findViewById(R.id.musiclistListView);
        musiclistSpinner = view.findViewById(R.id.musiclistSpinner);
        spinnerList=new ArrayList<>();
        spinnerList.add("Don't ignore - include all");
        spinnerList.add("2");
        spinnerList.add("6");
        spinnerList.add("10");
        spinnerList.add("15");
        spinnerList.add("30");

        ArrayAdapter<String> equalizerPresetSpinnerAdapter
                = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item,
                spinnerList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                text.setTextSize(18);
                text.setPadding(20, 10, 20, 10);
                view.setBackgroundColor(getResources().getColor(R.color.bar7));

                return view;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                text.setTextSize(18);
                text.setPadding(20, 10, 20, 10);
                return view;
            }
        };



        musiclistSpinner.setAdapter(equalizerPresetSpinnerAdapter);
        musiclistSpinner.setSelection(spinnerList.indexOf(sharedPreferences.getString("musicDuration",spinnerList.get(0))));
        musiclistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor=sharedPreferences.edit();
                editor.putString("musicDuration",spinnerList.get(position));
                editor.apply();
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getMusic();

        return view;
    }

    public void getMusic() {
        arrayList = new ArrayList<>();

        /**buradaki amaç müzik olan klasörleri elde tutmak bunları kapatarak o klasöre ait müzikleri listenden çıkarabilirsin*/
        ContentResolver contentResolver = view.getContext().getContentResolver();
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
                if (!arrayList.contains(split[0])) {
                    arrayList.add(split[0]);
                }
            }
            while (songCursor.moveToNext());
            myAdapter = new MyAdapter(activity, R.layout.fragment_settings_musiclist_item, arrayList);
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


        public MyAdapter(@NonNull Context context, int resource, ArrayList<String> list) {
            super(context, resource, list);
            this.context = context;
            this.resource = resource;
            this.list = list;
            adapterSet = sharedPreferences.getStringSet("listsettings", new HashSet<String>());
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(resource, parent, false);
                viewHolder.holderTextView = convertView.findViewById(R.id.settingsListTextView);
                convertView.setTag(viewHolder);
                viewHolder.holderCheckBox = convertView.findViewById(R.id.settingsListCheckBox);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.holderTextView.setText(list.get(position));

            for (String veri : adapterSet) {
            }

            if (adapterSet.contains(arrayList.get(position)))
                viewHolder.holderCheckBox.setChecked(true);
            else viewHolder.holderCheckBox.setChecked(false);

            viewHolder.holderCheckBox.setChecked(viewHolder.holderCheckBox.isChecked());
            viewHolder.holderCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    editor = sharedPreferences.edit();
                    Set<String> set;
                    set = sharedPreferences.getStringSet("listsettings", null);
                    if (set == null) set = new HashSet<>();
                    else {
                        editor.remove("listsettings");
                        editor.apply();
                        editor.commit();
                    }

                    if (isChecked) {
                        if (!set.contains(arrayList.get(position))) {
                            set.add(arrayList.get(position));
                        }

                    } else {
                        if (set.contains(arrayList.get(position))) {
                            for (String veri : set) {
                            }
                            set.remove(arrayList.get(position));

                        }
                    }

                    editor.putStringSet("listsettings", set);
                    editor.apply();
                    editor.commit();

                    for (String f : sharedPreferences.getStringSet("listsettings", new HashSet<String>())) {
                    }


                }
            });


            return convertView;
        }

        public class ViewHolder {
            public TextView holderTextView;
            public CheckBox holderCheckBox;
        }
    }

    /***************************************************************************************************/


}


