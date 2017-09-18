package com.github.jyoghurt.serial.handler;


import com.github.jyoghurt.core.redis.RedisHandler;
import com.github.jyoghurt.serial.enums.Module;
import com.github.jyoghurt.serial.enums.SerialType;
import com.github.jyoghurt.serial.service.SerialInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.community.base.serial.handler
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-11-11 09:59
 */
public abstract class SerialBaseRule {
    //步长
    static final int step = 100;
    Logger logger = LoggerFactory.getLogger(SerialBaseRule.class);

    static final String commNoContainerKey = "commNoContainerKey";
    static final String moduleDateNoContainer = "moduleDateNoContainer";
    static final String modulePrivateNoContainer = "modulePrivateNoContainer";


    @Autowired
    private RedisHandler redisHandler;

    private static SerialInfoService serialInfoService;

    //生成流水
    public abstract String generateSerialNo(Module module, SerialType serialType);

    /**
     * 向内存中设置流水号
     *
     * @param key     所属模块
     * @param startNo 起始
     */
    void setData(String key, int startNo, Map<String, List<Integer>> data) {
        List<Integer> dataRe = new ArrayList();
        for (int i = 0; i < step; i++) {
            dataRe.add(startNo + i);
        }
        data.put(key, dataRe);
    }
    /**
     * 获取模块的 最新的序号，并从内存中清除
     *
     * @param modelName
     * @return
     */
    Integer getNoFromData(String modelName, Map<String, List<Integer>> data) {
        List<Integer> nos = data.get(modelName);
        Integer integer = nos.get(0);
        nos.remove(0);
        return integer;
    }

    /**
     * 从redis中获取容器
     * @param key
     * @return
     */
    Map<String, List<Integer>> getContainerFromRedis(String key){
        Map<String, List<Integer>> result = (Map)redisHandler.getRedisTemplate().opsForValue().get
                (key);
        return result;
    }

    /**
     * 更新redis中的容器
     * @param key
     * @param map
     */
    void setContainerToRedis(String key,Map<String, List<Integer>> map){
        redisHandler.getRedisTemplate().opsForValue().set(key,map);
    }

    /**
     * 检查redis中的容器
     * @param key
     */
    void checkContrainerInRedis(String key){
        if(getContainerFromRedis(key)==null){
            Map<String, List<Integer>> container = new HashMap();
            setContainerToRedis(key,container);
        }
    }

    /**
     * 左补零
     *
     * @param serailNo
     * @return
     */
    String leftZeroPadding(Integer serailNo) {
        //得到一个NumberFormat的实例
        int i = serailNo.intValue();
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(4);
        //设置最小整数位数
        nf.setMinimumIntegerDigits(4);
        return nf.format(i);
    }


    /**
     * 根据模块CODE判断内存中是否有可用的 流水
     *
     * @param moduleName
     * @return
     */
    boolean checkExistSerialNoInMemery(String moduleName,Map<String, List<Integer>> container) {
        //内存中是否包含模块对应的业务主键集合
        if (!container.containsKey(moduleName)) {
            List<Integer> serialNO = new ArrayList();
            container.put(moduleName, serialNO);
            return false;
        } else {
            List<Integer> serialNO = container.get(moduleName);
            if (serialNO.size() == 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断内存中是否还有可用的 业务主键，如果不存在，在内存中创建
     *
     * @param moduleName 模块名称
     * @return
     */
    boolean checkExistSerialNoInMemery(String moduleName,String now,Map<String, List<Integer>> container) {
        moduleName = moduleName+now;
        //内存中是否包含模块对应的业务主键集合
        if (!container.containsKey(moduleName)) {
            logger.debug("★★★★★★★★内存中无可使用序号★★★★★★★★★★");
            logger.debug("模块："+moduleName);
            List<Integer> serialNO = new ArrayList();
            container.put(moduleName + now, serialNO);
            return false;
        } else {
            List<Integer> serialNO = container.get(moduleName);
            if (serialNO.size() == 0) {
                return false;
            }

        }
        return true;
    }

    public static SerialInfoService getSerialInfoService() {
        return serialInfoService;
    }

    public static void setSerialInfoService(SerialInfoService serialInfoService) {
        SerialBaseRule.serialInfoService = serialInfoService;
    }

}
