package com.wavemediaplayer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wavemediaplayer.adapter.MusicList;

import java.util.List;


public class DexterPermission {
    private MainActivity activity;
    private String[] permissions;

    public DexterPermission(MainActivity activity) {
        this.activity = activity;
        if(Build.VERSION.SDK_INT>=28){
            permissions=new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.FOREGROUND_SERVICE};
        }else{
            permissions=new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS};
        }

    }

    public void girisControl(){
        MainActivity.allPermGrand=true;
        for(String per:permissions){
            if (ContextCompat.checkSelfPermission(activity, per)!= PackageManager.PERMISSION_GRANTED){
                MainActivity.allPermGrand=false;
            }
        }
        if(!MainActivity.allPermGrand){
            multiPermission();
        }else{
            activity.createStart();
        }
    }

    public void multiPermission() {
        Dexter.withActivity(activity)
                .withPermissions(
                        permissions
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()) {
                    activity.createStart();
                }else{
                    showSettingsDialog();

                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }
}












//
//
//public class DexterPermission {
//    private MainActivity mainActivity;
//    private String[] permissions;
//
//    public DexterPermission(MainActivity mainActivity){
//        this.mainActivity=mainActivity;
//        startPerControl();
//
//
//    }
//
//    private void startPerControl(){
//
//
//        if(Build.VERSION.SDK_INT>=28){
//            permissions=new String[]{Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
//                    Manifest.permission.FOREGROUND_SERVICE};
//        }else{
//            permissions=new String[]{Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.MODIFY_AUDIO_SETTINGS};
//        }
//
//        MainActivity.allPermGrand=true;
//        for(String per:permissions){
//            if (ContextCompat.checkSelfPermission(mainActivity, per)!= PackageManager.PERMISSION_GRANTED){
//                MainActivity.allPermGrand=false;
//            }
//        }
//
//
//        if(! MainActivity.allPermGrand){
//            checkPerControl();
//        }
//
//    }
//
//    private void checkPerControl(){
//
//        Dexter.withActivity(mainActivity)
//                .withPermissions(
//                        permissions
//                ).withListener(new MultiplePermissionsListener() {
//            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
//                if(report.areAllPermissionsGranted()){
//                    MainActivity.allPermGrand=true;
//                    mainActivity.musicList.getMusic("notification", "ringtone");
//
//                }else{
//                    MainActivity.allPermGrand=false;
//                    checkPerControl();
//
//                }
//            }
//            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                token.continuePermissionRequest();
//
//            }
//        }).check();
//    }
//
//
////    public void multiPermission(String[] per){
////        Dexter.withActivity(activity)
////                .withPermissions(
////                        per
////                ).withListener(new MultiplePermissionsListener() {
////            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
////            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
////        }).check();
////    }
//}
