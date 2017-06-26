package com.project.indoorlocalization.utils;

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
    private Sensor mGravitySensor;
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
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
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
        mSensorManager.registerListener(mSensorEventListener, mGravitySensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private float[] acc_data, mag_data, ori_data, gyr_data = new float[3], gra_data;
    private float[] angle_velocity;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp = 0;
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
                case Sensor.TYPE_GRAVITY:
                    gra_data = event.values.clone();
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    //gyr_data = event.values.clone();
                    angle_velocity = event.values.clone();
                    if(timestamp != 0) {
                        // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                        final float dT = (event.timestamp - timestamp) * NS2S;
                        // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                        gyr_data[0] += event.values[0] * dT;
                        gyr_data[1] += event.values[1] * dT;
                        gyr_data[2] += event.values[2] * dT;

                        float[] q = Utils.getQuaternion(angle_velocity,dT);
                        M = Utils.updateM(q, M);
                    }
                    timestamp = event.timestamp;
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
    public float[] getAccData(int i) {
        return acc_data;
    }

    public String getOriData() {
        if (ori_data != null) {
            return ori_data[0] + "|" + ori_data[1] + "|" + ori_data[2];
        }
        return "";
    }
    public float[] getOriData(int i) {
        return ori_data;
    }

    public String getMagData() {
        if (mag_data != null) {
            return mag_data[0] + "|" + mag_data[1] + "|" + mag_data[2];
        }
        return "";
    }
    public float[] getMagData(int i) {
        return mag_data;
    }

    public String getGyrData() {
        if (gyr_data != null) {
            return gyr_data[0] + "|" + gyr_data[1] + "|" + gyr_data[2];
        }
        return "";
    }
    public float[] getGyrData(int i) {
        //if (gyr_data == null) return null;
        // 将弧度转化为角度
        float[] angle = new float[3];
        angle[0] = (float) Math.toDegrees(gyr_data[0]);
        angle[1] = (float) Math.toDegrees(gyr_data[1]);
        angle[2] = (float) Math.toDegrees(gyr_data[2]);

        return angle;
    }



    private float[][] M = new float[3][3];
    private float[] vh0;
    public void init() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                M[i][j] = 0;
            }
        }
        M[0][0] = 1;
        M[1][1] = 1;
        M[2][2] = 1;

        vh0 = Utils.getProjectVector(gra_data);
        timestamp = 0;
    }

    public float getAngle() {
        return Utils.getAngle(M, vh0, gra_data);
    }

    public void stop() {
        mSensorManager.unregisterListener(mSensorEventListener);
    }
}
