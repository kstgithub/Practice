package com.kst.funddemo.presenter;

import com.kst.funddemo.common.communication.RequestService;
import com.kst.funddemo.common.util.AppConstant;
import com.kst.funddemo.model.CallBackInterface;
import com.kst.funddemo.model.json.CategoryListBean;
import com.kst.funddemo.model.json.CategoryListParams;
import com.kst.funddemo.volley.volleyservice.VolleyRequestService;

import android.app.Activity;

public class CategoryBookListPresenter {


    private BasePresenter mPresenter;

    private boolean isLoading;

    private Activity mActivity;

    public CategoryBookListPresenter(BasePresenter mPresenter, Activity mActivity){
        this.mPresenter = mPresenter;
        this.mActivity = mActivity;
    }


    public void loadData(CategoryListParams params,int page, final boolean isPullDown){
        CategoryListBean bean = new CategoryListBean();
        bean.setCategoryid(params.id);
        bean.setThirdcategory(params.thirdcategory);
        bean.setActivitytype(params.activitytype);
        bean.setBookstatus(params.bookstatus);
        bean.setSorttype(params.sorttype);
        bean.setPage(String.valueOf(page));
//        new VolleyRequestService().volleyStringRequest(new CallBackInterface() {
//            @Override
//            public Object callBack(Object ob) {
//                isLoading=false;
//                if(ob!=null&&ob instanceof CategoryListBean){
//                    CategoryListBean data=(CategoryListBean) ob;
//                    if( data.getData()!=null&& data.getCode() == AppConstant.SUCCESS_OPERATION){
//                        if (isPullDown) {
//                            mPresenter.loadPullDownFinish(data);
//                        }else {
//                            mPresenter.loadPullUpFinish(data);
//                        }
//                    }else{
//                        mPresenter.loadFailed(data.getCode());
//                    }
//                }else{
//                    mPresenter.loadFailed(-1);
//                }
//                return null;
//            }
//        }, bean, mActivity, "",false, false,false, true, true);
        
        new RequestService().executeCommon(new CallBackInterface() {
            @Override
            public Object callBack(Object ob) {
                isLoading=false;
                if(ob!=null&&ob instanceof CategoryListBean){
                    CategoryListBean data=(CategoryListBean) ob;
                    if( data.getData()!=null&& data.getCode() == AppConstant.SUCCESS_OPERATION){
                        if (isPullDown) {
                            mPresenter.loadPullDownFinish(data);
                        }else {
                            mPresenter.loadPullUpFinish(data);
                        }
                    }else{
                        mPresenter.loadFailed(data.getCode());
                    }
                }else{
                    mPresenter.loadFailed(-1);
                }
                return null;
            }
        }, bean, mActivity, "",false, false,false, true, true);
    }
    /**
     * 加载数据
     * @param id 子分类ID
     */
    public void loadData(String id,int page){
        loadData(new CategoryListParams(id,"0","0","0","0"),page,true);
    }

}
