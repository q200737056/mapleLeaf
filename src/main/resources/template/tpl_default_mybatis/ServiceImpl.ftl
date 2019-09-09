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
	 	return ${lowEntName}Mapper.find${entName}Page(${entName} ${lowEntName});
	 }
	/**
	 * 新增
	 */
	 public int insert${entName}(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.insert${entName}(${lowEntName});
	 }
	<#if uniIdxMap??>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    /**
	 * 根据唯一索引${key}查找
	 */
	 public ${entName} find${entName}By${key?lower_case?cap_first}(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.find${entName}By${key?lower_case?cap_first}(${lowEntName});
	 }
	/**
	 * 根据唯一索引${key}修改
	 */
	 public int update${entName}By${key?lower_case?cap_first}(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.update${entName}By${key?lower_case?cap_first}(${lowEntName});
	 }
	/**
	 * 根据唯一索引${key}删除
	 */
	 public int delete${entName}By${key?lower_case?cap_first}(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.delete${entName}By${key?lower_case?cap_first}(${lowEntName});
	 }
	</#list>
    </#if>
    <#if refTables?? && (refTables?size>0)>
    <#list refTables as subtb>
    <#if subtb.refColumnMap?? && (subtb.refColumnMap?size>0)>
    /**
	 * 根据输入条件 关联查询
	 */
	 public List<Map> find${entName}ByCons(Map map){
	 	return ${lowEntName}Mapper.find${entName}ByCons(Map map);
	 }	
    </#if>
    </#list>
    </#if>
}
