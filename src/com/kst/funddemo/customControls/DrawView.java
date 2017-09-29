package com.kst.funddemo.customControls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by dell on 2017/3/1.
 * 绘制View
 */

public class DrawView extends View{
    private Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int canvasWidth = canvas.getWidth();
        Log.v("drawView","canvasWidth="+canvasWidth);
        int canvasHeight = canvas.getHeight();
        Log.v("drawView","canvasHeight="+canvasHeight);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        // 绘制背景
        canvas.drawARGB(255,156,25,185);

        //绘制x轴
        paint.setColor(0xff00ff00);
        canvas.drawLine(0,0,canvasWidth,0,paint);
        //绘制y轴
        paint.setColor(0xff0000ff);
        canvas.drawLine(0,0,0,canvasHeight,paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff00ff00);
        paint.setAntiAlias(true);
        paint.setTextSize(32);
        canvas.drawText("绘制文本",50,50,paint);
    }
}
