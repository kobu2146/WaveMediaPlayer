package com.wavemediaplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.wavemediaplayer.R;

import java.util.ArrayList;

public class AdapterDownload extends ArrayAdapter<String> {
    private ArrayList<String> links;

    public AdapterDownload(@NonNull Context context, int resource, ArrayList<String> links) {
        super(context, resource, links);
        this.links=links;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=convertView;
        if(view==null){
            view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_downloaditem,parent,false);
            ImageView downloadPlay=view.findViewById(R.id.downloadPlay);
            downloadPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });


            ImageView downloadDownload=view.findViewById(R.id.downloadDownload);
            downloadDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });


        }
        return view;
    }
}
