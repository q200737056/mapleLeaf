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
	//private String primaryKey;// 主键 弃用
	//private String primaryProperty;// 主键属性名 弃用
	//private String primaryPropertyType; // 主键属性类型 弃用
	//private String primaryCamelProperty;// 弃用
	//private String primaryKeyType;// 主键类型 弃用

	private List<String> importClassList = new ArrayList<String>();//需要导入字段类型的类
	private List<Column> columns = new ArrayList<Column>();// 字段
	//private List<Table> subTables = new ArrayList<Table>();// 从表 集合   弃用
	private List<RefTable> refTables = new ArrayList<>();// 所有关联表的集合
	//private String refType; //关联的表中需要赋值 关联类型
	//private Map<String, String> refColumnMap = new HashMap<>();//关联的表中需要赋值 关联字段map，key主表字段，value从表字段
	//private Map<String, String> refPropertyMap = new HashMap<>();//关联的表中需要赋值 关联属性map，key主表实体类属性，value从表实体类属性
	//private Table parentTable;// 主表 弃用
	private Map<String, List<Column>> uniIdxMap = new HashMap<>();// 唯一索引map集合，包含主键
																	// key:索引名
																	// value:Column集合
	private String exclude;//排除指定模板的文件生成(dao,service,controller,view,custom,entity)
	
	
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

	public String getTableFullName() {
		return tableFullName;
	}

	public void setTableFullName(String tableFullName) {
		this.tableFullName = tableFullName;
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

	public List<RefTable> getRefTables() {
		return refTables;
	}

	public void setRefTables(List<RefTable> refTables) {
		this.refTables = refTables;
	}

}
