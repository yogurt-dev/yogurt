package ${modulePackage}.dao;

import ${modulePackage}.po.${className}PO;
import ${modulePackage}.ao.${className}AO;
import com.github.yogurt.core.dao.BaseDAO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
/**
 * @author ${userName}
 */
public interface ${className}DAO extends BaseDAO<${className}PO> {

  @Query("select t from ${className}PO t")
  Page<ConfigProjectPO> findAll(${className}AO ${lowerName}AO, Pageable pageable);
}