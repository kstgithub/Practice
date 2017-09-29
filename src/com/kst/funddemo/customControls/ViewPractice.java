package com.kst.funddemo.customControls;

import com.kst.funddemo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class ViewPractice extends View{
	 /**
     * 默认圆角 弧度  dpi
     */
    private static final int  DEF_RADIUS = 6;
    /**
     * 默认字体大小
     */
    private static final int  DEF_TEXT_SIZE = 10;

    /**
     * 边框宽度
     */
    private static final int DEF_STROKE_WIDTH = 1;

    /**
     * 控件PADDING值，必须大于0 否则 绘制边框将超出屏幕
     */
    private static final int DEF_PADDING = 1;
	
    /** 默认圆角弧度*/
	private int radius;
	/** 默认字体大小*/
    private int textSize;
    /** 边框宽度*/
    private int boderWidth;
    /** PADDING值*/
    private int padding;
    
    private Paint mForegroundPaint;
    private Paint mTextPaint;
    
    /** 创建进度区域，进度动态更新 */
    private RectF mRectProgress;
    /** 创建边框绘制区域 */
    private RectF mRectBorder;
    /** 创建文字区域 */
    private Rect mTextBound;
    
    private Bitmap drawBitmap;
    private Canvas mBitmapCanvas;
    
    /** 渐变 */
    private LinearGradient gradient;
    
    /**
     * 渐变 开始|结束 色值
     */
    private static final int GRADIENT_START_COLOR  = 0xffff8331;
    private static final int GRADIENT_END_COLOR    = 0xfff4553f;
    
    /**
     * 进度 1-100
     */
    private int progress;
    
    /** 文字內容 */
    private String text = "";

	public ViewPractice(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public ViewPractice(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ViewPractice(Context context) {
		super(context);
		init(context, null);
	}
	
	/***
	 * 初始化
	 * @param context
	 * @param attrs
	 */
	private void init(Context context, AttributeSet attrs) {
		parseAttrs(context, attrs);
		// 抗锯齿
		mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// 防抖动
		mForegroundPaint.setDither(true);
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setDither(true);
		PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
		mTextPaint.setXfermode(xfermode);
		mTextPaint.setTextSize(textSize);
		
		//创建边框绘制区域
        mRectBorder = new RectF();
        //创建进度区域，进度动态更新
        mRectProgress = new RectF();
        //创建文字区域
        mTextBound = new Rect();
	}
	
	private void parseAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Progress_Button);
		if(typedArray == null) {
			return;
		}
		try {
			radius = typedArray.getDimensionPixelSize(R.styleable.Progress_Button_pb_radius, dipToPixel(DEF_RADIUS));
			textSize = typedArray.getDimensionPixelSize(R.styleable.Progress_Button_pb_text_size, dipToPixel(DEF_TEXT_SIZE));
			boderWidth = typedArray.getDimensionPixelSize(R.styleable.Progress_Button_pb_boder_width, dipToPixel(DEF_STROKE_WIDTH));
			padding = typedArray.getDimensionPixelSize(R.styleable.Progress_Button_pb_padding, dipToPixel(DEF_PADDING));
		} finally {
			typedArray.recycle();
		}
        
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(drawBitmap == null) {
			//创建空画布 不要在原有画布上进行绘制 主要用于图像间的渲染交互，叠加模式等
			drawBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			mBitmapCanvas = new Canvas(drawBitmap);
		}
		
		// 擦除画布内容
		drawBitmap.eraseColor(0);
		
		if (gradient == null) {
			gradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
					GRADIENT_START_COLOR, GRADIENT_END_COLOR,
					Shader.TileMode.MIRROR);
		}
		//使用 渐变的着色器 绘制边框 和进度
        mForegroundPaint.setShader(gradient);
        //绘制边框 空心画笔
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        //设置边框宽度
        mForegroundPaint.setStrokeWidth(boderWidth);
        
        mRectBorder.set(padding, padding, getMeasuredWidth()-padding ,getMeasuredHeight()-padding);
		
        // 绘制圆角边框
        mBitmapCanvas.drawRoundRect(mRectBorder, radius, radius, mForegroundPaint);
        
        // 绘制进度 画笔修改为填充
        mForegroundPaint.setStyle(Paint.Style.FILL);
        
        float pw = getProgressWidth();
        if(pw>0) {
        	// 绘制进度
        	mRectProgress.set(0, 0, pw, getMeasuredHeight());
        	mBitmapCanvas.drawRoundRect(mRectProgress, radius, radius, mForegroundPaint);

            if (pw < getMeasuredWidth() - radius) {
                //绘制进度  由于绘制矩形圆角无法单独绘制某个角，所以用直角填充覆盖原有圆角
                mRectProgress.set(radius, 0, pw, getMeasuredHeight());
                mBitmapCanvas.drawRect(mRectProgress, mForegroundPaint);
            }
        }
        
        //根据文字内容获取 文字显示区域边界位置
        mTextPaint.getTextBounds(text, 0, text.length(), mTextBound);

        //设置文字绘制颜色
        mTextPaint.setShader(gradient);
        mBitmapCanvas.drawText(
                text,
                getWidth() / 2 - mTextBound.width() / 2,
                getHeight() / 2 - mTextBound.height() / 2,
                mTextPaint);
        
        canvas.drawBitmap(drawBitmap, 0, 0, null);
	}
	
	 /**
     * 获取进度宽度
     * 由于当宽度小于 圆角半径时无法准确绘制圆角，对应条件对宽度进行转化
     * @return
     */
    private float getProgressWidth(){
        float w = getProgress()/100f * getMeasuredWidth();
        if (w < radius){
            w = 0;
        }else if (w < radius * 2){
            w = radius *2;
        }
        return w;
    }
    
    /**
     * 设置显示文字
     * @param text
     */
    public void setText(String text){
        this.text = text;
        invalidate();
    }

    /**
     * 获取当前显示文字
     */
    public String getText(){
        return text;
    }
    
    /**
     * 获取当前进度
     * @return
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 设置当前进度
     * @param progress
     */
    public void setProgress(int progress) {

        if (progress >100)
            progress =100;
        else if (progress < 0)
            progress =0;

        this.progress = progress;
        invalidate();
    }
	
	/**
	 * dip转成pixel
	 * 
	 * @param dip
	 *            dip尺寸
	 * @return
	 */
	public int dipToPixel(float dip) {
		try {
			return (int) (dip * getContext().getResources()
							.getDisplayMetrics().density + 0.5);
		} catch (Exception e) {
			e.printStackTrace();
			return (int) dip;
		}
	}
	
}
