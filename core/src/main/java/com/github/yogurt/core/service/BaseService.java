package com.github.yogurt.core.service;


import com.github.yogurt.core.exception.ServiceException;
import com.github.yogurt.core.po.BasePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

/**
 * 服务接口的基类
 *
 * @param <T>此服务接口服务的数据模型，即model
 */
public interface BaseService<T extends BasePO> {
    /**
     * 保存实体
     *
     * @param po 待保存的实体
     */
    void save(T po) throws ServiceException;

    /**
     * 更新实体
     *
     * @param po 业务实体
     */
    void update(T po);

    /**
     * 逻辑删除
     *
     * @param id 主键
     */
    void logicDelete(Serializable id) throws ServiceException;

    /**
     * 根据ID获取实体
     *
     * @param id 业务实体ID
     * @return 业务实体
     */
    <F extends Serializable> T findById(F id);

    /**
     * 查询所有记录
     *
     * @return 业务实体集合
     */
    List<T> findAll();

    /**
     * 分页查询
     *
     * @param po       业务实体
     * @param pageable 分页
     * @return spring-data的分页对象
     */
    Page<T> list(T po, Pageable pageable);

    /**
     * 批量保存
     *
     * @param poList 实体列表
     */
    void batchSave(List<T> poList);

    /**
     * 批量更新
     *
     * @param poList 实体列表
     */
    void batchUpdate(List<T> poList);
//
//
//    /**
//     * 根据选择的更新实体
//     *
//     * @param po 业务实体
//     *
//     */
//    void updateForSelective(T po) ;
//
}
