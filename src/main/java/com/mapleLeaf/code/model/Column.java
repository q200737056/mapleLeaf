package com.mapleLeaf.code.model;

import java.util.HashMap;
import java.util.Map;

public class Column {
    
    private String columnName;//字段名
    private String columnType; //字段的类型
    private String remark;//字段 注释
    private String propertyName; //属性名
    private String propertyType; //属性的java type
    private String fstUpperProName; //首字大写的属性名 
    private boolean isPk=false;//是否主键
    private boolean isNullable=true;//是否允许为空
    private Long length; //字段长度
    private Object defaultValue; //字段默认值
    
    //字段文本 默认 COMMENT中取   COMMENT形式(字段文本;val1:text1,val2:text2;)
    private String labelName=""; 
    //k-v型 字段值，key为 字段值 val1，val为描述text1
    //默认 COMMENT中取   COMMENT形式(字段文本;val1:text1,val2:text2;)
    private Map<String,String> colValueMap = new HashMap<>();
    //表单 标签类型，默认text;该属性比较开放，自己定义一套类型，在页面需要时，做判断 
    //比如 select 表示 下拉框，checkbox表示多选，radio 表示单选
    private String tagType="text"; 
    
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public boolean isPk() {
		return isPk;
	}
	public void setPk(boolean isPk) {
		this.isPk = isPk;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	
	public String getFstUpperProName() {
		return fstUpperProName;
	}
	public void setFstUpperProName(String fstUpperProName) {
		this.fstUpperProName = fstUpperProName;
	}
	public boolean isNullable() {
		return isNullable;
	}
	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public Map<String, String> getColValueMap() {
		return colValueMap;
	}
	public void setColValueMap(Map<String, String> colValueMap) {
		this.colValueMap = colValueMap;
	}
	public String getTagType() {
		return tagType;
	}
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}
	
}
