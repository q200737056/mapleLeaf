package com.mapleLeaf.code.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefTable {

	private String tableFullName;// 完整表名
	private String tableName; // 去掉prefix的表名
	private String fstLowEntityName; // 首字母小写的实体类名
	private String entityName; // 实体类名

	private List<Column> columns = new ArrayList<Column>();// 字段

	private String refType; //关联的表中需要赋值 关联类型
	private Map<String, String> refColumnMap = new HashMap<>();//关联的表中需要赋值 关联字段map，key主表字段，value从表字段
	private Map<String, String> refPropertyMap = new HashMap<>();//关联的表中需要赋值 关联属性map，key主表实体类属性，value从表实体类属性
	


	public Map<String, String> getRefColumnMap() {
		return refColumnMap;
	}

	public void setRefColumnMap(Map<String, String> refColumnMap) {
		this.refColumnMap = refColumnMap;
	}

	public Map<String, String> getRefPropertyMap() {
		return refPropertyMap;
	}

	public void setRefPropertyMap(Map<String, String> refPropertyMap) {
		this.refPropertyMap = refPropertyMap;
	}

	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
	public String getFstLowEntityName() {
		return fstLowEntityName;
	}

	public void setFstLowEntityName(String fstLowEntityName) {
		this.fstLowEntityName = fstLowEntityName;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	
	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getTableFullName() {
		return tableFullName;
	}

	public void setTableFullName(String tableFullName) {
		this.tableFullName = tableFullName;
	}

	public String getRefType() {
		return refType;
	}

	public void setRefType(String refType) {
		this.refType = refType;
	}

}
