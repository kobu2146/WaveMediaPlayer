package com.wavemediaplayer.adapter;

public class MusicData {

    private String title;
    private String artist;
    private Integer image;
    private String duration;
    private String location;
    private String id;
    private boolean isaretlendi = false;



    public MusicData(String title, String artist, Integer image, String duration, String location, String id) {
        this.title = title;
        this.artist = artist;
        this.image = image;
        this.duration = duration;
        this.location = location;
        this.id = id;
    }

    public String getTitles() {
        return title;
    }

    public void setTitles(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getImages() {
        return image;
    }

    public void setImages(Integer image) {
        this.image = image;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIds() {
        return id;
    }

    public void setIds(String id) {
        this.id = id;
    }

    public boolean getIsaretlendi() {
        return isaretlendi;
    }

    public void setIsaretlendi(boolean isaretlendi) {
        this.isaretlendi = isaretlendi;
    }


}
