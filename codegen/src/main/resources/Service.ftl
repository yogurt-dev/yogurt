package ${modulePackage}.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.github.yogurt.core.service.BaseService;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.ao.${className}AO;

/**
 * @author ${userName}
 */

public interface ${className}Service extends BaseService<${className}PO> {

    /**
    * 分页查询
    *
    * @param configProjectAO 查询条件
    * @param pageable 分页信息
    */
    Page<${className}PO> findAll(${className}AO ${lowerName}AO, Pageable pageable);
}
