<#import "/lib/mf.ftl" as mf/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="<@mf.daoPkg/>.${entName}Dao" >
    <#--设置关联表的resultMap-->
    <#if refTables?? && (refTables?size>0)>
    
    <resultMap type="<@mf.entityPkg/>.${entName}" id="${lowEntName}Result">
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
      <association property="${refTab.lowEntName}" javaType="<@mf.entityPkg/>.${refTab.entName}" 
      	    <#if refTab.forKey??>column="${refTab.forKey}"</#if>  resultMap="${refTab.lowEntName}Result" />
      <#elseif refTab.refType=="OneToMany" || refTab.refType=="ManyToMany">
       <collection property="${refTab.lowEntName}List" ofType="<@mf.entityPkg/>.${refTab.entName}"
	      	javaType="ArrayList" resultMap="${refTab.lowEntName}Result"/>
      </#if>
      </@mf.list>
      </resultMap>
      
      <@mf.list refTables;refTab>
      <resultMap type="<@mf.entityPkg/>.${refTab.entName}" id="${refTab.lowEntName}Result">
      <@mf.list refTab.columns;col>
	  <#if col.pk>
      	 <id property="${col.propName}" column="${col.colName}"/>
      <#else>
      	 <result property="${col.propName}" column="${col.colName}"/>
      </#if>
	  </@mf.list>
	  </resultMap>
	  </@mf.list>
    <#--分页查询-->
    <select id="find${entName}Page" parameterType="<@mf.entityPkg/>.${entName}" resultMap="${lowEntName}Result">
    	select <#list columns as col>a.${col.colName}<#if refTables?size gt 0><#sep></#if>,</#list>
    	<#list refTables as reftab>
    		<#if reftab_has_next>
    		<#list reftab.columns as col>r${col?index+1}.${col.colName},</#list>
    		<#else>
    		<#list reftab.columns as col>r${col?index+1}.${col.colName}<#sep>,</#list>
    		</#if>
    	</#list>
    	from ${tabName} a
    	<@mf.list refTables;reftab,idx>
    		<#if reftab.refType=="ManyToMany">
    		left join ${reftab.tabName} r${idx+1} on <@mf.map reftab.refColMap;k,v>a.${k}=m.${v}</@mf.map>
    		left join ${reftab.midTabName!} m on <@mf.map reftab.midRefColMap;k,v>m.${k}=r${idx+1}.${v}</@mf.map>
    		<#else>
    		left join ${reftab.tabName} r${idx+1} on <@mf.map reftab.refColMap;k,v>a.${k}=r${idx+1}.${v}</@mf.map>
    		</#if>
    	</@mf.list>
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
			and a.${col.colName} = <@mf.print "#"/>{${col.propName}}
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