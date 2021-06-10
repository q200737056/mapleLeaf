package com.mapleLeaf.code.confbean;

import java.util.ArrayList;
import java.util.List;
/**
 * 模块中表的定义
 * @author 
 *
 */
public class TableConf {
	private String tabName;
	
	private String exclude;
	
	private List<RefConf> refConfs = new ArrayList<>();
	
	private ColumnGroupConf colGroup;
	
	private String entName;
	
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getExclude() {
		return exclude;
	}
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
	public List<RefConf> getRefConfs() {
		return refConfs;
	}
	public void setRefConfs(List<RefConf> refConfs) {
		this.refConfs = refConfs;
	}
	public ColumnGroupConf getColGroup() {
		return colGroup;
	}
	public void setColGroup(ColumnGroupConf colGroup) {
		this.colGroup = colGroup;
	}

	public String getEntName() {
		return entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}
	
}
