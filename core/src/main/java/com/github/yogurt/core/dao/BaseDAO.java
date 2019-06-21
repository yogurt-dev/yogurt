package com.github.yogurt.core.dao;


import com.github.yogurt.core.po.BasePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


/**
 * @author jtwu
 */
@NoRepositoryBean
public interface BaseDAO<T extends BasePO> extends JpaRepository<T, Long> {

}
