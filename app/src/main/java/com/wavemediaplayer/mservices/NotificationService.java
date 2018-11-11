package com.wavemediaplayer.mservices;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.MusicData;
import com.wavemediaplayer.adapter.MusicList;
import com.wavemediaplayer.main.FPlayListener;
import com.wavemediaplayer.play.PlayMusic;

import java.util.ArrayList;

public class NotificationService extends Service {
    Notification status;
    private final String LOG_TAG = "NotificationService";
    public static MediaPlayer mediaPlayer;
    private ArrayList<MusicData> list;
    public static int currentPos=0;
    private RemoteViews views;
    private RemoteViews bigViews;
    private PendingIntent pendingIntent;
    private final IBinder mBinder = new MyBinder();
    private MusicData mData;
    private boolean calmaListesiMuzik;


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

    public void listeDegistir(ArrayList<MusicData> musicData,int currentPos){
        list=musicData;
        this.currentPos=currentPos;
        this.calmaListesiMuzik=FPlayListener.calmaListesiMuzik;
        FPlayListener.currentMusicPosition = currentPos;
        if(views!=null && bigViews!=null){
            mData=musicData.get(currentPos);
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
        if(intent.getAction()!=null){
            switch (intent.getAction()) {
                case Constants.ACTION.STARTFOREGROUND_ACTION:
                    showNotification();
                    // Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
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

//            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
//                showNotification();
//                // Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
//                mediaPlayer=PlayMusic.mediaPlayer;
//
//            } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
//                previousSong();
//                serviceBefore();
//            } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
//                pauseSong();
//            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
//                nextSong();
//                serviceNext();
//            } else if (intent.getAction().equals(
//                    Constants.ACTION.STOPFOREGROUND_ACTION)) {
//                exitPlayer();
//            }
        }

        Log.e("qqqq","service started");
        return START_STICKY;
    }

    private void nextSong(){
        if(list.size()-1>currentPos){
            currentPos++;
        }else{
            currentPos=0;
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer=MediaPlayer.create(getApplicationContext(), Uri.parse(list.get(currentPos).getLocation()));
            mediaPlayer.start();
            listeDegistir(list,currentPos);
        }
        Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Clicked Next");
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
        Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Clicked Play");
    }
    private void previousSong(){
        if(currentPos>0){
            currentPos--;
        }else{
            currentPos=list.size()-1;
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer=MediaPlayer.create(getApplicationContext(), Uri.parse(list.get(currentPos).getLocation()));
            mediaPlayer.start();
            listeDegistir(list,currentPos);
        }
        Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Clicked Previous");
    }
    private void exitPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
        Log.i(LOG_TAG, "Received Stop Foreground Intent");
        Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
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
        create();


    }

    private void create(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            String name = "Wave ";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("com.wavemediaplayer", name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
            status = new Notification.Builder(this).setChannelId(mChannel.getId()).build();

            status.contentView = views;
            status.bigContentView = bigViews;
            status.flags = Notification.FLAG_ONGOING_EVENT;
            status.icon = R.drawable.ic_launcher;
            status.contentIntent = pendingIntent;
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);

        }else{

            status = new Notification.Builder(this).build();
            status.contentView = views;
            status.bigContentView = bigViews;
            status.flags = Notification.FLAG_ONGOING_EVENT;
            status.icon = R.drawable.ic_launcher;
            status.contentIntent = pendingIntent;
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}