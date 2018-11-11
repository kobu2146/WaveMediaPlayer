package com.wavemediaplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wavemediaplayer.R;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> baslik;
    private Context context;

    public ListAdapter(@NonNull Context context, int resource, ArrayList<String> baslik) {
        super(context, resource,baslik);
        this.baslik = baslik;
        this.context = context;

        for (String b : baslik){
            Log.e("bbb",b);
        }
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
            convertView.setTag(holder);
        }
        else {
            holder = (ListAdapter.ViewHolder) convertView.getTag();
        }

        holder.txt.setText(baslik.get(position));
        Log.e("baslik,",baslik.get(position));

        return convertView;

    }


    private class ViewHolder{
        TextView txt;
    }
}
