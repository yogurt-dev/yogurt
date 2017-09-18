package com.github.jyoghurt.serial.service.impl;


import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.serial.dao.SerialInfoMapper;
import com.github.jyoghurt.serial.domain.SerialInfoT;
import com.github.jyoghurt.serial.enums.SerialType;
import com.github.jyoghurt.serial.exception.SerialException;
import com.github.jyoghurt.serial.service.SerialInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Service("serialInfoService")
public class SerialInfoServiceImpl extends ServiceSupport<SerialInfoT, SerialInfoMapper> implements SerialInfoService {

    @Autowired
    private SerialInfoMapper serialInfoMapper;

    @Override
    public SerialInfoMapper getMapper() {
        return serialInfoMapper;
    }

    @Override
    public void logicDelete(Serializable id)  {
        getMapper().logicDelete(SerialInfoT.class, id);
    }

    @Override
    public SerialInfoT find(Serializable id)  {
        return getMapper().selectById(SerialInfoT.class, id);
    }

    @Override
    public String querySerialInfoByModuleName(String module, Integer step, SerialType serialType) throws SerialException {

        Date now = new Date(System.currentTimeMillis());
        SerialInfoT serialInfoT = new SerialInfoT();

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        //格式化当前日期
        String date = sdf.format(now);

        //获得当天的业务模块的，具体策略的值
        List<SerialInfoT> serialInfoTs = this.findAll(new SerialInfoT().setModule(module).setGenerateDate(now).setType(serialType.getCode()));

        //如果不存在
        if (CollectionUtils.isEmpty(serialInfoTs)) {
            serialInfoT.setModule(module);
            serialInfoT.setGenerateDate(now);
            serialInfoT.setSerialNo(new Integer(1));
            serialInfoT.setType(serialType.getCode());
            serialInfoT.setDeleteFlag(false);
            //插入一条记录
            this.save(serialInfoT);
            return "1";
        }
        //获取当前模块序列的初始值
        serialInfoT = serialInfoTs.get(0);
        serialInfoT.setSerialNo(serialInfoT.getSerialNo());
        //更新数据库中模块的序列值，将已使用的序列补充上
        this.getMapper().updateSeriNo(module, date, step);
        return String.valueOf(serialInfoT.getSerialNo() + step);
    }

    @Override
    public String queryNormalSerialInfoByModuleName(String module, Integer step, SerialType serialType) throws  SerialException {
        //根据模块名称、策略，查询业务模块的流水号记录
        List<SerialInfoT> serialInfoTs = this.findAll(new SerialInfoT().setModule(module).setType(serialType.getCode()));
        SerialInfoT serialInfoT = new SerialInfoT();

        //如果不存在
        if (CollectionUtils.isEmpty(serialInfoTs)) {
            //抛出异常，提示应该配置流水号记录
            logger.error("查询业务模块的流水号记录失败，模块：{}在SerialInfoT表中无记录，请配置！",module);
            throw new BaseErrorException("查询业务模块的流水号记录失败，模块：{"+module+"}在SerialInfoT表中无记录，请配置！");
        }
        //如果返回结果数等于1，更新数据库，当前值增加步长
        serialInfoT = serialInfoTs.get(0);
        serialInfoT.setSerialNo(serialInfoT.getSerialNo());
        this.getMapper().updateNormalSeriNo(module,step);
        return String.valueOf(serialInfoT.getSerialNo() + step);
    }


    public Integer querySerialInfoByModuleName() {
        List<SerialInfoT> serialInfoTs = this.findAll(new SerialInfoT().setModule("COMM").setType("COMM"));
        SerialInfoT serialInfoT = serialInfoTs.get(0);
        Integer serialNumber = serialInfoT.getSerialNo();
        serialNumber = serialNumber+1;
        serialInfoT.setSerialNo(serialNumber);
        this.updateForSelective(serialInfoT);
        return serialNumber;
    }


    @Override
    public String queryCommSerialInfoByModuleName(String module, Integer step, String serialType)   {
        List<SerialInfoT> serialInfoTs = this.findAll(new SerialInfoT().setModule("COMM").setType("COMM"));
        SerialInfoT serialInfoT = serialInfoTs.get(0);
        this.getMapper().updateCommSeriNo(module,step,serialType);
        return String.valueOf(serialInfoT.getSerialNo()+step);
    }
}
