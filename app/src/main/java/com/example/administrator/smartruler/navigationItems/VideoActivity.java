package com.example.administrator.smartruler.navigationItems;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.VideoView;

import com.example.administrator.smartruler.R;


/**
 * Created by Administrator on 2016/8/20.
 */
public class VideoActivity extends Activity {

    private VideoView videoView;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.video_layout);

        videoView = (VideoView)findViewById(R.id.video_view);
        initVideoPath();
        videoView.start();
    }

    private void  initVideoPath(){
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.my_video_file;
        videoView.setVideoURI(Uri.parse(uri));
    }
}
