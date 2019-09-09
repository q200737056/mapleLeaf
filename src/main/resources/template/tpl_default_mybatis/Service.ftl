package ${basePackage}.${servicePackage};

import ${basePackage}.${entityPackage}.${entName};

/**
 * ${remark!}操作相关
 */
public interface ${entName}Service {
	/**
	 * 分页查询
	 */
	 public List<${entName}> find${entName}Page(${entName} ${lowEntName});
	/**
	 * 新增
	 */
	 public int insert${entName}(${entName} ${lowEntName});
	<#if uniIdxMap??>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    /**
	 * 根据唯一索引${key}查找
	 */
	 public ${entName} find${entName}By${key?lower_case?cap_first}(${entName} ${lowEntName});
	/**
	 * 根据唯一索引${key}修改
	 */
	 public int update${entName}By${key?lower_case?cap_first}(${entName} ${lowEntName});
	/**
	 * 根据唯一索引${key}删除
	 */
	 public int delete${entName}By${key?lower_case?cap_first}(${entName} ${lowEntName});
	</#list>
    </#if>
    <#if refTables?? && (refTables?size>0)>
    <#list refTables as subtb>
    <#if subtb.refColumnMap?? && (subtb.refColumnMap?size>0)>
    /**
	 * 根据输入条件 关联查询
	 */
	 public List<Map> find${entName}ByCons(Map map);	
    </#if>
    </#list>
    </#if>
}
