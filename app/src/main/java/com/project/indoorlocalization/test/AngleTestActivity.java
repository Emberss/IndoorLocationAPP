package com.project.indoorlocalization.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.project.indoorlocalization.R;
import com.project.indoorlocalization.http.Http;
import com.project.indoorlocalization.utils.SensorUtil;
import com.project.indoorlocalization.utils.UriPath;
import com.project.indoorlocalization.utils.Utils;

/**
 * Created by ljm on 2017/5/13.
 */
public class AngleTestActivity extends AppCompatActivity implements View.OnClickListener{
    private SensorUtil sensorUtil;
    private Button reset, save, button;
    private TextView mAngleView;
    private TextView mImg1View;
    private TextView mImg2View;
    private TextView mImg3View;
    private EditText editText;

    private String path1="", path2="", path3="";
    private ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.layout_angle_test);


        mAngleView = (TextView)findViewById(R.id.angle);
        mImg1View = (TextView)findViewById(R.id.img1);
        mImg2View = (TextView)findViewById(R.id.img2);
        mImg3View = (TextView)findViewById(R.id.img3);
        editText = (EditText)findViewById(R.id.ip);
        reset = (Button) findViewById(R.id.reset);
        button = (Button) findViewById(R.id.button);
        save = (Button) findViewById(R.id.save);
        sensorUtil = new SensorUtil(this);

        initData();



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img1:
                uploadImg(1);
                break;
            case R.id.img2:
                uploadImg(2);
                break;
            case R.id.img3:
                uploadImg(3);
                break;
        }
    }

    private void uploadImg(int code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, code);
    }

    private void initData() {
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        mImg1View.setOnClickListener(this);
        mImg2View.setOnClickListener(this);
        mImg3View.setOnClickListener(this);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sensorUtil.init();
//                mAngleView.setText("");

                //handler.sendEmptyMessageDelayed(0, 500);
                if (editText.getText() == null || editText.getText().length() == 0) {
                    Utils.setToast(AngleTestActivity.this, "ip地址不能为空！");
                } else {
                    Http.updateIP(editText.getText().toString());
                    Utils.setToast(AngleTestActivity.this, "修改成功！");
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Http.reset();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //float angle = sensorUtil.getAngle();
                //mAngleView.setText(angle+"");
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] sensorInfos = new String[4];
                        sensorInfos[0] = sensorUtil.getAccData();
                        sensorInfos[1] = sensorUtil.getGyrData();
                        sensorInfos[2] = sensorUtil.getMagData();
                        sensorInfos[3] = sensorUtil.getOriData();
                        String s = Http.uploadImgs(sensorInfos,new String[]{"0","0"}, new String[]{path1, path2, path3});
                        Message message = Message.obtain();
                        message.obj = s;
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                float angle = sensorUtil.getAngle();
                mAngleView.setText(angle+"");
                handler.sendEmptyMessageDelayed(0, 500);
            } else if (msg.what == 1) {
                dialog.dismiss();

                String s = (String) msg.obj;
                Utils.setToast(AngleTestActivity.this, s);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK ) {
            Uri uri = data.getData();
            String p = UriPath.getImageAbsolutePath(this, uri);
//            ContentResolver contentResolver = this.getContentResolver();
//            Cursor cursor = contentResolver.query(uri, null, null, null, null);
//            if (cursor == null) return;
//            cursor.moveToFirst();
            switch (requestCode) {
                case 1:
                    path1 = p;//cursor.getString(cursor.getColumnIndex("_data"));
                    mImg1View.setText(path1);
                    break;
                case 2:
                    path2 = p;//cursor.getString(cursor.getColumnIndex("_data"));
                    mImg2View.setText(path2);
                    break;
                case 3:
                    path3 = p;//cursor.getString(cursor.getColumnIndex("_data"));
                    mImg3View.setText(path3);
                    break;
            }
            //cursor.close();
        }
    }
}
