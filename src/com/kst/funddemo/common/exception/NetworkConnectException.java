package com.kst.funddemo.common.exception;

/**
 * 网络连接错误
 * @author wangyue
 *
 */
public class NetworkConnectException extends TDException {
	private static final long serialVersionUID = 1L;
	
	public NetworkConnectException() {
		super("");
	}
	
	public NetworkConnectException(String string) {
		super(string);
	}
	
	public NetworkConnectException(Throwable throwable) {
		super(throwable);
	}
	
	public NetworkConnectException(String string, Throwable throwable) {
		super(string, throwable);
	}
}
