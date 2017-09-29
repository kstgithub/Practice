package com.kst.funddemo;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.kst.funddemo.adapter.VpAdapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;  
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

/***
 * 引导页+指示器
 * @author dell
 *
 */
public class GuideDotActivity extends BaseActivity implements OnPageChangeListener{
	private ViewPager vPager;  
    private VpAdapter vpAdapter;  
    /** 要显示的图片资源 */
    private int [] imgs = {R.drawable.bg_page_01,R.drawable.bg_page_02,R.drawable.bg_page_03,R.drawable.bg_page_04,R.drawable.bg_page_05};  
    /** 用于包含引导页要显示的图片  */
    private ArrayList<ImageView> imageViews;
    /** 用于包含底部小圆点的图片，实现导航的功能。  */
    private ImageView[] dotViews;
    
    private EdgeEffectCompat leftEdge;  
    private EdgeEffectCompat rightEdge; 
    
    private boolean misScrolled;
    
    private Button btn;
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_guide_dot);  
        vPager = (ViewPager)findViewById(R.id.viewpager);  
        
        btn = (Button) findViewById(R.id.btn_guide_dot);
        btn.setVisibility(View.GONE);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(GuideDotActivity.this, LoadMoreActivity.class));
//				GuideDotActivity.this.finish();
			}
		});
        
        initViewPager();   
          
        initImages();  
        initDots();  
          
        vpAdapter = new VpAdapter(imageViews);  
        vPager.setAdapter(vpAdapter);  
        vPager.setOnPageChangeListener(this);  
    }

	private void initViewPager() {
		try {
			Field leftEdgeField = vPager.getClass().getDeclaredField(
					"mLeftEdge");
			Field rightEdgeField = vPager.getClass().getDeclaredField(
					"mRightEdge");
			if (leftEdgeField != null && rightEdgeField != null) {
				leftEdgeField.setAccessible(true);
				rightEdgeField.setAccessible(true);
				leftEdge = (EdgeEffectCompat) leftEdgeField.get(vPager);
				rightEdge = (EdgeEffectCompat) rightEdgeField.get(vPager);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  
    /** 
     * 把引导页要显示的图片添加到集合中，以传递给适配器，用来显示图片。 
     */  
      
    private void initImages()  
    {  
        LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);//设置每一张图片都填充窗口  
        imageViews = new ArrayList<ImageView>();  
          
        for(int i = 0; i < imgs.length; i++)  
        {  
            ImageView iv = new ImageView(this);  
            iv.setLayoutParams(mParams);//设置布局  
            iv.setImageResource(imgs[i]);//为Imageview添加图片资源  
            iv.setScaleType(ScaleType.FIT_XY);//设置图片拉伸效果  
            imageViews.add(iv);  
            if(i== imgs.length - 1)//为最后一张添加点击事件  
            {  
//                iv.setOnClickListener(new OnClickListener() {  
//                      
//                    @Override  
//                    public void onClick(View v) {  
//                        Toast.makeText(GuideDotActivity.this, "跳转。。。", Toast.LENGTH_SHORT).show();  
//                    }  
//                });  
            }  
        }  
    }  
      
    /** 
     * 根据引导页的数量，动态生成相应数量的导航小圆点，并添加到LinearLayout中显示。 
     */  
    private void initDots()  
    {  
        LinearLayout layout = (LinearLayout)findViewById(R.id.dot_layout);  
        LayoutParams mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
        mParams.setMargins(10, 0, 10, 0);//设置小圆点左右之间的间隔  
          
        dotViews = new ImageView[imgs.length];  
          
        for(int i = 0; i < imageViews.size(); i++)  
        {  
            ImageView imageView = new ImageView(this);  
            imageView.setLayoutParams(mParams);  
            imageView.setImageResource(R.drawable.dot_selector);  
            if(i== 0)  
            {  
                imageView.setSelected(true);//默认启动时，选中第一个小圆点  
            }  
            else {  
                imageView.setSelected(false);  
            }  
            dotViews[i] = imageView;//得到每个小圆点的引用，用于滑动页面时，（onPageSelected方法中）更改它们的状态。  
            layout.addView(imageView);//添加到布局里面显示  
        }  
          
    }  
      
      
    @Override  
    public void onPageScrollStateChanged(int state) {  
    	// 到了最后一张并且还继续拖动，出现蓝色限制边条了
//		if (rightEdge != null && !rightEdge.isFinished()) {
//			startActivity(new Intent(GuideDotActivity.this, LoadMoreActivity.class));
//			GuideDotActivity.this.finish();
//		}
		
		// 方法二  滑至最后一页时，跳转
		switch (state) {
		case ViewPager.SCROLL_STATE_DRAGGING:
			misScrolled = false;
			break;
		case ViewPager.SCROLL_STATE_SETTLING:
			misScrolled = true;
			break;
		case ViewPager.SCROLL_STATE_IDLE:
			if (vPager.getCurrentItem() == vPager.getAdapter().getCount() - 1 && !misScrolled) {
				startActivity(new Intent(this, LoadMoreActivity.class));
//				GuideDotActivity.this.finish();
			}
			misScrolled = true;
			break;
		}
    }  
  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    	// 禁止蓝边出现
    	if (leftEdge != null && rightEdge != null) {  
            leftEdge.finish();  
            rightEdge.finish();  
            leftEdge.setSize(0, 0);  
            rightEdge.setSize(0, 0);  
        }  
    }  
  
    /** 
     * arg0：当前滑动显示页面的索引值，可以根据这个值，来设置相应小圆点的状态。 
     */  
    @Override  
    public void onPageSelected(int arg0) {  
//    	if(arg0 == vpAdapter.getCount()-1) {
//    		btn.setVisibility(View.VISIBLE);
//    	} else {
//    		btn.setVisibility(View.INVISIBLE);
//    	}
        for(int i = 0; i < dotViews.length; i++)  
        {  
            if(arg0 == i)  
            {  
                dotViews[i].setSelected(true);  
            }  
            else {  
                dotViews[i].setSelected(false);  
            }  
        }  
          
    }  

}
