package ${modulePackage}.service.impl;

import com.github.yogurt.core.service.impl.BaseServiceImpl;
import ${modulePackage}.dao.${className}DAO;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.ao.${className}AO;
import ${modulePackage}.service.${className}Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ${userName}
 */
@Slf4j
public class ${className}ServiceImpl extends BaseServiceImpl<${className}PO> implements ${className}Service {
	@Autowired
    private ${className}DAO ${lowerName}DAO;

    @Override
    public Page<${className}PO> findAll(${className}AO ${lowerName}AO, Pageable pageable) {
        return ${lowerName}DAO.findAll(${lowerName}AO,pageable);
    }
}
