package com.wavemediaplayer.mservices;

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
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.MusicData;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;
import com.wavemediaplayer.main.FPlayListener;
import com.wavemediaplayer.play.PlayMusic;
import com.wavemediaplayer.settings.AllReceiver;

import java.util.ArrayList;
import java.util.Random;

public class NotificationService extends Service {
    public static MediaPlayer mediaPlayer;
    public static ArrayList<MusicData> list;
    public static int currentPos = 0;
    public static boolean calmaListesiMuzik;
    public static ArrayList<Integer> calimaListesiOncekiPos = new ArrayList<>();
    public static ArrayList<Integer> mainListeOncekiPos = new ArrayList<>();
    public static ArrayList<Integer> kaldirilanPos = new ArrayList<>();
    private final String LOG_TAG = "NotificationService";
    private final IBinder mBinder = new MyBinder();
    Notification status;
    private RemoteViews views;
    private RemoteViews bigViews;
    private PendingIntent pendingIntent;
    private MusicData mData;
    private int tekrarPos;
    PhoneStateListener phoneStateListener;
    boolean isPlayed = false;
    private AllReceiver allReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void activityPlay() {
        if (views != null) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.svgpause);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    R.drawable.svgpause);
            create();
        }
    }

    public void activityPause() {
        if (views != null) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.svgplay);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    R.drawable.svgplay);
            create();
        }


    }

    public void listeDegistir(ArrayList<MusicData> musicData, int currentPos) {

        list = musicData;
        activityPlay();
        tekrarPos = currentPos;
        this.currentPos = currentPos;
        this.calmaListesiMuzik = FPlayListener.calmaListesiMuzik;
        FPlayListener.currentMusicPosition = currentPos;
        if (views != null && bigViews != null) {
            Log.e("rndpos",""+currentPos);
            if (musicData.size() < currentPos){
                currentPos = new Random().nextInt(musicData.size());
            }
            mData = musicData.get(currentPos);
            views.setTextViewText(R.id.status_bar_track_name, musicData.get(currentPos).getTitles());
            bigViews.setTextViewText(R.id.status_bar_track_name, musicData.get(currentPos).getTitles());
            views.setTextViewText(R.id.status_bar_artist_name, musicData.get(currentPos).getArtist());
            bigViews.setTextViewText(R.id.status_bar_artist_name, musicData.get(currentPos).getArtist());
            bigViews.setTextViewText(R.id.status_bar_artist_name, "");
        }
        create();
    }

    private void servicePause() {
        Intent myIntent = new Intent("speedExceeded");
        myIntent.putExtra("servicePause", "servicePause");
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }

    private void servicePlay() {
        Intent myIntent = new Intent("speedExceeded");
        myIntent.putExtra("servicePlay", "servicePlay");
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }

    private void serviceNext() {
        FPlayListener.calmaListesiMuzik = this.calmaListesiMuzik;
        FPlayListener.currentMusicPosition = currentPos;

        Intent myIntent = new Intent("speedExceeded");
        myIntent.putExtra("serviceNext", "serviceNext");
        myIntent.putExtra("serviceNextpos", FPlayListener.currentMusicPosition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }

    private void serviceBefore() {
        Intent myIntent = new Intent("speedExceeded");
        myIntent.putExtra("serviceBefore", "serviceBefore");
        myIntent.putExtra("serviceBeforepos", currentPos);
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
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
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        allReceiver=new AllReceiver(this);
        allReceiver.registerReceiver();
    }

    private void nextSong() {
        activityPlay();
        calmayaDevamEt(true);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(list.get(currentPos).getLocation()));
            mediaPlayer.start();
            listeDegistir(list, currentPos);
        }
    }

    public void pauseSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                activityPause();
                servicePause();
            } else {
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
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(list.get(currentPos).getLocation()));
            mediaPlayer.start();
            listeDegistir(list, currentPos);
        }
    }

    private void exitPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        stopForeground(true);
        stopSelf();
    }

    private void showNotification() {
        views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);


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

        views.setTextViewText(R.id.status_bar_track_name, " ");
        bigViews.setTextViewText(R.id.status_bar_track_name, " ");

        views.setTextViewText(R.id.status_bar_artist_name, " ");
        bigViews.setTextViewText(R.id.status_bar_artist_name, " ");

        bigViews.setTextViewText(R.id.status_bar_artist_name, " ");
    }

    private void create() {

        if (phoneStateListener == null){
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        if (PlayMusic.mediaPlayer != null){
                            if (PlayMusic.mediaPlayer.isPlaying()){
                                isPlayed = true;
                                PlayMusic.mediaPlayer.pause();
                            }
                        }


                        //Incoming call: Pause music
                    } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                        if (PlayMusic.mediaPlayer != null)
                            if (isPlayed){
                                PlayMusic.mediaPlayer.start();
                                isPlayed = false;
                            }

                        //Not in call: Play music
                    } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        //A call is dialing, active or on hold
                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            if(mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }

        Notification.Builder nBuilder = new Notification.Builder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            String name = "Wave ";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("com.wavemediaplayer", name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
            nBuilder.setChannelId(mChannel.getId());
        }

        status = new Notification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
            nBuilder.setPublicVersion(status);
        }
        status = nBuilder.build();

        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.mipmap.ic_stat_play_circle_outline;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    public void setSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.KARISIK_CAL, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getSharedPreferences(MainActivity.SARKIYI_TEKRARLA, Context.MODE_PRIVATE);
        PlayMusic.karisikCal = sharedPreferences.getBoolean("karisik", true);
        PlayMusic.tekrarla = sharedPreferences2.getInt("tekrarla", 0);
        if (FPlayListener.calmaListesiMuzik) {
            list = OynatmaListesiFragment.music_oynat_list;
            tekrarPos = FPlayListener.currentMusicPosition;
            this.currentPos = FPlayListener.currentMusicPosition;


        } else {
            list = MusicList.musicData;
            tekrarPos = FPlayListener.currentMusicPosition;
            this.currentPos = FPlayListener.currentMusicPosition;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        allReceiver.unRegisterReceiver();

    }

    /**
     * BURAYA KARIIŞMA MK ------------------------------------------------
     */
    public void calmayaDevamEt(boolean ileriCal) {
        if (!PlayMusic.karisikCal) { // Sıralı calma aktifse
            if (!NotificationService.calmaListesiMuzik) {//Ana playerdan calınacaksa
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
                            NotificationService.currentPos = list.size() - 1;
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
                            NotificationService.currentPos = list.size() - 1;
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
            // ana music playerdan karısık calma
            if (!NotificationService.calmaListesiMuzik) {
                if (NotificationService.list.size() == 0) {
                    return;
                }
                int rndPositin = new Random().nextInt(NotificationService.list.size());
                if (PlayMusic.tekrarla == 0 || PlayMusic.tekrarla == 2 || PlayMusic.tekrarla == 3) {
                    if (ileriCal) {
                        if (NotificationService.kaldirilanPos.size() > 0) {
                            rndPositin = NotificationService.kaldirilanPos.get(NotificationService.kaldirilanPos.size() - 1);
                            NotificationService.kaldirilanPos.remove(NotificationService.kaldirilanPos.size() - 1);
                            if (rndPositin < 0) {
                                NotificationService.kaldirilanPos.clear();
                                rndPositin = new Random().nextInt(NotificationService.list.size());
                            }
                        } else {
                            rndPositin = new Random().nextInt(NotificationService.list.size());
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
                                rndPositin = new Random().nextInt(NotificationService.list.size());
                            }
                        }
                    }
                    while (rndPositin > list.size()){
                        rndPositin = new Random().nextInt(NotificationService.list.size());
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
                        if (NotificationService.kaldirilanPos.size() > 0) {
                            rndPositin = NotificationService.kaldirilanPos.get(NotificationService.kaldirilanPos.size() - 1);
                            NotificationService.kaldirilanPos.remove(NotificationService.kaldirilanPos.size() - 1);
                            if (rndPositin < 0) {
                                NotificationService.kaldirilanPos.clear();
                                rndPositin = new Random().nextInt(NotificationService.list.size());
                            }
                        } else {
                            rndPositin = new Random().nextInt(NotificationService.list.size());
                            NotificationService.calimaListesiOncekiPos.add(rndPositin);
                        }

                    } else {
                        if (NotificationService.calimaListesiOncekiPos.size() > 0) {
                            NotificationService.kaldirilanPos.add(NotificationService.calimaListesiOncekiPos.size() - 1);
                            NotificationService.calimaListesiOncekiPos.remove(NotificationService.calimaListesiOncekiPos.size() - 1);
                            try {
                                rndPositin = NotificationService.calimaListesiOncekiPos.get(NotificationService.calimaListesiOncekiPos.size() - 1);
                            } catch (IndexOutOfBoundsException ex) {
                                Log.e("HATA", ex.getMessage());
                                rndPositin = new Random().nextInt(NotificationService.list.size());
                            }
                        }
                    }
                    while (rndPositin > list.size()){
                        rndPositin = new Random().nextInt(NotificationService.list.size());
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

    public class MyBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

}