package com.mapleLeaf.code.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

	private String tableFullName;// 完整表名
	private String tableName; // 去掉prefix的表名
	private String fstLowEntityName; // 首字母小写的实体类名
	private String entityName; // 实体类名
	private String remark; // 表注释
	private String primaryKey;// 主键 弃用
	private String primaryProperty;// 主键属性名 弃用
	private String primaryPropertyType; // 主键属性类型 弃用
	private String primaryCamelProperty;// 弃用
	private String primaryKeyType;// 主键类型 弃用

	private List<String> importClassList = new ArrayList<String>();//需要导入字段类型的类
	private List<Column> columns = new ArrayList<Column>();// 字段
	private List<Table> subTables = new ArrayList<Table>();// 从表 集合
	private String refType; // 关联类型
	private Map<String, String> refColumnMap = new HashMap<>();// 主从关联字段map，key主表字段，value从表字段
	private Map<String, String> refPropertyMap = new HashMap<>();// 主从关联属性map，key主表实体类属性，value从表实体类属性
	private Table parentTable;// 主表
	private Map<String, List<Column>> uniIdxMap = new HashMap<>();// 唯一索引map集合，包含主键
																	// key:索引名
																	// value:Column集合
	private String exclude;//排除指定模板的文件生成(dao,service,controller,view,custom,entity)
	
	public Table getParentTable() {
		return parentTable;
	}

	public void setParentTable(Table parentTable) {
		this.parentTable = parentTable;
	}

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
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

	public List<String> getImportClassList() {
		return importClassList;
	}

	public void setImportClassList(List<String> importClassList) {
		this.importClassList = importClassList;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getPrimaryKeyType() {
		return primaryKeyType;
	}

	public void setPrimaryKeyType(String primaryKeyType) {
		this.primaryKeyType = primaryKeyType;
	}

	public String getPrimaryProperty() {
		return primaryProperty;
	}

	public void setPrimaryProperty(String primaryProperty) {
		this.primaryProperty = primaryProperty;
	}

	public String getPrimaryCamelProperty() {
		return primaryCamelProperty;
	}

	public void setPrimaryCamelProperty(String primaryCamelProperty) {
		this.primaryCamelProperty = primaryCamelProperty;
	}

	public String getPrimaryPropertyType() {
		return primaryPropertyType;
	}

	public void setPrimaryPropertyType(String primaryPropertyType) {
		this.primaryPropertyType = primaryPropertyType;
	}

	
	public String getTableFullName() {
		return tableFullName;
	}

	public void setTableFullName(String tableFullName) {
		this.tableFullName = tableFullName;
	}

	public List<Table> getSubTables() {
		return subTables;
	}

	public void setSubTables(List<Table> subTables) {
		this.subTables = subTables;
	}

	public String getRefType() {
		return refType;
	}

	public void setRefType(String refType) {
		this.refType = refType;
	}

	public Map<String, List<Column>> getUniIdxMap() {
		return uniIdxMap;
	}

	public void setUniIdxMap(Map<String, List<Column>> uniIdxMap) {
		this.uniIdxMap = uniIdxMap;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
	

}
