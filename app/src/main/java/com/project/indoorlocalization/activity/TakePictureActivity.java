package com.project.indoorlocalization.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.indoorlocalization.R;
import com.project.indoorlocalization.http.Http;
import com.project.indoorlocalization.test.StartActivity;
import com.project.indoorlocalization.utils.Data;
import com.project.indoorlocalization.utils.SensorUtil;
import com.project.indoorlocalization.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

/**
 * Created by ljm on 2017/6/27.
 */
public class TakePictureActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView takePictureView;
    private TextView imgCountView;

    private TextView clearView, uploadView, previewView;
    private ImageView helpView;

    private Camera camera;
    private TextureView textureView;

    private SensorUtil sensorUtil;
    private float angle1, angle2;                              //角度信息
    private String img_sensor1,img_sensor2,img_sensor3;     //每张图片的传感器信息
    private String[] img_path = new String[3];                //图片存储路径

    private ProgressDialog dialog;
//    private int img_count = 0;

    @Override
    public void onCreate(@Nullable Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.actitvity_take_picture);

        takePictureView = (TextView)findViewById(R.id.take_picture);
        imgCountView = (TextView)findViewById(R.id.img_count);
        clearView = (TextView)findViewById(R.id.clear);
        uploadView = (TextView)findViewById(R.id.upload);
        previewView = (TextView)findViewById(R.id.preview);
        textureView = (TextureView)findViewById(R.id.textureView);
        helpView = (ImageView)findViewById(R.id.help);

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("请稍候...");

        initData();
        initHandler();
        Utils.checkTakePicturePermission(this);
    }

    private void initData() {
        setTextureViewListener();

        clearView.setOnClickListener(this);
        uploadView.setOnClickListener(this);
        previewView.setOnClickListener(this);
        helpView.setOnClickListener(this);
        imgCountView.setText(Data.imgs.size()+"");
        sensorUtil = new SensorUtil(this);

        takePictureView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear:
                Data.recycleBitmap(Data.imgs);
                imgCountView.setText(Data.imgs.size()+"");

                angle1 = angle2 = 0;
                img_sensor1 = img_sensor2 = img_sensor3 = "";
                break;
            case R.id.upload:
                uploadImages();
                break;
            case R.id.take_picture:
                takePicture();
                break;
            case R.id.help:
                showHelp();
                break;
            case R.id.preview:
                if (Data.imgs.size() == 0) {
                    Utils.setToast(TakePictureActivity.this, "请先拍照");
                } else {
                    Intent intent = new Intent(TakePictureActivity.this, editphoto.class);
                    startActivityForResult(intent, 0);
                }
                break;
        }
    }

    private void uploadImages() {
        if (Data.imgs.size() != 3) {
            Utils.setToast(this, "请先拍好三张照片");
            return;
        }

        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = Http.uploadImgs(
                        new String[]{img_sensor1,img_sensor2, img_sensor3},
                        new String[]{angle1+"", (angle2)+""},
                        img_path
                );
                Message msg = Message.obtain();
                msg.obj = s;
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void takePicture() {
        if (Data.imgs.size() == 3) {
            dialog_upload();
        } else {
            Data.imgs.add(textureView.getBitmap());
            imgCountView.setText(Data.imgs.size()+"");

            if (Data.imgs.size() == 1) {
                sensorUtil.init();
                savePicture(textureView.getBitmap(), 0);

                getImgSensorInfo(1);
            } else if (Data.imgs.size() == 2){
                angle1 = sensorUtil.getAngle();
                savePicture(textureView.getBitmap(), 1);

                getImgSensorInfo(2);

                sensorUtil.init();
            } else {
                angle2 = sensorUtil.getAngle();
                savePicture(textureView.getBitmap(), 2);

                getImgSensorInfo(3);
            }

            if (Data.imgs.size() == 3){
                dialog_upload();
            }
        }
    }

    private void getImgSensorInfo(int i) {
        String tmp = Utils.parseSensorInfo(
                sensorUtil.getAccData(0),
                sensorUtil.getMagData(0),
                sensorUtil.getOriData(0),
                sensorUtil.getGyrData(0)
        );
        switch (i) {
            case 1:
                img_sensor1 = tmp;
                break;
            case 2:
                img_sensor2 = tmp;
                break;
            case 3:
                img_sensor3 = tmp;
                break;
        }
    }

    private void savePicture(Bitmap bitmap, int i) {
        String path = Data.getPictureSavePath();
        String name = Data.imgs.size()+".png";
        Utils.saveBitmap(bitmap, path, name);

        img_path[i] = path + File.separator + name;
    }

    private void showHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("拍照帮助");
        View v  = LayoutInflater.from(this).inflate(R.layout.dialog_take_photo_hint, null);
        builder.setView(v);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void dialog_upload() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("拍照结束");
        builder.setMessage("当前已拍完三张照片，是否立即上传？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadView.performClick();
            }
        });
        builder.show();
    }

    private Handler handler;
    private void initHandler() {
        handler = new Handler(){
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:     //上传图片
                        dialog.dismiss();

                        String s = (String) message.obj;
                        loc_result(s);
                        break;
                }
            }
        };
    }

    private void setTextureViewListener() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                initCamera();
                if (camera == null) return;
                try {
                    camera.setPreviewTexture(surfaceTexture);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

        textureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera == null)
                    return;
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        if (b) {
                            //Utils.setToast(TakePictureActivity.this, "true");
                        } else {
                            //Utils.setToast(TakePictureActivity.this, "false");
                        }
                    }
                });
            }
        });
    }

    //定位结果
    private void loc_result(String s) {
        if (s.equals(getString(R.string.loc_error))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.s1));
            builder.setMessage(getString(R.string.loc_fail));
            builder.setCancelable(false);
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("重新拍照", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    clearView.performClick();
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
        int index = s.indexOf('|');
        if (index == -1) {
            Utils.setToast(TakePictureActivity.this, "定位失败！");
        }else {
            String pos1 = s.substring(0, index);
            String pos2 = s.substring(index + 1);
            double x = Double.parseDouble(pos1);
            Data.x = Double.parseDouble(pos2);
            Data.y = Data.mMaxX - x;
            Utils.setToast(TakePictureActivity.this, "定位成功！");
            clearView.performClick();
            finish();
        }
    }

    private void initCamera() {
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            if (camera == null) return;
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            camera.setDisplayOrientation(0);
            //camera.cancelAutoFocus();
            camera.autoFocus(null);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imgCountView != null) imgCountView.setText(Data.imgs.size()+"");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (clearView != null) clearView.performClick();
    }


}
