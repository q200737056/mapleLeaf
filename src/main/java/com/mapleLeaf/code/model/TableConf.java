package com.mapleLeaf.code.model;

import java.util.ArrayList;
import java.util.List;
/**
 * 模块中表的定义
 * @author mars.liu
 *
 */
public class TableConf {
	private String name; //表名
	private String prefix;//表前缀
	private String entityName;//配置的实体类名
	private String parentField;// 如果是主从表，则从表需设置该属性，表示父表的关联属性 ??
	private String refType; //表关联类型,主表对从表 分为OneToOne,OneToMany等
	private String refColumns;//关联的字段，主表字段=从表字段 多个逗号分隔
	private String exclude;//排除指定模板的文件生成(dao,service,controller,view,custom,entity)
	private List<TableConf> subTables = new ArrayList<TableConf>();//从表集合
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	public List<TableConf> getSubTables() {
		return subTables;
	}
	public void setSubTables(List<TableConf> subTables) {
		this.subTables = subTables;
	}
	
	public String getParentField() {
		return parentField;
	}
	public void setParentField(String parentField) {
		this.parentField = parentField;
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
	public String getExclude() {
		return exclude;
	}
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
	
}
