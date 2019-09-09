package ${basePackage}.${servicePackage}.${serviceImplPackage};

import ${basePackage}.${entityPackage}.${entName};
import ${basePackage}.${servicePackage}.${entName}Service;
import ${basePackage}.${daoPackage}.${entName}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${remark!}操作相关
 */
@Service
public class ${entName}ServiceImpl implements ${entName}Service {

	@Autowired
	private ${entName}Dao ${lowEntName}Dao;
	
	/**
	 * 分页查询
	 */
	 public List<${entName}> find${entName}Page(${entName} ${lowEntName}){
	 	return ${lowEntName}Dao.find${entName}Page(${entName} ${lowEntName});
	 }
	/**
	 * 新增
	 */
	 public int insert${entName}(${entName} ${lowEntName}){
	 	return ${lowEntName}Dao.insert${entName}(${lowEntName});
	 }
	<#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#if uniIdxMap[key][0].pk>
    /**
	 * 根据主键查找
	 */
	  public ${entName} get${entName}ById(Long id){
	  	return ${lowEntName}Dao.get${entName}ById(id);
	  }
	 /**
	 * 修改
	 */
	 public int update${entName}(${entName} ${lowEntName}){
	 	return ${lowEntName}Dao.update${entName}(${lowEntName});
	 }
	/**
	 * 根据主键删除
	 */
	 public int delete${entName}ById(Long id){
	 	return ${lowEntName}Dao.delete${entName}ById(id);
	 }
	 <#else>
    
	 </#if>
	</#list>
    </#if>
}
