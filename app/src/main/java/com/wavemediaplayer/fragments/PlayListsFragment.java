package com.wavemediaplayer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.playlist.CreatePlayList;

import java.util.ArrayList;
import java.util.Map;

public class PlayListsFragment extends DialogFragment {


    /**
     *
     * option menudeki add playlist kısmı icin
     * AÇılır bir dialog fragment acılacak ve play listeleri gosterilecek
     * */
    ListView playListView;
    ArrayList<String> playLists = new ArrayList<>();

    ArrayList<Integer> music_position = new ArrayList<>();

    Button btn_add;
    EditText edit_playlist_name;

    Context context;

    public PlayListsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow()!=null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        if(dialog.getWindow()!=null){
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        dialog.setTitle("Add");
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_play_lists,container);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

       playListView = view.findViewById(R.id.playListView);
       btn_add = view.findViewById(R.id.btn_add);
       edit_playlist_name = view.findViewById(R.id.edit_playlist_name);
       context = view.getContext();

        SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences( "WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);


        /** tum playlistleri ve iceriklerini cekiyor cekiyor */
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            playLists.add(entry.getKey());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, playLists);
        playListView.setAdapter(adapter);

        clickEvent();
    }

    private void clickEvent(){
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String list_name = edit_playlist_name.getText().toString();
                if (!list_name.equals("")){
                    new CreatePlayList(MainActivity.context).createAndAddList(list_name,music_position);
                    SharedPreferences sharedPreferences = MainActivity.context.getSharedPreferences( "WAVE MUSIC PLAYLIST", Context.MODE_PRIVATE);
                    String s =  sharedPreferences.getString(list_name,null);
                }
                else {
                    Toast.makeText(context,"Please, enter a title",Toast.LENGTH_LONG).show();
                }
                klavyeDisable();
                dismiss();
            }
        });

        /** Listviiew deki elemana tıklandıgında yen geleln itemlleri o listview iceriisine atıyor */
        playListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new CreatePlayList(MainActivity.context).createAndAddList(playLists.get(position),music_position);
                Toast.makeText(MainActivity.context,"Music added to "+playLists.get(position),Toast.LENGTH_SHORT).show();
                dismiss();

            }
        });
    }

    public void setList(ArrayList<Integer> tempList){
        music_position = tempList;
    }
    private void klavyeDisable(){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_playlist_name.getApplicationWindowToken(), 0);
    }




}
