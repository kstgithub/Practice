package com.kst.funddemo.customControls;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author crazychen
 * @version 1.0
 * @date 2015/11/11
 */
public class AdaptableTextView extends TextView{
	/**
     * 文本是否变化
     */
    boolean mIsDirty = false;
    AdaptableText mAdaptableText;
    
    public AdaptableTextView(Context context) {
        super(context);
    }

    public AdaptableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        mIsDirty = true;
    }

    @Override
    public int getLineCount() {
        AdaptableText helper = getAdaptableText();
        return helper.getLineCount();
    }
    
    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        mIsDirty = true;
    }

	@Override
    protected void onDraw(Canvas canvas) {
        getAdaptableText();
        if(mIsDirty) {
            mIsDirty = false;
            String text = getText().toString();
//            int maxLines = getMaxLines();
            if(!mAdaptableText.getText().equals(text))
                mAdaptableText.setText(text);
//            if(mAdaptableText.getMaxLines() != maxLines)
//                mAdaptableText.setMaxLines(maxLines);
        }
        mAdaptableText.draw(canvas);
    }

    private AdaptableText getAdaptableText() {
        if(mAdaptableText == null) {
            TextPaint paint = getPaint();
            paint.setColor(getCurrentTextColor());
            paint.drawableState = getDrawableState();
            mAdaptableText = new AdaptableText(getText().toString(), paint, getMeasuredWidth()-getPaddingLeft()-getPaddingRight(), getLineHeight());
//            mAdaptableText.setMaxLines(getMaxLines());
        }else {
        	mAdaptableText.changeText(getText().toString(), getMeasuredWidth()-getPaddingLeft()-getPaddingRight(), getLineHeight());
        }
        return mAdaptableText;
    }

	private class AdaptableText{
    	/***
    	 * 文本宽高
    	 */
        int mLineWidth, mLineHeight;
        /** 最大行数 */
        int mMaxLines;
        private TextPaint mPaint;
        private String mText;
        /**
         * 存储分割后的每行
         */
        ArrayList<String> strs = new ArrayList<String>();
        
        public AdaptableText(String text,TextPaint paint, int lineWidth,int lineHeight){
            mLineHeight = lineHeight;
            mLineWidth = lineWidth;
            mPaint = paint;
            mText = text;
            parseText();
        }
        
        public void changeText(String text, int lineWidth,int lineHeight){
        	mLineHeight = lineHeight;
            mLineWidth = lineWidth;
            mText = text;
            parseText();
        }

        /**
         * 根据控件宽度，计算得出每行的字符串
         */
        private void parseText(){
            strs.clear();
            int start = 0; // 行起始index
            int curLineWidth = 0; //当前行宽
            for (int i = 0; i < mText.length(); i++) {
                char ch = mText.charAt(i); //获取当前字符
                float[] widths = new float[1];
                String srt = String.valueOf(ch);
                mPaint.getTextWidths(srt, widths); //获取这个字符的宽度
                if (ch == '\n'){//如果是换行符，则单独一行
                    strs.add(mText.substring(start, i));
                    start = i + 1;
                    curLineWidth = 0;
                }else{
                    curLineWidth += (int) (Math.ceil(widths[0])); //计算当前宽度
                    if (curLineWidth > mLineWidth){//直到当前行宽度大于控件宽度，截取为一行
                        strs.add(mText.substring(start, i));
                        start = i;
                        i--;
                        curLineWidth = 0;
                    }else{
                        if (i == (mText.length() - 1)){ //剩余的单独一行
                            strs.add(mText.substring(start, mText.length()));
                        }
                    }
                }
            }
        }

        public void draw(Canvas canvas) {
        	setHeight(mLineHeight * getLineCount() + getPaddingBottom() + getPaddingTop());
            int lines = mMaxLines > 0 && mMaxLines <= strs.size() ? mMaxLines : strs.size();
            for (int i = 0; i < lines; i++) {
                String text = strs.get(i);
                //如果是最大行的最后一行但不是真实的最后一行则自动添加省略号
                if(i == lines - 1 && i < strs.size() - 1)
                    text = text.substring(0, text.length() - 3) + "...";
                canvas.drawText(text, getPaddingLeft(), getPaddingTop()+mPaint.getTextSize() + mLineHeight * i, mPaint);
            }
        }
        
        public void setText(String text) {
            mText = text;
            parseText();
        }

        public String getText() {
            return mText;
        }

        public void setMaxLines(int maxLines) {
            mMaxLines = maxLines;
        }

        public int getMaxLines() {
            return mMaxLines;
        }

        public int getLineCount() {
            return strs.size();
        }

        public int getLineEnd(int line) {
            int size = 0;
            for(int i = 0; i <= line; i++) {
                size += strs.get(i).length();
            }
            return size;
        }
    } 
}
