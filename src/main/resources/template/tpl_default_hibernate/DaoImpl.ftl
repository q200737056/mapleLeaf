package ${basePackage}.${daoPackage};

import ${basePackage}.${entityPackage}.${entName};
import org.hibernate.SessionFactory;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;   
/**
 * ${remark!}操作相关
 */
@Repository 
public class ${entName}DaoImpl implements ${entName}Dao {
	
	@Autowired    
   	private SessionFactory sessionFactory;
   	private Session getCurrSession(){
   		return sessionFactory.getCurrentSession();
   	}
	/**
	 * 分页查询
	 */
	 public List<${entName}> find${entName}Page(${entName} ${lowEntName});
	/**
	 * 新增
	 */
	 public int insert${entName}(${entName} ${lowEntName}){
	 	 int res = this.getCurrSession().save(${lowEntName});
	 	 return res;
	 }
	<#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#if uniIdxMap[key][0].pk>
    /**
	 * 根据主键查找
	 */
	  public ${entName} get${entName}ById(Long id){
	  	  ${entName} ${lowEntName} = (${entName})this.getCurrSession().get(${entName}.class,id);
	  	  return ${lowEntName};
	  }
	 /**
	 * 修改
	 */
	 public int update${entName}(${entName} ${lowEntName}){
	 	  int res = this.getCurrSession().saveOrUpdate(${lowEntName});
	 }
	/**
	 * 根据主键删除
	 */
	 public int delete${entName}ById(Long id){
 		  ${entName} ${lowEntName}=get${entName}ById(id);
 		  if(${lowEntName}!=null){
 			  return this.getCurrSession().delete(${lowEntName});
 		  }
 		  return 0;
	 }
	 <#else>
   
	 </#if>
	
	</#list>
    </#if>
   
    
}
