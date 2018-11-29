package com.wavemediaplayer.play;

import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.adapter.MusicData;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;
import com.wavemediaplayer.main.FPlayListener;
import com.wavemediaplayer.mservices.NotificationService;
import com.wavemediaplayer.settings.InitilationMediaPlayer;

import java.io.File;
import java.util.Random;

/**
 * Gonderilen dosya linkine gore muzik calma islemi
 */
public class PlayMusic {
    public static boolean karisikCal = true;
    public static int tekrarla = 0;
    public static MediaPlayer mediaPlayer;
    public static MusicData prevMusicDAta;
    public static boolean isMediaPlayerCreated = false;
    private static Runnable runnable;
    private static Handler myHandler;
    private static String playPrev = "";
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mediaPlayer != null && fromUser) {

                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
    private Context context;
    private SeekBar myseekbar;
    private TextView mytext1;
    private TextView mytext2;
    private ImageView myimageview;
    private Handler handler;
    private BassBoost bassBoost;
    private InitilationMediaPlayer initilationMediaPlayer;
    private MainActivity mainActivity;

    public PlayMusic(MainActivity mainActivity, SeekBar myseekbar, TextView mytext1, TextView mytext2, ImageView myimageview, Handler handler) {
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        this.myseekbar = myseekbar;
        this.mytext1 = mytext1;
        this.mytext2 = mytext2;
        this.myimageview = myimageview;
        this.handler = handler;
    }

    /**
     * Dosya var mı yok mu belirtilecek varsa calmaya baslar
     */
    public void playMusic(String link) {
        File file = new File(link);
        try {
            isMediaPlayerCreated = true;
            if (isMyServiceRunning(NotificationService.class)) {
                mediaPlayer = NotificationService.mediaPlayer;
            }
            if (file.exists()) {
                if (!playPrev.equals(link)) {
                    playPrev = link;
                    stopPlaying();
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                    }

                    /**initilation mediaplyer sharedtteki ayarları alıp mediaplayere entegre ediyorumki equalizer açıldığında yeni ayarlar orda aktifleşiyordu bullshit*/
                    if (initilationMediaPlayer == null)
                        initilationMediaPlayer = new InitilationMediaPlayer(context).init(mediaPlayer);
                    bassBoost = new BassBoost(1, mediaPlayer.getAudioSessionId());
                    bassBoost.setEnabled(true);
                    BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
                    BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
                    bassBoostSetting.strength = 1000;
                    bassBoost.setStrength((short) 1000);
                    bassBoost.setProperties(bassBoostSetting);
                    mediaPlayer.setAuxEffectSendLevel(1.0f);
                    mediaPlayer.attachAuxEffect(bassBoost.getId());
                    mediaPlayer.setDataSource(context, Uri.parse(link));
                    mediaPlayer.prepareAsync();
//                    mediaPlayer=MediaPlayer.create(context,Uri.parse(link));

                } else {
                    if (mediaPlayer != null) {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        } else {
                            mediaPlayer.start();
                        }
                    }
                }
                seekBarChange();
                if (isMyServiceRunning(NotificationService.class)) {
                    NotificationService.mediaPlayer = mediaPlayer;
                }
            } else {
                Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show();
                calmayaDevamEt(true);
            }
        } catch (Exception ex) {
        }
    }

    public void calmayaDevamEt(boolean ileriCal) {
        if (!karisikCal) { // Sıralı calma aktifse
            if (!FPlayListener.calmaListesiMuzik) {//Ana playerdan calınacaksa
                if (MusicList.musicData.size() == 0) return;
                if (tekrarla == 0 || tekrarla == 2) {
                    if (ileriCal) {
                        FPlayListener.currentMusicPosition++;
                    } else {
                        if (FPlayListener.currentMusicPosition != 0) {
                            FPlayListener.currentMusicPosition--;
                        } else {
                            FPlayListener.currentMusicPosition = MusicList.musicData.size() -1;
                        }
                    }
                }
                if (FPlayListener.currentMusicPosition < MusicList.musicData.size()) {
                    prevMusicDAta = MusicList.musicData.get(FPlayListener.currentMusicPosition);
                    MainActivity.fPlayListener.song_artis.setText(MusicList.musicData.get(FPlayListener.currentMusicPosition).getArtist());
                    MainActivity.fPlayListener.song_title.setText(MusicList.musicData.get(FPlayListener.currentMusicPosition).getTitles());
                    MainActivity.fPlayListener.play.setVisibility(View.GONE);
                    MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
                    MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
                    MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
                    playMusic(MusicList.musicData.get(FPlayListener.currentMusicPosition).getLocation());
                    if (mainActivity.s != null)
                        mainActivity.s.listeDegistir(MusicList.musicData, FPlayListener.currentMusicPosition);
                } else {
                    if (tekrarla == 0) {
                        FPlayListener.currentMusicPosition = 0;
                        prevMusicDAta = MusicList.musicData.get(FPlayListener.currentMusicPosition);
                        MainActivity.fPlayListener.song_artis.setText(MusicList.musicData.get(FPlayListener.currentMusicPosition).getArtist());
                        MainActivity.fPlayListener.song_title.setText(MusicList.musicData.get(FPlayListener.currentMusicPosition).getTitles());
                        MainActivity.fPlayListener.play.setVisibility(View.GONE);
                        MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
                        MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
                        MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
                        playMusic(MusicList.musicData.get(FPlayListener.currentMusicPosition).getLocation());
                        if (mainActivity.s != null)
                            mainActivity.s.listeDegistir(MusicList.musicData, FPlayListener.currentMusicPosition);
                    }

                }
            } else { //Playlistden veya baska biryerden ise
                if (OynatmaListesiFragment.music_oynat_list.size() == 0) return;

                if (tekrarla == 0 || tekrarla == 2) {
                    if (ileriCal) {
                        FPlayListener.currentMusicPosition++;
                    } else {
                        if (FPlayListener.currentMusicPosition != 0) {
                            FPlayListener.currentMusicPosition--;
                        } else {
                            FPlayListener.currentMusicPosition = OynatmaListesiFragment.music_oynat_list.size() -1;
                        }
                    }
                }
                if (OynatmaListesiFragment.music_oynat_list.size() > FPlayListener.currentMusicPosition) {
                    prevMusicDAta = OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition);
                    MainActivity.fPlayListener.song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getArtist());
                    MainActivity.fPlayListener.song_title.setText(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getTitles());
                    MainActivity.fPlayListener.play.setVisibility(View.GONE);
                    MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
                    MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
                    MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
                    MainActivity.fPlayListener.playFromPlayList(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getLocation());
                    if (mainActivity.s != null)
                        mainActivity.s.listeDegistir(OynatmaListesiFragment.music_oynat_list, FPlayListener.currentMusicPosition);
                } else {
                    if (tekrarla == 0) {
                        FPlayListener.currentMusicPosition = 0;
                        prevMusicDAta = OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition);
                        MainActivity.fPlayListener.song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getArtist());
                        MainActivity.fPlayListener.song_title.setText(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getTitles());
                        MainActivity.fPlayListener.play.setVisibility(View.GONE);
                        MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
                        MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
                        MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
                        MainActivity.fPlayListener.playFromPlayList(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getLocation());
                        if (mainActivity.s != null)
                            mainActivity.s.listeDegistir(OynatmaListesiFragment.music_oynat_list, FPlayListener.currentMusicPosition);
                    }
                }
            }
        }
        // Karısık sarkı calma
        else {
            // ana music playerdan karısık calma
            if (!FPlayListener.calmaListesiMuzik) {
                if (MusicList.musicData.size() == 0) return;
                int rndPositin = new Random().nextInt(MusicList.musicData.size());
                if (tekrarla == 0 || tekrarla == 2 || tekrarla == 3) {
                    if (ileriCal) {
                        if (NotificationService.kaldirilanPos.size() > 0) {
                            rndPositin = NotificationService.kaldirilanPos.get(NotificationService.kaldirilanPos.size() - 1);
                            NotificationService.kaldirilanPos.remove(NotificationService.kaldirilanPos.size() - 1);
                            if (rndPositin < 0){
                                NotificationService.kaldirilanPos.clear();
                                rndPositin = new Random().nextInt(MusicList.musicData.size());
                            }
                        } else {
                            rndPositin = new Random().nextInt(MusicList.musicData.size());
                            NotificationService.mainListeOncekiPos.add(rndPositin);
                        }
                    } else {
                        if (NotificationService.mainListeOncekiPos.size() > 0) {
                            NotificationService.kaldirilanPos.add(NotificationService.mainListeOncekiPos.size() - 1);
                            NotificationService.mainListeOncekiPos.remove(NotificationService.mainListeOncekiPos.size() - 1);
                            try {
                                rndPositin = NotificationService.mainListeOncekiPos.get(NotificationService.mainListeOncekiPos.size() - 1);
                            } catch (IndexOutOfBoundsException ex) {
                                Log.e("HATA", ex.getMessage());
                                rndPositin = new Random().nextInt(MusicList.musicData.size());
                            }
                        }
                        else {
                            NotificationService.kaldirilanPos.clear();
                        }
                    }
                    //İleri butonuna tıklandıgı zaman gecerli sarkıyı tekrarlada ise bir sonraki sarkıya atlattrırıp tekrala = 1 olacak
                    if (tekrarla == 3) {
                        tekrarla = 1;
                    }

                    FPlayListener.currentMusicPosition = rndPositin;
                    if (rndPositin <= MusicList.musicData.size()) {
                        prevMusicDAta = MusicList.musicData.get(rndPositin);
                        MainActivity.fPlayListener.song_artis.setText(MusicList.musicData.get(rndPositin).getArtist());
                        MainActivity.fPlayListener.song_title.setText(MusicList.musicData.get(rndPositin).getTitles());
                        MainActivity.fPlayListener.play.setVisibility(View.GONE);
                        MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
                        MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
                        MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
                        playMusic(MusicList.musicData.get(rndPositin).getLocation());
                        if (mainActivity.s != null)
                            mainActivity.s.listeDegistir(MusicList.musicData, FPlayListener.currentMusicPosition);
                    }


                } else if (tekrarla == 1) {
                    if (prevMusicDAta != null) {
                        MainActivity.fPlayListener.song_artis.setText(prevMusicDAta.getArtist());
                        MainActivity.fPlayListener.song_title.setText(prevMusicDAta.getTitles());
                        MainActivity.fPlayListener.play.setVisibility(View.GONE);
                        MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
                        MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
                        MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
                        playMusic(prevMusicDAta.getLocation());
                        if (mainActivity.s != null)
                            mainActivity.s.listeDegistir(MusicList.musicData, FPlayListener.currentMusicPosition);
                    }
                }
            }
            // Calma listesinden karısık calma
            else {
                if (OynatmaListesiFragment.music_oynat_list.size() == 0) return;
                if (tekrarla == 0 || tekrarla == 2 || tekrarla == 3) {
                    int rndPositin = new Random().nextInt(OynatmaListesiFragment.music_oynat_list.size());
                    if (ileriCal) {
                        if (NotificationService.kaldirilanPos.size() > 0) {
                            rndPositin = NotificationService.kaldirilanPos.get(NotificationService.kaldirilanPos.size() - 1);
                            NotificationService.kaldirilanPos.remove(NotificationService.kaldirilanPos.size() - 1);
                            if (rndPositin < 0){
                                NotificationService.kaldirilanPos.clear();
                                rndPositin = new Random().nextInt(OynatmaListesiFragment.music_oynat_list.size());
                            }
                        } else {
                            rndPositin = new Random().nextInt(OynatmaListesiFragment.music_oynat_list.size());
                            NotificationService.calimaListesiOncekiPos.add(rndPositin);
                            Log.e("ana", "bitti");
                        }
                    } else {
                        if (NotificationService.calimaListesiOncekiPos.size() > 0) {
                            NotificationService.kaldirilanPos.add(NotificationService.mainListeOncekiPos.size() - 1);
                            NotificationService.calimaListesiOncekiPos.remove(NotificationService.calimaListesiOncekiPos.size() - 1);
                            try {
                                rndPositin = NotificationService.calimaListesiOncekiPos.get(NotificationService.calimaListesiOncekiPos.size() - 1);
                            } catch (IndexOutOfBoundsException ex) {
                                Log.e("HATA", ex.getMessage());
                                rndPositin = new Random().nextInt(OynatmaListesiFragment.music_oynat_list.size());
                            }
                        }
                        else {
                            NotificationService.kaldirilanPos.clear();
                        }

                    }

                    //İleri butonuna tıklandıgı zaman gecerli sarkıyı tekrarlada ise bir sonraki sarkıya atlattrırıp tekrala = 1 olacak
                    if (tekrarla == 3) {
                        tekrarla = 1;
                    }
                    FPlayListener.currentMusicPosition = rndPositin;
                    if (rndPositin <= OynatmaListesiFragment.music_oynat_list.size()) {
                        prevMusicDAta = OynatmaListesiFragment.music_oynat_list.get(rndPositin);
                        MainActivity.fPlayListener.song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(rndPositin).getArtist());
                        MainActivity.fPlayListener.song_title.setText(OynatmaListesiFragment.music_oynat_list.get(rndPositin).getTitles());
                        MainActivity.fPlayListener.play.setVisibility(View.GONE);
                        MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
                        MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
                        MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
                        MainActivity.fPlayListener.playFromPlayList(OynatmaListesiFragment.music_oynat_list.get(rndPositin).getLocation());
                        if (mainActivity.s != null)
                            mainActivity.s.listeDegistir(OynatmaListesiFragment.music_oynat_list, FPlayListener.currentMusicPosition);
                    }
                } else if (tekrarla == 1) {
                    if (prevMusicDAta != null) {
                        MainActivity.fPlayListener.song_artis.setText(prevMusicDAta.getArtist());
                        MainActivity.fPlayListener.song_title.setText(prevMusicDAta.getTitles());
                        MainActivity.fPlayListener.play.setVisibility(View.GONE);
                        MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
                        MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
                        MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
                        playMusic(prevMusicDAta.getLocation());
                        if (mainActivity.s != null)
                            mainActivity.s.listeDegistir(OynatmaListesiFragment.music_oynat_list, FPlayListener.currentMusicPosition);
                    }
                }
            }
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

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            // mediaPlayer.release();
            //mediaPlayer = null;
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    private void seekBarChange() {

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                myseekbar.setMax(mediaPlayer.getDuration());
                myseekbar.setOnSeekBarChangeListener(seekBarChangeListener);
                mytext2.setText(String.valueOf(android.text.format.DateFormat.format("mm:ss", mediaPlayer.getDuration())));
                mytext2.setTag("var");
                mediaPlayer.start();
            }
        });
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
//                handler.removeCallbacks(runnable);
//                runnable = null;

                mediaPlayer.pause();
            }
        }
    }

    public void iconKapat(boolean acikmi) {
        if (acikmi) {
            MainActivity.fPlayListener.play.setVisibility(View.GONE);
            MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
            MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
            MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
        } else {
            MainActivity.fPlayListener.play.setVisibility(View.VISIBLE);
            MainActivity.fPlayListener.play_main.setVisibility(View.VISIBLE);
            MainActivity.fPlayListener.pause.setVisibility(View.GONE);
            MainActivity.fPlayListener.pause_main.setVisibility(View.GONE);
        }
    }

    public void startRunableWithMediaPlayer() {


        if (isMyServiceRunning(NotificationService.class)) {
            mediaPlayer = NotificationService.mediaPlayer;
            FPlayListener.currentMusicPosition = NotificationService.currentPos;
        }

        MainActivity.fPlayListener.icerikDegistirme();

        if (mediaPlayer != null) {
            myseekbar.setMax(mediaPlayer.getDuration());
            if (mediaPlayer.isPlaying()) {
                iconKapat(true);
            } else {
                iconKapat(false);
            }
        } else {
            iconKapat(false);
        }
        if (runnable == null) {

            myHandler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && isMediaPlayerCreated) {
                        if (mytext2.getTag() == null || !mytext2.getTag().toString().equals("var")) {
                            mytext2.setTag("var");
                            mytext2.setText(String.valueOf(android.text.format.DateFormat.format("mm:ss", mediaPlayer.getDuration())));

                        }
                        myseekbar.setProgress(mediaPlayer.getCurrentPosition());
                        mytext1.setText(String.valueOf(android.text.format.DateFormat.format("mm:ss", mediaPlayer.getCurrentPosition())));

                        int current = mediaPlayer.getCurrentPosition();
                        int total = mediaPlayer.getDuration();
                        if (current >= total) {
                            calmayaDevamEt(true);
                        } else if (total - current <= 300) {
                            calmayaDevamEt(true);
                        }

                    }
                    Log.e("qqqqqqqqqq","runablee");
                    myHandler.postDelayed(runnable, 1000);
                }
            };
            runnable.run();
        }

    }

    public void startRunable() {
        startRunableWithMediaPlayer();
        myseekbar.setOnSeekBarChangeListener(seekBarChangeListener);


    }

    public void stopRunable() {
        if (myHandler != null) {
            myHandler.removeCallbacks(runnable);
            myHandler = null;
            runnable = null;
        }

    }
}
