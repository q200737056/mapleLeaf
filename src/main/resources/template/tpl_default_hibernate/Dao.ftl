package ${basePackage}.${daoPackage};

import ${basePackage}.${entityPackage}.${entName};

/**
 * ${remark!}操作相关
 */
public interface ${entName}Dao {
	/**
	 * 分页查询
	 */
	 public List<${entName}> find${entName}Page(${entName} ${lowEntName});
	/**
	 * 新增
	 */
	 public int insert${entName}(${entName} ${lowEntName});
	<#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#if uniIdxMap[key][0].pk>
    /**
	 * 根据主键查找
	 */
	  public ${entName} get${entName}ById(Long id);
	 /**
	 * 修改
	 */
	 public int update${entName}(${entName} ${lowEntName});
	/**
	 * 根据主键删除
	 */
	 public int delete${entName}ById(Long id);
	 <#else>
    
	 </#if>
	</#list>
    </#if>
    
}
