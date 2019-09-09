package com.mapleLeaf.code.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefTable {

	private String tabName;// 表名
	private String lowEntName; // 首字母小写的实体类名
	private String entName; // 实体类名

	private List<Column> columns = new ArrayList<Column>();// 字段

	private String refType; //关联的表中需要赋值 关联类型
	
	private Map<String, String> refColMap = new HashMap<>();//关联的表中需要赋值 关联字段map，key主表字段，value从表字段
	
	private Map<String, String> refPropMap = new HashMap<>();//关联的表中需要赋值 关联属性map，key主表实体类属性，value从表实体类属性
	
	public Map<String, String> getRefColMap() {
		return refColMap;
	}

	public void setRefColMap(Map<String, String> refColMap) {
		this.refColMap = refColMap;
	}
	
	public Map<String, String> getRefPropMap() {
		return refPropMap;
	}

	public void setRefPropMap(Map<String, String> refPropMap) {
		this.refPropMap = refPropMap;
	}
	
	public String getLowEntName() {
		return lowEntName;
	}

	public void setLowEntName(String lowEntName) {
		this.lowEntName = lowEntName;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
	public String getEntName() {
		return entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}

	public String getRefType() {
		return refType;
	}

	public void setRefType(String refType) {
		this.refType = refType;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
}
