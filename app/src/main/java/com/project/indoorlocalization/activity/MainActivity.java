package com.project.indoorlocalization.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.project.indoorlocalization.R;

import com.project.indoorlocalization.indoormapview.MapView;
import com.project.indoorlocalization.indoormapview.Position;
import com.project.indoorlocalization.indoormapview.OnRealLocationMoveListener;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private TextView mInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapview);
        mInfoTextView = (TextView) findViewById(R.id.tv_current_location);

        try {
            mMapView.initNewMap(getAssets().open("gogo.png"), 1, 0, new Position(652, 684));
            //mMapView.initNewMap(getResources().openRawResource(R.raw.gogo), 1, 0, new Position(652, 684));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMapView.updateMyLocation(new Position(652, 684));
        mMapView.setOnRealLocationMoveListener(new OnRealLocationMoveListener() {
            @Override
            public void onMove(Position position) {
                mInfoTextView.setText(position.toString());
            }
        });
    }
}
