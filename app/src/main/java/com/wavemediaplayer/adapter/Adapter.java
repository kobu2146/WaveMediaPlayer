package com.wavemediaplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class Adapter extends ArrayAdapter<MusicData> {

    private Context context;
    private ArrayList<MusicData> musicData;
    private int kaynak;

    public Adapter(Context context, int resource, ArrayList<MusicData> musicData,int kaynak) {
        super(context, resource, musicData);
        this.context = context;
        this.musicData = musicData;
        this.kaynak = kaynak;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // TODO Auto-generated method stub

        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView,@NonNull ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position,  View convertView, ViewGroup parent) {


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
                Log.e("Secilen item",o.toString());
                if (kaynak == 0){
                    MainActivity.musicListView.startDrag(((Integer) o).intValue());
                }
                else if (kaynak == 1){
                    OynatmaListesiFragment.oynatma_listesi.startDrag(((Integer) o).intValue());
                }

            }
            return false;
        }
    };
}
