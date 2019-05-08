package ${basePackage}.${daoPackage};

import ${basePackage}.${entityPackage}.${entityName};
import org.hibernate.SessionFactory;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;   
/**
 * ${remark!}操作相关
 */
@Repository 
public class ${entityName}DaoImpl implements ${entityName}Dao {
	
	@Autowired    
   	private SessionFactory sessionFactory;
   	private Session getCurrSession(){
   		return sessionFactory.getCurrentSession();
   	}
	/**
	 * 分页查询
	 */
	 public List<${entityName}> find${entityName}Page(${entityName} ${fstLowEntityName});
	/**
	 * 新增
	 */
	 public int insert${entityName}(${entityName} ${fstLowEntityName}){
	 	 int res = this.getCurrSession().save(${fstLowEntityName});
	 	 return res;
	 }
	<#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#if uniIdxMap[key][0].pk>
    /**
	 * 根据主键查找
	 */
	  public ${entityName} get${entityName}ById(Long id){
	  	  ${entityName} ${fstLowEntityName} = (${entityName})this.getCurrSession().get(${entityName}.class,id);
	  	  return ${fstLowEntityName};
	  }
	 /**
	 * 修改
	 */
	 public int update${entityName}(${entityName} ${fstLowEntityName}){
	 	  int res = this.getCurrSession().saveOrUpdate(${fstLowEntityName});
	 }
	/**
	 * 根据主键删除
	 */
	 public int delete${entityName}ById(Long id){
 		  ${entityName} ${fstLowEntityName}=get${entityName}ById(id);
 		  if(${fstLowEntityName}!=null){
 			  return this.getCurrSession().delete(${fstLowEntityName});
 		  }
 		  return 0;
	 }
	 <#else>
   
	 </#if>
	
	</#list>
    </#if>
   
    
}
