<#--双向关联-->

package ${basePackage}.${entityPackage};
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
public class ${entName} extends Page implements Serializable {
	private static final long serialVersionUID = 1L;
	<#if columns?? && (columns?size>0)>
	<#list columns as col>
	/**
	 * ${col.remark!}
	 */
	<#assign type=col.propType>
	<#if col.pk>
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	</#if>
	@Column(name="${col.colName}")
	private ${type} ${col.propName};
	
	</#list>
	
	<#--关联表属性,从表-->
	<#if refTables?? && (refTables?size>0)>
	<#list refTables as subtb>
	<#--与从表的关联设置（循环）,如果是单个字段关联的，则取出,subrefSize为关联字段的个数;这里只做了单列关联-->
	<#assign subrefSize=0>
	<#if subtb.refColMap?? && (subtb.refColMap?size>0)>
	<#assign subrefSize=subtb.refColMap?size>
	<#if subrefSize==1>
	<#list subtb.refColMap?keys as key>
	<#assign subrefKey=key>
	<#assign subrefVal=subtb.refColMap[key]>
	</#list>
	</#if>
	</#if>
	
	<#if subrefSize==1>
	<#if subtb.refType=="OneToOne" || subtb.refType=="ManyToOne">
	@${subtb.refType}
	@JoinColumn(name="${subrefKey}",referencedColumnName="${subrefVal}")
	private ${subtb.entName} ${subtb.lowEntName};
	<#elseif subtb.refType=="OneToMany" || subtb.refType=="ManyToMany">
	
	<#if subtb.refType=="ManyToMany">
	@ManyToMany
	@JoinTable(name="",
	joinColumns=@JoinColumn(name="${subrefKey}",referencedColumnName="${subrefKey}"),
    inverseJoinColumns=@JoinColumn(name="${subrefVal}",referencedColumnName="${subrefVal}"))
	<#elseif subtb.refType=="OneToMany">
	@OneToMany(mappedBy="${lowEntName}")
	</#if>
	private Set<${subtb.entName}> ${subtb.lowEntName}Set;
	</#if>
	</#if>
		
	</#list>
	</#if>
	
	<#list columns as col>
	
	public void set${col.upperPropName}(${col.propType} ${col.propName}){
		this.${col.propName}=${col.propName};
	}
	public ${col.propType} get${col.upperPropName}(){
		return this.${col.propName};
	}
	</#list>
	
	<#--关联表get,set,-->
	<#if refTables?? && (refTables?size>0)>
	<#list refTables as subtb>
	
	<#if subtb.refColMap?? && (subtb.refColMap?size==1)>
	<#if subtb.refType=="OneToOne" || subtb.refType=="ManyToOne">
	
	public void set${subtb.entName}(${subtb.entName} ${subtb.lowEntName}){
		this.${subtb.lowEntName}=${subtb.lowEntName};
	}
	
	public ${subtb.entName} get${subtb.entName}(){
		return this.${subtb.lowEntName};
	}
	<#elseif subtb.refType=="OneToMany" || subtb.refType=="ManyToMany">
	
	public void set${subtb.entName}Set(List<${subtb.entName}> ${subtb.lowEntName}Set){
		this.${subtb.lowEntName}Set=${subtb.lowEntName}Set;
	}
	public Set<${subtb.entName}> get${subtb.entName}Set(){
		return this.${subtb.lowEntName}Set;
	}
	</#if>
	</#if>
		
	</#list>
	</#if>
	
	</#if>

}