package com.kst.funddemo.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 圆形显示百分比
 * Created by dell on 2017/8/2.
 */

public class PercentView extends View{
    private final static String TAG = PercentView.class.getSimpleName();
    private Paint mPaint;
    private RectF oval;

    public PercentView(Context context) {
        super(context);
        init();
    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oval = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:

                break;
            case MeasureSpec.AT_MOST:

                break;
            case MeasureSpec.UNSPECIFIED:

                break;
        }
        Log.e(TAG, "onMeasure--widthMode-->" + widthMode);
        Log.e(TAG, "onMeasure--widthSize-->" + widthSize);
        Log.e(TAG, "onMeasure--heightMode-->" + heightMode);
        Log.e(TAG, "onMeasure--heightSize-->" + heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        int width = getWidth();
        int height = getHeight();
        Log.e(TAG, "onDraw---width=" + width + " height=" + height);
        float radius = width / 4;
        canvas.drawCircle(width / 2, width / 2, radius, mPaint);
        mPaint.setColor(Color.BLUE);
        //用于定义的圆弧的形状和大小的界限
        oval.set(width / 2 - radius, width / 2 - radius, width / 2
                + radius, width / 2 + radius);
        //根据进度画圆弧
        canvas.drawArc(oval,270,120,true,mPaint);
    }
}
