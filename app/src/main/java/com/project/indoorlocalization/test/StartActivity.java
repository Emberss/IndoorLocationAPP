package com.project.indoorlocalization.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.project.indoorlocalization.R;
import com.project.indoorlocalization.activity.MainActivity;

/**
 * Created by ljm on 2017/5/4.
 */
public class StartActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.layout_first);

        TextView upload_info = (TextView)findViewById(R.id.upload_info);
        TextView media_recorder = (TextView)findViewById(R.id.media_recorder);
        TextView index = (TextView)findViewById(R.id.index);
        TextView test = (TextView)findViewById(R.id.test);

        if (upload_info == null || media_recorder == null || test == null || index == null) return;
        upload_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, IndoorLocalization.class);
                startActivity(intent);
            }
        });
        media_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MediaRecorderActivity.class);
                startActivity(intent);
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, AngleTestActivity.class);
                startActivity(intent);
            }
        });
        index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
