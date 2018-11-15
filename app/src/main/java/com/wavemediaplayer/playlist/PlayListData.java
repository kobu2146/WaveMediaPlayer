package com.wavemediaplayer.playlist;

public class PlayListData {

    private String listBaslik;
    private boolean isaretlendi = false;

    public PlayListData(String listBaslik){
        this.listBaslik = listBaslik;
    }

    public String getListBaslik() {
        return listBaslik;
    }

    public void setListBaslik(String listBaslik) {
        this.listBaslik = listBaslik;
    }

    public boolean getIsaretlendi() {
        return isaretlendi;
    }

    public void setIsaretlendi(boolean isaretlendi) {
        this.isaretlendi = isaretlendi;
    }
}
