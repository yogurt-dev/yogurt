package ${modulePackage}.service.impl;


import com.github.yogurt.core.service.impl.BaseServiceImpl;
import ${modulePackage}.dao.${className}DAO;
import ${modulePackage}.po.${className}PO;
import ${modulePackage}.service.${className}Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("${lowerName}Service")
public class ${className}ServiceImpl extends BaseServiceImpl<${className}PO> implements ${className}Service {
	@Autowired
    private ${className}DAO ${lowerName}DAO;

}
