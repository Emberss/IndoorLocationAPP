package com.project.indoorlocalization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.indoorlocalization.R;

import com.project.indoorlocalization.indoormapview.MapView;
import com.project.indoorlocalization.indoormapview.Position;
import com.project.indoorlocalization.indoormapview.OnRealLocationMoveListener;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MapView mMapView;
    private TextView mInfoTextView;

    private ImageView takePictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapview);
        mInfoTextView = (TextView) findViewById(R.id.tv_current_location);
        takePictureView = (ImageView)findViewById(R.id.take_picture);

        initData();
        initMapView();

    }

    private void initData() {
        takePictureView.setOnClickListener(this);
    }

    private void initMapView() {
        try {
            mMapView.initNewMap(getAssets().open("gogo.png"), 1, 0, new Position(652, 684));
            //mMapView.initNewMap(getResources().openRawResource(R.raw.gogo), 1, 0, new Position(652, 684));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMapView.updateMyLocation(new Position(652, 684));
        //mMapView.toggleRealLocationSymbol();
        mMapView.setOnRealLocationMoveListener(new OnRealLocationMoveListener() {
            @Override
            public void onMove(Position position) {
                mInfoTextView.setText(position.toString());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_picture:
                Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
                startActivity(intent);
                break;
        }
    }
}
