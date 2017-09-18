package com.github.jyoghurt.serial.handler;


import com.github.jyoghurt.serial.enums.Module;
import com.github.jyoghurt.serial.enums.SerialType;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-11-11 09:46
 */
@Component("serialHanlder")
public class SerialHanlder {
    private SerialBaseRule serialBaseRule;
    public String fetchSerialNumber(Module module, SerialType serialType){
        return serialBaseRule.generateSerialNo(module,serialType);
    }

    public void setSerialBaseRule(SerialBaseRule serialBaseRule) {
        this.serialBaseRule = serialBaseRule;
    }
}
