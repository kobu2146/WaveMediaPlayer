package com.wavemediaplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wavemediaplayer.R;

import java.util.ArrayList;

public class AdapterPlayList extends ArrayAdapter<String>{


    private Context context;
    private ArrayList<String> title;
    private ArrayList<String> artist;
    private ArrayList<String> duration;
    private ArrayList<Integer> thumbnail;

    public AdapterPlayList(Context context, int resource, ArrayList<String> title, ArrayList<String> artist, ArrayList<Integer> thumbnail,ArrayList<String> duration) {
        super(context, resource, artist);
        this.context = context;
        this.title = title;
        this.artist = artist;
        this.thumbnail = thumbnail;
        this.duration = duration;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        return getCustomView(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position,  View convertView, ViewGroup parent) {


        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View myList = inflater.inflate(R.layout.custom_list_item, parent, false);
        holder = new ViewHolder();

        holder.title = myList.findViewById(R.id.music_title);

        holder.image = myList.findViewById(R.id.music_image);
        holder.artist = myList.findViewById(R.id.music_artist);





        holder.title.setText(title.get(position));
        holder.image.setImageResource(thumbnail.get(position));
        holder.artist.setText(artist.get(position));





        myList.setTag(holder);

        return myList;

    }

    private class ViewHolder {
        ImageView image;
        TextView title;
        TextView artist;


    }

}

