<#import "/lib/mf.ftl" as mf/>
package <@mf.serviceImplPkg/>;

import <@mf.entityPkg/>.${entName};
import <@mf.servicePkg/>.${entName}Service;
import <@mf.daoPkg/>.${entName}Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${remark!}操作相关
 */
@Service
public class ${entName}ServiceImpl implements ${entName}Service {

	@Autowired
	private ${entName}Dao ${lowEntName}Dao;
	
	/**
	 * 分页查询
	 */
	 public List<${entName}> find${entName}Page(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.find${entName}Page(${entName} ${lowEntName});
	 }
	/**
	 * 新增
	 */
	 public int insert${entName}(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.insert${entName}(${lowEntName});
	 }
	<@mf.map uniIdxMap;idxnm,cols>
    /**
	 * 根据唯一索引${idxnm}查找
	 */
	 public ${entName} find${entName}By${idxnm?cap_first}(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.find${entName}By${idxnm?cap_first}(${lowEntName});
	 }
	/**
	 * 根据唯一索引${idxnm}修改
	 */
	 public int update${entName}By${idxnm?cap_first}(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.update${entName}By${idxnm?cap_first}(${lowEntName});
	 }
	/**
	 * 根据唯一索引${idxnm}删除
	 */
	 public int delete${entName}By${idxnm?cap_first}(${entName} ${lowEntName}){
	 	return ${lowEntName}Mapper.delete${entName}By${idxnm?cap_first}(${lowEntName});
	 }
	</@mf.map>
    <#if refTables?? && (refTables?size>0)>
    <#list refTables as subtb>
    <#if subtb.refColumnMap?? && (subtb.refColumnMap?size>0)>
    /**
	 * 根据输入条件 关联查询
	 */
	 public List<Map> find${entName}ByCons(Map map){
	 	return ${lowEntName}Mapper.find${entName}ByCons(Map map);
	 }	
    </#if>
    </#list>
    </#if>
}
