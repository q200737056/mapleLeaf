# mapleLeaf
#### mapleLeaf程序员工具现包含 _代码生成工具和扒取工具_  两大模块

#### 一、主要特点：

1. 项目运行不依赖数据库，简单方便。
2. 强大的代码生成功能，可自由编辑新增模板，生成自己想要的代码。支持主流数据库如：mysql，Oracle等；支持数据库表  _一对一，一对多，多
对多_ ；支持mybatis，hibernate等持久层生成。
3. 模板编辑使用freemarker，具体使用请看项目中的页面有使用文档，也可参考项目中的示例。简单编辑好配置文件，就可方便生成代码。总的来说就是（模板+配置文件）。
4. 扒取工具现有扒取网页，支持有用户登录的页面扒取，通过设置cookie（可用浏览器工具查看cookie）。可用作自己项目的模板，也可以静态演示。

#### 二、项目部署：
1. 如果用maven打成jar包运行时，需要在jar包所在目录下加入config.xml配置文件与template模板文件。
![](http://i1.fuimg.com/686411/444adfe9d444d9af.png "")
2. 用maven打成war包运行，则不需要这些，直接启动服务就行。

#### 三、模板代码片段：

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
##### 获取关联表信息
```
<#if refTables?? && (refTables?size>0)>
  <#list refTables as reftb>
    ${reftb.entityName}  //实体类名
    ${reftb.refType}  //关联关系 如OneToOne，OneToMany等
    ${reftb.fstLowEntityName} //实体类名首字母小写，可用作变量名
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

##### 整个项目：

![](http://i2.tiimg.com/686411/69d23332dbb10510.gif "")

##### 扒取登陆用户页面时，通过浏览器工具获取cookies，把HTTP打钩的name,value填进cookie文本域就行：

![](https://gitee.com/uploads/images/2019/0506/193643_baffe7d2_1135865.gif "在这里输入图片标题")

#### 四、留言：
v1.0.1已发布，如果大家觉得有点用处，请给个星 :star: ，谢谢大家。

