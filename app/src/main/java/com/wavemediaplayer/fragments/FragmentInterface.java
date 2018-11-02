package com.wavemediaplayer.fragments;

import android.support.v4.app.Fragment;

public interface FragmentInterface {
    void addFragment(Fragment ...fragment);
    boolean removeFragment(Fragment ...fragment);
    void hideFragment(Fragment ...fragment);

}
