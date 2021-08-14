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
	 	return ${lowEntName}Dao.find${entName}Page(${lowEntName});
	 }
	/**
	 * 新增
	 */
	 public int insert${entName}(${entName} ${lowEntName}){
	 	return ${lowEntName}Dao.insert${entName}(${lowEntName});
	 }
	<#if uniIdxCols?? && (uniIdxCols?size>0)>
    /**
	 * ${tabName}查找
	 */
	 public ${entName} get${entName}(${entName} ${lowEntName}){
	 	return ${lowEntName}Dao.get${entName}(${lowEntName});
	 }
	/**
	 * ${tabName}修改
	 */
	 public int update${entName}(${entName} ${lowEntName}){
	 	return ${lowEntName}Dao.update${entName}(${lowEntName});
	 }
	/**
	 * ${tabName}删除
	 */
	 public int delete${entName}(${entName} ${lowEntName}){
	 	return ${lowEntName}Dao.delete${entName}(${lowEntName});
	 }
	</#if>
  
}
