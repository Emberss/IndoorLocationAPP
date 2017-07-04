package com.project.indoorlocalization.indoormapview;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class LocationSymbol extends BaseMapSymbol {

    private float mRadius;
    private Paint mCirclePaint = null;
    private Paint mRangeCirclePaint = null;
    private Paint mCircleEdgePaint = null;
    private float mRangeRadius = 0;
    private int mRangeCircleColor = 0x00ffffff;


    private Rect mClickRect = new Rect(0, 0, 0, 0);


    public LocationSymbol(int mMainColor, int mEdgeColor, float mRadius) {
        this.mThreshold = 0f;
        this.mRotation = 0f;
        this.mVisibility = true;
        this.mOnMapSymbolListener = null;
        this.mRadius = mRadius;
        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Style.FILL);
        mCirclePaint.setColor(mMainColor);
        mCirclePaint.setAntiAlias(true);
        mCircleEdgePaint = new Paint();
        mCircleEdgePaint.setStyle(Style.STROKE);
        mCircleEdgePaint.setColor(mEdgeColor);
        mCirclePaint.setAntiAlias(true);

    }

    public void setRangeCircle(float rangeCircleRadius, int rangeCircleColor) {
        mRangeRadius = rangeCircleRadius;
        mRangeCirclePaint = new Paint();
        mRangeCirclePaint.setStyle(Style.FILL);
        mRangeCircleColor = rangeCircleColor;
        mRangeCirclePaint.setColor(mRangeCircleColor);
    }

    @Override
    public void draw(Canvas canvas, Matrix matrix, float scale) {
        if (!mVisibility || scale < mThreshold)
            return;
        float[] locationValue = new float[]{(float) mLocation.getX(),
                (float) mLocation.getY()};
        matrix.mapPoints(locationValue);

        // paint range circle
        if (mRangeCirclePaint != null) {
            float radiusValue = mRangeRadius * scale;
            canvas.drawCircle(locationValue[0], locationValue[1], radiusValue,
                    mRangeCirclePaint);
        }
        // paint circle edge
        canvas.drawCircle(locationValue[0], locationValue[1], mRadius,
                mCirclePaint);
        // paint circle
        canvas.drawCircle(locationValue[0], locationValue[1], mRadius + 1,
                mCircleEdgePaint);


        int left = (int)(mLocation.getX() - mRadius);
        int right = (int)(left + 2 * mRadius);
        int top = (int)(mLocation.getY() - mRadius);
        int bottom = (int)(top + 2 * mRadius);
        mClickRect.set(left, top, right, bottom);
    }

    @Override
    public boolean isPointInClickRect(float x, float y) {
        return mClickRect != null && x >= mClickRect.left && x <= mClickRect.right && y >= mClickRect.top && y <= mClickRect.bottom;
    }
}
