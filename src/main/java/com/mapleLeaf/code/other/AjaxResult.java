package com.mapleLeaf.code.other;

public class AjaxResult<T> {
	private String code;//状态码
	private String rusultState;//处理结果状态
	private String msg;//信息
	private T data;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getRusultState() {
		return rusultState;
	}
	public void setRusultState(String rusultState) {
		this.rusultState = rusultState;
	}
	
}
