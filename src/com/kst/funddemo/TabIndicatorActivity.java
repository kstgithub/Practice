package com.kst.funddemo;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.kst.funddemo.common.util.Tools;
import com.kst.funddemo.customControls.slidingLayer.CustomViewAbove;
import com.kst.funddemo.customControls.slidingLayer.SlidingMenu;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by dell on 2017/2/27.
 */

public class TabIndicatorActivity extends SlidingFragmentActivity{
    /** 侧滑布局 */
    private SlidingMenu menu;
    /** 侧滑布局高度 */
    private final float SLIDING_MENU_WIDTH = Tools.dipToPixel(295);
    /** 控制抽屉式拖动的主界面 */
    private View drawerMainView = null;
    private float mScreenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tab_indicate);

        mScreenHeight = Tools.getScreenHeight();
        initView();
        initMenu();
        setBehindContentView(R.layout.td_main_user_info_layout);
    }

    private void initView() {
        drawerMainView = findViewById(R.id.main_layout);
    }

    /**
     * 初始化Menu菜单
     */
    private void initMenu() {
        menu = getSlidingMenu();
        menu.setBehindWidth((int) SLIDING_MENU_WIDTH);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setBehindScrollScale(0.8f);
        menu.setOnMenuSlidingListener(new CustomViewAbove.OnMenuSlidingListener() {

            @Override
            public void onMenuSliding(float x) {
                menu.setBackgroundResource(x == 0 ? R.color.titlebar_login_bg
                        : R.drawable.user_info_background);
                float v = x / SLIDING_MENU_WIDTH;
                //value 0.75-1
                float scale = (1 - v) * 0.25f + 0.75f;
                //menu background切换后 prvot会丢失
                ViewHelper.setPivotX(drawerMainView, 0);
                ViewHelper.setPivotY(drawerMainView,
                        mScreenHeight / 2f+mScreenHeight/20f);
//				ViewHelper.setTranslationY(drawerMainView, mScreenHeight * v
//						/ 40);
                ViewHelper.setScaleX(drawerMainView, scale);
                ViewHelper.setScaleY(drawerMainView, scale);
            }
        });
        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {

            @Override
            public void onOpened() {
                setSlidingEnabled(true);
            }
        });
        menu.setOnClosedListener(new SlidingMenu.OnClosedListener() {

            @Override
            public void onClosed() {
                ViewHelper.setScaleX(drawerMainView, 1);
                ViewHelper.setScaleY(drawerMainView, 1);

            }
        });
    }

}
