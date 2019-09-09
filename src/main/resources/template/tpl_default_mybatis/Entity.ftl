package ${basePackage}.${entityPackage};
import java.io.Serializable;
<#--导入包-->
<#if impClasses?? && (impClasses?size>0)>
<#list impClasses as clz>
${clz};
</#list>
</#if>
/**
* ${remark!}
*/

public class ${entName} extends Page implements Serializable {
	private static final long serialVersionUID = 1L;
	<#if columns?? && (columns?size>0)>
	<#list columns as col>
	/**
	 * ${col.labelName}
	 */
	<#assign type=col.propType>
	
	private ${type} ${col.propName};
	</#list>
	
	<#--关联表属性-->
	<#if refTables?? && (refTables?size>0)>
	<#list refTables as subtb>
	<#if subtb.refType=="OneToOne" || subtb.refType=="ManyToOne">
	private ${subtb.entName} ${subtb.lowEntName};
	<#elseif subtb.refType=="OneToMany" || subtb.refType=="ManyToMany">
	private List<${subtb.entName}> ${subtb.lowEntName}List;
	</#if>	
	</#list>
	</#if>
	
	<#list columns as col>
	<#assign type=col.propType>
	public void set${col.upperPropName}(${type} ${col.propName}){
		this.${col.propName}=${col.propName};
	}
	public ${type} get${col.upperPropName}(){
		return this.${col.propName};
	}
	</#list>
	
	<#--关联表get,set-->
	<#if refTables?? && (refTables?size>0)>
	<#list refTables as subtb>
	<#if subtb.refType=="OneToOne" || subtb.refType=="ManyToOne">
	public void set${subtb.entName}(${subtb.entName} ${subtb.lowEntName}){
		this.${subtb.lowEntName}=${subtb.lowEntName};
	}
	public ${subtb.entName} get${subtb.entName}(){
		return this.${subtb.lowEntName};
	}
	<#elseif subtb.refType=="OneToMany" || subtb.refType=="ManyToMany">
	public void set${subtb.entName}List(List<${subtb.entName}> ${subtb.lowEntName}List){
		this.${subtb.lowEntName}List=${subtb.lowEntName}List;
	}
	public List<${subtb.entName}> get${subtb.entName}List(){
		return this.${subtb.lowEntName}List;
	}
	</#if>	
	</#list>
	</#if>
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("${entName}[");
		<#list columns as col>
		sb.append("${(col_index gt 0)?string(",","")}${col.propName}=");
		sb.append(${col.propName});
		</#list>
		sb.append("]");
		return sb.toString();
	}
	</#if>

}
