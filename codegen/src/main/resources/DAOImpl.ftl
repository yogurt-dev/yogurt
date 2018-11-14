package ${modulePackage}.dao.impl;

import org.jooq.Table;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.dao.${className}DAO;
import com.github.yogurt.core.dao.impl.BaseDAOImpl;
import ${modulePackage}.dao.jooq.${className}Record;
import org.springframework.stereotype.Service;

import static ${modulePackage}.dao.jooq.${className}.${table?upper_case};
/**
 * @author ${userName}
 */
@Service
public class ${className}DAOImpl extends BaseDAOImpl<${className}PO, ${className}Record>  implements ${className}DAO{

    @Override
    public Table<${className}Record> getTable() {
    return ${table?upper_case};
    }

}
