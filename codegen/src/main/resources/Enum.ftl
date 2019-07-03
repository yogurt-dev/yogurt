package ${modulePackage}.enums;
/**
 * @author ${userName}
 */
public enum ${fieldDefinition.enumClassName} {
    <#list fieldDefinition.enumValues as enumValue>${enumValue.name}("${enumValue.annotation}")<#if enumValue_has_next>,</#if>
    </#list>;
    <#--<#if enumValue.annotation??>-->
    <#--/**-->
     <#--* ${enumValue.annotation}-->
     <#--*/-->
    <#--</#if>-->

	private String content;

	${fieldDefinition.enumClassName}(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
