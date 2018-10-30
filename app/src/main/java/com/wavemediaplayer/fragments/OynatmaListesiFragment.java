package com.wavemediaplayer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.AdapterPlayList;
import com.wavemediaplayer.main.FPlayListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class OynatmaListesiFragment extends Fragment {


    ListView oynatma_listesi;
    ArrayList<String> oynat_list = new ArrayList<>();
    ArrayList<String>  title_list = new ArrayList<>();
    ArrayList<String> artist_list = new ArrayList<>();
    ArrayList<Integer> thumb_list = new ArrayList<>();
    ArrayList<String> location_list = new ArrayList<>();


    FPlayListener fPlayListener;
    Context context;
    // Tum oynatma listesini gpruntulemek icin
    private boolean isList = true;


    public OynatmaListesiFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_oynatma_listesi, container, false);
        oynatma_listesi = view.findViewById(R.id.oynatma_listesi);
        context = getActivity();
        fPlayListener = new FPlayListener(MainActivity.context,MainActivity.mainView);

        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences( "WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);

        /** tum playlistleri ve iceriklerini cekiyor cekiyor */
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.e("aaaa","aasdsadasda");
            Log.e("map values", entry.getKey() + ": " + entry.getValue().toString());
            oynat_list.add(entry.getKey());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, oynat_list);
        oynatma_listesi.setAdapter(adapter);

        clickEvent();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){




    }

    private void clickEvent(){

        oynatma_listesi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isList){
                    SharedPreferences sharedPreferences  = context.getSharedPreferences("WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);
                    Map<String, ?> allEntries = sharedPreferences.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        try {
                            if (entry.getKey().equals(oynat_list.get(position))){
                                JSONArray jsonArray = new JSONArray(entry.getValue().toString());
                                for (int i = 0;i<jsonArray.length();i++){


                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String title = jsonObject.getString("title");
                                    Log.e("title",title);
                                    String artist = jsonObject.getString("artist");
                                    Log.e("artist",artist);
                                    int thumbnail = jsonObject.getInt("thumbnail");
                                    Log.e("thumbnail",""+thumbnail);
                                    String location = jsonObject.getString("location");
                                    Log.e("location",location);

                                    title_list.add(title);
                                    artist_list.add(artist);
                                    thumb_list.add(thumbnail);
                                    location_list.add(location);


                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    AdapterPlayList adapterPlayList = new AdapterPlayList(context,R.layout.custom_list_item,title_list,artist_list,thumb_list,location_list);
                    oynatma_listesi.setAdapter(adapterPlayList);
                    isList = false;
                }

                else {
                    Log.e("music","caliyor");
                    fPlayListener.playFromPlayList(location_list.get(position));
                }

            }
        });
    }





}
