package com.kst.funddemo.common.exception;

/**
 * 自定义解析xml数据错误
 * @author wangyue
 *
 */
public class ParseException extends TDException{

	private static final long serialVersionUID = 1L;

	public ParseException(Throwable throwable) {
		super(throwable);
	}
}
