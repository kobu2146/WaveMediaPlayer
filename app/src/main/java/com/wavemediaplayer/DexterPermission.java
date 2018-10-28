package com.wavemediaplayer;

import android.app.Activity;

public class DexterPermission {
    private Activity activity;
    public DexterPermission(Activity activity){
        this.activity=activity;
    }

//    public void multiPermission(String[] per){
//        Dexter.withActivity(activity)
//                .withPermissions(
//                        per
//                ).withListener(new MultiplePermissionsListener() {
//            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
//            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
//        }).check();
//    }
}
