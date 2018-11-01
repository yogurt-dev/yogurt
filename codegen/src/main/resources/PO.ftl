package ${modulePackage}.po;

import com.github.yogurt.core.po.BasePO;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.persistence.Column;
import lombok.EqualsAndHashCode;

<#list fields as field>
    <#if field.columnType == "enum">
import  ${field.classFullName};
    </#if>
</#list>

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class ${className}PO extends BasePO<${className}PO>{
<#list fields as field>
    <#if field.columnName !="id">
    /**
    *  ${field.comment}
    */
    @Column(name = "${field.columnName}")
	private ${field.className} ${field.codeName};
    </#if>
</#list>
}
