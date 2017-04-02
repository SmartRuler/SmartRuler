package com.example.administrator.smartruler.aboutCamera;

/**
 * Created by Administrator on 2016/10/5.
 */

import android.hardware.Camera;


public class CameraUtils {
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        return getCameraInstance(-1);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            if(cameraId == -1) {
                c = Camera.open(); // attempt to get a Camera instance
            } else {
                c = Camera.open(cameraId); // attempt to get a Camera instance
            }
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
