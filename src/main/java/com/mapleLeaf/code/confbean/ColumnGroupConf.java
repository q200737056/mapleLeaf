package com.mapleLeaf.code.confbean;

import java.util.HashMap;
import java.util.Map;

public class ColumnGroupConf {

	
	private String exclude; //排除的字段
	private String searchPos; //查询条件字段
	private String listPos;//列表中显示的字段
	private String inputPos;//需要输入显示的字段
	
	private Map<String,ColumnConf>  colConfMap = new HashMap<String, ColumnConf>();
	
	public String getExclude() {
		return exclude;
	}
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
	public Map<String, ColumnConf> getColConfMap() {
		return colConfMap;
	}
	public void setColConfMap(Map<String, ColumnConf> colConfMap) {
		this.colConfMap = colConfMap;
	}
	public String getSearchPos() {
		return searchPos;
	}
	public void setSearchPos(String searchPos) {
		this.searchPos = searchPos;
	}
	public String getListPos() {
		return listPos;
	}
	public void setListPos(String listPos) {
		this.listPos = listPos;
	}
	public String getInputPos() {
		return inputPos;
	}
	public void setInputPos(String inputPos) {
		this.inputPos = inputPos;
	}
	
}
