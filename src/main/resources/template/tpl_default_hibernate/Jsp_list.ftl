<#import "/lib/mf.ftl" as mf/>
<%@page language="java" pageEncoding="UTF-8"%>
<%@include file="../common/tag.jsp"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>${remark!}查询</title>
        <%@include file="../common/header.jsp"%>
    </head>
    <body>
        <form class="layui-form" action="" id="tableFrom">
            
              
                <#list searchColumns as col>
                <#--如果未配置searchPos，则显示全部字段-->
              
                <div class="layui-form-item">
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
                </div>
                </#list>
                
             <div class="layui-form-item">
                <div class="btn-layui">
                    <a href="javascript:termQueryPage();" class="layui-btn layui-btn-small">查询</a>
                    <a href="javascript:resetFrom();" class="layui-btn layui-btn-small">清空</a>
                </div>
            </div>
        </form>

        <div class="container" style="width: 100%; padding: 0; margin: 0;">
            <table class="layui-table tree table-bordered table-hover" lay-even="" lay-skin="">
                <thead>
                	<tr>
                   <@mf.list listColumns;col>
                   	<th>${col.labelName}</th>
                   </@mf.list>
                   <tr>
                </thead>
                <tbody id="list">
					
                </tbody>
            </table>
        </div>
        <%@include file="../common/footer.jsp"%>
       
    </body>
</html>