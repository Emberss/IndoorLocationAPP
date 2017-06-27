package com.project.indoorlocalization.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.project.indoorlocalization.R;
import com.project.indoorlocalization.utils.Data;
import com.project.indoorlocalization.utils.Utils;

import java.io.IOException;
import java.sql.Date;

/**
 * Created by ljm on 2017/6/27.
 */
public class TakePictureActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView takePictureView;
    private TextView imgCountView;

    private TextView clearView, uploadView, previewView;

    private Camera camera;
    private TextureView textureView;

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

        initData();
        Utils.checkTakePicturePermission(this);
    }

    private void initData() {
        setTextureViewListener();

        clearView.setOnClickListener(this);
        uploadView.setOnClickListener(this);
        previewView.setOnClickListener(this);

        takePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Data.imgs.size() == 3) {
                    dialog_upload();
                } else {
                    Data.imgs.add(textureView.getBitmap());
                    savePicture(textureView.getBitmap());
                    imgCountView.setText(Data.imgs.size()+"");

                    if (Data.imgs.size() == 3){
                        dialog_upload();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear:
                Data.recycleBitmap(Data.imgs);
                imgCountView.setText(Data.imgs.size()+"");
                break;
            case R.id.upload:
                break;
            case R.id.preview:
                break;
        }
    }

    private void savePicture(Bitmap bitmap) {
        String path = Data.getPictureSavePath();
        String name = Data.imgs.size()+".png";
        Utils.saveBitmap(bitmap, path, name);
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

            }
        });
        builder.show();
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
}
