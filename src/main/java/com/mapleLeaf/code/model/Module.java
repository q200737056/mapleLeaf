package com.mapleLeaf.code.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块信息
 * @author 
 */
public class Module {
	private String name; //模块名称
	
	private String persistance; //持久层框架  全局参数
	private boolean isDeleteTablePrefix; //是否删除表前缀  全局参数
	
	private String daoPackage;
	private String daoImplPackage;
	private String servicePackage;
	private String serviceImplPackage;
	private String entityPackage;
	private String controllerPackage;
	private String viewPackage;
	private String customPackage;
	private String mapperPackage;
	private String myBatisPackage;
	private List<TableConf> tables; //配置的数据表信息 
	private boolean columnIsCamel;//表字段是否驼峰命名  全局参数
	private Map<String,String> attrsMap=new HashMap<>();//标签属性集  key:标签名_属性名  value:属性值
	private Map<String,String> paramMap=new HashMap<>();//自定义参数
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isDeleteTablePrefix() {
		return isDeleteTablePrefix;
	}
	public void setDeleteTablePrefix(boolean isDeleteTablePrefix) {
		this.isDeleteTablePrefix = isDeleteTablePrefix;
	}
	
	public String getDaoPackage() {
		return daoPackage;
	}
	public void setDaoPackage(String daoPackage) {
		this.daoPackage = daoPackage;
	}
	public String getDaoImplPackage() {
		return daoImplPackage;
	}
	public void setDaoImplPackage(String daoImplPackage) {
		this.daoImplPackage = daoImplPackage;
	}
	public String getServicePackage() {
		return servicePackage;
	}
	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}
	public String getServiceImplPackage() {
		return serviceImplPackage;
	}
	public void setServiceImplPackage(String serviceImplPackage) {
		this.serviceImplPackage = serviceImplPackage;
	}
	public String getEntityPackage() {
		return entityPackage;
	}
	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}
	public String getControllerPackage() {
		return controllerPackage;
	}
	public void setControllerPackage(String controllerPackage) {
		this.controllerPackage = controllerPackage;
	}
	public String getViewPackage() {
		return viewPackage;
	}
	public void setViewPackage(String viewPackage) {
		this.viewPackage = viewPackage;
	}

	
	public String getCustomPackage() {
		return customPackage;
	}
	public void setCustomPackage(String customPackage) {
		this.customPackage = customPackage;
	}
	public String getMapperPackage() {
		return mapperPackage;
	}
	public void setMapperPackage(String mapperPackage) {
		this.mapperPackage = mapperPackage;
	}
	public List<TableConf> getTables() {
		return tables;
	}
	public void setTables(List<TableConf> tables) {
		this.tables = tables;
	}
	
	public String getPersistance() {
		return persistance;
	}
	public void setPersistance(String persistance) {
		this.persistance = persistance;
	}
	
	public String getMyBatisPackage() {
		return myBatisPackage;
	}
	public void setMyBatisPackage(String mybatisPackage) {
		this.myBatisPackage = mybatisPackage;
	}
	
	public boolean isColumnIsCamel() {
		return columnIsCamel;
	}
	public void setColumnIsCamel(boolean columnIsCamel) {
		this.columnIsCamel = columnIsCamel;
	}
	
	public Map<String, String> getAttrsMap() {
		return attrsMap;
	}
	public void setAttrsMap(Map<String, String> attrsMap) {
		this.attrsMap = attrsMap;
	}
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
}
