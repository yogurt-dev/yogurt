package com.github.yogurt.core.dao;


import com.github.yogurt.core.exception.DaoException;
import com.github.yogurt.core.po.BasePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

/**
 * @author jtwu
 */
public interface BaseDAO<T extends BasePO> {

    /**
     * 保存
     * @param po 持久化对象
     * @throws DaoException
     */
    void save(T po) throws DaoException;

    /**
     * 更新
     * @param po 持久化对象
     */
    void update(T po);

    /**
     * 根据主键查询
     * @param id 主键
     * @return po
     */
    T findById(Serializable id);

    /**
     * 查询所有
     * @return 所有po
     */
    List<T> findAll();

    /**
     * 分页查询
     * @param po 查询条件
     * @param pageable 分页条件
     * @return 分页集合
     */
    Page<T> list(T po, Pageable pageable);


    /**
     * 批量保存
     * @param poList 持久化对象集合
     */
    void batchSave(List<T> poList);


    /**
     * 批量更新
     * @param poList 持久化对象集合
     */
    void batchUpdate(List<T> poList);

    /**
     * 更新非空字段
     * @param po 持久化对象
     */
	void updateForSelective(T po);
}
