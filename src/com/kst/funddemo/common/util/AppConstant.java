package com.kst.funddemo.common.util;

public class AppConstant {
	
	/** httpheader */
	public static final String httpHeader = "http://";
	/** 操作成功的返回码 */
	public static final int SUCCESS_OPERATION = 100;
	
	/** sdcard根目录 */
	public static final String sdcardRootPath = android.os.Environment
			.getExternalStorageDirectory().getPath();
	/** 机身根目录 */
	public static final String dataRootPath = android.os.Environment
			.getDataDirectory() + "/data/" + Tools.getPackageName() + "/files/";
	
	/** 主动取消联网 */
	public static final String NetworkForceCloseException = "600";
	/** 无网络 */
	public static final String NoNetWork = "601";
	/** 网络超时 */
	public static final String NetworkTimeoutException = "602";
	/** 其它网络异常导致连接失败 */
	public static final String NetWorkConnectException = "603";
	
}
