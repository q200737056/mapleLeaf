package com.mapleLeaf.code.model;

import java.util.HashMap;
import java.util.Map;

public class Column {
    
    private String colName;//字段名
    private String colType; //字段的类型
    private String remark;//字段 注释
    private String propName; //属性名
    private String propType; //属性的java type
    private String upperPropName; //首字大写的属性名 
    private boolean isPk=false;//是否主键
    private boolean isNullable=true;//是否允许为空
    private Long length; //字段长度
    private Object defaultValue; //字段默认值
    
    //字段 文本,表单类型,字段标识 默认 COMMENT中取  
	//COMMENT约定形式(字段文本;表单类型;val1:text1,val2:text2;)
    //例如 flag字段   状态;select;0:初始,1:已审核,2:已退回;
    private String labelName=""; 
    //k-v型 字段值，key为 字段值 val，value为标识描述text
    //默认 COMMENT中取 （如果有）
    private Map<String,String> colValueMap = new HashMap<>();
    //表单 标签类型，默认text;
    //默认 COMMENT中取 （如果有）
    private String tagType="text"; 
   
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getColType() {
		return colType;
	}
	public void setColType(String colType) {
		this.colType = colType;
	}
	public String getPropName() {
		return propName;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	public String getPropType() {
		return propType;
	}
	public void setPropType(String propType) {
		this.propType = propType;
	}
	public String getUpperPropName() {
		return upperPropName;
	}
	public void setUpperPropName(String upperPropName) {
		this.upperPropName = upperPropName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	public boolean isPk() {
		return isPk;
	}
	public void setPk(boolean isPk) {
		this.isPk = isPk;
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
