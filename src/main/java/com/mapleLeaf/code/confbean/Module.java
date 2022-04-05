package com.mapleLeaf.code.confbean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块信息
 * @author 
 */
public class Module {
	private String name;
	
	private String persistence; //持久层框架  全局参数

	private boolean columnIsCamel;//表字段是否驼峰命名  全局参数
	//java基本类型是否为包装
	private boolean wrapperClass;
	private String baseTabPrefix;//表名前缀，可以多个 全局参数
	//全部表字段名前缀，可以多个
	private String baseColPrefix;
	private String daoPackage;
	private String daoImplPackage;
	private String servicePackage;
	private String serviceImplPackage;
	private String entityPackage;
	private String controllerPackage;

	private String mapperPackage;
	private String myBatisPackage;
	private List<TableConf> tables;
	private List<CodeFileConf> codeFiles;
	
	private Map<String,String> paramMap=new HashMap<>();
	
	private ReportConf reportConf;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	public String getPersistence() {
		return persistence;
	}
	public void setPersistence(String persistence) {
		this.persistence = persistence;
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
	
	
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	public String getBaseTabPrefix() {
		return baseTabPrefix;
	}
	public void setBaseTabPrefix(String baseTabPrefix) {
		this.baseTabPrefix = baseTabPrefix;
	}
	public List<CodeFileConf> getCodeFiles() {
		return codeFiles;
	}
	public void setCodeFiles(List<CodeFileConf> codeFiles) {
		this.codeFiles = codeFiles;
	}
	public String getBaseColPrefix() {
		return baseColPrefix;
	}
	public void setBaseColPrefix(String baseColPrefix) {
		this.baseColPrefix = baseColPrefix;
	}
	public boolean isWrapperClass() {
		return wrapperClass;
	}
	public void setWrapperClass(boolean wrapperClass) {
		this.wrapperClass = wrapperClass;
	}
	public ReportConf getReportConf() {
		return reportConf;
	}
	public void setReportConf(ReportConf reportConf) {
		this.reportConf = reportConf;
	}
	
}
