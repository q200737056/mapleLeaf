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
	<#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#if uniIdxMap[key][0].pk>
    /**
	 * 根据主键查找
	 */
	  public ${entityName} get${entityName}ById(Long id);
	 /**
	 * 修改
	 */
	 public int update${entityName}(${entityName} ${fstLowEntityName});
	/**
	 * 根据主键删除
	 */
	 public int delete${entityName}ById(Long id);
	 <#else>
    
	 </#if>
	</#list>
    </#if>
}
