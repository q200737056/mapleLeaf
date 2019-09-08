package ${basePackage}.${servicePackage};

import ${basePackage}.${entityPackage}.${entityName};

/**
 * ${remark!}操作相关
 */
public interface ${entityName}Service {
	/**
	 * 分页查询
	 */
	 public List<${entityName}> find${entityName}Page(${entityName} ${fstLowEntityName});
	/**
	 * 新增
	 */
	 public int insert${entityName}(${entityName} ${fstLowEntityName});
	<#if uniIdxMap??>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    /**
	 * 根据唯一索引${key}查找
	 */
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
    <#if refTables?? && (refTables?size>0)>
    <#list refTables as subtb>
    <#if subtb.refColumnMap?? && (subtb.refColumnMap?size>0)>
    /**
	 * 根据输入条件 关联查询
	 */
	 public List<Map> find${entityName}ByCons(Map map);	
    </#if>
    </#list>
    </#if>
}
