<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${basePackage}.${daoPackage}.${entName}Dao" >
    <#--设置关联表的resultMap-->
    <#if refTables?? && (refTables?size>0)>
    
    <resultMap type="${basePackage}.${entityPackage}.${entName}" id="${lowEntName}Map">
      <#list columns as col>
      <result property="${col.propName}" column="${col.colName}"/>
      </#list>
      <#list refTables as subtab>
      <#--判断一对一，or一对多-->
      <#if subtab.refType=="OneToOne">
          <association property="${subtab.lowEntName}" javaType="${basePackage}.${entityPackage}.${subtab.entName}">
	      <#list subtab.columns as col>
	      <result property="${col.propName}" column="${col.colName}"/>
	      </#list>
      	  </association>
      <#elseif subtab.refType=="OneToMany">
	      <collection property="${subtab.lowEntName}List" ofType="${basePackage}.${entityPackage}.${subtab.entName}"
	      	javaType="ArrayList">
	      <#list subtab.columns as col>
	      <result property="${col.propName}" column="${col.colName}"/>
	      </#list>
	      </collection>
      </#if>
      </#list>
    </resultMap>
    <#--分页查询-->
    <select id="find${entName}Page" parameterType="${basePackage}.${entityPackage}.${entName}" resultMap="${lowEntName}Map">
    	select a.* from ${tabName} a
    	<where>
    	<#list columns as col>
    	<if test="${col.propName}!=null and ${col.propName}!='' ">
			and a.${col.colName} = ${r"#{"}${col.propName}}
		</if>
    	</#list>
    	</where>
    </select>
    
    <#else>
    <#--没配置map,分页查询-->
    <select id="find${entName}Page" parameterType="${basePackage}.${entityPackage}.${entName}" resultType="${basePackage}.${entityPackage}.${entName}">
    	select a.* from ${tabName} a
    	<where>
    	<#list columns as col>
    	<if test="${col.propName}!=null and ${col.propName}!='' ">
			a.${col.colName} = ${r"#{"}${col.propName}}
		</if>
    	</#list>
    	</where>
    </select>
    </#if>
    
    <#--新增-->
    <insert id="insert${entName}" parameterType="${basePackage}.${entityPackage}.${entName}">
        insert into ${tabName} (
			<#list columns as col>${col.colName}<#if col_has_next>,</#if></#list>
        )values(
 			<#list columns as col>${r'#{'}${col.propName}${r'}'}<#if col_has_next>,</#if></#list>
		)
    </insert>
    <#if uniIdxMap?? && (uniIdxMap?size>0)>
    <#assign keys=uniIdxMap?keys />
    <#list keys as key>
    <#--查询-->
    <select id="find${entName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entName}" 
    	resultType="${basePackage}.${entityPackage}.${entName}">
        select * from ${tabName} where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.colName}=${r'#{'}${idxCol.propName}${r'}'}
         </#list>  		
    </select>
    <#--修改-->
    <update id="update${entName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entName}">
        update ${tabName} set 
        <#list columns as col>${col.colName}=${r'#{'}${col.propName}${r'}'}<#if col_has_next>,</#if></#list>
         where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.colName}=${r'#{'}${idxCol.propName}${r'}'}
         </#list>  		
    </update>
    <#--删除-->
    <delete id="delete${entName}By${key?lower_case?cap_first}" parameterType="${basePackage}.${entityPackage}.${entName}">
         delete from ${tabName} where 1=1 
         <#list uniIdxMap[key] as idxCol>
         and ${idxCol.colName}=${r'#{'}${idxCol.propName}${r'}'}
         </#list>  		
    </delete>
	</#list>
    </#if>
    <#if refTables?? && (refTables?size>0)>
    <#list refTables as subtb>
    <#if subtb.refColMap?? && (subtb.refColMap?size>0)>
    <select id="find${entName}ByCons" parameterType="map" 
    	resultType="map">
        select * from ${tabName} a left join ${subtb.tabName} b on 
        <#list subtb.refColMap?keys as key>a.${key}=b.${subtb.refColMap["${key}"]}</#list> 		
    </select>
    </#if>
    </#list>
    </#if>
</mapper>