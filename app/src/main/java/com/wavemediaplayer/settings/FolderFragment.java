package com.wavemediaplayer.settings;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.Adapter;
import com.wavemediaplayer.adapter.MusicData;
import com.yydcdut.sdlv.Menu;

import java.util.ArrayList;

public class FolderFragment extends Fragment {
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        view = inflater.inflate(R.layout.fragment_folder, parent, false);
        return view;
    }


}