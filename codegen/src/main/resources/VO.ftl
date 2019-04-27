package ${modulePackage}.vo;

import ${modulePackage}.po.${className}PO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.EqualsAndHashCode;


/**
 * @author ${userName}
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class ${className}VO extends ${className}PO{

}
