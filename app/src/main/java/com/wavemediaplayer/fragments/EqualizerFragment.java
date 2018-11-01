package com.wavemediaplayer.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.wavemediaplayer.visaulizer.VerticalSeekBar;
import com.wavemediaplayer.R;
import com.wavemediaplayer.visaulizer.VisualizerView;

import java.util.ArrayList;

import static com.wavemediaplayer.play.PlayMusic.mediaPlayer;


public class EqualizerFragment extends Fragment {
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;
    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;
    private View view;
    private ArrayList<SeekBar> seekBars;
    private Croller equalizerVolume,equalizerRL,equalizerBass;
    //    private TextView mStatusTextView;
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        view=inflater.inflate(R.layout.fragment_equalizer, parent, false);

        seekBars=new ArrayList<>();
//        create the equalizer with default priority of 0 & attach to our media player
        mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

//        set up visualizer and equalizer bars
        setupVisualizerFxAndUI();
        setupEqualizerFxAndUI();

        // enable the visualizer
        mVisualizer.setEnabled(true);

        // listen for when the music stream ends playing
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
//                disable the visualizer as it's no longer needed
                mVisualizer.setEnabled(false);
            }
        });


        clickListener();
        createCroller();
        return view;
    }

    private void createCroller(){
        final AudioManager audio = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        equalizerVolume=view.findViewById(R.id.equalizerVolume);
        equalizerVolume.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        equalizerVolume.setProgress(currentVolume);
        equalizerVolume.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                Log.e("qqqq",String.valueOf(progress));
            }
        });

//
//
//        AudioTrack m = (AudioTrack) view.getContext().getSystemService(Context.AUDIO_SERVICE);
//
//        m.setStereoVolume(leftVolume, rightVolume);

        equalizerRL=view.findViewById(R.id.equalizerRL);
        equalizerRL.setMax(100);
        equalizerRL.setProgress(50);
        equalizerRL.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                if(progress<50){
                    mediaPlayer.setVolume(progress/50f,50/50f);
                }else if(progress>50){
                   mediaPlayer.setVolume(50/50f,(50-(progress-50))/50f);
                }else{
                    mediaPlayer.setVolume(50/50f,50/50f);
                }
            }
        });


        equalizerBass=view.findViewById(R.id.equalizerBass);
        equalizerBass.setMax(1000);


        final BassBoost bassBoost = new BassBoost(1, mediaPlayer.getAudioSessionId());
        bassBoost.setEnabled(true);




        equalizerBass.setOnProgressChangedListener(new Croller.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                bassBoost.setStrength((short)progress);
            }
        });


    }

    private void clickListener(){

    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }



    /* shows spinner with list of equalizer presets to choose from
     - updates the seekBar progress and gain levels according
     to those of the selected preset*/
    private void equalizeSound() {
//        set up the spinner
        ArrayList<String> equalizerPresetNames = new ArrayList<>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter
                = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner equalizerPresetSpinner =view.findViewById(R.id.spinner);

//        get list of the device's equalizer presets
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }

        equalizerPresetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

//        handle the spinner item selections
        equalizerPresetSpinner.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                //first list item selected by default and sets the preset accordingly
                mEqualizer.usePreset((short) position);
//                get the number of frequency bands for this equalizer engine
                short numberFrequencyBands = mEqualizer.getNumberOfBands();
//                get the lower gain setting for this equalizer band
                final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

                short equalizerBandIndex;
//                set seekBar indicators according to selected preset
                for (short i = 0; i < numberFrequencyBands; i++) {
                    equalizerBandIndex = i;
                    SeekBar seekBar = seekBars.get(i);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
                    Log.e(String.valueOf(equalizerBandIndex),String.valueOf(mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel));
                    seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                not used
            }
        });
    }



    /* displays the SeekBar sliders for the supported equalizer frequency bands
     user can move sliders to change the frequency of the bands*/
    private void setupEqualizerFxAndUI() {
//        get reference to linear layout for the seekBars
        mLinearLayout =view.findViewById(R.id.linearLayoutEqual);
        mLinearLayout.setBackgroundColor(Color.RED);


//        equalizer heading
        TextView equalizerHeading = new TextView(view.getContext());
        String ss="'Equalizer'";
        equalizerHeading.setText(ss);
        equalizerHeading.setTextSize(20);
//        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
        mLinearLayout.addView(equalizerHeading);

//        get number frequency bands supported by the equalizer engine
        short numberFrequencyBands = mEqualizer.getNumberOfBands();

//        get the level ranges to be used in setting the band level
//        get lower limit of the range in milliBels
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//        get the upper limit of the range in millibels
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

        LinearLayout horizontal=new LinearLayout(view.getContext());
        horizontal.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        horizontal.setWeightSum(5f);
        horizontal.setLayoutParams(p1);
        horizontal.setBackgroundColor(Color.BLUE);

//        loop through all the equalizer bands to display the band headings, lower
//        & upper levels and the seek bars
        for (short i = 0; i < numberFrequencyBands; i++) {
            final short equalizerBandIndex = i;

//            frequency header for each seekBar
//            TextView frequencyHeaderTextview = new TextView(view.getContext());
//            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
            String s=String.valueOf((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000)) + " Hz";
//            frequencyHeaderTextview
//                    .setText(s);
//            horizontal.addView(frequencyHeaderTextview);

//            set up linear layout to contain each seekBar
            LinearLayout seekBarRowLayout = new LinearLayout(view.getContext());
            LinearLayout.LayoutParams paramssss = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            paramssss.weight=1f;
            seekBarRowLayout.setLayoutParams(paramssss);
            seekBarRowLayout.setBackgroundColor(Color.GRAY);
//            paramssss.setMargins(150, 0, 150, 0);


            seekBarRowLayout.setOrientation(LinearLayout.VERTICAL);
            seekBarRowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

//            set up lower level textview for this seekBar
            TextView lowerEqualizerBandLevelTextview = new TextView(view.getContext());
            lowerEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            String s1=String.valueOf((lowerEqualizerBandLevel / 100)) + " dB";
            lowerEqualizerBandLevelTextview.setText(s+"\n\n\n"+s1);
            lowerEqualizerBandLevelTextview.setGravity(Gravity.CENTER_HORIZONTAL);

//            set up upper level textview for this seekBar
            TextView upperEqualizerBandLevelTextview = new TextView(view.getContext());
            upperEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            String s2=String.valueOf((upperEqualizerBandLevel / 100)) + " dB";
            upperEqualizerBandLevelTextview.setText(s2);
            upperEqualizerBandLevelTextview.setGravity(Gravity.CENTER_HORIZONTAL);


            //            **********  the seekBar  **************
//            set the layout parameters for the seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    600);

//            create a new seekBar
            VerticalSeekBar seekBar = new VerticalSeekBar(view.getContext());
//            give the seekBar an ID
            seekBars.add(seekBar);
            seekBar.setId(i);

            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
//            set the progress for this seekBar
            seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex));

//            change progress as its changed by moving the sliders
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progress + lowerEqualizerBandLevel));
                    Log.e("naberrrr",String.valueOf(11));

                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    //not used
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    //not used
                }
            });

//            add the lower and upper band level textviews and the seekBar to the row layout
            seekBarRowLayout.addView(lowerEqualizerBandLevelTextview);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(upperEqualizerBandLevelTextview);
            horizontal.addView(seekBarRowLayout);

            //        show the spinner
            equalizeSound();
        }
        mLinearLayout.addView(horizontal);

    }

    /*displays the audio waveform*/
    private void setupVisualizerFxAndUI() {

        mLinearLayout =view.findViewById(R.id.linearLayoutVisual);
        // Create a VisualizerView to display the audio waveform for the current settings
        mVisualizerView = new VisualizerView(view.getContext());
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {

            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }



//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if (isFinishing() && mMediaPlayer != null) {
//            mVisualizer.release();
//            mEqualizer.release();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//    }
}
