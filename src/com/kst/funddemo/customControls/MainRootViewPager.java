package com.kst.funddemo.customControls;

import android.support.v4.view.ViewPager;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.kst.funddemo.common.util.Tools;

/**
 * Created by dell on 2017/1/23.
 * Tab页切换
 */

public class MainRootViewPager extends ViewPager{
    /** 书架view视图对应的索引号 */
    public static final int BOOKSHELF_INDEX = 0;
    /** 书城view视图对应的索引号 */
    public static final int BOOKSTORE_INDEX = 1;
    /** 书吧view视图对应的索引号 */
    public static final int BOOKBAR_INDEX = 2;

    /** 向左滑动*/
    private static final int SLIDE_LEFT = 1;
    /** 向右滑动*/
    private static final int SLIDE_RIGHT = 2;

    /** 当前view视图索引 */
    private int currentViewIndex = BOOKSHELF_INDEX;

    private int currentSlideDirection = 0;

    private boolean isWebViewTouch = false;

    private boolean isCanScroll = true;

    private int mLastX;

    /** 屏幕宽 */
    private int widthPixels;

    private OnPageOffsetListener listener;

    public interface  OnPageOffsetListener {
        void onSlideOffset(float widthPixels,float x);
    }

    public MainRootViewPager(Context context) {
        super(context);
        init();
    }

    public MainRootViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        widthPixels = Tools.getScreenWidth();
    }

    /**
     * 选中的是否为书架
     * @return
     */
    public boolean isSelectedBookShelf () {
        return currentViewIndex == BOOKSHELF_INDEX;
    }

    /**
     * 选中的是否为书城
     * @return
     */
    public boolean isSelectedBookStore () {
        return currentViewIndex == BOOKSTORE_INDEX;
    }

    /**
     * 选中的是否为书吧
     * @return
     */
    public boolean isSelectedBookBar () {
        return currentViewIndex == BOOKBAR_INDEX;
    }

    /**
     * 当前选中项INDEX
     * @return
     */
    public int getCurrentViewIndex(){
        return currentViewIndex;
    }

    /**
     * 设置当前选中的TAB index
     * @param index
     */
    public void setCurrentViewIndex(int index){
        this.currentViewIndex = index;
    }

    public void setSlideDirectionToDefault() {
        this.currentSlideDirection = 0;
    }

    /**
     * 当前滑动方向
     * @return
     */
    public int getCurrentSlideDirection(){
        return currentSlideDirection;
    }

    /**
     * WebView需要滑动
     * */
    public void ignoreTouchCancel(boolean isTouch) {
        isWebViewTouch = isTouch;
    }

    /**
     * 是否可以滑动
     * @param canScroll
     */
    public void setCanScroll(boolean canScroll){
        isCanScroll = canScroll;
    }

    public void setOnPageOffsetListener(OnPageOffsetListener listener) {
        this.listener = listener;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        //activity被系统回收后禁止恢复状态
        super.onRestoreInstanceState(null);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (listener != null){
            listener.onSlideOffset(widthPixels,x);
        }
        super.scrollTo(x, y);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            int x = (int) event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int diffX = x - mLastX;
                    if (diffX < 0){
                        currentSlideDirection = SLIDE_LEFT;
                    }else if (diffX > 0){
                        currentSlideDirection = SLIDE_RIGHT;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
                default:break;
            }


            mLastX = x;

            return super.dispatchTouchEvent(event);

        }catch (Exception e){
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!isCanScroll)
            return false;

//        if (TDMainActivity.tdMainInstance.getBookShelfManager().isEditState()) {
//            return false;
//        }

//        if (TDMainActivity.tdMainInstance.getBookShelfManager().getShelfDialogManager().isBottomPopupShow()) {
//            return false;
//        }

//        if (DragGridView.isDraging) {
//            return false;
//        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isWebViewTouch) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isWebViewTouch = false;
                break;
            default:break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {

            return isCanScroll
//                    && !TDMainActivity.tdMainInstance.getBookShelfManager().isEditState()
                    && !isWebViewTouch
                    && super.onTouchEvent(ev);

        }catch (Exception e){
            return false;
        }

    }
}
