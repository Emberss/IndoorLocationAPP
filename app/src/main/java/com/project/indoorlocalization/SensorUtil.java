package com.project.indoorlocalization;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by ljm on 2017/4/13.
 */
public class SensorUtil {
    private Context context;
    private SensorManager mSensorManager;

    private Sensor mMagneticSensor;
    private Sensor mAccelerometerSensor;
    private Sensor mGyroscopeSensor;
    private Sensor mOrientationSensor;

    public SensorUtil(Context context) {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        initSensor();
        registerSensor();
    }

    private void initSensor() {
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void registerSensor() {
        mSensorManager.registerListener(mSensorEventListener, mAccelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorEventListener, mGyroscopeSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorEventListener, mOrientationSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorEventListener, mMagneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private float[] acc_data, mag_data, ori_data, gyr_data;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION:
                    ori_data = event.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    acc_data = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mag_data = event.values.clone();
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyr_data = event.values.clone();
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public String getAccData() {
        if (acc_data != null) {
            return acc_data[0] + "|" + acc_data[1] + "|" + acc_data[2];
        }
        return "";
    }
    public String getOriData() {
        if (ori_data != null) {
            return ori_data[0] + "|" + ori_data[1] + "|" + ori_data[2];
        }
        return "";
    }
    public String getMagData() {
        if (mag_data != null) {
            return mag_data[0] + "|" + mag_data[1] + "|" + mag_data[2];
        }
        return "";
    }
    public String getGyrData() {
        if (gyr_data != null) {
            return gyr_data[0] + "|" + gyr_data[1] + "|" + gyr_data[2];
        }
        return "";
    }
}
