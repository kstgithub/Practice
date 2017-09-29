package com.kst.funddemo.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kst.funddemo.common.util.Tools;

/**
 * 自定义CircleView
 * Created by dell on 2017/2/9.
 */

public class CircleImageView extends ImageView{
    private Paint paint = new Paint();

    public CircleImageView(Context context) {
        this(context,null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        // 消除锯齿
        paint.setAntiAlias(true);
        // 设置颜色
        paint.setColor(0xfffe89ba);
        // 空心圆
        paint.setStyle(Paint.Style.STROKE);
        // 设置宽度
        paint.setStrokeWidth(Tools.dipToPixel(1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
