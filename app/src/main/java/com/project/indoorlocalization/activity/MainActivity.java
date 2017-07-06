package com.project.indoorlocalization.activity;

import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.indoorlocalization.R;

import com.project.indoorlocalization.indoormapview.MapView;
import com.project.indoorlocalization.indoormapview.Position;
import com.project.indoorlocalization.indoormapview.OnRealLocationMoveListener;
import com.project.indoorlocalization.test.AngleTestActivity;
import com.project.indoorlocalization.test.StartActivity;
import com.project.indoorlocalization.utils.Data;
import com.project.indoorlocalization.utils.Utils;

import java.io.IOException;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MapView mMapView;
    private TextView mInfoTextView;

    private ImageView takePictureView;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapview);
        mInfoTextView = (TextView) findViewById(R.id.tv_current_location);
        takePictureView = (ImageView)findViewById(R.id.take_picture);

        initData();
        initMapView();

        //Utils.setToast(this, Utils.readShopData(this));
    }

    private void initData() {
        dialog  = new ProgressDialog(this);

        takePictureView.setOnClickListener(this);
        takePictureView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Intent intent = new Intent(MainActivity.this, StartActivity.class);
                //startActivity(intent);
//                Random random = new Random();
//                Data.x = random.nextInt(1200) + 20;
//                Data.y = random.nextInt(1500) + 1300;

//                try {
//                    mMapView.initNewMap(getAssets().open("gogo.png"), 0.8, 0, new Position(Data.x, Data.y));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                updateLocation();

                Intent intent = new Intent(MainActivity.this, AngleTestActivity.class);
                startActivity(intent);

                return true;
            }
        });
    }

    private void initMapView() {
        try {
            mMapView.initNewMap(getAssets().open("gogo.png"), 0.8, 0, new Position(Data.x, Data.y));
            //mMapView.initNewMap(getResources().openRawResource(R.raw.gogo), 1, 0, new Position(652, 684));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        mMapView.setContext(this);

        mMapView.updateMyLocation(new Position(652, 1712));
        //mMapView.toggleRealLocationSymbol();
        mMapView.setOnRealLocationMoveListener(new OnRealLocationMoveListener() {
            @Override
            public void onMove(Position position) {
                mInfoTextView.setText(position.toString());
            }
        });
        mMapView.centerMyLocation();

        mMapView.setListener(new MapView.OnShopSymbolClickListener() {
            @Override
            public void onClick(String s) {
                Utils.setToast(MainActivity.this, s);
            }
        });
//        mMapView.ff();
        //mMapView.scrollBy(0, -100);
        //mMapView.transformToMapCoordinate(new float[]{0, -100});

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_picture:
                dialog.show();

                Intent intent = new Intent(MainActivity.this, TakePictureActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void updateLocation() {
        mInfoTextView.setText(new Position(Data.x, Data.y).toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(300);
                    mMapView.updateMyLocation(new Position(Data.x, Data.y));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dialog != null && dialog.isShowing()) dialog.dismiss();
//        Random random = new Random();
//        Data.x = random.nextInt(1200) + 20;
//        Data.y = random.nextInt(1500) + 1300;
        //mMapView.centerMyLocation();
        updateLocation();
    }
}
