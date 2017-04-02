package com.example.administrator.smartruler.aboutCamera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;


public class ScannerView extends FrameLayout implements Camera.PreviewCallback {
    public static  Camera mCamera;
    private CameraPreview mPreview;

    public ScannerView(Context context) {
        super(context);
        addView(mPreview = new CameraPreview(getContext()));
    }

//    public ScannerView(Context context, AttributeSet attributeSet) {
//        super(context, attributeSet);
//        addView(mPreview = new CameraPreview(getContext()));
//    }


    public void setContentView(int res) {
        try {
            View  showPanel = View.inflate(getContext(), res, null);
            addView(showPanel);
        } catch (Exception e) {
            return;
        }
    }

    public void startCamera(int cameraId) {
        startCamera(CameraUtils.getCameraInstance(cameraId));
    }

    public void startCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mPreview.setCamera(mCamera, this);
            mPreview.initCameraPreview();
        }
    }

    public void stopCamera() {
        if (mCamera != null) {
            mPreview.stopCameraPreview();
            mPreview.setCamera(null, null);
            Camera temporary = mCamera;
            mCamera = null;
            temporary.release();
        }
    }


    public boolean cameraAvailable() {

        return mCamera == null ? false : true;
    }


    Timer timer;

    private void cancelTask() {
        timer.cancel();
    }

    private void startFreshThread(final Camera camera, final Camera.PreviewCallback callback) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e("Run", "Running");
                if (camera == null || !cameraAvailable()) {
                    cancelTask();
                    return;
                }
                camera.setOneShotPreviewCallback(callback);
            }
        }, 0, 1500);
    }

    private boolean onlyOnce;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (onlyOnce) {
            startFreshThread(camera, this);
            onlyOnce = false;
        }
    }

}
