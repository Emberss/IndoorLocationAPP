package com.project.indoorlocalization;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ljm on 2017/4/13.
 */
public class IndoorLocalization extends AppCompatActivity {
    private TextView latitude, longitude, imagepath;
    private Button select, upload;
    private SensorUtil sensorUtil;
    private ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);

        setContentView(R.layout.layout_main);

        initView();
        initData();
    }

    private void initView() {
        latitude = (TextView)findViewById(R.id.latitude);
        longitude = (TextView)findViewById(R.id.longitude);
        imagepath = (TextView)findViewById(R.id.path);
        select = (Button) findViewById(R.id.select);
        upload = (Button) findViewById(R.id.upload);
    }

    private String[] sensorInfos = new String[4];
    private void initData() {
        sensorUtil = new SensorUtil(this);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("上传中，请稍候...");

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImagePath == null || mImagePath.length() == 0) {
                    setToast("请先选择图片");
                    return;
                }
                dialog.show();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sensorInfos[0] = sensorUtil.getAccData();
                        sensorInfos[1] = sensorUtil.getGyrData();
                        sensorInfos[2] = sensorUtil.getMagData();
                        sensorInfos[3] = sensorUtil.getOriData();
                        String result = Http.getInfo(sensorInfos, new String[]{mImagePath});
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }).start();

            }
        });
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dialog.dismiss();
                    String result = (String)msg.obj;
                    if (result.length() == 0) {
                        setToast("上传失败！");
                        return;
                    } else {
                        setToast("上传成功！");
                        result = result.substring(result.indexOf('|'));
                        int i = result.indexOf('('), j = result.indexOf(',');
                        String s1 = result.substring(i + 1, j);
                        String s2 = result.substring(j + 2, result.length() - 1);
                        longitude.setText(s1);
                        latitude.setText(s2);
                    }
                    //setToast(sensorInfos[0]);
                    break;
            }
        }
    };

    private String mImagePath;
    @Override
    public void onActivityResult(int requestcode, int resultCode, Intent data) {
        super.onActivityResult(requestcode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            ContentResolver cv = getContentResolver();
            Cursor cursor = cv.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                mImagePath = cursor.getString(cursor.getColumnIndex("_data"));
                imagepath.setText(mImagePath);
                cursor.close();
            }
        }
    }

    private void setToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
