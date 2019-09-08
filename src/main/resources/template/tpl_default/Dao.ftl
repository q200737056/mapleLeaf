package ${basePackage}.${daoPackage};

import ${basePackage}.${entityPackage}.${entityName};

/**
 * ${remark!}操作相关
 */
public interface ${entityName}Dao {
	
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
}
