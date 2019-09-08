package ${basePackage}.${entityPackage};

import java.io.Serializable;
import java.math.BigDecimal;

/**
* ${remark!}
*/
<#if persistance=="hibernate" || persistance=="jpa">
@Entity
@Table(name="${tableFullName!}")
</#if>

public class ${entityName!} {

	private static final long serialVersionUID = 1L;
	
	<#if columns??>
	<#list columns as col>
	/**
	 * ${col.remark!}
	 */
	<#assign type=col.propertyType>
	<#assign type=type?replace("java.util.","")>
	<#assign type=type?replace("java.math.","")>
	<#assign defaultValue=col.defaultValue!>
	<#if defaultValue?length gt 0>
		<#if type=="Date">
			<#assign defaultValue="new Date()">
		<#elseif type=="Long">
			<#assign defaultValue=defaultValue+"L">
		<#elseif type=="Double">
			<#assign defaultValue=defaultValue+"d">
		</#if>
	</#if>
	<#if col.nullable==false && (persistance=="hibernate" || persistance=="jpa")>
		<#if type=="String">
	@NotEmpty(message="${col.remark!}不能为空")
		<#else>
	@NotNull(message="${col.remark!}不能为空")	
		</#if>
	</#if>
		<#if "DATETIME"==type ||"TIMESTAMP"==type ||"Date"==type>
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8" )
	</#if>
	private ${type!} ${col.propertyName}${(defaultValue?length>0)?string("="+defaultValue,"")};
	</#list>
	<#-- 生成关联表属性 -->
	<#if refTables??>
		<#list refTables as sub>
		<#if sub.refType=="OneToOne">
			<#if "DATETIME"==type ||"TIMESTAMP"==type ||"Date"==type>
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8" )
			</#if>
	private ${sub.entityName} ${sub.fstLowEntityName?uncap_first};
		<#else>
			<#if "DATETIME"==type ||"TIMESTAMP"==type ||"Date"==type>
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8" )
			</#if>
	private List<${sub.entityName}> ${sub.fstLowEntityName?uncap_first}List;
		</#if>
		</#list>
	</#if>
	
	<#list columns as col>
	<#assign type=col.propertyType>
	<#assign type=type?replace("java.util.","")>
	<#assign type=type?replace("java.math.","")>
	public void set${col.fstUpperProName}(${type} ${col.propertyName}){
		this.${col.propertyName}=${col.propertyName};
	}
	<#if persistance=="hibernate" || persistance=="jpa">
	<#if col.pk>
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	</#if>
	@Column(name="${col.columnName}",columnDefinition="${col.columnType}")
	</#if>
	public ${type} get${col.fstUpperProName}(){
		return this.${col.propertyName};
	}
	
	</#list>
	<#-- 生成关联表属性 -->
	<#if refTables??>
		<#list refTables as sub>
		<#if sub.refType=="OneToOne"><#-- 一对一 -->
	public void set${sub.entityName}(${sub.entityName} ${sub.fstLowEntityName?uncap_first}){
		this.${sub.fstLowEntityName?uncap_first}=${sub.fstLowEntityName?uncap_first};
	}
	<#if persistance=="hibernate" || persistance=="jpa">
	@Transient
	</#if>
	public ${sub.entityName} get${sub.entityName}(){
		return this.${sub.fstLowEntityName?uncap_first};
	}	
		<#else><#-- 一对多 -->
	public void set${sub.entityName}List(List<${sub.entityName}> ${sub.fstLowEntityName?uncap_first}List){
		this.${sub.fstLowEntityName?uncap_first}List=${sub.fstLowEntityName?uncap_first}List;
	}
	<#if persistance=="hibernate" || persistance=="jpa">
	@Transient
	</#if>
	public List<${sub.entityName}> get${sub.entityName}List(){
		return this.${sub.fstLowEntityName?uncap_first}List;
	}
		</#if>
		</#list>
	</#if>
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("${entityName!}[");
		<#list columns as col>
		sb.append("${(col_index gt 0)?string(",","")}${col.propertyName}=");
		sb.append(${col.propertyName});
		</#list>
		sb.append("]");
		return sb.toString();
	}
	</#if>

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}
