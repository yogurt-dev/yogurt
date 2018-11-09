package ${modulePackage}.enums;
/**
 * @author ${userName}
 */
public enum ${fieldDefinition.enumClassName} {
    <#list fieldDefinition.enumValues as enumValue>
    <#if enumValue.annotation??>
    /**
     * ${enumValue.annotation}
     */
    </#if>
     ${enumValue.name},
    </#list>
}
