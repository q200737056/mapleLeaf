package com.mapleLeaf.code.model;

import java.util.ArrayList;
import java.util.List;

public class Table {
	// 表名
	private String tabName; 
	// 首字母小写的实体类名
	private String lowEntName; 
	// 实体类名
	private String entName; 
	// 表注释
	private String remark; 
	//需要导入字段类型的类
	private List<String> impClasses = new ArrayList<String>();
	// 字段集合
	private List<Column> columns = new ArrayList<Column>();
	// 查询条件字段集合
	private List<Column> searchColumns = new ArrayList<Column>();
	// 列表字段集合
	private List<Column> listColumns = new ArrayList<Column>();
	// 表单输入字段集合
	private List<Column> inputColumns = new ArrayList<Column>();
	//所有关联表的集合
	private List<RefTable> refTables = new ArrayList<>();
	
	//排除指定包下的代码生成(dao,service,controller,custom,entity)
	private String exclude;
	// 其中一组的表唯一索引（或主键）的字段集合
	private List<Column> uniIdxCols = new ArrayList<>();
	
													    
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

	public List<Column> getSearchColumns() {
		return searchColumns;
	}

	public void setSearchColumns(List<Column> searchColumns) {
		this.searchColumns = searchColumns;
	}

	public List<Column> getListColumns() {
		return listColumns;
	}

	public void setListColumns(List<Column> listColumns) {
		this.listColumns = listColumns;
	}

	public List<Column> getInputColumns() {
		return inputColumns;
	}

	public void setInputColumns(List<Column> inputColumns) {
		this.inputColumns = inputColumns;
	}
	
}
