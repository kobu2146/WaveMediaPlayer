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
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
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
    private int i=0;
    private RemoteViews views;
    private RemoteViews bigViews;
    private PendingIntent pendingIntent;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            showNotification();
           // Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
            mediaPlayer=PlayMusic.mediaPlayer;
            list=MusicList.musicData;



        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            previousSong();
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            pauseSong();
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            nextSong();
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            exitPlayer();
        }
        return START_STICKY;
    }

    private void nextSong(){
        if(list.size()-1>i){
            i++;
        }else{
            i=0;
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer=MediaPlayer.create(getApplicationContext(), Uri.parse(list.get(i).getLocation()));
            mediaPlayer.start();
        }
        Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Clicked Next");
    }
    private void pauseSong(){
        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                views.setImageViewResource(R.id.status_bar_play,
                        R.drawable.apollo_holo_dark_play);
                bigViews.setImageViewResource(R.id.status_bar_play,
                        R.drawable.apollo_holo_dark_play);
            }else{
                mediaPlayer.start();
                views.setImageViewResource(R.id.status_bar_play,
                        R.drawable.apollo_holo_dark_pause);
                bigViews.setImageViewResource(R.id.status_bar_play,
                        R.drawable.apollo_holo_dark_pause);
            }
            create();
        }
        Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Clicked Play");
    }
    private void previousSong(){
        if(i>0){
            i--;
        }else{
            i=list.size()-1;
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer=MediaPlayer.create(getApplicationContext(), Uri.parse(list.get(i).getLocation()));
            mediaPlayer.start();
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
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
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
                R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);

        views.setTextViewText(R.id.status_bar_track_name, "Song Title");
        bigViews.setTextViewText(R.id.status_bar_track_name, "Song Title");

        views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_artist_name, "Artist Name");

        bigViews.setTextViewText(R.id.status_bar_artist_name, "Album Name");
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}