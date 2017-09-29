package com.kst.funddemo.common.exception;

/**
 * 自定义异常基类
 * @author wangyue
 *
 */
public class TDException extends Exception{
	
	private static final long serialVersionUID = 1L;
	public TDException() {
		super();
	}
	public TDException(String message) {
		super(message);
	}
	public TDException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public TDException(Throwable throwable) {
		super(throwable);
	}
	
}
