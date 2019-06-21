package ${modulePackage}.enums;
/**
 * @author ${userName}
 */
public enum ${fieldDefinition.enumClassName} {
    <#list fieldDefinition.enumValues as enumValue>
    <#--<#if enumValue.annotation??>-->
    <#--/**-->
     <#--* ${enumValue.annotation}-->
     <#--*/-->
    <#--</#if>-->
     ${enumValue.name}("${enumValue.annotation}")<#if enumValue_has_next>,</#if>
    </#list>;

	private String content;

${fieldDefinition.enumClassName}(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
