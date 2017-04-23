package com.example.administrator.smartruler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.smartruler.aboutCamera.ScannerView;
import com.example.administrator.smartruler.aboutCamera.ScreenShotService;
import com.example.administrator.smartruler.navigationItems.Photometer;
import com.example.administrator.smartruler.navigationItems.SettingDialog;
import com.example.administrator.smartruler.navigationItems.ShareDialog;
import com.example.administrator.smartruler.navigationItems.VideoActivity;
import com.example.administrator.smartruler.sensor.OrientationDetector;
import com.example.administrator.smartruler.sensor.OrientationService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_MEDIA_PROJECTION = 18;
    public static final int GETDISTANCE = 1;
    public static final int GETHEIGHT = 0;
    public static int changeDirection = 1;

    private ScannerView scannerView;
    private OrientationService.OrientationBinder mBinder;
    private ScreenShotService.ScreenShotBinder screenShotBinder;
    private Thread thread;
    private TextView measurement_text, orientation_text;
    private TextView show_h, show_H, show_h_plus_H;
    private ImageButton changeOrientation_btn;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case GETDISTANCE:
                    float distance = msg.getData().getFloat("distance");
                    if(distance < 0){
                        measurement_text.setText(R.string.error);
                    }else {
                        measurement_text.setText(""+ distance);
                    }
                    break;
                case GETHEIGHT:
                    measurement_text.setText("" + msg.getData().getFloat("height"));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        scannerView = new ScannerView(this);
        scannerView.setContentView(R.layout.activity_main);
        setContentView(scannerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        measurement_text = (TextView)findViewById(R.id.measurement);
        orientation_text = (TextView)findViewById(R.id.orientation);
        show_h = (TextView)findViewById(R.id.show_h);
        show_H = (TextView)findViewById(R.id.show_H);
        show_h_plus_H = (TextView)findViewById(R.id.show_h_plus_H);
        changeOrientation_btn = (ImageButton) findViewById(R.id.changeOrientation);
        changeOrientation_btn.setOnClickListener(this);

        startOrientationService();
        showMeasureInfomation();

        requestCapturePermission();

        if(thread == null){
            thread = new Thread(){
              @Override
                public void run(){

                  while(true){
                      try{
                          Thread.sleep(500);
                      }catch(InterruptedException e){
                          e.printStackTrace();
                      }
                      if(OrientationService.STARTSERVICE){
                          if(changeDirection == GETDISTANCE){
                              Message msg = Message.obtain();
                              msg.what = GETDISTANCE;
                              Bundle data = new Bundle();
                              data.putFloat("distance", OrientationDetector.resultOfDistance);
                              msg.setData(data);
                              handler.sendMessage(msg);

                          }else if(changeDirection == GETHEIGHT){
                              Message msg = Message.obtain();
                              msg.what = GETHEIGHT;
                              Bundle data = new Bundle();
                              data.putFloat("height", OrientationDetector.resultOfHeight);
                              msg.setData(data);
                              handler.sendMessage(msg);
                          }
                      }
                  }
              }
            };
            thread.start();
        }
    }

    private void requestCapturePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)  return;
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:

                if (resultCode == RESULT_OK && data != null) {
                    ScreenShotService.setResultData(data);
                    Intent intent = new Intent(getApplicationContext(), ScreenShotService.class);
                    startService(intent);
                    bindService(intent, screenShotServiceConnection, BIND_AUTO_CREATE);
                }
                break;
        }
    }

    private void showMeasureInfomation(){
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        double h = Double.parseDouble(pref.getString("h","150")) / 100.0;
        double H = Double.parseDouble(pref.getString("H","0")) / 100.0;
        double h_plus_H = h + H;
        show_h.setText("h: " + h + " m");
        show_H.setText("H: " + H + " m");
        show_h_plus_H.setText("h+H = " + (double)Math.round(h_plus_H*100)/100);
        OrientationDetector.set_h_plus_H(h_plus_H - 0.1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera(-1);
        Log.e(TAG, "onResume");
    }

    private void startOrientationService(){
        Intent startServiceIntent = new Intent(this, OrientationService.class);
        startService(startServiceIntent);
        bindService(startServiceIntent,connection,BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (OrientationService.OrientationBinder)iBinder;

        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinder = null;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopOrientationService();
        unbindService(connection);
        changeDirection = 1;
    }

    private void stopOrientationService(){
        Intent stopServiceIntent = new Intent(this, OrientationService.class);
        stopService(stopServiceIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeOrientation:
                changeDirection = (changeDirection + 1) % 2;
                if(changeDirection == GETDISTANCE){
                    orientation_text.setText(R.string.distance);
                }else if(changeDirection == GETHEIGHT){
                    orientation_text.setText(R.string.height);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    private ServiceConnection screenShotServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            screenShotBinder = (ScreenShotService.ScreenShotBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.screenshots:
                screenShotBinder.startScreenShot();
                Toast.makeText(MainActivity.this,"ScreenSot Successfully!",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_photometer) {
            Intent intent = new Intent(MainActivity.this, Photometer.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            SettingDialog dialog = new SettingDialog(MainActivity.this);
            dialog.setOnDialogListener(new SettingDialog.OnDialogListener(){
                @Override
                public void onInput(String inputStr_h, String inputStr_H){
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("h", inputStr_h);
                    editor.putString("H", inputStr_H);
                    editor.apply();
                    showMeasureInfomation();
                }
            });
            dialog.show();
        } else if (id == R.id.nav_share) {
            ShareDialog dialog = new ShareDialog(MainActivity.this);
            dialog.show();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
