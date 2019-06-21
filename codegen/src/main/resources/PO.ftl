package ${modulePackage}.po;

import com.github.yogurt.core.po.BasePO;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.persistence.Column;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

<#list fields as field>
    <#if field.columnType == "enum">
import  ${field.classFullName};
    </#if>
</#list>

/**
 * @author ${userName}
 */
@Entity
@Table(name = "${table}")
@DynamicInsert
@DynamicUpdate

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class ${className}PO extends BasePO<${className}PO>{
<#list fields as field>
    <#if field.columnName !="id">
    /**
     *  ${field.comment}
     */
    <#if field.className ?index_of("Enum")!=-1>@Enumerated(EnumType.STRING)</#if>
    @Column(name = "${field.columnName}" <#if !field.nullable>,nullable = false </#if>  )
	private ${field.className} ${field.codeName};
    </#if>
</#list>
}
