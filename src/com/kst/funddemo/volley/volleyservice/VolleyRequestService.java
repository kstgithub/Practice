package com.kst.funddemo.volley.volleyservice;/**
 * Created by Administrator on 2016/4/14.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.kst.funddemo.R;
import com.kst.funddemo.common.util.HttpTools;
import com.kst.funddemo.common.util.Tools;
import com.kst.funddemo.dialog.CustomProgressdialog;
import com.kst.funddemo.dialog.CustomTextViewDialog;
import com.kst.funddemo.model.BaseBeen;
import com.kst.funddemo.model.CallBackInterface;
import com.kst.funddemo.model.FileUploadBean;
import com.kst.funddemo.volley.AuthFailureError;
import com.kst.funddemo.volley.Request;
import com.kst.funddemo.volley.VolleyError;
import com.kst.funddemo.volley.VolleyManager;
import com.kst.funddemo.volley.listener.Listener;
import com.kst.funddemo.volley.model.FormFile;
import com.kst.funddemo.volley.model.ResultData;
import com.kst.funddemo.volley.toolbox.FileDownloader;
import com.kst.funddemo.volley.toolbox.MultipartRequest;
import com.kst.funddemo.volley.toolbox.StringRequest;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class VolleyRequestService {

    private final String TAG = this.getClass().getName();


    // 设置联网dialog
    private CustomProgressdialog pd;
    private boolean cancel = false;

    private StringRequest stringRequest;

    /**
     * 通用网络连接类，参考同名函数，此处最后一个参数默认为false
     *
     * @param cb
     * @param been
     * @param activity
     * @param dialogMessage
     * @param isShowDialog
     * @param isCancelDialog
     * @param isFinishActivity
     * @param isGet
     */
    public void volleyStringRequest(final CallBackInterface cb, final BaseBeen been,
                                    final Activity activity, String dialogMessage,
                                    boolean isShowDialog, boolean isCancelDialog,
                                    boolean isFinishActivity, final boolean isGet) {
        volleyStringRequest(cb, been, activity, dialogMessage, isShowDialog,
                isCancelDialog, isFinishActivity, isGet, false);
    }

    /**
     * 通用网络连接类
     *
     * @param callBackInterface 回调
     * @param baseBeen          请求实体
     * @param activity          上下文
     * @param dialogMessage     等待框显示文字
     * @param isShowDialog      是否显示等待框
     * @param isCancelDialog    是否可取消
     * @param isFinishActivity  错误是否关闭页面
     * @param isGet             是否用get方式请求： true-get false-post
     * @param isIgnoreError     是否忽略错误，如果忽略则不会报告任何错误
     */
    public void volleyStringRequest(final CallBackInterface callBackInterface, final BaseBeen baseBeen,
                                    final Activity activity, String dialogMessage,
                                    boolean isShowDialog, boolean isCancelDialog,
                                    final boolean isFinishActivity, final boolean isGet,
                                    final boolean isIgnoreError) {
        if (isShowDialog) {
            try {
                pd = new CustomProgressdialog(activity, dialogMessage,
                        isCancelDialog, true);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel = true;
                        VolleyManager.getInstance().getRequestQueue().cancelAll(activity);
                        if (isFinishActivity) {
                            activity.finish();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pd = null;
        }
        String url = Tools.readAddress() + baseBeen.getUrl();
        if (isGet) {
            if (HttpTools.getNameValuePair(baseBeen).size() > 0) {
                url = url + "?" + URLEncodedUtils.format(HttpTools.getNameValuePair(baseBeen), HTTP.UTF_8);
            }
        }
        stringRequest = new StringRequest(isGet ? Request.Method.GET : Request.Method.POST, url, new Listener<String>() {
            @Override
            public void onSuccess(String response) {
                Log.e("volley_response=",response);
                hideDialog(activity);
                if (!isValidated(response, stringRequest, activity)) {
                    callBackInterface.callBack(null);
                }
                BaseBeen result = new BaseBeen();
                try {
                    result = (BaseBeen) HttpTools.json2Object(response, baseBeen);
//                    if (result.getCode() == AppConstant.KEY_FAILURE) {
//                        //如果秘钥失效下载秘钥文件
//                        voleyFileDownRequest(new FileNetCallback() {
//                            @Override
//                            public void loadFinish(Exception e) {
//                                if (e == null) {
//                                    RequestEncryption.setTdcn(AppConstant.dataRootPath, AppConstant.tk, getBytes(AppConstant.dataRootPath + AppConstant.tk));
//                                    if (ApplicationData.tkp != null
//                                            && ApplicationData.tkp.length > 0) {
//                                        ApplicationData.tkp = null;
//                                    }
//                                }
//                            }
//                            @Override
//                            public void onProgress(long fileSize, long downloadedSize) {
//                            }
//                        }, result.getMessage(), AppConstant.dataRootPath + AppConstant.tk, isIgnoreError, activity,false,"",true, isFinishActivity);
//                    }
                } catch (Exception e) {
                    if (isIgnoreError) {

                    } else {
                        onException(e, activity, isFinishActivity);
                    }
                }
                callBackInterface.callBack(result);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("volley_response_error=",error.toString());
                hideDialog(activity);
                if (isIgnoreError) {

                } else {
                    onException(error, activity, isFinishActivity);
                }
                callBackInterface.callBack(null);
                super.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                if (!isGet) {
                    map = HttpTools.getParams(baseBeen);
                }
                return map;
            }
        };
        //为request设置tag 可以在onStop中根据tag取消
        stringRequest.setTag(activity);
        // 将StringRequest对象添加进RequestQueue请求队列中
        VolleyManager.getInstance().addToRequestQueue(stringRequest);
    }

    private void hideDialog(Activity activity) {
        if (!cancel) {
            if (pd != null && pd.isShowing() && null != activity && !activity.isFinishing()) {
                pd.dismiss();
            }
        }
    }

    /**
     * 统一的异常的处理方式，实现此方法，如果还需要基类处理统一错误，请添加super.handleException(e);
     *
     * @param e 异步处理抛出的异常
     */
    public void onException(Exception e, Activity activity, boolean isFinishActivity) {
        // Log.e("tadu", e.getMessage());
//        if (e instanceof NetworkForceCloseException) {
//            // 主动取消联网返回异常，不做任何处理
//        } else if (e instanceof NetworkNotException) {
//            showNoNetWorkDialog(activity, isFinishActivity);
//        } else if (e instanceof NetworkTimeoutException) {
//            showRetryDialog("网络连接超时！", activity, isFinishActivity);
//        } else if (e instanceof NetworkConnectException) {
//            showRetryDialog("网络连接错误！" + e.getMessage(), activity, isFinishActivity);
//        } else if (e instanceof ParseException) {
//            String code = AppConstant.PARSE_EXCEPTION;
//            ClientLogManager.INSTANCE.savaErrorLog(code, e.getMessage(), true);
//            showRetryDialog("目前网络不稳定，加载失败，请重新加载。", activity, isFinishActivity);
//        } else if (e instanceof APIException) {
//            APIException ex = (APIException) e;
//            int status = ex.getresponseInfo().getStatus();
//            if (status == AppConstant.IS_LACK_OF_BALANCE) {
//                lackOfBalanceDialog(ex.getresponseInfo().getMessage(),
//                        (String) ex.getObject(), activity, isFinishActivity);
//            } else if (status == AppConstant.NO_SESSION) { // 本地无session登录状态失效
//                showNoSessionDialog(activity, isFinishActivity);
//            } else if (status == AppConstant.KEY_FAILURE) {// 密钥失效
//                showRetryDialog("密钥失效，请重试！", activity, isFinishActivity);
//            } else if (status == AppConstant.REPEAT_SUBSCRIPTION) {// 重复订购
//                Tools.showToast(ex.getresponseInfo().getMessage(), false);
//                if (isFinishActivity) {
//                    activity.finish();
//                }
//            } else {
//                showRetryDialog(ex.getresponseInfo().getMessage(), activity, isFinishActivity);
//            }
//        } else if (e instanceof SdcardException) {
//            SdcardException ex = (SdcardException) e;
//            String message = "存储卡错误！";
//            if (ex.getErrorCode() == SdcardException.SDCARD_ERROR) {
//                message = Tools.getString(R.string.loading_sdcard_error);
//            } else if (ex.getErrorCode() == SdcardException.SDCARD_FULL) {
//                message = Tools.getString(R.string.loading_sdcard_full);
//            }
//            sdCardErrorDialog(message, activity, isFinishActivity);
//        } else {
            Log.e("showRetryDialog", e.toString());
            showRetryDialog(Tools.getString(R.string.connect_failed), activity, isFinishActivity);
//        }
    }

    /**
     * 无网络对话框
     */
    private void showNoNetWorkDialog(final Activity activity, final boolean isFinishActivity) {
        final CustomTextViewDialog dialog = new CustomTextViewDialog(activity);
        dialog.setTitle(R.string.connect_message);
        dialog.setMessage(R.string.no_network);
        dialog.setCertainButton(R.string.setting, new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
                Tools.openSystemNetworkSetting(activity);
                if (isFinishActivity) {
                    activity.finish();
                }
            }
        });
        dialog.setCancelButton(R.string.cancel, new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
                if (isFinishActivity) {
                    activity.finish();
                }
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel();
                    if (isFinishActivity) {
                        activity.finish();
                    }

                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 重试对话框
     *
     * @param message 提示的内容
     */
    private void showRetryDialog(final String message, final Activity activity, final boolean isFinishActivity) {
        if (activity != null) {


            final CustomTextViewDialog dialog = new CustomTextViewDialog(activity);
            dialog.setTitle(R.string.connect_message);
            dialog.setMessage(message);
            dialog.setCertainButton(R.string.retry, new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.cancel();
                    if (stringRequest != null) {
                        VolleyManager.getInstance().addToRequestQueue(stringRequest);
                    }
                }
            });
            dialog.setCancelButton(R.string.cancel, new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.cancel();
                    // activity.getClass();

                    if (isFinishActivity) {
                        activity.finish();
                    }
                }
            });
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.cancel();
                        if (isFinishActivity) {
                            activity.finish();
                        }
                        return true;
                    }
                    return false;
                }
            });
            dialog.show();
        }
    }

    //获得指定文件的byte数组
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 存储卡错误提示框
     *
     * @param message 提示的话术
     */
    private void sdCardErrorDialog(final String message, final Activity activity, final boolean isFinishActivity) {
        final CustomTextViewDialog dialog = new CustomTextViewDialog(activity);
        dialog.setTitle(R.string.connect_message);
        dialog.setMessage(message);
        dialog.setCertainButton(R.string.certain, new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();

                if (isFinishActivity) {
                    activity.finish();
                }
            }
        });
        dialog.setCancelButton(R.string.cancel, new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
                if (isFinishActivity) {
                    activity.finish();
                }
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel();
                    if (isFinishActivity) {
                        activity.finish();
                    }
                    return true;
                }
                return false;
            }
        });
        dialog.show();
    }

    /**
     * 无网络对话框
     */
    private void showNoSessionDialog(final Activity activity, final boolean isFinishActivity) {
//        final CustomTextViewDialog dialog = new CustomTextViewDialog(activity);
//        dialog.setTitle(R.string.connect_message);
//        dialog.setMessage(R.string.no_session);
//        dialog.setCertainButton("去登录", new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.cancel();
//                Intent intent = new Intent(activity, LoginActivity.class);
//                activity.startActivity(intent);
//                if (isFinishActivity) {
//                    activity.finish();
//                }
//            }
//        });
//        dialog.setCancelButton(R.string.cancel, new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.cancel();
//                if (isFinishActivity) {
//                    activity.finish();
//                }
//            }
//        });
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            public boolean onKey(DialogInterface dialog, int keyCode,
//                                 KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dialog.cancel();
//                    if (isFinishActivity) {
//                        activity.finish();
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    /**
     * 可以监测进度的文件下载
     * @param fileDownloadCallbacek
     * @param url
     * @param fileName
     * @param isIgnoreError
     * @param activity
     * @param isFinishActivity
     */
    public void voleyFileDownRequest(final FileNetCallback fileDownloadCallbacek, String url, String fileName, final boolean isIgnoreError, final Activity activity,
                                     boolean isShowDialog, String dialogMessage,  boolean isCancelDialog, final boolean isFinishActivity) {
        if (isShowDialog) {
            try {
                pd = new CustomProgressdialog(activity, dialogMessage,
                        isCancelDialog, true);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel = true;
                        VolleyManager.getInstance().getRequestQueue().cancelAll(activity);
                        if (isFinishActivity) {
                            activity.finish();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pd = null;
        }
        final FileDownloader mDownloader = new FileDownloader(VolleyManager.getInstance().getRequestQueue(), 3);

        FileDownloader.DownloadController controller = mDownloader.add(fileName, url,
                new Listener<Void>() {

                    // 注：如果暂停或放弃了该任务，onFinish()不会回调
                    @Override
                    public void onFinish() {
                    }

                    // 注：如果暂停或放弃了该任务，onSuccess()不会回调
                    @Override
                    public void onSuccess(Void response) {
                        hideDialog(activity);
                        mDownloader.clearAll();
                        fileDownloadCallbacek.loadFinish(null);
                    }

                    // 注：如果暂停或放弃了该任务，onError()不会回调
                    @Override
                    public void onError(VolleyError error) {
                        mDownloader.clearAll();
                        hideDialog(activity);
                        fileDownloadCallbacek.loadFinish(error);
                        if (isIgnoreError) {

                        } else {
                            onException(error, activity, isFinishActivity);
                        }
                    }

                    // Listener添加了这个回调方法专门用于获取进度
                    @Override
                    public void onProgressChange(long fileSize, long downloadedSize) {
                        fileDownloadCallbacek.onProgress(fileSize, downloadedSize);
                    }
                });
    }


    public interface FileNetCallback {
        public void loadFinish(Exception e);

        public void onProgress(long fileSize, long downloadedSize);
    }



    /**
     * 文件上传方法接口  显示上传的进度
     *
     * @param fileNetCallback  回调
     * @param been             请求实体
     * @param activity         上下文
     * @param dialogMessage    等待框显示文字
     * @param isShowDialog     是否显示等待框
     * @param isCancelDialog   是否可取消
     * @param isFinishActivity 错误是否关闭页面
     */
    public void executeFileUpload(final FileNetCallback fileNetCallback,
                                  final FileUploadBean been, final Activity activity,
                                  String dialogMessage, boolean isShowDialog, boolean isCancelDialog,
                                  final boolean isFinishActivity) {
        if (isShowDialog) {
            try {
                pd = new CustomProgressdialog(activity, dialogMessage,
                        isCancelDialog, true);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel = true;
                        VolleyManager.getInstance().getRequestQueue().cancelAll(activity);
                        if (isFinishActivity) {
                            activity.finish();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pd = null;
        }
        Map<String, String> params = new HashMap<String, String>();
        FormFile formFile = new FormFile(been.getFile().getName(), been.getFile(), been.getFile().getName(), null);
        FormFile[] files = {formFile};
        MultipartRequest multipartRequest = new MultipartRequest(been.getUrl(), new Listener<String>() {
            @Override
            public void onSuccess(String response) {
                hideDialog(activity);
                fileNetCallback.loadFinish(null);
            }

            @Override
            public void onError(VolleyError error) {
                hideDialog(activity);
                fileNetCallback.loadFinish(error);
                onException(error, activity, isFinishActivity);
            }

            @Override
            public void onProgressChange(long fileSize, long downloadedSize) {
                fileNetCallback.onProgress(fileSize,downloadedSize);
            }
        }, params, files);
        VolleyManager.getInstance().addToRequestQueue(multipartRequest);
    }
    /**
     * 文件上传方法接口
     *
     * @param cb               回调
     * @param been             请求实体
     * @param activity         上下文
     * @param dialogMessage    等待框显示文字
     * @param isShowDialog     是否显示等待框
     * @param isCancelDialog   是否可取消
     * @param isFinishActivity 错误是否关闭页面
     */
    public void executeFileUpload(final CallBackInterface cb,
                                  final FileUploadBean been, final Activity activity,
                                  String dialogMessage, boolean isShowDialog, boolean isCancelDialog,
                                  final boolean isFinishActivity) {
        if (isShowDialog) {
            try {
                pd = new CustomProgressdialog(activity, dialogMessage,
                        isCancelDialog, true);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancel = true;
                        VolleyManager.getInstance().getRequestQueue().cancelAll(activity);
                        if (isFinishActivity) {
                            activity.finish();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pd = null;
        }
        Map<String, String> params = new HashMap<String, String>();
        FormFile formFile = new FormFile(been.getFile().getName(), been.getFile(), been.getFile().getName(), null);
        FormFile[] files = {formFile};
        MultipartRequest multipartRequest = new MultipartRequest(Tools.readAddress()+been.getUrl(), new Listener<String>() {
            @Override
            public void onSuccess(String response) {
                hideDialog(activity);
                cb.callBack(HttpTools.json2Object(response, been));
            }

            @Override
            public void onError(VolleyError error) {
                hideDialog(activity);
                onException(error, activity, isFinishActivity);
            }

            @Override
            public void onProgressChange(long fileSize, long downloadedSize) {
            }
        }, params, files);
        VolleyManager.getInstance().addToRequestQueue(multipartRequest);
    }

    /**
     * 验证response是否合法
     *
     * @param response
     * @return
     * @throws JSONException
     */
    public boolean isValidated(String response, Request request, final Activity activity) {
        try {
            response = doEmpty(response, "");

            if (!isEmpty(response)) {
                JSONObject jsonObject = new JSONObject(response);
                ResultData resultData = new ResultData();

                resultData.parseJson(jsonObject);

//                if (AppConstant.IS_SESSION == resultData.stat) {
//                    // 如果登录过期，隐藏登录  自动登陆后再重新请求网络
//                    try {
//                        autoLogin(request, activity);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return false;
//                } else if (AppConstant.NO_SESSION == resultData.stat) {
//                    showNoSessionDialog(activity, false);
//                    return false;
//                } else if (AppConstant.PARAMETER_ERROR == resultData.stat) {
//                    Tools.showToast("参数错误", false);
//                    return false;
//                }

                return true;
            } else {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * @description 验证字符串是否为空
     * @version 1.0
     * @update May 31, 2010 2:16:46 PM
     */
    public static boolean isEmpty(String str) {
        if ((str == null) || str.equals("null") || str.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 处理空字符串
     *
     * @param str
     * @param defaultValue
     * @return String
     */
    public static String doEmpty(String str, String defaultValue) {
        if ((str == null) || str.equalsIgnoreCase("null") || str.trim().equals("")) {
            str = defaultValue;
        } else if (str.startsWith("null")) {
            str = str.substring(4, str.length());
        }
        return str.trim();
    }

    /**
     * 执行静默注册 返回是否注册成功
     */
    private void autoLogin(final Request request, final Activity activity) {
//        if (!ApplicationData.globalContext.getUserManager().isRegist()) {
//            String url = Tools.readAddress() + "/api4/register_new/";
//
//            stringRequest = new StringRequest(Request.Method.POST,
//                    url,
//                    new Listener<String>() {
//                        @Override
//                        public void onSuccess(String response) {
//
//                            try {
//                                RegisterLoginInfo registerLoginInfo =
//                                        RequestDataParse.parseRegisterInfo(response);
//
//                                if (registerLoginInfo.getResponseInfo().getStatus()
//                                        == AppConstant.SUCCESS_OPERATION) {
//                                    if (
//                                            TextUtils.isEmpty(
//                                                    ApplicationData.globalContext.getUserManager().getSessionId())
//                                                    && TextUtils.isEmpty(
//                                                    registerLoginInfo.getUserInfo().getSessionId())) {
//                                    }
//
//                                    new RequestService().initRegisterInfo(registerLoginInfo,
//                                            false);
//
//                                    // 历史数据库更新
//                                    HistoryTable historyDatabase = new HistoryTable();
//
//                                    historyDatabase.updateUserNmaeWhenAdmin(
//                                            registerLoginInfo.getUserInfo()
//                                                    .getUsername());
//
//                                    // 静默注册成功 再次请求当前的网络请求
//                                    if (request != null) {
//                                        VolleyManager.getInstance().addToRequestQueue(request);
//                                    }
//                                } else {
//                                    throw new APIException(registerLoginInfo.getResponseInfo());
//                                }
//                            } catch (Exception e) {
//                            }
//                        }
//
//                        @Override
//                        public void onError(VolleyError error) {
//                            super.onError(error);
//                        }
//                    }) {
//                @Override
//                public Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> map = new HashMap<String, String>();
//                    return map;
//                }
//            };
//            // 将StringRequest对象添加进RequestQueue请求队列中
//            stringRequest.setTag(this);
//            VolleyManager.getInstance().addToRequestQueue(stringRequest);
//        } else {
//            // 登录过期的情况
//            showNoSessionDialog(activity, false);
//        }
    }
}
