<#import "/lib/mf.ftl" as mf/>

package <@mf.entityPkg/>;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
<#--导入包-->
<#if impClasses?? && (impClasses?size>0)>
<#list impClasses as clz>
${clz};
</#list>
</#if>
/**
* ${remark!}
*/
@Entity
@Table(name = "${tabName}")
public class ${entName} implements Serializable {
	private static final long serialVersionUID = 1L;
	
	<#list columns as col>
	<#if col.pk>
	/**
	 * ${col.remark!}
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private ${col.propType} ${col.propName};
	<#else>
	<#--过滤外键（关联字段）-->
	<#if !mf.getFks()?seq_contains(col.colName)>
	/**
	 * ${col.remark!}
	 */
	@Column(name="${col.colName}")
	private ${col.propType} ${col.propName};
	</#if>
	
	</#if>
	</#list>
	
	<#--关联表属性配置-->
	<@mf.list refTables;reftab>
	/**
	 * ${reftab.remark!}
	 */
	<#if reftab.refType=="ManyToOne" || reftab.refType=="OneToOne">
	<#--多对一时，主表有外键，配置JoinColumn，一对一时，如果外键在主表中，也配置JoinColumn-->
	@${reftab.refType}(targetEntity=${reftab.entName}.class)
	<#if reftab.forKey??>
	@JoinColumn(name="${reftab.forKey}")
	</#if>
	private ${reftab.entName} ${reftab.lowEntName};
	<#elseif reftab.refType=="OneToMany">
	<#--一对多时，配置外键由 关联表的类中属性维护-->
	@OneToMany(targetEntity=${reftab.entName}.class,mappedBy="${lowEntName}")
	private ${reftab.entName} ${reftab.lowEntName}Set;
	<#else>
	<#--多对多时，配置中间表-->
	@ManyToMany
	@JoinTable(name="${reftab.midTabName}",
		joinColumns={@JoinColumn(<@mf.map reftab.refColMap;k,v>name="${v}",referenceColumnName="${k}"</@mf.map>)},
		inverseJoinColumns={@JoinColumn(<@mf.map reftab.midRefColMap;k,v>name="${k}",referenceColumnName="${v}"</@mf.map>)}
		)
	private ${reftab.entName} ${reftab.lowEntName}Set;
	</#if>
	</@mf.list>
	
	
	<#list columns as col>
	<#if !mf.getFks()?seq_contains(col.colName)>
	public void set${col.propName?cap_first}(${col.propType} ${col.propName}){
		this.${col.propName}=${col.propName};
	}
	public ${col.propType} get${col.propName?cap_first}(){
		return this.${col.propName};
	}
	
	</#if>
	</#list>
	
	<#--关联表属性get,set,-->
	<@mf.list refTables;reftab>
	<#if reftab.refType=="OneToOne" || reftab.refType=="ManyToOne">
	
	public void set${reftab.entName}(${reftab.entName} ${reftab.lowEntName}){
		this.${reftab.lowEntName}=${reftab.lowEntName};
	}
	public ${reftab.entName} get${reftab.entName}(){
		return this.${reftab.lowEntName};
	}
	
	<#elseif reftab.refType=="OneToMany" || reftab.refType=="ManyToMany">
	public void set${reftab.entName}Set(List<${reftab.entName}> ${reftab.lowEntName}Set){
		this.${reftab.lowEntName}Set=${reftab.lowEntName}Set;
	}
	public Set<${reftab.entName}> get${reftab.entName}Set(){
		return this.${reftab.lowEntName}Set;
	}
	
	</#if>
	</@mf.list>

	
}