<#import "/lib/mf.ftl" as mf/>
package <@mf.servicePkg/>;

import <@mf.entityPkg/>.${entName};

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
	<#if uniIdxCols?? && (uniIdxCols?size>0)>
	/**
	 * ${tabName}查找
	 */
	 public ${entName} get${entName}(${entName} ${lowEntName});
	/**
	 * ${tabName}修改
	 */
	 public int update${entName}(${entName} ${lowEntName});
	/**
	 * ${tabName}删除
	 */
	 public int delete${entName}(${entName} ${lowEntName});
	</#if>
	
}
