<#import "/lib/mf.ftl" as mf/>
﻿<%@page language="java"  pageEncoding="UTF-8"%>
<%@include file="../common/tag.jsp"%>
<!DOCTYPE html>
<html>
    <head>
        <title>查看详情</title>
        <meta name="renderer" content="webkit">
        <%@include file="../common/header.jsp"%>
    </head>
    <body>
        <table class="layui-table del-table">
            <colgroup>
                <col width="100">
                <col width="120">
                <col width="100">
                <col width="120">
            </colgroup>
            <tbody>
            	<#list columns?chunk(2) as row>
            	<tr>
            		<#list row as col>
            			<td class="td_colo">${col.labelName}</td>
                    	<td><@mf.print "$"/>{item.${col.propName}}</td>
            		</#list>
            	</tr>	
            	</#list>
            </tbody>
        </table>
        <%@include file="../common/footer.jsp"%>
    </body>
</html>