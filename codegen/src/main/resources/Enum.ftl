package ${modulePackage}.enums;
/**
 * @author ${userName}
 */
public enum ${fieldDefinition.enumClassName} {
    <#list fieldDefinition.enumValues as enumVlaue>${enumVlaue}<#sep>,</#list>
}
