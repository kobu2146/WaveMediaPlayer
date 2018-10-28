package com.wavemediaplayer.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.visaulizer.VisualizerView;

import java.util.ArrayList;

import static com.wavemediaplayer.MainActivity.mediaPlayer;


public class EqualizerFragment extends Fragment {
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;
    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;
    private View view;
    private ArrayList<SeekBar> seekBars;
    private Button equalizerHideFragment;
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
        return view;
    }

    private void clickListener(){
        equalizerHideFragment=view.findViewById(R.id.equalizerHideFragment);
        equalizerHideFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.hide(EqualizerFragment.this);
                ((MainActivity)getActivity()).mainFrame.setBackgroundColor(Color.TRANSPARENT);
                ft.commit();
            }
        });
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
        mLinearLayout.addView(equalizerHeading);

//        get number frequency bands supported by the equalizer engine
        short numberFrequencyBands = mEqualizer.getNumberOfBands();

//        get the level ranges to be used in setting the band level
//        get lower limit of the range in milliBels
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//        get the upper limit of the range in millibels
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

//        loop through all the equalizer bands to display the band headings, lower
//        & upper levels and the seek bars
        for (short i = 0; i < numberFrequencyBands; i++) {
            final short equalizerBandIndex = i;

//            frequency header for each seekBar
            TextView frequencyHeaderTextview = new TextView(view.getContext());
            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
            String s=String.valueOf((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000)) + " Hz";
            frequencyHeaderTextview
                    .setText(s);
            mLinearLayout.addView(frequencyHeaderTextview);

//            set up linear layout to contain each seekBar
            LinearLayout seekBarRowLayout = new LinearLayout(view.getContext());
            seekBarRowLayout.setOrientation(LinearLayout.VERTICAL);

//            set up lower level textview for this seekBar
            TextView lowerEqualizerBandLevelTextview = new TextView(view.getContext());
            lowerEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            String s1=String.valueOf((lowerEqualizerBandLevel / 100)) + " dB";
            lowerEqualizerBandLevelTextview.setText(s1);
//            set up upper level textview for this seekBar
            TextView upperEqualizerBandLevelTextview = new TextView(view.getContext());
            upperEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            String s2=String.valueOf((upperEqualizerBandLevel / 100)) + " dB";
            upperEqualizerBandLevelTextview.setText(s2);

            //            **********  the seekBar  **************
//            set the layout parameters for the seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    500);

//            create a new seekBar
            SeekBar seekBar = new SeekBar(view.getContext());
            seekBar.setRotation(270);
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

            mLinearLayout.addView(seekBarRowLayout);

            //        show the spinner
            equalizeSound();
        }
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
