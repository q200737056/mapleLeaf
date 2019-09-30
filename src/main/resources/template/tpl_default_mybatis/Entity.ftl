<#import "/lib/mf.ftl" as mf/>
package <@mf.entityPkg/>;
import java.io.Serializable;
<#--导入包-->
<@mf.list impClasses;clz>
${clz};
</@mf.list>
/**
* ${remark!}
*/

public class ${entName} implements Serializable {
	private static final long serialVersionUID = 1L;
	<#if columns?? && (columns?size>0)>
	
	<@mf.list columns;col>
	/**
	 * ${col.labelName}
	 */
	private ${col.propType} ${col.propName};
	</@mf.list>
	
	<#--关联表属性-->
	<@mf.list refTables;refTab>
	/**
	 * ${refTab.remark!}
	 */
	<#if refTab.refType=="OneToOne" || refTab.refType=="ManyToOne">
	private ${refTab.entName} ${refTab.lowEntName};
	<#elseif refTab.refType=="OneToMany" || refTab.refType=="ManyToMany">
	private List<${refTab.entName}> ${refTab.lowEntName}List;
	</#if>
	</@mf.list>
	
	<@mf.list columns;col>
	public void set${col.upperPropName}(${col.propType} ${col.propName}){
		this.${col.propName}=${col.propName};
	}
	public ${col.propType} get${col.upperPropName}(){
		return this.${col.propName};
	}	
	</@mf.list>
	
	<#--关联表get,set-->
	<@mf.list refTables;refTab>
	<#if refTab.refType=="OneToOne" || refTab.refType=="ManyToOne">
	public void set${refTab.entName}(${refTab.entName} ${refTab.lowEntName}){
		this.${refTab.lowEntName}=${refTab.lowEntName};
	}
	public ${refTab.entName} get${refTab.entName}(){
		return this.${refTab.lowEntName};
	}
	<#elseif refTab.refType=="OneToMany" || refTab.refType=="ManyToMany">
	public void set${refTab.entName}List(List<${refTab.entName}> ${refTab.lowEntName}List){
		this.${refTab.lowEntName}List=${refTab.lowEntName}List;
	}
	public List<${refTab.entName}> get${refTab.entName}List(){
		return this.${refTab.lowEntName}List;
	}
	</#if>
	</@mf.list>
	
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
