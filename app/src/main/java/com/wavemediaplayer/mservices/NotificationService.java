package com.wavemediaplayer.mservices;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.MusicData;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;
import com.wavemediaplayer.main.FPlayListener;
import com.wavemediaplayer.play.PlayMusic;

import java.util.ArrayList;
import java.util.Random;

public class NotificationService extends Service {
    Notification status;
    private final String LOG_TAG = "NotificationService";
    public static MediaPlayer mediaPlayer;
    public static ArrayList<MusicData> list;
    public static int currentPos = 0;
    public static boolean calmaListesiMuzik;
    public static ArrayList<Integer> calimaListesiOncekiPos = new ArrayList<>();
    public static ArrayList<Integer> mainListeOncekiPos = new ArrayList<>();
    private final IBinder mBinder = new MyBinder();
    private RemoteViews views;
    private RemoteViews bigViews;
    private PendingIntent pendingIntent;
    private MusicData mData;
    private int tekrarPos;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    public void activityPlay(){
        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.svgpause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.svgpause);
        Log.e("test","play");
        create();

    }
    public void activityPause(){
        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.svgplay);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.svgplay);
        Log.e("test","pause");
        create();

    }

    public void listeDegistir(ArrayList<MusicData> musicData, int currentPos) {
        Log.e("test","lllllllllllllşş");

        list = musicData;
        activityPlay();
        tekrarPos = currentPos;
        this.currentPos = currentPos;
        this.calmaListesiMuzik = FPlayListener.calmaListesiMuzik;
        FPlayListener.currentMusicPosition = currentPos;
        if (views != null && bigViews != null) {
            mData = musicData.get(currentPos);
            views.setTextViewText(R.id.status_bar_track_name, musicData.get(currentPos).getTitles());
            bigViews.setTextViewText(R.id.status_bar_track_name, musicData.get(currentPos).getTitles());
            views.setTextViewText(R.id.status_bar_artist_name, musicData.get(currentPos).getArtist());
            bigViews.setTextViewText(R.id.status_bar_artist_name, musicData.get(currentPos).getArtist());
            bigViews.setTextViewText(R.id.status_bar_artist_name, "");
        }
        create();
    }


    private void servicePause(){
        Intent myIntent = new Intent("speedExceeded");
        myIntent.putExtra("servicePause", "servicePause");
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }
    private void servicePlay(){
        Intent myIntent = new Intent("speedExceeded");
        myIntent.putExtra("servicePlay", "servicePlay");
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }
    private void serviceNext(){
        FPlayListener.calmaListesiMuzik = this.calmaListesiMuzik;
        FPlayListener.currentMusicPosition = currentPos;

        Intent myIntent = new Intent("speedExceeded");
        myIntent.putExtra("serviceNext", "serviceNext");
        myIntent.putExtra("serviceNextpos", FPlayListener.currentMusicPosition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }
    private void serviceBefore(){
        Intent myIntent = new Intent("speedExceeded");
        myIntent.putExtra("serviceBefore", "serviceBefore");
        myIntent.putExtra("serviceBeforepos", currentPos);
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null && intent.getAction()!=null){
            switch (intent.getAction()) {
                case Constants.ACTION.STARTFOREGROUND_ACTION:
                    showNotification();
                    mediaPlayer = PlayMusic.mediaPlayer;

                    break;
                case Constants.ACTION.PREV_ACTION:
                    previousSong();
                    serviceBefore();
                    break;
                case Constants.ACTION.PLAY_ACTION:
                    pauseSong();
                    break;
                case Constants.ACTION.NEXT_ACTION:
                    nextSong();
                    serviceNext();
                    break;
                case Constants.ACTION.STOPFOREGROUND_ACTION:
                    exitPlayer();
                    break;
            }
        }

        Log.e("qqqq","service started");
        return START_STICKY;
    }

    private void nextSong() {
        activityPlay();
        calmayaDevamEt(true);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer=MediaPlayer.create(getApplicationContext(), Uri.parse(list.get(currentPos).getLocation()));
            mediaPlayer.start();
            listeDegistir(list,currentPos);
        }
    }
    private void pauseSong(){
        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                activityPause();
                servicePause();
            }else{
                mediaPlayer.start();
                activityPlay();
                servicePlay();
            }
            create();
        }

    }

    private void previousSong() {
        activityPlay();
        calmayaDevamEt(false);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer=MediaPlayer.create(getApplicationContext(), Uri.parse(list.get(currentPos).getLocation()));
            mediaPlayer.start();
            listeDegistir(list,currentPos);
        }
    }
    private void exitPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
        stopForeground(true);
        stopSelf();
    }


    private void showNotification() {
// Using RemoteViews to bind custom layouts into Notification
        views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

// showing default album image
//        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
//        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.svgpause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.svgpause);

        views.setTextViewText(R.id.status_bar_track_name, "");
        bigViews.setTextViewText(R.id.status_bar_track_name, "");

        views.setTextViewText(R.id.status_bar_artist_name, "");
        bigViews.setTextViewText(R.id.status_bar_artist_name, "");

        bigViews.setTextViewText(R.id.status_bar_artist_name, "");


    }

    private void create(){
        Notification.Builder nBuilder=new Notification.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            String name = "Wave ";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("com.wavemediaplayer", name, importance);
            mNotificationManager.createNotificationChannel(mChannel);

            nBuilder.setChannelId(mChannel.getId());



//            status = new Notification.Builder(this).setPublicVersion().setChannelId(mChannel.getId()).build();


        }else{
//            status = new Notification.Builder(this).build();



        }

        status=new Notification();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            nBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
            nBuilder.setPublicVersion(status);
        }
        status=nBuilder.build();

        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.ic_launcher;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    public void setSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.KARISIK_CAL, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getSharedPreferences(MainActivity.SARKIYI_TEKRARLA, Context.MODE_PRIVATE);
        PlayMusic.karisikCal = sharedPreferences.getBoolean("karisik", true);
        PlayMusic.tekrarla = sharedPreferences2.getInt("tekrarla", 0);
        if (FPlayListener.calmaListesiMuzik) {
            list=OynatmaListesiFragment.music_oynat_list;
            tekrarPos = FPlayListener.currentMusicPosition;
            this.currentPos = FPlayListener.currentMusicPosition;


        } else {
            list=MusicList.musicData;
            tekrarPos = FPlayListener.currentMusicPosition;
            this.currentPos = FPlayListener.currentMusicPosition;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * BURAYA KARIIŞMA MK ------------------------------------------------
     */
    public void calmayaDevamEt(boolean ileriCal) {
        if (!PlayMusic.karisikCal) { // Sıralı calma aktifse
            Log.e("sirali", "cal");
            if (!NotificationService.calmaListesiMuzik) {//Ana playerdan calınacaksa
                Log.e("ana", "music");
                if (list.size() == 0) {
                    return;
                }
                if (PlayMusic.tekrarla == 0 || PlayMusic.tekrarla == 2 || PlayMusic.tekrarla == 3) {
                    if (ileriCal) {
                        NotificationService.currentPos++;
                        if (NotificationService.currentPos >= NotificationService.list.size()) {
                            NotificationService.currentPos = 0;
                        }
                    } else {
                        if (NotificationService.currentPos != 0) {
                            NotificationService.currentPos--;
                        } else {
                            NotificationService.currentPos = list.size()-1;
                        }
                    }

                    tekrarPos = currentPos;
                    //İleri butonuna tıklandıgı zaman gecerli sarkıyı tekrarlada ise bir sonraki sarkıya atlattrırıp tekrala = 1 olacak
                    if (PlayMusic.tekrarla == 3) {
                        PlayMusic.tekrarla = 1;
                        listeDegistir(list, currentPos);
                    } else {
                        listeDegistir(list, currentPos);
                    }
                }
                // tekrar = 1 ise
                else {
                    listeDegistir(list, tekrarPos);
                }
            }

            // calma listesi sirali calma
            else {
                if (list.size() == 0) {
                    return;
                }
                if (PlayMusic.tekrarla == 0 || PlayMusic.tekrarla == 2 || PlayMusic.tekrarla == 3) {
                    if (ileriCal) {
                        NotificationService.currentPos++;
                        if (NotificationService.currentPos >= NotificationService.list.size()) {
                            NotificationService.currentPos = 0;
                        }
                    } else {
                        if (NotificationService.currentPos != 0) {
                            NotificationService.currentPos--;
                        } else {
                            NotificationService.currentPos = list.size()-1;
                        }
                    }

                    tekrarPos = currentPos;
                    //İleri butonuna tıklandıgı zaman gecerli sarkıyı tekrarlada ise bir sonraki sarkıya atlattrırıp tekrala = 1 olacak
                    if (PlayMusic.tekrarla == 3) {
                        PlayMusic.tekrarla = 1;
                        listeDegistir(list, currentPos);
                    } else {
                        listeDegistir(list, currentPos);
                    }
                }
                // tekrar = 1 ise
                else {
                    listeDegistir(list, tekrarPos);
                }

            }
        }
        // Karısık sarkı calma
        else {
            Log.e("karisik", "cal");
            // ana music playerdan karısık calma
            if (!NotificationService.calmaListesiMuzik) {
                if (NotificationService.list.size() == 0) {
                    return;
                }
                int rndPositin = new Random().nextInt(NotificationService.list.size());
                if (PlayMusic.tekrarla == 0 || PlayMusic.tekrarla == 2 || PlayMusic.tekrarla == 3) {
                    if (ileriCal) {
                        NotificationService.mainListeOncekiPos.add(rndPositin);
                    } else {
                        if (NotificationService.mainListeOncekiPos.size() > 1) {
                            NotificationService.mainListeOncekiPos.remove(NotificationService.mainListeOncekiPos.size() - 1);
                            rndPositin = NotificationService.mainListeOncekiPos.get(NotificationService.mainListeOncekiPos.size() - 1);
                        }
                    }
                    currentPos = rndPositin;
                    tekrarPos = currentPos;
                    //İleri butonuna tıklandıgı zaman gecerli sarkıyı tekrarlada ise bir sonraki sarkıya atlattrırıp tekrala = 1 olacak
                    if (PlayMusic.tekrarla == 3) {
                        PlayMusic.tekrarla = 1;
                        listeDegistir(list, currentPos);
                    } else {
                        listeDegistir(list, currentPos);
                    }
                } else {
                    listeDegistir(list, tekrarPos);
                }
            }
            // calma listesi ise
            else {
                if (NotificationService.list.size() == 0) {
                    return;
                }
                int rndPositin = new Random().nextInt(NotificationService.list.size());
                if (PlayMusic.tekrarla == 0 || PlayMusic.tekrarla == 2 || PlayMusic.tekrarla == 3) {
                    if (ileriCal) {
                        NotificationService.calimaListesiOncekiPos.add(rndPositin);
                    } else {
                        if (NotificationService.calimaListesiOncekiPos.size() > 1) {
                            NotificationService.calimaListesiOncekiPos.remove(NotificationService.calimaListesiOncekiPos.size() - 1);
                            rndPositin = NotificationService.calimaListesiOncekiPos.get(NotificationService.calimaListesiOncekiPos.size() - 1);
                        }
                    }
                    currentPos = rndPositin;
                    tekrarPos = currentPos;
                    //İleri butonuna tıklandıgı zaman gecerli sarkıyı tekrarlada ise bir sonraki sarkıya atlattrırıp tekrala = 1 olacak
                    if (PlayMusic.tekrarla == 3) {
                        PlayMusic.tekrarla = 1;
                        listeDegistir(list, currentPos);
                    } else {
                        listeDegistir(list, currentPos);
                    }
                } else {
                    listeDegistir(list, tekrarPos);
                }
            }

        }
    }

}