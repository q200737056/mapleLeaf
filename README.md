# mapleLeaf
#### mapleLeaf程序员工具现包含代码  _生成工具和扒取工具_  两大模块

#### 主要特点：

1. 项目运行不依赖数据库，简单方便。
2. 强大的代码生成功能，可自由编辑新增模板，生成自己想要的代码。支持主流数据库如：mysql，Oracle等；支持数据库表  _一对一，一对多，多
对多_ ；支持mybatis，hibernate等持久层生成。
3. 模板编辑使用freemarker，具体使用请看项目中的页面有使用文档，也可参考项目中的示例。简单编辑好配置文件，就可方便生成代码。总的来说就是（模板+配置文件）。
4. 扒取工具现有扒取网页，支持有用户登录的页面扒取，通过设置cookie（可用浏览器工具查看cookie）。可用作自己项目的模板，也可以静态演示。

#### 模板代码片段：

##### 获取字段信息
```
<#if columns?? && (columns?size>0)>
  <#list columns as col>
     ${col.propertyName}  //实体类属性名
     ${col.propertyType}  //实体类属性类型
     ${col.columnName}  //表字段名
     ${col.pk}  //是否主键
     ...
  </#list>
</#if>

```
##### 获取从表信息（主表从表关系请看 配置文件说明）
```
<#if subTables?? && (subTables?size>0)>
  <#list subTables as subtb>
    ${subtb.entityName}  //从表实体类名
    ${subtb.refType}  //主从表关联关系 如OneToOne，OneToMany等
    ${subtb.fstLowEntityName} //从表实体类名首字母小写，可用作变量名
    ...	    
  </#list>
</#if>

```
##### 获取唯一索引（组合索引）或主键（组合主键）
```
<#if uniIdxMap?? && (uniIdxMap?size>0)>
  <#assign keys=uniIdxMap?keys />
  <#list keys as key>
     //循环获取索引字段信息,key为索引或主键名，value为字段信息
     <#list uniIdxMap[key] as idxCol> 
       ${idxCol.columnName}  //表字段名
       ${idxCol.pk}  //是否主键
       ...
     </#list>
  </#list>
</#if>

```

#### 整个项目：

![输入图片说明](http://i2.bvimg.com/686411/a823965ebd1981aa.gif "在这里输入图片标题")

#### 扒取登陆用户页面时，通过浏览器工具获取cookies，把HTTP打钩的name,value填进cookie文本域就行：

![输入图片说明](https://gitee.com/uploads/images/2019/0506/193643_baffe7d2_1135865.gif "在这里输入图片标题")

