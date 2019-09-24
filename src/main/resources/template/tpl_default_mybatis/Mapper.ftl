<#import "/lib/mf.ftl" as mf/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="<@mf.daoPkg/>.${entName}Dao" >
    <#--设置关联表的resultMap-->
    <#if refTables?? && (refTables?size>0)>
    
    <resultMap type="<@mf.entityPkg/>.${entName}" id="${lowEntName}Map">
      <@mf.list columns;col>
      <#if col.pk>
      <id property="${col.propName}" column="${col.colName}"/>
      <#else>
      <result property="${col.propName}" column="${col.colName}"/>
      </#if>
      </@mf.list>
      <@mf.list refTables;refTab>
      <#--判断有一个 或 有很多个-->
      <#if refTab.refType=="OneToOne" || refTab.refType=="ManyToOne">
          <association property="${refTab.lowEntName}" javaType="<@mf.entityPkg/>.${refTab.entName}">
	      <#list refTab.columns as col>
	      <result property="${col.propName}" column="${col.colName}"/>
	      </#list>
      	  </association>
      <#elseif refTab.refType=="OneToMany" || refTab.refType=="ManyToMany">
	      <collection property="${refTab.lowEntName}List" ofType="<@mf.entityPkg/>.${refTab.entName}"
	      	javaType="ArrayList">
	      <#list refTab.columns as col>
	      <result property="${col.propName}" column="${col.colName}"/>
	      </#list>
	      </collection>
      </#if>
      </@mf.list>
      
    </resultMap>
    <#--分页查询-->
    <select id="find${entName}Page" parameterType="<@mf.entityPkg/>.${entName}" resultMap="${lowEntName}Map">
    	select a.* from ${tabName} a
    	<where>
    	<@mf.list columns;col>
    	<if test="${col.propName}!=null and ${col.propName}!='' ">
			and a.${col.colName} = <@mf.print "#"/>{${col.propName}}
		</if>
    	</@mf.list>
    	</where>
    </select>
    
    <#else>
    <#--没配置map,分页查询-->
    <select id="find${entName}Page" parameterType="<@mf.entityPkg/>.${entName}" resultType="<@mf.entityPkg/>.${entName}">
    	select a.* from ${tabName} a
    	<where>
    	<@mf.list columns;col>
    	<if test="${col.propName}!=null and ${col.propName}!='' ">
			a.${col.colName} = <@mf.print "#"/>{${col.propName}}
		</if>
    	</@mf.list>
    	</where>
    </select>
    </#if>
    
    <#--新增-->
    <insert id="insert${entName}" parameterType="<@mf.entityPkg/>.${entName}">
        insert into ${tabName} (
			<#list columns as col>${col.colName}<#if col_has_next>,</#if></#list>
        )values(
 			<#list columns as col><@mf.print "#"/>{${col.propName}}<#if col_has_next>,</#if></#list>
		)
    </insert>
    <#if uniIdxCols?? && (uniIdxCols?size>0)>
    <#--查询-->
    <select id="find${entName}By" parameterType="<@mf.entityPkg/>.${entName}" 
    	resultType="<@mf.entityPkg/>.${entName}">
        select * from ${tabName} where 1=1 
         <@mf.list uniIdxCols;idxCol>
         and ${idxCol.colName}=<@mf.print "#"/>{${idxCol.propName}}
         </@mf.list>  		
    </select>
    <#--修改-->
    <update id="update${entName}" parameterType="<@mf.entityPkg/>.${entName}">
        update ${tabName} set 
        <#list columns as col>${col.colName}=<@mf.print "#"/>{${col.propName}}<#if col_has_next>,</#if></#list>
         where 1=1 
         <@mf.list uniIdxCols;idxCol>
         and ${idxCol.colName}=<@mf.print "#"/>{${idxCol.propName}}
         </@mf.list>  		
    </update>
    <#--删除-->
    <delete id="delete${entName}" parameterType="<@mf.entityPkg/>.${entName}">
         delete from ${tabName} where 1=1 
         <@mf.list uniIdxCols;idxCol>
         and ${idxCol.colName}=<@mf.print "#"/>{${idxCol.propName}}
         </@mf.list>   		
    </delete>
	</#if>
    
    
</mapper>