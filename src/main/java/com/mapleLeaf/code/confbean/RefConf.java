package com.mapleLeaf.code.confbean;

public class RefConf {
	private String refName; //关联从表的表名
	private String refType; //关联类型，一对一，一对多等。主对从
	private String refColumns; //关联的字段，主表字段=关联表字段(多对多时，主表字段=中间表字段) 多个逗号分隔 
	private String midName;//多对多关联时 的中间表
	private String midRefCol;//多对多关联时 中间表字段=关联表字段
	
	private String prefix;//表名前缀
	private String entityName;//配置的实体类名
	
	private ColumnGroupConf colGroup;
	
	public String getRefName() {
		return refName;
	}
	public void setRefName(String refName) {
		this.refName = refName;
	}
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
	public String getRefColumns() {
		return refColumns;
	}
	public void setRefColumns(String refColumns) {
		this.refColumns = refColumns;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public ColumnGroupConf getColGroup() {
		return colGroup;
	}
	public void setColGroup(ColumnGroupConf colGroup) {
		this.colGroup = colGroup;
	}
	public String getMidName() {
		return midName;
	}
	public void setMidName(String midName) {
		this.midName = midName;
	}
	public String getMidRefCol() {
		return midRefCol;
	}
	public void setMidRefCol(String midRefCol) {
		this.midRefCol = midRefCol;
	}
	
}
