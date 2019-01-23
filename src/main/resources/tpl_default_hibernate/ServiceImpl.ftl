package ${basePackage}.${servicePackage}.${serviceImplPackage};

import ${basePackage}.${entityPackage}.${entityName};
import ${basePackage}.${servicePackage}.${entityName}Service;
import ${basePackage}.${daoPackage}.${entityName}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${remark!}操作相关
 */
@Service
public class ${entityName}ServiceImpl implements ${entityName}Service {

	@Autowired
	private ${entityName}Dao ${fstLowEntityName}Dao;
	
	/**
	 * 分页查询
	 */
	 public List<${entityName}> find${entityName}Page(${entityName} ${fstLowEntityName}){
	 	return ${fstLowEntityName}Dao.find${entityName}Page(${entityName} ${fstLowEntityName});
	 }
	/**
	 * 新增
	 */
	 public int insert${entityName}(${entityName} ${fstLowEntityName}){
	 	return ${fstLowEntityName}Dao.insert${entityName}(${fstLowEntityName});
	 }
	<#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#if uniIdxMap[key][0].pk>
    /**
	 * 根据主键查找
	 */
	  public ${entityName} get${entityName}ById(Long id){
	  	return ${fstLowEntityName}Dao.get${entityName}ById(id);
	  }
	 /**
	 * 修改
	 */
	 public int update${entityName}(${entityName} ${fstLowEntityName}){
	 	return ${fstLowEntityName}Dao.update${entityName}(${fstLowEntityName});
	 }
	/**
	 * 根据主键删除
	 */
	 public int delete${entityName}ById(Long id){
	 	return ${fstLowEntityName}Dao.delete${entityName}ById(id);
	 }
	 <#else>
    
	 </#if>
	</#list>
    </#if>
}
