package com.wavemediaplayer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class InitilationMediaPlayer {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MediaPlayer mp;
    private Equalizer mEqualizer;
    private ArrayList<String> equalizerPresetNames;

    public InitilationMediaPlayer(Context context){
        this.context=context;
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        equalizerPresetNames=new ArrayList<>();

    }

    public InitilationMediaPlayer init(MediaPlayer mp){
        if(mp!=null){
            this.mp=mp;
        }

        mEqualizer = new Equalizer(0, mp.getAudioSessionId());
        mEqualizer.setEnabled(true);
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }
        /**custom equlizer ayarları için*/
        equalizerPresetNames.add("Custom");
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

        short numberFrequencyBands = mEqualizer.getNumberOfBands();
        for(short i=0;i<numberFrequencyBands;i++){
            if(equalizerPresetNames.get(sharedPreferences.getInt("EqualizerPreset",0)).equals("Custom")){
                mEqualizer.setBandLevel(i, (short) (sharedPreferences.getInt("band"+String.valueOf(i),1500) + lowerEqualizerBandLevel));
            }else{

                mEqualizer.usePreset((short) sharedPreferences.getInt("EqualizerPreset",0));
                break;

            }
        }

        mEqualizer.setEnabled(true);
        return this;
    }
}
