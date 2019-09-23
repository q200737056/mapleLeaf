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
	<@mf.map uniIdxMap;idxnm,cols>
    /**
	 * 根据唯一索引${idxnm}查找
	 */
	 public ${entName} find${entName}By${idxnm?cap_first}(${entName} ${lowEntName});
	
    /**
	 * 根据唯一索引${idxnm}修改
	 */
	 public int update${entName}By${idxnm?cap_first}(${entName} ${lowEntName});
	
    /**
	 * 根据唯一索引${idxnm}删除
	 */
	 public int delete${entName}By${idxnm?cap_first}(${entName} ${lowEntName});
	</@mf.map>
	
	<@mf.list refTables;refTab>
    /**
	 * 根据输入条件 关联查询
	 */
	 public List<Map> find${entName}ByCons(Map map);
	</@mf.list>
     
}
