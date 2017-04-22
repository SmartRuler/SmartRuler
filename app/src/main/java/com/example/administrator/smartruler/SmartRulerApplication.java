package com.example.administrator.smartruler;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by popmusic on 17-4-22.
 */

public class SmartRulerApplication extends Application {
    private Bitmap mScreenCaptureBitmap;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public Bitmap getmScreenCaptureBitmap() {
        return mScreenCaptureBitmap;
    }

    public void setmScreenCaptureBitmap(Bitmap mScreenCaptureBitmap) {
        this.mScreenCaptureBitmap = mScreenCaptureBitmap;
    }
}
