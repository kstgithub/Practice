package com.kst.funddemo.customControls;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.kst.funddemo.R;
import com.kst.funddemo.adapter.SlidePagerAdapter;
import com.kst.funddemo.common.util.Tools;
import com.kst.funddemo.model.BannerBean;
import com.kst.funddemo.widget.IndicatorLayout;

public class ViewBanner extends FrameLayout implements View.OnClickListener{


    /**
     * 轮播时间
     */
    private static final long SCROLL_DELAYED=4000;

    /**
     * 图像宽高比
     */
    private static final float SCALE = 720f/280f;

    private SlidePagerAdapter mAdapter;

    private ViewPager mViewPager;

    /**
     * 指示器
     */
    private IndicatorLayout mIndicator;

    private int pagecount;
    /**
     * 是否可播放
     */
    private boolean canScroll;
    /**
     * 暂停播放
     */
    private boolean stopScroll;
    /**
     * 当前页面是否有数据，无数据不进行自动播放
     */
    private boolean hasData;

    private ScrollHandler mHandler;
    /**
     * 控制手指触摸到幻灯片上，将停止自动播放，添加定时器，手指释放后2秒后开始播放
     */
    private CountDownTimer mTimer;

    /**
     * 在数据size<4时 由于pageadpter的预加载方式会照成item创建销毁逻辑错误
     * 对不符合要求的数据进行扩充
     */
    private int rideX = 1;

    private class ScrollHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mViewPager.getCurrentItem()==Integer.MAX_VALUE-1) {
                if (!stopScroll)
                    mViewPager.setCurrentItem(pagecount*100);
            }else  {
                if (!stopScroll)
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            }
            if (canScroll)
                mHandler.sendEmptyMessageDelayed(0,SCROLL_DELAYED);
        }
    }

    public ViewBanner(Context context) {
        super(context);
        init();
    }

    public ViewBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int screenWidth = Tools.getScreenWidth();
        mViewPager = new ViewPager(getContext());
        mIndicator = new IndicatorLayout(getContext());
        mAdapter = new SlidePagerAdapter();
        LayoutParams viewPagerParams = new LayoutParams(screenWidth, (int)(screenWidth/SCALE)-1);
        mViewPager.setLayoutParams(viewPagerParams);
        mViewPager.setAdapter(mAdapter);

        //初始化指示器UI参数
        //位置：右下角
        LayoutParams indicatorParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Tools.dipToPixel(28));
        indicatorParams.gravity = Gravity.BOTTOM|Gravity.RIGHT;
        indicatorParams.rightMargin = Tools.dipToPixel(16);
        mIndicator.setGravity(Gravity.CENTER_VERTICAL);
        mIndicator.setLayoutParams(indicatorParams);

        addView(mViewPager);
        addView(mIndicator);
    }

    /**
     * 开始轮播
     */
    public void startAutoScroll() {
        if (!hasData) return;
        canScroll = true;
        mHandler = new ScrollHandler();
        mHandler.sendEmptyMessageDelayed(0, SCROLL_DELAYED);
    }

    /**
     * 停止轮播
     */
    public void stopAutoScroll(){
        if (!hasData||null == mHandler) return;
        canScroll=false;
        mHandler.removeMessages(0);
    }

    /**
     * 销毁视图，释放资源
     */
    public void destroy(){
        stopAutoScroll();
        List<View> views = mAdapter.getViews();
        for (View view : views) {
            ImageView mImageView = (ImageView) view;
            GlideBitmapDrawable drawable = (GlideBitmapDrawable) mImageView.getDrawable();
            if(drawable != null) {
                Bitmap bmp = drawable.getBitmap();
                if (null != bmp && !bmp.isRecycled()) {
                    bmp.recycle();
                    bmp = null;
                }
            }
        }
    }

    /**
     * 是否需要更新
     * 只有当数据为空的时候才触发更新
     * @return
     */
    public boolean needUpdate(){
        return mAdapter.getRealCount() == 0;
    }

    /**
     * 添加数据
     * @param data
     */
    public void appendData(List<BannerBean> data) {


        List<BannerBean> list = new ArrayList<BannerBean>();

        //当前viewpager无限循环滑动，由于ViewPager的缓存策略，在ITEM数量小于4的情况下会造成重复添加子view的情况
        //扩充数据，保证数据在4条或以上
        if (data.size()<4) {
            rideX = data.size()<2?4:2;
        }

        for (int i = 0; i < rideX ; i++) {
            list.addAll(data);
        }


        try {
            if (list.size() > 0) {
                hasData = true;
                mViewPager.setVisibility(View.VISIBLE);
            }

            this.pagecount = list.size();

            List<View> views = new ArrayList<View>();
            for (int i = 0; i < list.size(); i++) {
                BannerBean bean = list.get(i);
                ImageView image = new ImageView(getContext());
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                image.setLayoutParams(lp);
                image.setTag(bean);
                image.setOnClickListener(this);
                image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                image.setBackgroundResource(R.color.image_placeholder_color);
                Glide.with(getContext())
                        .load(bean.getImg())
                        .centerCrop()
                        .into(image);

                views.add(image);
            }
            mAdapter.addViews(views);

            if (pagecount > 1){
                //传递数据扩张倍数，保证指示器正确显示
                mIndicator.setRideX(rideX);
                mIndicator.setViewPager(mViewPager);
                addListener();
            }
            //首次加载需绕过缓存位置调整，之后调整正确显示位置
            mViewPager.setCurrentItem(pagecount*100-2,false);
            mViewPager.setCurrentItem(pagecount*100,false);
            //启动自动播放
//            startAutoScroll();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addListener() {
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //当幻灯片的TOUCH事件被触发时，应当禁止幻灯片的自动滑动
                if (i == 1){
                    stopScroll = true;
                }else if (i==0){
                    if (stopScroll){
                        if (mTimer != null){
                            mTimer.cancel();
                            mTimer = null;
                        }

                        mTimer = new CountDownTimer(2000,1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                stopScroll = false;
                            }
                        }.start();
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        Object bean= view.getTag();
        if (null != bean && bean instanceof BannerBean) {
            BannerBean imageBean = (BannerBean) bean;
//            EventMessage message = new EventMessage(EventManager.TD_MAIN_OPEN_SECOND_BROWER_ID,imageBean.getUrl());
            //打开二级浏览器
//            EventBus.getDefault().post(message);
            Toast.makeText(getContext(), imageBean.getImg(), Toast.LENGTH_SHORT).show();
        }
    }

}
