# mapleLeaf
#### mapleLeaf程序员工具现包含 代码生成工具和扒取工具  两大模块


#### 一、本项目主要特点：

1. 项目运行不依赖数据库，简单方便。
2. 代码生成功能。
3. 扒取工具现有扒取网页，支持有用户登录的页面扒取，通过设置cookie（可用浏览器工具查看cookie）。可用作自己项目的模板，也可以静态演示。

#### 二、代码生成功能有哪些优势
>1. 主持多种主流数据库：mysql，oracle，sqlserver，postgresql等；还能很容易的扩展需要支持的数据库。
>2. 支持多种类型的持久层：mybatis，hibernate等other持久层。
>3. 支持表之间的复杂关联关系，一对一，一对多，多对一，多对多，单双向的关联。
>4. 支持模块化生成代码，各模块之间相互独立，互不影响。
>5. 支持模板文件内容自定义，可自由编辑新增模板，生成自己业务需要的代码。模板使用了freemarker。
>6. 对视图中的代码生成，很友好；比如：字段表单类型分类，字段所在页面位置标识等。
>7. 不仅支持默认的java项目的dao,service,controller三层架构，而且支持强大的自定义，只要使用模板中现有的数据编辑模板，可以生成其他编程语言的文件。比如：页面jsp，js等等。还可以自定义参数，比如定义了参数 author作者，date日期等。
>8. 操作简单，只需要配置config.xml配置文件。

#### 三、项目部署及代码生成演示：
1. 如果用maven打成jar包运行时，需要在jar包所在目录下加入config.xml配置文件与template模板文件。（推荐）
![](http://i2.tiimg.com/686411/4dd3530c40c4b0d3.png "")
2. 用maven打成war包运行，则不需要这些，直接启动服务即可。

以mysql数据库为例，分别生成mybatis,hibernate。

##### 表结构

```
CREATE TABLE
    sys_user
    (
        user_id INT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
        dept_id INT COMMENT '部门ID',
        user_info_id INT COMMENT '用户个人信息ID',
        login_name VARCHAR(30) NOT NULL COMMENT '登录账号',
        password VARCHAR(50) COMMENT '密码',
        user_name VARCHAR(30) NOT NULL COMMENT '用户昵称',
        email VARCHAR(50) COMMENT '用户邮箱',
        phonenumber VARCHAR(11) COMMENT '手机号码',
        sex CHAR(1) DEFAULT '0' COMMENT '用户性别；radio；0：男,1：女,2：未知',
        status CHAR(1) DEFAULT '0' COMMENT '账号状态；select；0：正常 ，1：停用',
        remark VARCHAR(500) COMMENT '备注',
        PRIMARY KEY (user_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
CREATE TABLE
    sys_dept
    (
        dept_id INT NOT NULL AUTO_INCREMENT COMMENT '部门id',
        parent_id INT DEFAULT '0' COMMENT '父部门id',
        dept_name VARCHAR(30) COMMENT '部门名称',
        leader VARCHAR(20) COMMENT '负责人',
        phone VARCHAR(11) COMMENT '联系电话',
        status CHAR(1) DEFAULT '0' COMMENT '部门状态；select；0：正常 ，1：停用',
        PRIMARY KEY (dept_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='部门表';
CREATE TABLE
    sys_user_info
    (
        user_info_id INT NOT NULL AUTO_INCREMENT COMMENT '用户个人信息ID',
        real_name VARCHAR(30) NOT NULL COMMENT '真实姓名',
        address VARCHAR(100) COMMENT '住址',
        height DOUBLE COMMENT '身高',
        birthday TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON
    UPDATE
        CURRENT_TIMESTAMP COMMENT '出生日期',
        PRIMARY KEY (user_info_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户个人信息表';
CREATE TABLE
    sys_role
    (
        role_id INT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
        role_name VARCHAR(30) NOT NULL COMMENT '角色名称',
        status CHAR(1) NOT NULL COMMENT '角色状态；radio；0：正常 ，1：停用',
        remark VARCHAR(500) COMMENT '备注',
        PRIMARY KEY (role_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色信息表';
CREATE TABLE
    sys_user_role
    (
        user_id INT NOT NULL COMMENT '用户ID',
        role_id INT NOT NULL COMMENT '角色ID',
        PRIMARY KEY (user_id, role_id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户和角色关联表';
```
+ 用户表 1-->1 用户个人信息表；用户表 n-->1 部门表；
+ 用户表 n-->n 角色表，用户和角色关联表为中间表；
+ COMMET约定格式（不区分中英逗号，分号，推荐中文；自动去除空格）： 字段文本；表单类型；val1:text1,val2:text2
+ 程序会自动加载,这些属性。
+ 表单类型现支持text,textarea,date,select,checkbox,radio,hidden,file。

##### config.xml配置

```
<?xml version="1.0" encoding="UTF-8"?>
<configure>
	<global>
		<!--生成代码路径 ,默认项目所在目录-->
		<property name="baseDir" value="f:\\code" />
		<!--基础包名，默认mapleLeaf.code-->
		<property name="basePackage" value="com.test" />
		<!-- 实体类名是否去掉表名前缀 ,默认false-->
		<property name="deleteTabPrefix" value="true" />
		<!-- 全局表名前缀，多个逗号分隔 ,默认""-->
		<property name="baseTabPrefix" value="sys_" />
		<!-- 表字段是否下划线转驼峰命名 ，默认false-->
		<property name="columnCamel" value="true" />
		<!-- 持久层框架(mybatis,hibernate,other)，默认"mybatis" -->
		<property name="persistence" value="hibernate" />
		<!-- 一些公共类，工具类,父类等原样输出，不必需(包名=模板文件名(也是类名)，多个逗号隔开) -->
		<!-- 这里配置了分页类，mybatis分页拦截器, mybatis通用配置-->
		<!--  
		<common>common=Page,common=PageInterceptor,resource=Xml_mybatis</common>
		-->
	</global>

	<!-- 数据库配置 -->
	<db>
		<!-- 数据库类型(mysql,oracle,sqlserver,postgresql,informix) -->
		<dbType>mysql</dbType>
		<!-- 数据库名 -->
		<dbName>demo2</dbName>
		<user>root</user>
		<pwd>q123</pwd>
		<driver>com.mysql.jdbc.Driver</driver>
		<url><![CDATA[jdbc:mysql://localhost:3306/demo2?useUnicode=true&characterEncoding=UTF-8]]></url>
	</db>

	<!-- 模块化配置，可以配置多个module,
			各模块相互独立,name属性为模块名 -->
	<module name="test">
		<!-- 
			package标签 主要支持大多数  dao,service,controller 三层结构
			包名配置，如果为空或没配置，则该包对应的类不会生成代码 
		-->
		<package>
			<property name="entityPkg" value="entity"/>
			<property name="daoPkg" value="dao"/>
			<property name="servicePkg" value="service"/>
			<property name="controllerPkg" value="controller"/>
			<property name="mapperPkg" value="mapper"/>
		</package>
		<!--
			customArea自定义区域 ，只要提供模板，任意生成代码，
			可以说 不仅仅生成java语言
		-->
		<customArea>
			<!-- 自定义生成，可以配置多组标签，不限于java语言，属性customPkg包名，属性suffix 文件后缀;
				标签内容为模板文件(不需要后缀)，多个逗号分隔-->
			<codeFile customPkg="page" suffix="jsp">Jsp_list,Jsp_add，Jsp_view</codeFile>
			<!-- 自定义参数设置 ，模板中可以通过${param['author']!}获取参数值 -->
			<param key="author" value="mapleLeaf"></param>
		</customArea>
		
		<!-- table配置表，属性tabName表名，
			  exclude排除指定模板代码生成，多个逗号分隔（dao,service,controller,entity,custom）
			，prefix表前缀，有全局配置，感觉没什么用了-->
		<table tabName="sys_user" prefix="">
			<!-- 
			ref标签（单向）关联表，如果需要双向关联  两个table都需配置对应对方的ref
			ref所对应的 tabName，必须配置table
			ref标签内容为 主表字段=关联表字段，默认可以多组，逗号分隔（推荐单组字段）（多对多时，则为 主表字段=中间表字段）
			多对多时：需配置midTabName和midRefCol属性
			midTabName中间表 不用配置table;midRefCol属性为（中间表字段=关联表字段）
			多对一时：需配置forKey 主表的关联字段（即外键）
			一对一时：关联字段（即外键）是否在 主表中，如果是，则配置forKey
			-->
			<ref tabName="sys_role" type="ManyToMany" midTabName="sys_user_role" midRefCol="role_id=role_id">
				user_id=user_id
			</ref>
			<ref tabName="sys_dept" type="ManyToOne" forKey="dept_id">dept_id=dept_id</ref>
			<ref tabName="sys_user_info" type="OneToOne" forKey="user_info_id">
			user_info_id=user_info_id
			</ref>
			<!-- columnGroup字段集合标签，对字段属性设置
				exclude：排除字段，多个字段逗号分隔，以下属性一样。
				searchPos：页面查询条件；listPos：页面查询结果列表；inputPos：页面表单输入
			 -->
			<columnGroup searchPos="login_name,status" 
				inputPos="user_id,login_name,email,phonenumber,sex,status">
				<!-- 
					column字段标签，可自定义字段属性。
					colName：字段名；tagType：表单类型，默认text；labelName：字段文本
					column标签内容：字段值
				 -->
				<column colName="sex" tagType="radio" labelName="性别">
					0=男，1=女</column>
				<column colName="remark" tagType="textarea" />
			</columnGroup>
		</table>
		
		<table tabName="sys_role">
			<ref tabName="sys_user" type="ManyToMany" midTabName="sys_user_role" midRefCol="user_id=user_id">
				role_id=role_id
			</ref>
		</table>
		
		<table tabName="sys_dept" exclude="custom">
			<ref tabName="sys_user" type="OneToMany">dept_id=dept_id</ref>
		</table>
		
		<table tabName="sys_user_info"></table>
	</module>
</configure>
```

##### 生成的部分代码展示

1. hibernate生成的实体类

![User类部分代码](http://i1.fuimg.com/686411/1bcb83b9b1780fba.png "User类部分代码")
![Role类部分代码](http://i1.fuimg.com/686411/33c87cdee59ed294.png "Role类部分代码")

2. mybatis生成的xml配置(配置文件设置成 实体类名不去掉表前缀)

![](http://i1.fuimg.com/686411/451da44f46910397.png)
![](http://i2.tiimg.com/686411/6d976b33393fd208.png)

3. 页面生成的代码（简单处理了）
 
![表单输入](http://i1.fuimg.com/686411/bc05e85ca6050a62.png "表单输入") 
![查询出来的列表项](http://i1.fuimg.com/686411/8f25be0a789aac43.png "查询出来的列表项")

#### 四、模板代码片段：

（有自定义宏标签和函数，temlate/lib/mf.ftl）

##### 获取表字段信息
```
<#list columns as col>
	 ${col.propName}  //实体类属性名
	 ${col.propType}  //实体类属性java类型
	 ${col.colName}  //表字段名
	 ${col.pk}  //是否主键
     ...
</#list>
//也可以使用 temlate/lib/mf.ftl中的自定义标签<@list>，对null进行了初始化
<@mf.list columns;col>
	${col.propName}   //实体类属性名
</@mf.list>
```
##### 获取所有关联表信息
```
<@mf.list refTables;reftb>
    ${reftb.entName}  //实体类
    ${reftb.refType}  //关联关系 如OneToOne，OneToMany等
    ${reftb.lowEntName} //实体类名首字母小写，可用作变量名
    ...	    
</@mf.list>

```
##### 获取唯一索引（组合索引）或主键（组合主键）
```
// 如果表存在主键（组合主键），则uniIdxCols存主键字段
// 如果没有主键，查询唯一索引，取其中一组
// 如果主键，唯一索引都没有，则为空集合
<@mf.list uniIdxCols;col>
    ${col.colName}  //表字段名
    ${col.propName}  //字段对应属性
       ...
</@mf.list>

```

##### 整个项目：

![](http://i2.tiimg.com/686411/69d23332dbb10510.gif "")

##### 扒取登陆用户页面时，通过浏览器工具获取cookies，把HTTP打钩的name,value填进cookie文本域就行：

![](https://gitee.com/uploads/images/2019/0506/193643_baffe7d2_1135865.gif "在这里输入图片标题")

#### 五、留言：
[freemarker模板学习]("http://freemarker.foofun.cn/index.html")
v2.0.1已发布，如果大家觉得有点用处，请给个星 :star: ，谢谢大家。

