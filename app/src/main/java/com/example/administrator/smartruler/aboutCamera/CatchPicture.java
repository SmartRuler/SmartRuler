package com.example.administrator.smartruler.aboutCamera;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2016/9/11.
 */
public class CatchPicture {
    Camera mCamera;
    private  Context mContext;

    public CatchPicture(Context context,Camera camera){
        this.mContext = context;
        this.mCamera = camera;
    }

    public  void capture(){
        if(CameraPreview.mPreviewing){
            mCamera.takePicture(null, null, jpegPictureCallback);
        }
    }

    android.hardware.Camera.ShutterCallback shutterCallback = new android.hardware.Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // 按下快门瞬间会执行此处代码
        }
    };

    android.hardware.Camera.PictureCallback rawPictureCallback = new android.hardware.Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // 此处代码可以决定是否需要保存原始照片信息
        }
    };

    Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {

            String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString()
                    + File.separator
                    + "PicTest_" + System.currentTimeMillis() + ".jpg";
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            try {
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(file));
                bos.write(arg0);
                bos.flush();
                bos.close();
                scanFileToPhotoAlbum(file.getAbsolutePath());
                Toast.makeText(mContext ,"[Test] Photo take and store in" + file.toString(),Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(mContext, "Picture Failed" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

    public void scanFileToPhotoAlbum( String path) {

        MediaScannerConnection.scanFile(mContext,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

}

