<#--双向关联-->
<#--与主表（只一个）的关联设置,如果是单个字段关联的，则取出,refSize为关联字段的个数;这里只做了单列关联-->
<#assign refSize=0>
<#if refColumnMap?? && (refColumnMap?size>0)>
<#assign refSize=refColumnMap?size>
<#if refSize==1>
<#list refColumnMap?keys as key>
<#assign refKey=key>
<#assign refVal=refColumnMap[key]>
</#list>
</#if>
</#if>
package ${basePackage}.${entityPackage};
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
<#--导入包-->
<#if importClassList?? && (importClassList?size>0)>
<#list importClassList as clz>
${clz};
</#list>
</#if>
/**
* ${remark!}
*/
@Entity
@Table(name = "${tableFullName}")
public class ${entityName} extends Page implements Serializable {
	private static final long serialVersionUID = 1L;
	<#if columns?? && (columns?size>0)>
	<#list columns as col>
	/**
	 * ${col.remark!}
	 */
	<#assign type=col.propertyType>
	<#if col.pk>
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	</#if>
	@Column(name="${col.columnName}")
	private ${type} ${col.propertyName};
	
	</#list>
	<#--关联表属性,主表-->
	<#if refSize==1>
	<#if refType=="OneToOne" || refType=="OneToMany">
	<#if refType=="OneToOne">
	@OneToOne
	<#elseif refType=="OneToMany">
	@ManyToOne
	</#if>
	@JoinColumn(name="${refVal}",referencedColumnName="${refKey}")
	private ${parentTable.entityName} ${parentTable.fstLowEntityName};
	<#elseif refType=="ManyToOne" || refType=="ManyToMany">
	
	<#if refType=="ManyToMany">
	@ManyToMany
	@JoinTable(name="",
	joinColumns=@JoinColumn(name="${refVal}",referencedColumnName="${refVal}"),
    inverseJoinColumns=@JoinColumn(name="${refKey}",referencedColumnName="${refKey}"))
	<#elseif refType=="ManyToOne">
	@OneToMany(mappedBy="${fstLowEntityName}")
	</#if>
	private Set<${parentTable.entityName}> ${parentTable.fstLowEntityName}Set;
	
	</#if>
	</#if>
	<#--关联表属性,从表-->
	<#if subTables?? && (subTables?size>0)>
	<#list subTables as subtb>
	<#--与从表的关联设置（循环）,如果是单个字段关联的，则取出,subrefSize为关联字段的个数;这里只做了单列关联-->
	<#assign subrefSize=0>
	<#if subtb.refColumnMap?? && (subtb.refColumnMap?size>0)>
	<#assign subrefSize=subtb.refColumnMap?size>
	<#if subrefSize==1>
	<#list subtb.refColumnMap?keys as key>
	<#assign subrefKey=key>
	<#assign subrefVal=subtb.refColumnMap[key]>
	</#list>
	</#if>
	</#if>
	
	<#if subrefSize==1>
	<#if subtb.refType=="OneToOne" || subtb.refType=="ManyToOne">
	@subtb.refType
	@JoinColumn(name="${refKey}",referencedColumnName="${refVal}")
	private ${subtb.entityName} ${subtb.fstLowEntityName};
	<#elseif subtb.refType=="OneToMany" || subtb.refType=="ManyToMany">
	
	<#if subtb.refType=="ManyToMany">
	@ManyToMany
	@JoinTable(name="",
	joinColumns=@JoinColumn(name="${subrefKey}",referencedColumnName="${subrefKey}"),
    inverseJoinColumns=@JoinColumn(name="${subrefVal}",referencedColumnName="${subrefVal}"))
	<#elseif subtb.refType=="OneToMany">
	@OneToMany(mappedBy="${fstLowEntityName}")
	</#if>
	private Set<${subtb.entityName}> ${subtb.fstLowEntityName}Set;
	</#if>
	</#if>
		
	</#list>
	</#if>
	
	<#list columns as col>
	
	public void set${col.fstUpperProName}(${col.propertyType} ${col.propertyName}){
		this.${col.propertyName}=${col.propertyName};
	}
	public ${col.propertyType} get${col.fstUpperProName}(){
		return this.${col.propertyName};
	}
	</#list>
	<#--关联表get,set,主表-->
	<#if refSize==1>
	<#if refType=="OneToOne" || refType=="OneToMany">
	
	public void set${parentTable.entityName}(${parentTable.entityName} ${parentTable.fstLowEntityName}){
		this.${parentTable.fstLowEntityName}=${parentTable.fstLowEntityName};
	}
	public ${parentTable.entityName} get${parentTable.entityName}(){
		return this.${parentTable.fstLowEntityName};
	}
	
	<#elseif refType=="ManyToOne" || refType=="ManyToMany">
	public void set${parentTable.entityName}Set(Set<${parentTable.entityName}> ${parentTable.fstLowEntityName}Set){
		this.${parentTable.fstLowEntityName}Set=${parentTable.fstLowEntityName}Set;
	}
	public Set<${parentTable.entityName}> get${parentTable.entityName}Set(){
		return this.${parentTable.fstLowEntityName}Set;
	}
	</#if>
	</#if>
	<#--关联表get,set,从表-->
	<#if subTables?? && (subTables?size>0)>
	<#list subTables as subtb>
	
	<#if subtb.refColumnMap?? && (subtb.refColumnMap?size==1)>
	<#if subtb.refType=="OneToOne" || subtb.refType=="ManyToOne">
	
	public void set${subtb.entityName}(${subtb.entityName} ${subtb.fstLowEntityName}){
		this.${subtb.fstLowEntityName}=${subtb.fstLowEntityName};
	}
	
	public ${subtb.entityName} get${subtb.entityName}(){
		return this.${subtb.fstLowEntityName};
	}
	<#elseif subtb.refType=="OneToMany" || subtb.refType=="ManyToMany">
	
	public void set${subtb.entityName}Set(List<${subtb.entityName}> ${subtb.fstLowEntityName}Set){
		this.${subtb.fstLowEntityName}Set=${subtb.fstLowEntityName}Set;
	}
	public Set<${subtb.entityName}> get${subtb.entityName}Set(){
		return this.${subtb.fstLowEntityName}Set;
	}
	</#if>
	</#if>
		
	</#list>
	</#if>
	
	</#if>

}