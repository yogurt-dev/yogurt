package ${modulePackage}.service.impl;

import com.github.yogurt.core.service.impl.BaseServiceImpl;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.po.Q${className}PO;
import ${modulePackage}.service.${className}Service;
import com.querydsl.core.types.dsl.EntityPathBase;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ${userName}
 */
@Slf4j
@Service
public class ${className}ServiceImpl extends BaseServiceImpl<${className}PO> implements ${className}Service {
    @Override
    protected EntityPathBase<${className}PO> getEntityPathBase() {
        return Q${className}PO.${lowerName}PO;
    }
}
