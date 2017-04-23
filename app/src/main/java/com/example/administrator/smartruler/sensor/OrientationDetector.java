package com.example.administrator.smartruler.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.administrator.smartruler.MainActivity;

/**
 * Created by Administrator on 2016/9/16.
 */
public class OrientationDetector implements SensorEventListener {
    private static final int DISTANCE = 1;
    private static final int HEIGHT = 0;
    private float[] accelerometerValues = new float[3];
    private float[] magneticValues = new float[3];
    private float[] R = new float[9];
    private static float[] values = new float[3];
    private static double angleOfX;  //pitch
    private static double angleOfY;  //roll
    private static double angleOfZ;
    private double tempD;
    private double tempH;
    private static double h_plus_H;//手机摄像头距地面的垂直距离
    public static float resultOfDistance;
    public static float resultOfHeight;

    public OrientationDetector(Context context){
        super();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static void set_h_plus_H(double mh_plus_H){
        h_plus_H = mh_plus_H;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometerValues = sensorEvent.values.clone();
        }else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magneticValues = sensorEvent.values.clone();
        }
        SensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
        SensorManager.getOrientation(R,values);
        angleOfX = values[1];
        angleOfY = values[2];
        angleOfZ = values[0];
        if(MainActivity.changeDirection == MainActivity.GETDISTANCE){
            getDistance();
        }else if(MainActivity.changeDirection == MainActivity.GETHEIGHT){
            double distance = tempD; //用于垂直测距时事先确定好水平距离
            getHeight(distance);
        }
    }

    private void getDistance(){
        double calcAngle = getCalcAngle();
        if(calcAngle >= Math.PI/2){
            resultOfDistance = (float)-1;
        }else{
            tempD = h_plus_H / Math.tan(calcAngle);
            tempD = calibrate(tempD, calcAngle);
            resultOfDistance = (float)(Math.round(tempD * 10) / 10.0);//保留一位小数
        }
    }

    private void getHeight(double distance){
        double calAngle = getCalcAngle();
        double tempL = h_plus_H / Math.tan(calAngle);
        tempL = calibrate(tempL, calAngle);
        tempH = h_plus_H*(tempL - distance) / tempL;
        resultOfHeight = (float)(Math.round(tempH * 10) / 10.0);
    }

    private double getCalcAngle(){
        //double t = -3.518 + 3.511*Math.cos(angleOfY) +3.507*Math.cos(angleOfX)-2.487*Math.cos(angleOfX)*Math.cos(angleOfY);
        //return Math.asin(t);
        double a = Math.cos(angleOfY);
        double b =  Math.cos(angleOfX);
        return Math.asin( a*b);
    }

    private double calibrate(double value, double calcAngle){
        double correction;
        double fb = 1 / Math.tan(calcAngle); //误差校正
        if(fb <= 1.33){
            correction = 0;
        }else{
            correction = (fb-1.33) / 11.0;
        }
        return value / (1-correction);
    }

}
