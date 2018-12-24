package com.wavemediaplayer.jamendo.data;

import java.util.ArrayList;

public class Album {


    private String id;
    private String title;
    private String artist;
    private String album_name;
    private String image;
    private String link;

    public String getSongsId() {
        return id;
    }

    public void setSongsId(String id) {
        this.id = id;
    }

    public String getSongsTitle() {
        return title;
    }

    public void setSongsTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getSongsImage() {
        return image;
    }

    public void setSongsImage(String image) {
        this.image = image;
    }

    public String getSongLink() {
        return link;
    }

    public void setSongLink(String link) {
        this.link = link;
    }
}
