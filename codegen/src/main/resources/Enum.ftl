package ${modulePackage}.enums;

public enum ${fieldDefinition.enumClassName} {
    <#list fieldDefinition.enumValues as enumVlaue>${enumVlaue}<#sep>,</#list>
}
