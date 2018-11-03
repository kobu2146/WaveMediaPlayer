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

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<MusicData> {

    private Context context;


    private ArrayList<MusicData> musicData;
    private int resource;
    public Adapter(Context context, int resource, ArrayList<MusicData> musicData) {
        super(context, resource, musicData);
        this.context = context;
        this.musicData = musicData;
        this.resource = resource;
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

    public View getCustomView(int position,  View convertView, ViewGroup parent) {


        ViewHolder holder = null;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(resource, null);
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

        holder.title.setText(musicData.get(position).getTitles());
        holder.image.setImageResource(musicData.get(position).getImages());
        holder.artist.setText(musicData.get(position).getArtist());
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
                MainActivity.musicListView.startDrag(((Integer) o).intValue());
            }
            return false;
        }
    };
}
