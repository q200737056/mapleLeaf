<#--map输出(自带null判断)；推荐map类型循环时使用-->
<#macro map map={}>
<#list map?keys as key>
  <#nested key,map[key]>
</#list>
</#macro>
<#--list输出(自带null判断)；推荐list类型循环时使用-->
<#macro list list=[]>
<#list list as item>
  <#nested item,item_index>
</#list>
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

<#--函数，获取主表中所有关联字段（即外键）-->
<#function getFks>
<#local fks>
<@compress single_line=true>
<@mf.list refTables;reftab>
<#if reftab.forKey??>
${reftab.forKey},
</#if>
</@mf.list>
</@compress>
</#local>
<#return fks?split(",")>
</#function>
