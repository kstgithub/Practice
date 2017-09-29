package com.kst.funddemo;

import java.util.ArrayList;
import java.util.List;

import com.kst.funddemo.customControls.ViewBanner;
import com.kst.funddemo.model.BannerBean;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;

/***
 * 广告位方式一
 * @author dell
 *
 */
public class BannerActivity extends Activity {
	/**
     * 顶部幻灯片
     */
    private ViewBanner mViewBanner;
    
    private List<BannerBean> banner = new ArrayList<BannerBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_banner);
		
		mViewBanner = (ViewBanner) findViewById(R.id.category_banner);
		initBannerBean(banner);
		updateBanner(banner);
		
	}

	
	private void initBannerBean(List<BannerBean> banner) {
		BannerBean bean1 = new BannerBean();
		bean1.setImg("http://media.tadu.com/2016/05/10/8/2/9/8/82986a0b9c5c4e0d926f68878b58e750.jpg");
		banner.add(bean1);
		BannerBean bean2 = new BannerBean();
		bean2.setImg("http://media.tadu.com/2016/06/23/2/2/e/2/22e2382ac0ae4905a4805fc65a12538b.jpg");
		banner.add(bean2);
		BannerBean bean3 = new BannerBean();
		bean3.setImg("http://media.tadu.com/2016/05/19/f/3/1/a/f31ae84bd353414eb4360a2590196c69.jpg");
		banner.add(bean3);
	}


	@Override
	protected void onDestroy() {
		if(mViewBanner!=null) {
    		mViewBanner.destroy();
    	}
		super.onDestroy();
	}
	
	/**
     * 更新顶部幻灯片
     * @param banner
     */
    private void updateBanner(List<BannerBean> banner){
        if (banner.size() > 0 && mViewBanner.needUpdate()) {
            mViewBanner.setVisibility(View.VISIBLE);
            mViewBanner.appendData(banner);
        }

        if (banner.size() <= 0){
            mViewBanner.setVisibility(View.GONE);
        }
    }
	
    public void viewShow() {
        mViewBanner.startAutoScroll();
    }
    
    public void viewHide() {
        mViewBanner.stopAutoScroll();
    }
    
}
