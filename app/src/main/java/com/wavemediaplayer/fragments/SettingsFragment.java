package com.wavemediaplayer.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;

public class SettingsFragment extends DialogFragment {
    private TextView fragment_settings_foldersettings,fragment_settings_equalizer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container);
        fragment_settings_foldersettings=view.findViewById(R.id.fragment_settings_foldersettings);
        fragment_settings_equalizer=view.findViewById(R.id.fragment_settings_equalizer);


        fragment_settings_foldersettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).mainMenu.musiclist();
                dismiss();
            }
        });

        fragment_settings_equalizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).mainMenu.equalizer();
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        Window window = getDialog().getWindow();
        if(((MainActivity)getActivity()).isSwipeOpen)  window.setGravity(Gravity.TOP|Gravity.RIGHT);
        else window.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
        Log.e("qqqsettingfrag",String.valueOf(((MainActivity)getActivity()).isSwipeOpen));

        WindowManager.LayoutParams params = window.getAttributes();
        params.y = 100;
        window.setAttributes(params);

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyCustomAnimation;
        return dialog;
    }
}
