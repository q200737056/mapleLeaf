<#import "/lib/mf.ftl" as mf/>
package <@mf.daoPkg/>;

import <@mf.entityPkg/>.${entName};

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
	<#if uniIdxCols?? && (uniIdxCols?size>0)>
    /**
	 * 查找
	 */
	  public ${entName} get${entName}(Long id);
	 /**
	 * 修改
	 */
	 public int update${entName}(${entName} ${lowEntName});
	/**
	 * 删除
	 */
	 public int delete${entName}(Long id);
    </#if>
    
}
