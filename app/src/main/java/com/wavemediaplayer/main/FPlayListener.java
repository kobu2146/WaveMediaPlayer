package com.wavemediaplayer.main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.adapter.Utils;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;
import com.wavemediaplayer.fragments.SettingsFragment;
import com.wavemediaplayer.play.PlayMusic;

public class FPlayListener {

    public static ImageButton play;
    public static ImageButton pause;
    public static ImageButton play_main;
    public static ImageButton pause_main;
    public static TextView song_title;
    public static TextView song_artis;
    public static int currentMusicPosition; //Sıralı calmada tutulacak pozisyon
    public static boolean calmaListesiMuzik = false; //Music calma listesinden mi ana listeden mi?
    public PlayMusic pl;
    public MainActivity mainActivity;
    private ImageButton like;
    private ImageButton notlike;
    private ImageButton dislike;
    private ImageButton notdislike;
    private ImageButton song_next;
    private ImageButton song_prev;
    private ImageButton tekrarla;
    private ImageButton karisik_cal;
    private ImageView sample_main_settings;
    private SeekBar myseekbar;
    private TextView mytext1;
    private TextView mytext2;
    private Handler handler;
    private Context context;
    private View view;

    public FPlayListener(MainActivity mainActivity, View view) {
        this.mainActivity = mainActivity;
        this.context = MainActivity.context;
        this.view = view;


        init();
    }


    private void init() {
        like = (ImageButton) view.findViewById(R.id.sample_main_imageButton2);
        notlike = (ImageButton) view.findViewById(R.id.sample_main_imageButton2new);
        dislike = (ImageButton) view.findViewById(R.id.sample_main_button);
        notdislike = (ImageButton) view.findViewById(R.id.sample_main_buttontwo);
        play = (ImageButton) view.findViewById(R.id.sample_main_play_button);
        pause = (ImageButton) view.findViewById(R.id.sample_main_pause_button);
        play_main = (ImageButton) view.findViewById(R.id.sample_main_play_button_main);
        pause_main = (ImageButton) view.findViewById(R.id.sample_main_pause_button_main);
        song_next = (ImageButton) view.findViewById(R.id.sapmle_next);
        song_prev = (ImageButton) view.findViewById(R.id.sapmle_prev);
        karisik_cal = (ImageButton) view.findViewById(R.id.sample_karisik_cal);
        tekrarla = (ImageButton) view.findViewById(R.id.sample_tekrarla);
        song_title = view.findViewById(R.id.songs_title);
        song_artis = view.findViewById(R.id.songs_artist_name);
        sample_main_settings = view.findViewById(R.id.sample_main_settings);

        mytext1 = view.findViewById(R.id.sample_main_StartTime);
        mytext2 = view.findViewById(R.id.sample_main_endTime);
        myseekbar = view.findViewById(R.id.sample_main_seekBar3);
        handler = new Handler();

        if (PlayMusic.tekrarla == 0) {
            tekrarla.setBackground(Utils.getDrawable(context, R.drawable.baseline_repeat_white));
        } else if (PlayMusic.tekrarla == 1) {
            tekrarla.setBackground(Utils.getDrawable(context, R.drawable.svg_repeat_one));
        } else if (PlayMusic.tekrarla == 2) {
            tekrarla.setBackground(Utils.getDrawable(context, R.drawable.svg_liste_finish));
        }

        if (PlayMusic.karisikCal) {
            karisik_cal.setBackground(Utils.getDrawable(context, R.drawable.baseline_shuffle_white));
        } else {
            karisik_cal.setBackground(Utils.getDrawable(context, R.drawable.svg_sirali));
        }


        pl = new PlayMusic(mainActivity, myseekbar, mytext1, mytext2, play, handler);
    }


    public void playFromPlayList(String link) {
        /** Music play */
        pl.playMusic(link);

        calmaListesiMuzik = true;
        // play tab on screen
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        if (play_main.getVisibility() == View.VISIBLE) {
            play_main.setVisibility(View.GONE);
            pause_main.setVisibility(View.VISIBLE);
        }

        // main play button
        play_main.setVisibility(View.GONE);
        pause_main.setVisibility(View.VISIBLE);
        if (play.getVisibility() == View.VISIBLE) {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    }


    public void playMusic(int position) {
        song_title.setText(MusicList.musicData.get(position).getTitles());
        song_artis.setText(MusicList.musicData.get(position).getArtist());
        pl.playMusic(MusicList.musicData.get(position).getLocation());


        // play tab on screen
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        if (play_main.getVisibility() == View.VISIBLE) {
            play_main.setVisibility(View.GONE);
            pause_main.setVisibility(View.VISIBLE);
        }

        // main play button
        play_main.setVisibility(View.GONE);
        pause_main.setVisibility(View.VISIBLE);
        if (play.getVisibility() == View.VISIBLE) {
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;
            }
        }
        return false;
    }

    public void icerikDegistirme() {
        if (!MusicList.musicData.isEmpty()) {
            if (!calmaListesiMuzik) {
                song_title.setText(MusicList.musicData.get(currentMusicPosition).getTitles());
                song_artis.setText(MusicList.musicData.get(currentMusicPosition).getArtist());
            } else {
                song_title.setText(OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition).getTitles());
                song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition).getArtist());
            }
        }
    }

    public void f_ListenerEvent() {

        sample_main_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                DialogFragment dialogFragment = new SettingsFragment();
                dialogFragment.show(fragmentTransaction, "SettingsFragment");
            }
        });

        karisik_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayMusic.karisikCal) {
                    PlayMusic.karisikCal = false;
                    karisik_cal.setBackground(Utils.getDrawable(context, R.drawable.svg_sirali));
                } else {
                    PlayMusic.karisikCal = true;
                    karisik_cal.setBackground(Utils.getDrawable(context, R.drawable.baseline_shuffle_white));
                }
                SharedPreferences sharedPreferences;
                SharedPreferences.Editor editor;
                sharedPreferences = context.getSharedPreferences(MainActivity.KARISIK_CAL, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("karisik", PlayMusic.karisikCal);
                editor.apply();
            }
        });

        tekrarla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayMusic.tekrarla == 0) {
                    PlayMusic.tekrarla = 1;
                    tekrarla.setBackground(Utils.getDrawable(context, R.drawable.svg_repeat_one));
                } else if (PlayMusic.tekrarla == 1) {
                    PlayMusic.tekrarla = 2;
                    tekrarla.setBackground(Utils.getDrawable(context, R.drawable.svg_liste_finish));
                } else {
                    PlayMusic.tekrarla = 0;
                    tekrarla.setBackground(Utils.getDrawable(context, R.drawable.baseline_repeat_white));
                }


                SharedPreferences sharedPreferences;
                SharedPreferences.Editor editor;
                sharedPreferences = context.getSharedPreferences(MainActivity.SARKIYI_TEKRARLA, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putInt("tekrarla", PlayMusic.tekrarla);

                editor.apply();
            }
        });

        song_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayMusic.tekrarla == 1) {
                    PlayMusic.tekrarla = 3;
                }

                pl.calmayaDevamEt(true);
            }
        });

        song_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayMusic.tekrarla == 1) {
                    PlayMusic.tekrarla = 3;
                }
                pl.calmayaDevamEt(false);
            }
        });


        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notlike.setVisibility(View.VISIBLE);
                if (notdislike.getVisibility() == View.VISIBLE) {
                    notdislike.setVisibility(View.GONE);
                }
            }
        });

        notlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notlike.setVisibility(View.GONE);
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notdislike.setVisibility(View.VISIBLE);
                if (notlike.getVisibility() == View.VISIBLE) {
                    notlike.setVisibility(View.GONE);
                }
            }
        });

        notdislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notdislike.setVisibility(View.GONE);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!calmaListesiMuzik) {
                    if (MusicList.musicData.size() == 0) {
                        return;
                    }
                    PlayMusic.prevMusicDAta = MusicList.musicData.get(currentMusicPosition);
                    song_title.setText(MusicList.musicData.get(currentMusicPosition).getTitles());
                    song_artis.setText(MusicList.musicData.get(currentMusicPosition).getArtist());
                    pl.playMusic(MusicList.musicData.get(currentMusicPosition).getLocation());
                    if (mainActivity.s != null)
                        mainActivity.s.listeDegistir(MusicList.musicData, currentMusicPosition);

                } else {
                    if (OynatmaListesiFragment.music_oynat_list.size() == 0) {
                        return;
                    }
                    PlayMusic.prevMusicDAta = OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition);
                    song_title.setText(OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition).getTitles());
                    song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition).getArtist());
                    pl.playMusic(OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition).getLocation());
                    if (mainActivity.s != null)
                        mainActivity.s.listeDegistir(OynatmaListesiFragment.music_oynat_list, currentMusicPosition);

                }


                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                if (mainActivity.s != null) mainActivity.s.activityPlay();
                if (play_main.getVisibility() == View.VISIBLE) {
                    play_main.setVisibility(View.GONE);
                    pause_main.setVisibility(View.VISIBLE);
                }

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pl.pauseMusic();
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                if (pause_main.getVisibility() == View.VISIBLE) {
                    pause_main.setVisibility(View.GONE);
                    play_main.setVisibility(View.VISIBLE);
                    if (mainActivity.s != null) mainActivity.s.activityPause();

                }
            }
        });

        play_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!calmaListesiMuzik) {
                    if (MusicList.musicData.size() == 0) {
                        return;
                    }
                    PlayMusic.prevMusicDAta = MusicList.musicData.get(currentMusicPosition);
                    song_title.setText(MusicList.musicData.get(currentMusicPosition).getTitles());
                    song_artis.setText(MusicList.musicData.get(currentMusicPosition).getArtist());
                    pl.playMusic(MusicList.musicData.get(currentMusicPosition).getLocation());
                    if (mainActivity.s != null)
                        mainActivity.s.listeDegistir(MusicList.musicData, currentMusicPosition);
                } else {
                    if (OynatmaListesiFragment.music_oynat_list.size() == 0) {
                        return;
                    }
                    PlayMusic.prevMusicDAta = OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition);
                    song_title.setText(OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition).getTitles());
                    song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition).getArtist());
                    pl.playMusic(OynatmaListesiFragment.music_oynat_list.get(currentMusicPosition).getLocation());
                    if (mainActivity.s != null)
                        mainActivity.s.listeDegistir(OynatmaListesiFragment.music_oynat_list, currentMusicPosition);
                }


                play_main.setVisibility(View.GONE);
                pause_main.setVisibility(View.VISIBLE);
                if (mainActivity.s != null) mainActivity.s.activityPlay();

                if (play.getVisibility() == View.VISIBLE) {
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);

                }
            }
        });

        pause_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pl.pauseMusic();
                pause_main.setVisibility(View.GONE);
                play_main.setVisibility(View.VISIBLE);
                if (pause.getVisibility() == View.VISIBLE) {
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                    if (mainActivity.s != null) mainActivity.s.activityPause();
                }
            }
        });


    }
}
