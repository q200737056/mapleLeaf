<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
	
	<settings>
		<setting name="cacheEnabled"             value="true" />  <!-- 全局映射器启用缓存 -->
		<setting name="useGeneratedKeys"         value="true" />  <!-- 允许 JDBC 支持自动生成主键 -->
		<setting name="defaultExecutorType"      value="REUSE" /> <!-- 配置默认的执行器 -->
		<setting name="logImpl"                  value="SLF4J" /> <!-- 指定 MyBatis 所用日志的具体实现 -->
		<setting name="mapUnderscoreToCamelCase" value="false"/> <!-- 驼峰式命名 -->
	</settings>
	<!-- mybatis拦截器，实现分页 -->
    <bean id="pageInterceptor" class="com.test.common.PageInterceptor">
        <property name="pattern" value="^.*Page$"></property>
        <property name="dialect" value="mysql"></property>
    </bean>
</configuration>
