package com.mapleLeaf.code.confbean;

public class RefConf  {
	private String tabName; 
	
	private String refType; 
	private String refColumns;  
	private String midTabName;
	private String midRefCol;
	private String forKey;
	
	
	private ColumnGroupConf colGroup;
	private String entName;
	
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
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

	public String getEntName() {
		return entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}
	
	
}
