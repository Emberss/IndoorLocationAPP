package com.project.indoorlocalization.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.project.indoorlocalization.R;
import com.project.indoorlocalization.http.Http;
import com.project.indoorlocalization.utils.Data;
import com.project.indoorlocalization.utils.SensorUtil;
import com.project.indoorlocalization.utils.UriPath;
import com.project.indoorlocalization.utils.Utils;

/**
 * Created by ljm on 2017/5/13.
 */
public class AngleTestActivity extends AppCompatActivity implements View.OnClickListener{
    private SensorUtil sensorUtil;
    private Button reset, save, button;
    private Button zero, display, dataView;
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
        dataView = (Button) findViewById(R.id.data);
        reset = (Button) findViewById(R.id.reset);
        button = (Button) findViewById(R.id.button);
        zero = (Button) findViewById(R.id.zero);
        display = (Button) findViewById(R.id.display);
        save = (Button) findViewById(R.id.save);
        sensorUtil = new SensorUtil(this);

        initData();


        handler.sendEmptyMessageDelayed(0, 500);
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
            case R.id.data:
                Intent intent = new Intent(AngleTestActivity.this, MediaRecorderActivity.class);
                startActivity(intent);
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
        dataView.setOnClickListener(this);

        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorUtil.init();
                mAngleView.setText("");
            }
        });
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float angle = sensorUtil.getAngle();
                mAngleView.setText(angle+"");
            }
        });

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
//                dialog.show();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String[] sensorInfos = new String[4];
//                        sensorInfos[0] = sensorUtil.getAccData();
//                        sensorInfos[1] = sensorUtil.getGyrData();
//                        sensorInfos[2] = sensorUtil.getMagData();
//                        sensorInfos[3] = sensorUtil.getOriData();
//                        String s = Http.uploadImgs(sensorInfos,new String[]{"0","0"}, new String[]{path1, path2, path3});
//                        Message message = Message.obtain();
//                        message.obj = s;
//                        message.what = 1;
//                        handler.sendMessage(message);
//                    }
//                }).start();
                uploadImages();
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
            } else if (msg.what == 111) {
                dialog.dismiss();
                String s = (String) msg.obj;
                loc_result(s);
            }
        }
    };

    private void uploadImages() {
        dialog.show();
        handler.removeCallbacksAndMessages(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = Http.uploadImgs(
                        new String[]{"","", ""},
                        new String[]{30+"", (30)+""},
                        new String[]{path1,path2,path3}
                );
                Message msg = Message.obtain();
                msg.obj = s;
                msg.what = 111;
                handler.sendMessage(msg);
            }
        }).start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = Data.getPictureSavePath();
        if (resultCode == Activity.RESULT_OK ) {
            Uri uri = data.getData();
            String p = UriPath.getImageAbsolutePath(this, uri);
            String name = requestCode+".png";
            switch (requestCode) {
                case 1:
                    Bitmap bitmap = Utils.resizeBitmap(Utils.getBitmap(p), 228, 128);
                    Utils.saveBitmap(bitmap, path, name);
                    path1 = path +"/"+ name;
                    mImg1View.setText(path1);
                    break;
                case 2:
                    bitmap = Utils.resizeBitmap(Utils.getBitmap(p), 228, 128);
                    Utils.saveBitmap(bitmap, path, name);
                    path2 = path +"/"+ name;
                    mImg2View.setText(path2);
                    break;
                case 3:
                    bitmap = Utils.resizeBitmap(Utils.getBitmap(p), 228, 128);
                    Utils.saveBitmap(bitmap, path, name);
                    path3 = path +"/"+ name;
                    mImg3View.setText(path3);
                    break;
            }
        }
    }



    private void loc_result(String s) {
        dialog.dismiss();

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
            builder.show();
        }

        String[] data = s.split("#");
        if (data == null || data.length < 5) {
            Utils.setToast(AngleTestActivity.this, "定位失败！\n"+s);
            Data.labelShowing = false;
        }else {

            Data.label1 = data[2];
            Data.label2 = data[3];
            Data.label3 = data[4];
            double x = 0;
            try {
                x = Double.parseDouble(data[0]);
                Data.x = Double.parseDouble(data[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            Data.y = Data.mMaxX - x;
            Utils.setToast(AngleTestActivity.this, "定位成功！");
            Data.labelShowing = true;
            finish();
        }
    }
}
