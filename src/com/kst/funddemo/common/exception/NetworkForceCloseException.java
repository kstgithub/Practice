package com.kst.funddemo.common.exception;

/**
 * 联网取消异常类
 * @author wangyue
 *
 */
public class NetworkForceCloseException extends TDException{

	private static final long serialVersionUID = 1L;

	public NetworkForceCloseException() {
		super();
	}
	
	public NetworkForceCloseException(String message) {
		super(message);
	}
	
}
