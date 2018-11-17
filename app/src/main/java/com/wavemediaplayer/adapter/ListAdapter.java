package com.wavemediaplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wavemediaplayer.R;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;
import com.wavemediaplayer.playlist.PlayListData;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<PlayListData> {

    private ArrayList<PlayListData> baslik;
    private Context context;

    public ListAdapter(@NonNull Context context, int resource, ArrayList<PlayListData> baslik) {
        super(context, resource,baslik);
        this.baslik = baslik;
        this.context = context;


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

    private View getCustomView(int position,  View convertView, ViewGroup parent){
        ViewHolder holder = null;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.basic_list, null);
            holder = new ListAdapter.ViewHolder();

            holder.txt = convertView.findViewById(R.id.basic_txt);
            holder.layout = convertView.findViewById(R.id.basic_listview_layout);
            convertView.setTag(holder);
        }
        else {
            holder = (ListAdapter.ViewHolder) convertView.getTag();
        }

        if (OynatmaListesiFragment.oynat_list.get(position).getIsaretlendi()){
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.holo_gray_light));
        }
        else {
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }
        holder.txt.setText(baslik.get(position).getListBaslik());


        return convertView;

    }


    private class ViewHolder{
        TextView txt;
        LinearLayout layout;
    }
}
