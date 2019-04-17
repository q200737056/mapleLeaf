<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${basePackage}.${daoPackage}.${entityName}Mapper" >
    
    <insert id="insert${entityName}" parameterType="${basePackage}.${entityPackage}.${entityName}">
        insert into ${tableFullName} (
			<#list columns as col>${col.columnName}<#if col_has_next>,</#if></#list>
        )values(
 			<#list columns as col>${r'#{'}${col.propertyName}${r'}'}<#if col_has_next>,</#if></#list>
		)
    </insert>
    <#if uniIdxMap??>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <select id="find${entityName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entityName}" 
    	resultType="${basePackage}.${entityPackage}.${entityName}">
        select * from ${tableFullName} where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.columnName}=${r'#{'}${idxCol.propertyName}${r'}'}
         </#list>  		
    </select>
    
    <update id="update${entityName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entityName}">
        update ${tableFullName} set 
        <#list columns as col>${col.columnName}=${r'#{'}${col.propertyName}${r'}'}<#if col_has_next>,</#if></#list>
         where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.columnName}=${r'#{'}${idxCol.propertyName}${r'}'}
         </#list>  		
    </update>
    
    <delete id="delete${entityName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entityName}">
         delete from ${tableFullName} where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.columnName}=${r'#{'}${idxCol.propertyName}${r'}'}
         </#list>  		
    </delete>
	</#list>
    </#if>
</mapper>