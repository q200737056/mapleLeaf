<#--map输出(自带空判断)；推荐map类型循环时使用-->
<#macro map map={}>
<#if map?size gt 0>
<#list map?keys as key>
  <#nested key,map[key]>
</#list>
</#if>
</#macro>
<#--list输出(自带空判断)；推荐list类型循环时使用-->
<#macro list list=[]>
<#if list?size gt 0>
<#list list as item>
  <#nested item,item_index>
</#list>
</#if>
</#macro>
<#--特殊字符原样输出，等同于<#noescape></#noescape>，${r''}，
	为什么要自定义，因为比前者简单，比后者更直观
-->
<#macro print str>${str}</#macro>
<#--没什么好说的，封装了一下包路径-->
<#macro daoPkg>${basePackage}.${daoPackage}</#macro>
<#macro daoImplPkg>${basePackage}.${daoPackage}.${daoImplPackage}</#macro>
<#macro servicePkg>${basePackage}.${servicePackage}</#macro>
<#macro serviceImplPkg>${basePackage}.${servicePackage}.${serviceImplPackage}</#macro>
<#macro controllerPkg>${basePackage}.${controllerPackage}</#macro>
<#macro entityPkg>${basePackage}.${entityPackage}</#macro>

