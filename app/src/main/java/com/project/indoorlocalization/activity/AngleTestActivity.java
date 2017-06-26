package com.project.indoorlocalization.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.indoorlocalization.R;
import com.project.indoorlocalization.utils.SensorUtil;

/**
 * Created by ljm on 2017/5/13.
 */
public class AngleTestActivity extends AppCompatActivity {
    private SensorUtil sensorUtil;
    private Button reset, save;
    private TextView mAngleView;

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.layout_angle_test);


        mAngleView = (TextView)findViewById(R.id.angle);
        reset = (Button) findViewById(R.id.reset);
        save = (Button) findViewById(R.id.save);
        sensorUtil = new SensorUtil(this);

        initData();

        //sensorUtil.init();

    }

    private void initData() {
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorUtil.init();
                mAngleView.setText("");

                //handler.sendEmptyMessageDelayed(0, 500);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float angle = sensorUtil.getAngle();
                mAngleView.setText(angle+"");
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                float angle = sensorUtil.getAngle();
                mAngleView.setText(angle+"");
                handler.sendEmptyMessageDelayed(0, 500);
            }
        }
    };
}
