package com.github.jyoghurt.activiti.business.dao;

import com.github.jyoghurt.activiti.business.vo.MainFormUpdateVo;
import com.github.jyoghurt.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * user:dell
 * date: 2016/9/19.
 */
public interface MainFormMapper extends BaseMapper {
    /**
     * 根据查询条件统计收银台 现金
     *
     * @param mainFormUpdateVo
     * @return
     */
    int updateMainFormState(@Param("mainFormUpdateVo")MainFormUpdateVo mainFormUpdateVo);
}
