package com.kst.funddemo;

import java.util.List;

import com.kst.funddemo.adapter.CategoryBookListAdapter;
import com.kst.funddemo.customControls.ObservableListView;
import com.kst.funddemo.customControls.ObservableListView.ObservableScrollViewCallbacks;
import com.kst.funddemo.customControls.ObservableListView.ScrollState;
import com.kst.funddemo.model.json.CategoryBookBean;
import com.kst.funddemo.model.json.CategoryListBean;
import com.kst.funddemo.model.json.CategoryListData;
import com.kst.funddemo.model.json.CategoryListParams;
import com.kst.funddemo.presenter.BasePresenter;
import com.kst.funddemo.presenter.CategoryBookListPresenter;
import com.kst.funddemo.pulltorefresh.LoadMoreContainer;
import com.kst.funddemo.pulltorefresh.LoadMoreHandler;
import com.kst.funddemo.pulltorefresh.LoadMoreListViewContainer;
import com.kst.funddemo.pulltorefresh.PtrClassicFrameLayout;
import com.kst.funddemo.pulltorefresh.PtrDefaultHandler;
import com.kst.funddemo.pulltorefresh.PtrFrameLayout;
import com.kst.funddemo.pulltorefresh.PtrHandler;

import android.os.Bundle;
import android.view.View;

/***
 * 下拉刷新，下拉加载控件
 * @author dell
 *
 */
public class LoadMoreActivity extends BaseActivity implements ObservableScrollViewCallbacks
							, PtrHandler, LoadMoreHandler, BasePresenter {
	/**
     * 下拉刷新控件
     */
    private PtrClassicFrameLayout mPTRLayout;
    /**
     * 上拉加载更多控件
     */
    private LoadMoreListViewContainer mLoadMoreLayout;
    /**
     * 用于监听上下滑动的列表控件
     */
    private ObservableListView mListView;
    /**
     * 当前页码
     */
    private int currentPage = 1;
    /**
     * 总页码
     */
    private int sumPage;
    /***
     * 适配器
     */
    private CategoryBookListAdapter mAdapter;
    /**
     * 请求参数
     */
    private CategoryListParams params;
    /**
     * 当前页面控制器
     */
    private CategoryBookListPresenter mPresenter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_more);
		
		params = new CategoryListParams("131");
		
		// 请求网络数据
		mPresenter = new CategoryBookListPresenter(this,this);
		
		
		mPTRLayout = (PtrClassicFrameLayout) findViewById(R.id.pull_to_refresh_layout);
		mLoadMoreLayout = (LoadMoreListViewContainer) findViewById(R.id.load_more_list_view);
		mListView = (ObservableListView) findViewById(R.id.category_details_listview);
		
		// ListView 滑动状态的监听
		mListView.setScrollViewCallbacks(this);
		// 下拉刷新监听
		mPTRLayout.setPtrHandler(this);
		// 上拉加载更多监听
		mLoadMoreLayout.setLoadMoreHandler(this);
		
		mAdapter = new CategoryBookListAdapter(this);
		mListView.setAdapter(mAdapter);
		mPresenter.loadData("131",currentPage);
	}


	//ObservableScrollViewCallbacks
	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll,
			boolean dragging, ScrollState scrollState) {
		if(scrollState == ObservableListView.ScrollState.UP) {
			
		}
	}


	@Override
	public void onDownMotionEvent() {
		
	}


	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		
	}


	//PtrHandler
	@Override
	public boolean checkCanDoRefresh(PtrFrameLayout frame, View content,
			View header) {
		//判断是否可以下拉刷新
		return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
	}


	@Override
	public void onRefreshBegin(PtrFrameLayout frame) {
		// 刷新回调函数，用户在这里写自己的刷新功能实现，处理业务数据的刷新。 
		currentPage = 1;
		mPresenter.loadData(params,currentPage,true);
	}

	//LoadMoreHandler
	@Override
	public void onLoadMore(LoadMoreContainer loadMoreContainer) {
		mPresenter.loadData(params,currentPage+1,false);
	}

	//BasePresenter
	@Override
	public void loadPullUpFinish(Object object) {
		//上拉加载的回调
		if (object instanceof CategoryListBean){
            mPTRLayout.refreshComplete();
            CategoryListBean data=(CategoryListBean) object;
            updateView(data.getData(),false);
            hasMore();
        }
	}


	@Override
	public void loadPullDownFinish(Object object) {
		// 下拉刷新的回调
		if (object instanceof CategoryListBean){
            mPTRLayout.refreshComplete();
            CategoryListBean data=(CategoryListBean) object;
            List<CategoryBookBean> mList = data.getData().getBookList();
            updateView(data.getData(),true);
            hasMore();
            // TODO 如果数据size为0 显示无数据视图
		}
	}


	@Override
	public void loadFailed(int code) {
		mPTRLayout.refreshComplete();
		// TODO 显示失败视图
	}

	/**
     * 获取网络数据更新UI
     * @param data
     * @param isPullDown 是否为下拉刷新
     */
    private void updateView(CategoryListData data,boolean isPullDown){
        currentPage = data.getPage();
        sumPage = data.getSumPage();
        if (isPullDown) {
            mAdapter.reloadList(data.getBookList());
        }else {
            mAdapter.appendList(data.getBookList());
        }
    }
    
    /**
     * 是否还有更多
     */
    private void hasMore(){
        mLoadMoreLayout.loadMoreFinish(mAdapter.isEmpty(),currentPage < sumPage);
    }

	
}
