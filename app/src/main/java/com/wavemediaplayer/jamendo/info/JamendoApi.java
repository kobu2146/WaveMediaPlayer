package com.wavemediaplayer.jamendo.info;

public interface JamendoApi {
    void beforeData();
    void getApiData(String jsonStr);
    void afterData();
}
