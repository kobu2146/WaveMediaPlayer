package com.wavemediaplayer.mfcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.AdapterDownload;

import java.util.ArrayList;

public class DownloadFragment extends Fragment {
    private ListView downloadListView;
    private ArrayList<String> links;
    private AdapterDownload adapterDownload;
    private EditText downloadEditText;
    private Button downloadSearch;


    public DownloadFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_download, container, false);
        downloadListView=view.findViewById(R.id.downloadListView);
        downloadEditText=view.findViewById(R.id.downloadEditText);
        downloadSearch=view.findViewById(R.id.downloadSearch);



        links=new ArrayList<>();
        links.add("111111");
        links.add("222222");
        links.add("3333333");
        adapterDownload=new AdapterDownload(view.getContext(),R.layout.fragment_download,links);
        downloadListView.setY(downloadListView.getY()+MainActivity.tabsHeigh);
        downloadListView.setAdapter(adapterDownload);

        createListener();
        return view;
    }

    private void createListener(){
        downloadSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getText=downloadEditText.getText().toString();
            }
        });
    }







































}