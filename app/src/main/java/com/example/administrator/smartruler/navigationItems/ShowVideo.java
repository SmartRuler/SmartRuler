package com.example.administrator.smartruler.navigationItems;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.administrator.smartruler.R;

public class ShowVideo extends Activity {

    private VideoView videoView;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        back = (Button)findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        videoView = (VideoView)findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        initVideoPath();
        videoView.setMediaController(mediaController);
        videoView.start();
    }

    private void  initVideoPath(){
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.my_video_file;
        videoView.setVideoURI(Uri.parse(uri));
    }
}
