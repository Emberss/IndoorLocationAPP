package com.project.indoorlocalization.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
    private Button zero, display;
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
//        if (Data.imgs.size() != 3) {
//            Utils.setToast(this, "请先拍好三张照片");
//            return;
//        }

        dialog.show();
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
                    //clearView.performClick();
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
        int index = s.indexOf('|');
        if (index == -1) {
            Utils.setToast(AngleTestActivity.this, "定位失败！");
        }else {
            String pos1 = s.substring(0, index);
            String pos2 = s.substring(index + 1);
            double x = Double.parseDouble(pos1);
            Data.x = Double.parseDouble(pos2);
            Data.y = Data.mMaxX - x;
            Utils.setToast(AngleTestActivity.this, "定位成功！");
            //clearView.performClick();
            finish();
        }
    }
}
