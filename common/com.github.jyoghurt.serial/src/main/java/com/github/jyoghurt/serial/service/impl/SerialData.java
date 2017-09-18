package com.github.jyoghurt.serial.service.impl;//package com.df.community.base.serial.service.impl;
//
//import com.df.community.base.serial.exception.SerialException;
//import com.df.community.base.serial.service.SerialInfoService;
//import com.github.jyoghurt.com.xy.course.general.core.exception.BaseErrorException;
//import org.springframework.web.context.ContextLoader;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.sql.Date;
//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * @Project: 驴鱼社区-车险帮
// * @Package: com.df.community.base.serial.service.impl
// * @Description:
// * @author: baoxiaobing@lvyushequ.com
// * @date: 2016-03-21 09:04
// */
//public course SerialData {
//
//    //步长
//    private static final int step = 100;
//
//    private static SerialInfoService serialInfoService;
//
//    //数据存储
//    private static Map<String, List<Integer>> data;
//
//    /**
//     * 设置数据
//     *
//     * @param key     所属模块
//     * @param startNo 起始
//     */
//    public static void setData(String key, int startNo) {
//        List<Integer> dataRe = new ArrayList();
//        for (int i = startNo; i < step; i++) {
//            dataRe.add(i);
//        }
//        data.remove(key);
//        data.put(key, dataRe);
//    }
//
//    /**
//     * 取业务流水
//     *
//     * @param moduelName 模块名称
//     */
//    public static String fetchData(String moduelName) throws SerialException {
//
//        if (!isContain(moduelName)) {
//            String startNo = queryStartNum(moduelName);
//            setData(moduelName, Integer.parseInt(startNo));
//        }
//
//        Date now = new Date(System.currentTimeMillis());
//
//        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
//
//        return generateSerailNO(moduelName, sdf.format(now), leftZeroPadding(getNoFroamData(moduelName)));
//    }
//
//    /**
//     * 判断内容中是否还有可用的 业务主键
//     *
//     * @param moduelName 模块名称
//     * @return
//     */
//    private static boolean isContain(String moduelName) {
//
//        if (!data.containsKey(moduelName)) {
//            List<Integer> serialNO = new ArrayList();
//            data.put(moduelName, serialNO);
//            return false;
//        } else {
//            List<Integer> serialNO = data.get(moduelName);
//            if (serialNO.size() == 0) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 查询模块业务主键开始值
//     *
//     * @param moduelName
//     * @return
//     */
//    private static String queryStartNum(String moduelName) throws SerialException {
//        try {
//            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
//            serialInfoService = (SerialInfoService) context.getBean("serialInfoService");
//            return serialInfoService.querySerialInfoByModuleName(moduelName, step);
//        } catch (BaseErrorException e) {
//            throw new SerialException("1", "获取业务主键异常：" + e.getMessage());
//        }
//    }
//
//
//    /**
//     * 获取模块的 最新的序号
//     *
//     * @param modelName
//     * @return
//     */
//    private static Integer getNoFroamData(String modelName) {
//        List<Integer> nos = data.get(modelName);
//        Integer integer = nos.get(0);
//        nos.remove(0);
//        return integer;
//    }
//
//    /**
//     * 生成业务流水号
//     *
//     * @param modelName
//     * @param date
//     * @param serailNo
//     * @return
//     */
//    private static String generateSerailNO(String modelName, String date, String serailNo) {
//        return modelName.concat("-").concat(date).concat("-").concat(serailNo);
//    }
//
//
//    /**
//     * 左补零
//     *
//     * @param serailNo
//     * @return
//     */
//    private static String leftZeroPadding(Integer serailNo) {
//        //得到一个NumberFormat的实例
//        int i = serailNo.intValue();
//        NumberFormat nf = NumberFormat.getInstance();
//        //设置是否使用分组
//        nf.setGroupingUsed(false);
//        //设置最大整数位数
//        nf.setMaximumIntegerDigits(3);
//        //设置最小整数位数
//        nf.setMinimumIntegerDigits(3);
//        return nf.format(i);
//    }
//
//
//}
