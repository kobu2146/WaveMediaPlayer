package com.wavemediaplayer;

import com.wavemediaplayer.settings.FolderFragment;


public class MainMenu {
    private MainActivity mainActivity;
    private FolderFragment folderFragment;

    public MainMenu(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        folderFragment = mainActivity.folderFragment;
    }

    public void search() {
    }

    public void equalizer() {
        mainActivity.fragmentListener.addFragment(mainActivity.equalizerFragment);
    }

    public void musiclist() {
        mainActivity.fragmentListener.addFragment(mainActivity.musicListSettingsFragment);
    }

    public void folder() {
        android.support.v4.app.FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (!folderFragment.isAdded()) {
            fragmentTransaction.add(android.R.id.content, folderFragment);
        } else {
            if (folderFragment.isHidden()) {
                fragmentTransaction.show(folderFragment);
            } else {
                fragmentTransaction.hide(folderFragment);
            }
        }
        fragmentTransaction.commit();
    }
}