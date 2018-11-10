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
 *
 * Gonderilen dosya linkine gore muzik calma islemi
 * */
public class PlayMusic {
    public static boolean karisikCal = true;
    public static int tekrarla = 0;
    private Context context;
    public static MediaPlayer mediaPlayer;
    private SeekBar myseekbar;
    private TextView mytext1;
    private TextView mytext2;
    private ImageView myimageview;
    private static Runnable runnable;
    private static Handler myHandler;
    private  Handler handler;
    private BassBoost bassBoost;
    private static String playPrev = "";
    public static MusicData prevMusicDAta;
    private InitilationMediaPlayer initilationMediaPlayer;


    /***/



    public PlayMusic(Context context, SeekBar myseekbar,TextView mytext1, TextView mytext2, ImageView myimageview,Handler handler){
        this.context = context;
        this.myseekbar = myseekbar;
        this.mytext1 = mytext1;
        this.mytext2 = mytext2;
        this.myimageview = myimageview;
        this.handler = handler;

    }




    /** Dosya var mı yok mu belirtilecek varsa calmaya baslar */
    public void playMusic(String link){



        Log.e("music yolu",link);
        File file =  new File(link);
        try {
            if(isMyServiceRunning(NotificationService.class)){
                mediaPlayer=NotificationService.mediaPlayer;
                stopRunable();
                startRunable();
                Log.e("qqqqqqqqqq","sssssstart eser11");


            }
            if (file.exists()){
                if (!playPrev.equals(link)){
                    playPrev = link;
                    stopPlaying();
                    mediaPlayer = new MediaPlayer();
                    /**initilation mediaplyer sharedtteki ayarları alıp mediaplayere entegre ediyorumki equalizer açıldığında yeni ayarlar orda aktifleşiyordu bullshit*/
                    if(initilationMediaPlayer==null) initilationMediaPlayer=new InitilationMediaPlayer(context).init(mediaPlayer);
                    bassBoost = new BassBoost(1, mediaPlayer.getAudioSessionId());
                    bassBoost.setEnabled(true);
                    BassBoost.Settings bassBoostSettingTemp =  bassBoost.getProperties();
                    BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
                    bassBoostSetting.strength=1000;
                    bassBoost.setStrength((short)1000);
                    bassBoost.setProperties(bassBoostSetting);
                    mediaPlayer.setAuxEffectSendLevel(1.0f);
                    mediaPlayer.attachAuxEffect(bassBoost.getId());
                    mediaPlayer.setDataSource(context,Uri.parse(link));
                    mediaPlayer.prepareAsync();
//                    mediaPlayer=MediaPlayer.create(context,Uri.parse(link));

                }
                else {
                    if (mediaPlayer != null){
                        if (!mediaPlayer.isPlaying()){

                            mediaPlayer.start();

                        }
                    }
                }
                seekBarChange();
                stopRunable();
                startRunable();
                if(isMyServiceRunning(NotificationService.class)){
                    NotificationService.mediaPlayer=mediaPlayer;
                }
            }
            else {
                Log.e("File not found","Belirtilen dosya yok");
                Toast.makeText(context,"File not found",Toast.LENGTH_LONG).show();
                calmayaDevamEt(true);
            }
        }
        catch (Exception ex){
            Log.e("FILE NOT FOUND",ex.getMessage());
        }
    }

    public void calmayaDevamEt(boolean ileriCal){
        if (!karisikCal){ // Sıralı calma aktifse
            if (!FPlayListener.calmaListesiMuzik){//Ana playerdan calınacaksa
                if (tekrarla == 0 || tekrarla == 2){
                    if (ileriCal){ FPlayListener.currentMusicPosition++; }
                    else {
                        if (  FPlayListener.currentMusicPosition != 0){FPlayListener.currentMusicPosition--;}
                        else { FPlayListener.currentMusicPosition = 0; }
                    }
                }


                if (FPlayListener.currentMusicPosition < MusicList.musicData.size()){
                    prevMusicDAta = MusicList.musicData.get(FPlayListener.currentMusicPosition);
                    MainActivity.fPlayListener.song_artis.setText(MusicList.musicData.get(FPlayListener.currentMusicPosition).getArtist());
                    MainActivity.fPlayListener.song_title.setText(MusicList.musicData.get(FPlayListener.currentMusicPosition).getTitles());
                    playMusic(MusicList.musicData.get(FPlayListener.currentMusicPosition).getLocation());
                }
                else {
                    if (tekrarla == 0){
                        FPlayListener.currentMusicPosition = 0;
                        prevMusicDAta = MusicList.musicData.get(FPlayListener.currentMusicPosition);
                        MainActivity.fPlayListener.song_artis.setText(MusicList.musicData.get(FPlayListener.currentMusicPosition).getArtist());
                        MainActivity.fPlayListener.song_title.setText(MusicList.musicData.get(FPlayListener.currentMusicPosition).getTitles());
                        playMusic(MusicList.musicData.get( FPlayListener.currentMusicPosition).getLocation());
                    }

                }
            }
            else { //Playlistden veya baska biryerden ise
                if (tekrarla == 0 || tekrarla == 2){
                    if (ileriCal){ FPlayListener.currentMusicPosition++; }
                    else {
                        if (  FPlayListener.currentMusicPosition != 0){FPlayListener.currentMusicPosition--;}
                        else { FPlayListener.currentMusicPosition = 0; }
                    }
                }
                FPlayListener.currentMusicPosition++;
                if (OynatmaListesiFragment.music_oynat_list.size() > FPlayListener.currentMusicPosition){
                    prevMusicDAta = OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition);
                    MainActivity.fPlayListener.song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getArtist());
                    MainActivity.fPlayListener.song_title.setText(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getTitles());
                    MainActivity.fPlayListener.playFromPlayList(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getLocation());
                }
                else {
                    if (tekrarla == 0){
                        FPlayListener.currentMusicPosition = 0;
                        prevMusicDAta = OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition);
                        MainActivity.fPlayListener.song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getArtist());
                        MainActivity.fPlayListener.song_title.setText(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getTitles());
                        MainActivity.fPlayListener.playFromPlayList(OynatmaListesiFragment.music_oynat_list.get(FPlayListener.currentMusicPosition).getLocation());
                    }

                }

            }
        }
        // Karısık sarkı calma
        else {
            Log.e("karisik","cal");
            // ana music playerdan karısık calma
            if (!FPlayListener.calmaListesiMuzik){
                if (tekrarla == 0 || tekrarla == 2 || tekrarla == 3){
                    //İleri butonuna tıklandıgı zaman gecerli sarkıyı tekrarlada ise bir sonraki sarkıya atlattrırıp tekrala = 1 olacak
                    if (tekrarla == 3){
                        tekrarla = 1;
                    }
                    int  rndPositin = new Random().nextInt(MusicList.musicData.size());
                    FPlayListener.currentMusicPosition = rndPositin;
                    if (rndPositin <= MusicList.musicData.size()){
                        prevMusicDAta = MusicList.musicData.get(rndPositin);
                        Log.e("prevdata",MusicList.musicData.get(rndPositin).getArtist());
                        MainActivity.fPlayListener.song_artis.setText(MusicList.musicData.get(rndPositin).getArtist());
                        MainActivity.fPlayListener.song_title.setText(MusicList.musicData.get(rndPositin).getTitles());
                        playMusic(MusicList.musicData.get(rndPositin).getLocation());
                    }
                }
                else if (tekrarla == 1){
                    if (prevMusicDAta != null){
                        MainActivity.fPlayListener.song_artis.setText(prevMusicDAta.getArtist());
                        MainActivity.fPlayListener.song_title.setText(prevMusicDAta.getTitles());
                        playMusic(prevMusicDAta.getLocation());
                    }
                }
            }
            // Calma listesinden karısık calma
            else {
                if (tekrarla == 0 || tekrarla == 2 || tekrarla == 3){
                    //İleri butonuna tıklandıgı zaman gecerli sarkıyı tekrarlada ise bir sonraki sarkıya atlattrırıp tekrala = 1 olacak
                    if (tekrarla == 3){
                        tekrarla = 1;
                    }
                    int  rndPositin = new Random().nextInt(OynatmaListesiFragment.music_oynat_list.size());
                    FPlayListener.currentMusicPosition = rndPositin;
                    if (rndPositin <= OynatmaListesiFragment.music_oynat_list.size()){
                        prevMusicDAta = OynatmaListesiFragment.music_oynat_list.get(rndPositin);
                        MainActivity.fPlayListener.song_artis.setText(OynatmaListesiFragment.music_oynat_list.get(rndPositin).getArtist());
                        MainActivity.fPlayListener.song_title.setText(OynatmaListesiFragment.music_oynat_list.get(rndPositin).getTitles());
                        MainActivity.fPlayListener.playFromPlayList(OynatmaListesiFragment.music_oynat_list.get(rndPositin).getLocation());
                    }
                }
                else if (tekrarla == 1){
                    if (prevMusicDAta != null){
                        MainActivity.fPlayListener.song_artis.setText(prevMusicDAta.getArtist());
                        MainActivity.fPlayListener.song_title.setText(prevMusicDAta.getTitles());
                        playMusic(prevMusicDAta.getLocation());
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
            mediaPlayer.release();
            //mediaPlayer = null;
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    private void seekBarChange(){

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                myseekbar.setMax(mediaPlayer.getDuration());
                myseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mediaPlayer!=null && fromUser){

                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        Log.e("Start touch","xxxxx");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Log.e("Stop touch","xxxxx");


                    }
                });
                mytext2.setText(String.valueOf(android.text.format.DateFormat.format("mm:ss", mediaPlayer.getDuration())));
                mediaPlayer.start();
            }
        });
    }

    public void pauseMusic(){
        if (mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
//                handler.removeCallbacks(runnable);
//                runnable = null;

                mediaPlayer.pause();
            }
        }
    }

    private void iconKapat(boolean acikmi){
        if(acikmi){
            MainActivity.fPlayListener.play.setVisibility(View.GONE);
            MainActivity.fPlayListener.play_main.setVisibility(View.GONE);
            MainActivity.fPlayListener.pause.setVisibility(View.VISIBLE);
            MainActivity.fPlayListener.pause_main.setVisibility(View.VISIBLE);
        }else{
            MainActivity.fPlayListener.play.setVisibility(View.VISIBLE);
            MainActivity.fPlayListener.play_main.setVisibility(View.VISIBLE);
            MainActivity.fPlayListener.pause.setVisibility(View.GONE);
            MainActivity.fPlayListener.pause_main.setVisibility(View.GONE);
        }



    }


    public void startRunableWithMediaPlayer(){

        if(mediaPlayer!=null){
            myseekbar.setMax(mediaPlayer.getDuration());
            if(mediaPlayer.isPlaying()){
                iconKapat(true);
            }else{
                iconKapat(false);
            }
        }else{
            iconKapat(false);
        }
        if (runnable == null) {

            myHandler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        Log.e("qqqqqq", "null deil mediaplyer");

                        myseekbar.setProgress(mediaPlayer.getCurrentPosition());
                        mytext1.setText(String.valueOf(android.text.format.DateFormat.format("mm:ss", mediaPlayer.getCurrentPosition())));

                        int current = mediaPlayer.getCurrentPosition();
                        int total = mediaPlayer.getDuration();
                        if (current >= total) {
                            calmayaDevamEt(true);
                        } else if (total - current <= 300) {
                            calmayaDevamEt(true);
                        }


                    } else {
                    }

                    Log.e("qqqqqq", "nulllllllllll");


                    myHandler.postDelayed(runnable, 1000);
                }
            };
            runnable.run();
        }

    }

    public void startRunable(){
        startRunableWithMediaPlayer();
        Log.e("qweqwe","startrunable");


    }

    public void stopRunable(){
        if(myHandler!=null){
            myHandler.removeCallbacks(runnable);
            myHandler=null;
            runnable=null;
            Log.e("qweqwe","stoprunable");
        }

    }



}
