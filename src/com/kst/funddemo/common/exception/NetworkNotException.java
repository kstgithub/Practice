package com.kst.funddemo.common.exception;

/**
 * 自定义无网络异常
 * @author wangyue
 *
 */
public class NetworkNotException extends TDException{

	private static final long serialVersionUID = 1L;

	public NetworkNotException() {
		super("NetworkNotAvailableException");
	}

}
