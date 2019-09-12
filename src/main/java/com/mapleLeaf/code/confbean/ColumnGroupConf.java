package com.mapleLeaf.code.confbean;

import java.util.HashMap;
import java.util.Map;

public class ColumnGroupConf {

	
	private String exclude;
	
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
	
	
}
