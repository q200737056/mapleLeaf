package com.mapleLeaf.code.model;

import java.util.ArrayList;
import java.util.List;

public class Table {

	private String tabName; // 表名
	
	private String lowEntName; // 首字母小写的实体类名
	private String entName; // 实体类名
	private String remark; // 表注释
	
	private List<String> impClasses = new ArrayList<String>();//需要导入字段类型的类
	private List<Column> columns = new ArrayList<Column>();// 字段

	private List<RefTable> refTables = new ArrayList<>();// 所有关联表的集合
	
	//private Map<String, List<Column>> uniIdxMap = new HashMap<>();// 唯一索引map集合，包含主键
																	// key:索引名
																	// value:Column集合
	private String exclude;//排除指定模板的文件生成(dao,service,controller,custom,entity)
	
	
	private List<Column> uniIdxCols = new ArrayList<>();// 其中一组的表唯一索引（或主键）的字段集合
													    
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public List<String> getImpClasses() {
		return impClasses;
	}

	public void setImpClasses(List<String> impClasses) {
		this.impClasses = impClasses;
	}

	
	public String getEntName() {
		return entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}

	
	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public List<RefTable> getRefTables() {
		return refTables;
	}

	public void setRefTables(List<RefTable> refTables) {
		this.refTables = refTables;
	}

	public List<Column> getUniIdxCols() {
		return uniIdxCols;
	}

	public void setUniIdxCols(List<Column> uniIdxCols) {
		this.uniIdxCols = uniIdxCols;
	}
	
	
}
