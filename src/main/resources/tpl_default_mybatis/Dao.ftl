package ${basePackage}.${daoPackage};

import ${basePackage}.${entityPackage}.${entityName};

/**
 * ${remark!}操作相关
 */
public interface ${entityName}Mapper {
	/**
	 * 分页查询
	 */
	 public List<${entityName}> find${entityName}Page(${entityName} ${fstLowEntityName});
	/**
	 * 新增
	 */
	 public int insert${entityName}(${entityName} ${fstLowEntityName});
	<#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#if uniIdxMap[key][0].pk>
    /**
	 * 根据主键查找
	 */
	 <#else>
    /**
	 * 根据唯一索引${key}查找
	 */
	 </#if>
	 public ${entityName} find${entityName}By${key?lower_case?cap_first}(${entityName} ${fstLowEntityName});
	/**
	 * 根据唯一索引${key}修改
	 */
	 public int update${entityName}By${key?lower_case?cap_first}(${entityName} ${fstLowEntityName});
	/**
	 * 根据唯一索引${key}删除
	 */
	 public int delete${entityName}By${key?lower_case?cap_first}(${entityName} ${fstLowEntityName});
	</#list>
    </#if>
    <#if subTables?? && (subTables?size>0)>
    <#list subTables as subtb>
    <#if subtb.refColumnMap?? && (subtb.refColumnMap?size>0)>
    /**
	 * 根据输入条件 关联查询
	 */
	 public List<Map> find${entityName}ByCons(Map map);	
    </#if>
    </#list>
    </#if>
    
}
