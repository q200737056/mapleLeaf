<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${basePackage}.${daoPackage}.${entityName}Mapper" >
    <#--设置关联表的resultMap-->
    <#if subTables?? && (subTables?size>0)>
    
    <resultMap type="${basePackage}.${entityPackage}.${entityName}" id="${fstLowEntityName}Map">
      <#list columns as col>
      <result property="${col.propertyName}" column="${col.columnName}"/>
      </#list>
      <#list subTables as subtab>
      <#--判断一对一，or一对多-->
      <#if subtab.refType=="OneToOne">
          <association property="${subtab.fstLowEntityName}" javaType="${basePackage}.${entityPackage}.${subtab.entityName}">
	      <#list subtab.columns as col>
	      <result property="${col.propertyName}" column="${col.columnName}"/>
	      </#list>
      	  </association>
      <#elseif subtab.refType=="OneToMany">
	      <collection property="${subtab.fstLowEntityName}List" ofType="${basePackage}.${entityPackage}.${subtab.entityName}"
	      	javaType="ArrayList">
	      <#list subtab.columns as col>
	      <result property="${col.propertyName}" column="${col.columnName}"/>
	      </#list>
	      </collection>
      </#if>
      </#list>
    </resultMap>
    <#--分页查询-->
    <select id="find${entityName}Page" parameterType="${basePackage}.${entityPackage}.${entityName}" resultMap="${fstLowEntityName}Map">
    	select a.* from ${tableFullName} a
    	<where>
    	<#list columns as col>
    	<if test="${col.propertyName}!=null and ${col.propertyName}!='' ">
			and a.${col.columnName} = ${r"#{"}${col.propertyName}}
		</if>
    	</#list>
    	</where>
    </select>
    
    <#else>
    <#--没配置map,分页查询-->
    <select id="find${entityName}Page" parameterType="${basePackage}.${entityPackage}.${entityName}" resultType="${basePackage}.${entityPackage}.${entityName}">
    	select a.* from ${tableFullName} a
    	<where>
    	<#list columns as col>
    	<if test="${col.propertyName}!=null and ${col.propertyName}!='' ">
			a.${col.columnName} = ${r"#{"}${col.propertyName}}
		</if>
    	</#list>
    	</where>
    </select>
    </#if>
    
    <#--新增-->
    <insert id="insert${entityName}" parameterType="${basePackage}.${entityPackage}.${entityName}">
        insert into ${tableFullName} (
			<#list columns as col>${col.columnName}<#if col_has_next>,</#if></#list>
        )values(
 			<#list columns as col>${r'#{'}${col.propertyName}${r'}'}<#if col_has_next>,</#if></#list>
		)
    </insert>
    <#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#--查询-->
    <select id="find${entityName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entityName}" 
    	resultType="${basePackage}.${entityPackage}.${entityName}">
        select * from ${tableFullName} where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.columnName}=${r'#{'}${idxCol.propertyName}${r'}'}
         </#list>  		
    </select>
    <#--修改-->
    <update id="update${entityName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entityName}">
        update ${tableFullName} set 
        <#list columns as col>${col.columnName}=${r'#{'}${col.propertyName}${r'}'}<#if col_has_next>,</#if></#list>
         where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.columnName}=${r'#{'}${idxCol.propertyName}${r'}'}
         </#list>  		
    </update>
    <#--删除-->
    <delete id="delete${entityName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entityName}">
         delete from ${tableFullName} where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.columnName}=${r'#{'}${idxCol.propertyName}${r'}'}
         </#list>  		
    </delete>
	</#list>
    </#if>
    <#if subTables?? && (subTables?size>0)>
    <#list subTables as subtb>
    <#if subtb.refColumnMap?? && (subtb.refColumnMap?size>0)>
    <select id="find${entityName}ByCons" parameterType="map" 
    	resultType="map">
        select * from ${tableFullName} a left join ${subtb.tableFullName} b on 
        <#list subtb.refColumnMap?keys as key>a.${key}=b.${subtb.refColumnMap["${key}"]}</#list> 		
    </select>
    </#if>
    </#list>
    </#if>
</mapper>