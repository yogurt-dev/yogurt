package com.github.jyoghurt.common.msgcen.common.convert;

import com.github.jyoghurt.core.exception.BaseErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * user:zjl
 * date: 2016/11/19.
 */
public class MsgConvert {

    public static Map<String, List> convertMsgToSendMap(Map<String, List> sendMap, String returnMsg) {
        String[] returnAttr = returnMsg.split("->");
        List<String> returnList = Arrays.asList(returnAttr);
        if (returnList.size() != 2) {
            throw new BaseErrorException("解析trigger返回值异常");
        }
        List<String> sendList = sendMap.get(returnList.get(0));
        if (sendList != null) {
            sendList.add(returnList.get(1));
            return sendMap;
        }
        List<String> list = new ArrayList<>();
        list.add(returnList.get(1));
        sendMap.put(returnList.get(0), list);
        return sendMap;
    }
}
