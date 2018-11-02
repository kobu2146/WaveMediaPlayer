package com.wavemediaplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;

import java.util.ArrayList;

/**
 * Playlist icin olusturulan list adapter
 *
 * */
public class AdapterPlayList extends ArrayAdapter<MusicData>{


    private Context context;
    private ArrayList<MusicData> music_data;


    public AdapterPlayList(Context context, int resource, ArrayList<MusicData> music_data) {
        super(context, resource, music_data);
        this.context = context;
        this.music_data = music_data;


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

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_list_item, null);
            holder = new ViewHolder();

            holder.title = convertView.findViewById(R.id.music_title);
            holder.image = convertView.findViewById(R.id.music_image);
            holder.artist = convertView.findViewById(R.id.music_artist);
            holder.image_logo = convertView.findViewById(R.id.music_logo);
            holder.image_logo.setOnTouchListener(mOnTouchListener);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(music_data.get(position).getTitles());
        holder.image.setImageResource(music_data.get(position).getImages());
        holder.artist.setText(music_data.get(position).getArtist());
        holder.image_logo.setImageDrawable(Utils.getDrawable(context,R.drawable.ic_reorder_grey_500_24dp));
        holder.image_logo.setTag(Integer.parseInt(position + ""));

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        ImageView image_logo;
        TextView title;
        TextView artist;

    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Object o = v.getTag();

            if (o != null && o instanceof Integer) {
                Log.e("kkk",o.toString());
                OynatmaListesiFragment.oynatma_listesi.startDrag(((Integer) o).intValue());
            }
            return false;
        }
    };

}

