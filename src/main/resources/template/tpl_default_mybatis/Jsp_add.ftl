<#import "/lib/mf.ftl" as mf/>
<%@page language="java"  pageEncoding="UTF-8"%>
<%@include file="../common/tag.jsp"%>
<!DOCTYPE html>
<html>
    <head>
        <title>添加${remark!}</title>
        <meta name="renderer" content="webkit">
        <%@include file="../common/header.jsp"%>
      
    </head>
    <body>
        <form class="layui-form" id="addFrom" method="post">
           	<#list columns?chunk(2) as row>
            <div class="layui-form-item">
           		<#list row as col>
           		<#if !col.pk && !mf.getFks()?seq_contains(col.colName)>
           			<div class="layui-inline">
	                    <label class="layui-form-label">${col.labelName}</label>
		                <#if col.tagType=="select">
		                	<div class="layui-input-block">
						      <select name="${col.propName}" id="${col.propName}" lay-verify="required">
						        <option value=""></option>
						        <@mf.map col.colValueMap;val,text>
						        <option value="${val}">${text}</option>
						        </@mf.map>
						      </select>
						    </div>
						<#elseif col.tagType=="checkbox">
							<div class="layui-input-block">
							  <@mf.map col.colValueMap;val,text>
						      <input type="checkbox" name="${col.propName}" value="${val}" title="${text}">
						      </@mf.map>
						    </div>
						<#elseif col.tagType=="radio">
							<div class="layui-input-block">
							  <@mf.map col.colValueMap;val,text>
						      <input type="radio" name="${col.propName}" value="${val}" title="${text}">
						      </@mf.map>
						    </div>
						<#elseif col.tagType=="textarea">
							<div class="layui-input-block">
					            <textarea name="${col.propName}" id="${col.propName}" class="layui-textarea">
					            </textarea>
					         </div>
					    <#elseif col.tagType=="date">
					         <div class="layui-input-inline">
					             <input type="text" name="${col.propName}" id="${col.propName}" lay-verify="date" 
					             	placeholder="yyyy-MM-dd" autocomplete="off" class="layui-input">
					         </div>
					    <#else>
					    	<div class="layui-input-block">
			                    <input type="text" name="${col.propName}" id="${col.propName}"
			                    	autocomplete="off" lay-verify="required" class="layui-input" />
		                	</div>
		                </#if>
	                </div>
           		</#if>	
           		</#list>
            </div>
            </#list>
            <div class="layui-form-item btn-box">
                <div class="btn-layui">
                    <button type="button" class="layui-btn" lay-submit lay-filter="add">提交</button>
                    <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                </div>
            </div>
        </form>
        <%@include file="../common/footer.jsp"%>
       
    </body>
</html>