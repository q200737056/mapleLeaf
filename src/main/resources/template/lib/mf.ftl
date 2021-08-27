<#--map输出(自带null判断)；推荐map类型循环时使用-->
<#macro map map={}>
<#list map?keys as key>
  <#nested key,map[key]>
</#list>
</#macro>
<#--list输出(自带null判断)；推荐list类型循环时使用-->
<#macro list list=[]>
<#list list as item>
  <#nested item,item?index>
</#list>
</#macro>
<#--分行输出list,默认1行1个，分隔符为‘，’-->
<#macro list_row prop num=1 list=[] sign=",">
<#if list?size gt 0>
	<#list list?chunk(num) as r>
		<#list r as item>${item[prop]}<#sep>${sign}</#list><#if r?has_next>${sign}</#if>
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

<#----------------------------以下为自定义函数------------------------------------->
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
<#--判断字段java类属性是否为数字类型-->
<#function isNum propType>
<#local num=["int","float","double","long","Integer","Float","Double","Long","BigDecimal"]/>
<#if num?seq_contains(propType)>
<#return "y"=="y">
<#else>
<#return "y"=="n">
</#if>
</#function>
<#--一组集合，按顺序判断，返回第一个不为空集合的list-->
<#function getNvlList lists...>
<#list lists as item>
<#if item?size gt 0>
<#return item>
</#if>
</#list>
<#return lists?first>
</#function>
