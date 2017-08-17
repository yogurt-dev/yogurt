package com.github.jyoghurt.sw.convert;

import com.github.jyoghurt.sw.domain.SwitchT;
import com.github.jyoghurt.sw.vo.SwitchVo;

import java.util.ArrayList;
import java.util.List;


public class SwitchConvert {

    public static List<SwitchVo> convert(List<SwitchT> list) {
        List<SwitchVo> result = new ArrayList<>();
        for (SwitchT switchT : list) {
            SwitchVo switchVo = new SwitchVo();
            switchVo.setSwitchGroupKey(switchT.getSwitchGroupKey());
            switchVo.setSwitchStatus(true);
            result.add(switchVo);
        }
        return result;
    }

}
