package com.wavemediaplayer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import static com.wavemediaplayer.play.PlayMusic.mediaPlayer;

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


        setBalance();
        setBass();

        /**Bass ayarlarını al*/


        return this;
    }

    private void setBass(){
        int progress=sharedPreferences.getInt("bass",50);
        BassBoost bassBoost = new BassBoost(1, mediaPlayer.getAudioSessionId());
        bassBoost.setEnabled(true);
        bassBoost.setStrength((short)progress);

    }

    /**balance ayarlarını al yani ses dengesini başlangıçta yapılandır*/
    private void setBalance(){
        int progress=sharedPreferences.getInt("balance",50);
        if(progress<49){
            Log.e(String.valueOf((float)progress/50f),String.valueOf(1f-(float)progress/50f));
            mediaPlayer.setVolume((float)progress/50f,1f);
        }else if(progress>51){
            Log.e("1",String.valueOf((50f-((float)progress-50f))/50f));
            mediaPlayer.setVolume(1f,(50f-((float)progress-50f))/50f);
        }else{
            mediaPlayer.setVolume(1f,1f);
        }
    }

}
