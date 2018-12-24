package com.wavemediaplayer.adapter.jamendo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wavemediaplayer.R;
import com.wavemediaplayer.jamendo.data.Album;
import com.wavemediaplayer.settings.ConvertToImage;

import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter<Album> {

    Context context;
    ArrayList<Album> albums;
    public AlbumAdapter(@NonNull Context context, int resource,ArrayList<Album> albums) {
        super(context, resource,albums);
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid, null);
            holder = new ViewHolder();

            holder.image = convertView.findViewById(R.id.grid_img);
            holder.text = convertView.findViewById(R.id.grid_text);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // internetten gelen image yi set edecez
        Picasso.get().load(albums.get(position).getSongsImage()).into(holder.image);
        holder.text.setText(albums.get(position).getAlbum_name());

        return convertView;

    }


    class ViewHolder {
        ImageView image;
        TextView text;
    }
}
