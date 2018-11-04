package com.wavemediaplayer.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.wavemediaplayer.MainActivity;

public class FragmentListener implements FragmentInterface {
    private MainActivity mainActivity;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public FragmentListener(MainActivity mainActivity){
        this.mainActivity=mainActivity;
        fragmentManager= mainActivity.getSupportFragmentManager();
    }



    @Override
    public void addFragment(Fragment ...fragment) {
        for(Fragment frag:fragment){
            if(frag!=null){
                fragmentTransaction=fragmentManager.beginTransaction();
                if(frag.isAdded()){
                    fragmentTransaction.show(frag);
                    fragmentTransaction.commit();
                }else{
                    fragmentTransaction.add(android.R.id.content,frag);
                    fragmentTransaction.commit();
                }
            }
        }
    }

    @Override
    public boolean removeFragment(Fragment ...fragment) {
        for(Fragment frag:fragment){
            if(frag!=null){
                fragmentTransaction=fragmentManager.beginTransaction();
                if(frag.isAdded()){
                    fragmentTransaction.remove(frag);
                    fragmentTransaction.commit();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void hideFragment(Fragment ...fragment) {
        for(Fragment frag:fragment) {
            if (frag != null && !frag.isHidden()) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(frag);
                fragmentTransaction.commit();
            }

        }
    }


}
