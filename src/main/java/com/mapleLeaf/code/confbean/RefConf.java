package com.mapleLeaf.code.confbean;

public class RefConf  {
	private String tabName; //表名
	
	private String prefix; //表名前缀
	private String refType; //关联类型，一对一，一对多等。主对从
	private String refColumns; //关联的字段，主表字段=关联表字段(多对多时，主表字段=中间表字段) 多个逗号分隔 
	private String midTabName;//多对多关联时 的中间表
	private String midRefCol;//多对多关联时 中间表字段=关联表字段
	private String forKey;//关联字段(即外键)
	
	
	private ColumnGroupConf colGroup;
	
	
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
	
	
	public ColumnGroupConf getColGroup() {
		return colGroup;
	}
	public void setColGroup(ColumnGroupConf colGroup) {
		this.colGroup = colGroup;
	}
	
	public String getMidTabName() {
		return midTabName;
	}
	public void setMidTabName(String midTabName) {
		this.midTabName = midTabName;
	}
	public String getMidRefCol() {
		return midRefCol;
	}
	public void setMidRefCol(String midRefCol) {
		this.midRefCol = midRefCol;
	}
	public String getForKey() {
		return forKey;
	}
	public void setForKey(String forKey) {
		this.forKey = forKey;
	}
	
	
}
