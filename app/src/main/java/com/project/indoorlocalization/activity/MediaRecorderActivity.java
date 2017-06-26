package com.project.indoorlocalization.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.project.indoorlocalization.R;
import com.project.indoorlocalization.utils.SensorUtil;
import com.project.indoorlocalization.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljm on 2017/5/4.
 */
public class MediaRecorderActivity extends AppCompatActivity{
    private TextView record, mCurrTimeView;
    private TextView mSettingView, mFrameView, mBitRateView;
    //private ImageView preview;
    private final String START = "开始拍摄";
    private final String END = "结束拍摄";

    private int mWidth = 1920, mHeight = 1080;
    private int mFrameRate = 30, mBitRate = 10;
    private long mCurrTime = 0;     //录像计时器
    private boolean isRecord = false;

    private Camera mCamera;
    private TextureView mTextureView;
    private MediaRecorder mediaRecorder;
    private File outputFile;

    private SensorUtil sensorUtil;
    private ProgressDialog dialog;
    private int mCurrFrameNum = 0;
    private float[] frame0_gyrData;
    private List<Integer> frame_num_list;
    private List<float[]> ori_data_list, acc_data_list, mag_data_list, gyr_data_list;
    private List<Float> angle_diff_list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.layout_mediarecorder);

        mTextureView = (TextureView)findViewById(R.id.textureView);
        record = (TextView)findViewById(R.id.record);
        mCurrTimeView = (TextView)findViewById(R.id.time);
        mSettingView = (TextView)findViewById(R.id.setting);
        mFrameView = (TextView)findViewById(R.id.frame);
        mBitRateView = (TextView)findViewById(R.id.bit_rate);

        initData();
        setTextureListener();
    }

    private void initData() {
        record.setText(START);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecord) {
                    record.setText(END);
                    startRecord();
                    isRecord = true;
                } else {
                    record.setText(START);
                    stopRecord();
                    isRecord = false;
                }
            }
        });
        mSettingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingDialog();
            }
        });
        String s = getString(R.string.frame) + mFrameRate + getString(R.string.frame_unit);
        mFrameView.setText(s);
        s = getString(R.string.bit_rate) + mBitRate + getString(R.string.bit_rate_unit);
        mBitRateView.setText(s);

        sensorUtil = new SensorUtil(this);
        dialog = new ProgressDialog(this);
        angle_diff_list = new ArrayList<>();
        frame_num_list = new ArrayList<>();
        ori_data_list = new ArrayList<>();
        mag_data_list = new ArrayList<>();
        acc_data_list = new ArrayList<>();
        gyr_data_list = new ArrayList<>();
    }

    private void showSettingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view, null);
        builder.setView(view);
        builder.setTitle("设置");
        final EditText frame = (EditText)view.findViewById(R.id.frame_num);
        final EditText bit_rate = (EditText)view.findViewById(R.id.bit_rate_num);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                if (frame.getText().length() == 0 || bit_rate.getText().length() == 0) {
                    Utils.setToast(MediaRecorderActivity.this, getString(R.string.error));
                    return;
                }
                int num1 = Integer.parseInt(frame.getText().toString());
                int num2 = Integer.parseInt(bit_rate.getText().toString());
                if (num1 < 10 || num1 > 30 || num2 < 5 || num2 > 20) {
                    Utils.setToast(MediaRecorderActivity.this, getString(R.string.error));
                } else {
                    mFrameRate = num1;
                    mBitRate = num2;
                    String s = getString(R.string.frame) + mFrameRate + getString(R.string.frame_unit);
                    mFrameView.setText(s);
                    s = getString(R.string.bit_rate) + mBitRate + getString(R.string.bit_rate_unit);
                    mBitRateView.setText(s);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog_tmp = builder.create();
        dialog_tmp.show();
    }

    private void setTextureListener() {
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                initCamera();
                try {
                    mCamera.setPreviewTexture(surface);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                if (!isRecord) return;

                //Utils.setToast(MediaRecorderActivity.this, "ss");

                //float[] data = sensorUtil.getGyrData(0);
                if (mCurrFrameNum == 0) {
                    //frame0_gyrData = data;
                    angle_diff_list.add(0f);
                } else {
                    angle_diff_list.add(sensorUtil.getAngle());
                }
                frame_num_list.add(mCurrFrameNum);
                ori_data_list.add(sensorUtil.getOriData(0));
                acc_data_list.add(sensorUtil.getAccData(0));
                mag_data_list.add(sensorUtil.getMagData(0));
                gyr_data_list.add(sensorUtil.getGyrData(0));

//                if (frame0_gyrData != null) {
//                    float[] tmp = new float[3];
//                    tmp[0] = data[0] - frame0_gyrData[0];
//                    tmp[1] = data[1] - frame0_gyrData[1];
//                    tmp[2] = data[2] - frame0_gyrData[2];
//                    angle_diff_list.add(tmp);
//                }
                ++mCurrFrameNum;
            }
        });
    }

    private void initCamera() {
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setDisplayOrientation(0);
        mCamera.cancelAutoFocus();
        mCamera.setParameters(parameters);
    }

    private void initRecorder() {
        if (mCamera == null) return;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoSize(mWidth, mHeight);
        mediaRecorder.setVideoFrameRate(mFrameRate);
        mediaRecorder.setVideoEncodingBitRate(mBitRate * 1024 * 1024);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        createSaveDir();
        mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSaveDir() {
        File dir = new File(Environment.getExternalStorageDirectory()+File.separator+"sampleVideo/");
        if (!dir.exists()) dir.mkdir();
        try {
            String name = new Date(System.currentTimeMillis()).toString();
            outputFile = new File(dir, name + ".mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecord() {
        initRecorder();
        mediaRecorder.start();

        countTime();
        sensorUtil.init();
    }

    private void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mediaRecorder = null;
        mCurrTime = 0;
        handler.removeCallbacksAndMessages(null);
        mCurrTimeView.setText("");

        dialog.setMessage("正在保存...");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = saveData();
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = flag;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private boolean saveData() {
        if (angle_diff_list.size() != frame_num_list.size()
                || angle_diff_list.size() != ori_data_list.size()) {
             //Utils.setToast(this, "数据不对应！");
            return false;
        } else {
            JSONArray array = new JSONArray();
            for (int i = 0; i < frame_num_list.size(); ++i) {
                int frame_num = frame_num_list.get(i);

                float[] acc_data = acc_data_list.get(i);
                float[] mag_data = mag_data_list.get(i);
                float[] ori_data = ori_data_list.get(i);
                float[] gyr_data = gyr_data_list.get(i);
                float angle = angle_diff_list.get(i);
                JSONObject acc_object = new JSONObject();
                JSONObject mag_object = new JSONObject();
                JSONObject ori_object = new JSONObject();
                JSONObject gyr_object = new JSONObject();
                //JSONObject angle_object = new JSONObject();
                if (acc_data == null || mag_data == null || ori_data == null || gyr_data == null) {
                    return false;
                }
                try {
                    acc_object.put("x", acc_data[0]);
                    acc_object.put("y", acc_data[1]);
                    acc_object.put("z", acc_data[2]);

                    mag_object.put("x", mag_data[0]);
                    mag_object.put("y", mag_data[1]);
                    mag_object.put("z", mag_data[2]);

                    ori_object.put("x", ori_data[0]);
                    ori_object.put("y", ori_data[1]);
                    ori_object.put("z", ori_data[2]);

                    gyr_object.put("x", gyr_data[0]);
                    gyr_object.put("y", gyr_data[1]);
                    gyr_object.put("z", gyr_data[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject tmp = new JSONObject();
                try {
                    tmp.put("frame", frame_num);
                    tmp.put("acc_data", acc_object);
                    tmp.put("mag_data", mag_object);
                    tmp.put("ori_data", ori_object);
                    tmp.put("gyr_data", gyr_object);
                    tmp.put("angle", angle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(tmp);
            }
            JSONObject object = new JSONObject();
            try {
                object.put("data", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            File dir = new File(Environment.getExternalStorageDirectory()+File.separator+"sampleVideo/");
            String name = new Date(System.currentTimeMillis()).toString()+"("+(mCurrFrameNum-1)+"帧)";
            name += (System.currentTimeMillis() % 10000);   //防止视频文件重名
            File newFile = new File(dir, name + ".mp4");
            outputFile.renameTo(newFile);

            //写入文件
            File dataFile = null;
            try {
                dataFile = new File(dir, name + ".json");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (dataFile == null) return false;
                FileOutputStream file = new FileOutputStream(dataFile);
                file.write(object.toString().getBytes());
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void countTime() {
        mCurrTimeView.setText("00:00");
        mCurrTime = 0;
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (mCurrTime >= 3600) return;
                ++mCurrTime;
                long minute = mCurrTime / 60;
                long second = mCurrTime % 60;
                String m = "0" + minute;
                if (minute > 9) m = ""+minute;
                String s = "0" + second;
                if (second > 9) s = ""+second;
                mCurrTimeView.setText(m + ":" + s);
                handler.sendEmptyMessageDelayed(0, 1000);
            } else if (msg.what == 1) {
                dialog.dismiss();
                mCurrFrameNum = 0;

                boolean flag = (boolean)msg.obj;
                if (flag) {
                    Utils.setToast(MediaRecorderActivity.this, "保存成功！");
                } else {
                    Utils.setToast(MediaRecorderActivity.this, "保存失败！");
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sensorUtil.stop();
    }
}
