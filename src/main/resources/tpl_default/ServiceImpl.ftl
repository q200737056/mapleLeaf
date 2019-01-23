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
	private ${entityName}Mapper ${fstLowEntityName}Mapper;
	
	/**
	 * 新增
	 */
	 public int insert${entityName}(${entityName} ${fstLowEntityName}){
	 	return ${fstLowEntityName}Mapper.insert${entityName}(${fstLowEntityName});
	 }
	<#if uniIdxMap??>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    /**
	 * 根据唯一索引${key}查找
	 */
	 public ${entityName} find${entityName}By${key?lower_case?cap_first}(${entityName} ${fstLowEntityName}){
	 	return ${fstLowEntityName}Mapper.find${entityName}By${key?lower_case?cap_first}(${fstLowEntityName});
	 }
	/**
	 * 根据唯一索引${key}修改
	 */
	 public int update${entityName}By${key?lower_case?cap_first}(${entityName} ${fstLowEntityName}){
	 	return ${fstLowEntityName}Mapper.update${entityName}By${key?lower_case?cap_first}(${fstLowEntityName});
	 }
	/**
	 * 根据唯一索引${key}删除
	 */
	 public int delete${entityName}By${key?lower_case?cap_first}(${entityName} ${fstLowEntityName}){
	 	return ${fstLowEntityName}Mapper.delete${entityName}By${key?lower_case?cap_first}(${fstLowEntityName});
	 }
	</#list>
    </#if>
}
