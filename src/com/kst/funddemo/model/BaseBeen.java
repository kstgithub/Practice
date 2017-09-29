package com.kst.funddemo.model;

/***
 * 模型基类
 * @author dell
 *
 */
public class BaseBeen {

	/** 状态码 */
	private int code;
	/** 信息 */
	private String message;
	/** 接口地址 */
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
