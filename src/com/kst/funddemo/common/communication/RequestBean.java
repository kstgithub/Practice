package com.kst.funddemo.common.communication;

import org.apache.http.protocol.HTTP;

import com.kst.funddemo.common.util.HttpTools;
import com.kst.funddemo.common.util.Tools;
import com.kst.funddemo.model.BaseBeen;
import com.kst.funddemo.model.FileUploadBean;

public class RequestBean {

	/** 联网底层类 负责网络数据的接收和发送 */
	public RequestConnection remoteRequester = new RequestConnection();

	/**
	 * 取消联网
	 */
	public void cancelConn() {
		remoteRequester.setCancel();
	}

	/**
	 * 获取联网状态
	 * 
	 * @return 是否连接
	 */
	public boolean getConnState() {
		return remoteRequester.getState();
	}

	// ################ 文件下载相关接口 ###################

	/**
	 * 获得文件内容
	 * 
	 * @param url
	 *            链接地址
	 * @return 文件内容
	 * @throws Exception
	 */
	public String doGetContent(String url) throws Exception {
		return new String(remoteRequester.doGetFile(url, null), HTTP.UTF_8);
	}

	/**
	 * 获取byte数组
	 * 
	 * @param url
	 *            连接地址
	 * @return 数据字节数组
	 * @throws Exception
	 */
	public byte[] doGetData(String url) throws Exception {
		return remoteRequester.doGetFile(url, null);
	}

	public FileUploadBean doPostFile(FileUploadBean bean) throws Exception {
		String url = Tools.readAddress() + bean.getUrl();
		return (FileUploadBean) HttpTools.json2Object(remoteRequester
				.doPostFile(url, bean.getFile().getAbsolutePath()), bean);
	}

	public BaseBeen doGet(BaseBeen been) throws Exception {
		return (BaseBeen) HttpTools.json2Object(remoteRequester.doGet(
				Tools.readAddress() + been.getUrl(),
				HttpTools.getNameValuePair(been)), been);
	}

	public BaseBeen doPost(BaseBeen been) throws Exception {
		return (BaseBeen) HttpTools.json2Object(remoteRequester.doPost(
				Tools.readAddress() + been.getUrl(),
				HttpTools.getNameValuePair(been)), been);
	}
}
