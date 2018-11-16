package com.wavemediaplayer.main;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.wavemediaplayer.mfcontroller.MainManager;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private final int SWIPE_THRESHOLD = 150;
    private final int SWIPE_MIN_DISTANCE = 120;
    private final int SWIPE_MAX_OFF_PATH = 250;
    private final int SWIPE_THRESHOLD_VELOCITY = 200;
    private MainManager mainManager;

    public GestureListener(MainManager mainManager){
        this.mainManager = mainManager;
    }

@Override
public boolean onDown(MotionEvent e) {
        return true;
        }

@Override
public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if ((e1.getAction() == MotionEvent.ACTION_DOWN) &&
        (e2.getAction() == MotionEvent.ACTION_MOVE) &&
        Math.abs(distanceX) > SWIPE_THRESHOLD) {

        if (e2.getPointerCount() > 1) {
        if (distanceX > 0)
        onTwoFingerSwipeLeft();
        else
        onTwoFingerSwipeRight();
        return true;
        }

        }
        return super.onScroll(e1, e2, distanceX, distanceY);
        }

@Override
public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
        float velocityY) {

        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
        if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
        || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY)
        return false;
        if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE)
        onSwipeUp();
        else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE)
        onSwipeDown();
        } else {
        if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY)
        return false;
        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE)
        mainManager.onSwipeLeft();
        else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE)
        mainManager.onSwipeRight();
        }
        return super.onFling(e1, e2, velocityX, velocityY);
        }





public void onSwipeUp() {}

public void onSwipeDown() {}

public void onTwoFingerSwipeLeft() {}

public void onTwoFingerSwipeRight() {}

}
