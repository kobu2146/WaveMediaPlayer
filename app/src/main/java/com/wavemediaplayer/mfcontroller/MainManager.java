package com.wavemediaplayer.mfcontroller;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.fragments.OynatmaListesiFragment;

public class MainManager {
    private MainActivity activity;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int[] tabIcons = {android.R.drawable.ic_menu_camera,android.R.drawable.ic_menu_agenda,android.R.drawable.ic_menu_add};

    public MainManager(final MainActivity activity){
        this.activity=activity;
        denemeeee();

        viewPager = (ViewPager) activity.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);


        ViewPager.OnPageChangeListener onPageChangeListener=new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if(i==0){
                    activity.musicListView.setX(-i1);
                    activity.musicListView.setVisibility(View.VISIBLE);
                    activity.mainSearchLayout.setX(-i1);
                    activity.mainSearchLayout.setVisibility(View.VISIBLE);
                }else{
                    activity.musicListView.setVisibility(View.GONE);
                    activity.mainSearchLayout.setVisibility(View.GONE);
                    if (i == 1){
                        Log.e("i",""+i);
                        if (MainActivity.playList_Ekleme_Yapildi){
                          new  OynatmaListesiFragment().getCalmaListeleri();
                            MainActivity.playList_Ekleme_Yapildi = false;
                        }
                    }
                    InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(activity.edit_search.getApplicationWindowToken(), 0);
                    activity.edit_search.setVisibility(View.INVISIBLE);
                }

               // Log.e(String.valueOf(i1),String.valueOf(i));
            }

            @Override
            public void onPageSelected(int i) {
//                if(activity.musicListView!=null){
//                    if(!(i==0 || i==1)){
//                        activity.musicListView.setVisibility(View.GONE);
//                    }else{
//                        activity.musicListView.setVisibility(View.VISIBLE);
//                    }
//                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
        viewPager.setOnPageChangeListener(onPageChangeListener);
        onPageChangeListener.onPageSelected(0);



    }



    private void denemeeee(){
//        Intent serviceIntent = new Intent(activity, NotificationService.class);
//        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//        activity.startService(serviceIntent);
    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager());
        adapter.addFragment(new MainFragment(),"One");
        adapter.addFragment(new OynatmaListesiFragment(), "PlayList");
        adapter.addFragment(new DownloadFragment(), "Download");

        viewPager.setAdapter(adapter);
    }
}
