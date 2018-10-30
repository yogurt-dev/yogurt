package ${modulePackage}.dao.impl;

import org.jooq.TableField;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.dao.${className}DAO;
import com.github.yogurt.core.dao.impl.BaseDAOImpl;
import ${modulePackage}.dao.jooq.${className}Record;
import org.springframework.stereotype.Service;

import static ${modulePackage}.dao.jooq.${className}.${className?upper_case};
/**
 * ${lowerName} DAOImpl
 *
 */
@Service
public class ${className}DAOImpl extends BaseDAOImpl<${className}PO, ${className}Record>  implements ${className}DAO{

    @Override
    public TableField getId() {
    return ${className?upper_case}.${priKey.codeName?upper_case};
    }

    @Override
    public Class<${className}PO> getType() {
        return ${className}PO.class;
    }
}
