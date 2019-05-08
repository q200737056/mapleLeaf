package ${basePackage}.${entityPackage};
import java.io.Serializable;
<#--导入包-->
<#if importClassList?? && (importClassList?size>0)>
<#list importClassList as clz>
${clz};
</#list>
</#if>
/**
* ${remark!}
*/

public class ${entityName} extends Page implements Serializable {
	private static final long serialVersionUID = 1L;
	<#if columns?? && (columns?size>0)>
	<#list columns as col>
	/**
	 * ${col.remark!}
	 */
	<#assign type=col.propertyType>
	
	private ${type} ${col.propertyName};
	</#list>
	<#--关联表属性，是否有主表-->
	<#if parentTable??>
	<#if refType=="OneToOne" || refType=="OneToMany">
	private ${parentTable.entityName} ${parentTable.fstLowEntityName};
	<#elseif refType=="ManyToOne" || refType=="ManyToMany">
	private List<${parentTable.entityName}> ${parentTable.fstLowEntityName}List;
	</#if>
	</#if>
	<#--关联表属性，是否有从表-->
	<#if subTables?? && (subTables?size>0)>
	<#list subTables as subtb>
	<#if subtb.refType=="OneToOne" || subtb.refType=="ManyToOne">
	private ${subtb.entityName} ${subtb.fstLowEntityName};
	<#elseif subtb.refType=="OneToMany" || subtb.refType=="ManyToMany">
	private List<${subtb.entityName}> ${subtb.fstLowEntityName}List;
	</#if>	
	</#list>
	</#if>
	
	<#list columns as col>
	<#assign type=col.propertyType>
	public void set${col.fstUpperProName}(${type} ${col.propertyName}){
		this.${col.propertyName}=${col.propertyName};
	}
	public ${type} get${col.fstUpperProName}(){
		return this.${col.propertyName};
	}
	</#list>
	<#--关联表get,set，是否有主表-->
	<#if parentTable??>
	<#if refType=="OneToOne" || refType=="OneToMany">
	public void set${parentTable.entityName}(${parentTable.entityName} ${parentTable.fstLowEntityName}){
		this.${parentTable.fstLowEntityName}=${parentTable.fstLowEntityName};
	}
	public ${parentTable.entityName} get${parentTable.entityName}(){
		return this.${parentTable.fstLowEntityName};
	}
	<#elseif refType=="ManyToOne" || refType=="ManyToMany">
	public void set${parentTable.entityName}List(List<${parentTable.entityName}> ${parentTable.fstLowEntityName}List){
		this.${parentTable.fstLowEntityName}List=${parentTable.fstLowEntityName}List;
	}
	public List<${parentTable.entityName}> get${parentTable.entityName}List(){
		return this.${parentTable.fstLowEntityName}List;
	}
	</#if>
	</#if>
	<#--关联表get,set，是否有从表-->
	<#if subTables?? && (subTables?size>0)>
	<#list subTables as subtb>
	<#if subtb.refType=="OneToOne" || subtb.refType=="ManyToOne">
	public void set${subtb.entityName}(${subtb.entityName} ${subtb.fstLowEntityName}){
		this.${subtb.fstLowEntityName}=${subtb.fstLowEntityName};
	}
	public ${subtb.entityName} get${subtb.entityName}(){
		return this.${subtb.fstLowEntityName};
	}
	<#elseif subtb.refType=="OneToMany" || subtb.refType=="ManyToMany">
	public void set${subtb.entityName}List(List<${subtb.entityName}> ${subtb.fstLowEntityName}List){
		this.${subtb.fstLowEntityName}List=${subtb.fstLowEntityName}List;
	}
	public List<${subtb.entityName}> get${subtb.entityName}List(){
		return this.${subtb.fstLowEntityName}List;
	}
	</#if>	
	</#list>
	</#if>
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("${entityName}[");
		<#list columns as col>
		sb.append("${(col_index gt 0)?string(",","")}${col.propertyName}=");
		sb.append(${col.propertyName});
		</#list>
		sb.append("]");
		return sb.toString();
	}
	</#if>

}
