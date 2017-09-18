package com.github.jyoghurt.common.payment.business.tencent.common.utils;

import com.github.jyoghurt.common.payment.business.tencent.common.config.TencentConfigure;
import com.github.jyoghurt.common.payment.common.utils.MD5Util;
import com.github.jyoghurt.common.payment.common.utils.XMLParserUtil;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.exception.UtilException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 15:23
 */

/**
 * 微信支付签名工具类
 */
public class TencentSignatureUtil {
    /**
     * 签名算法
     *
     * @param o 要参与签名的数据对象
     * @return 签名
     */
    public static String getSign(Object o, TencentConfigure tencentConfigure) {
        ArrayList<String> list = new ArrayList<>();
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                if (f.get(o) != null && f.get(o) != "") {
                    list.add(f.getName() + "=" + f.get(o) + "&");
                }
            } catch (IllegalAccessException e) {
                throw new BaseErrorException("生成微信签名异常，异常对象:{0}", o.toString());
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        //添加签名时，不添加sign本身及key
        result += "key=" + tencentConfigure.getKey();
        result = MD5Util.MD5Encode(result).toUpperCase();
        return result;
    }

    public static String getSign(Map<String, Object> map, TencentConfigure tencentConfigure) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != "") {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + tencentConfigure.getKey();
        result = MD5Util.MD5Encode(result).toUpperCase();
        return result;
    }

    public static String getSign(Map<String, Object> map) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != "") {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result = MD5Util.MD5Encode(result).toUpperCase();
        return result;
    }

    /**
     * 从API返回的XML数据里面重新计算一次签名
     *
     * @param responseString API返回的XML数据
     * @return 新鲜出炉的签名
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static String getSignFromResponseString(String responseString, TencentConfigure tencentConfigure) throws IOException,
            SAXException, ParserConfigurationException, UtilException {
        Map<String, Object> map = XMLParserUtil.getMapFromXML(responseString);
        //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        map.put("sign", "");
        //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
        return TencentSignatureUtil.getSign(map, tencentConfigure);
    }

    /**
     * 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
     *
     * @param responseString API返回的XML数据字符串
     * @return API签名是否合法
     * @throws UtilException
     */
    public static boolean checkIsSignValidFromResponseString(String responseString, TencentConfigure tencentConfigure) throws UtilException {
        try {
            Map<String, Object> map = XMLParserUtil.getMapFromXML(responseString);
            String signFromAPIResponse = map.get("sign").toString();
            //验证API返回的数据签名数据，有可能被第三方篡改!!!";
            if (signFromAPIResponse == "" || signFromAPIResponse == null) {
                return false;
            }
            //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
            map.put("sign", "");
            //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
            String signForAPIResponse = TencentSignatureUtil.getSign(map, tencentConfigure);
            if (!signForAPIResponse.equals(signFromAPIResponse)) {
                //签名验不过，表示这个API返回的数据有可能已经被篡改了
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new UtilException("微信支付验证签名工具类异常", e);
        }
    }

    /**
     * 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
     *
     * @param responseMap API返回的XML数据map对象
     * @return API签名是否合法
     * @throws UtilException
     */
    public static boolean checkIsSignValidFromResponseString(Map<String, Object> responseMap, TencentConfigure tencentConfigure) throws UtilException {
        try {
            String signFromAPIResponse = responseMap.get("sign").toString();
            //验证API返回的数据签名数据，有可能被第三方篡改!!!";
            if (signFromAPIResponse == "" || signFromAPIResponse == null) {
                return false;
            }
            //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
            responseMap.put("sign", "");
            //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
            String signForAPIResponse = TencentSignatureUtil.getSign(responseMap, tencentConfigure);
            if (!signForAPIResponse.equals(signFromAPIResponse)) {
                //签名验不过，表示这个API返回的数据有可能已经被篡改了
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new UtilException("微信支付验证签名工具类异常", e);
        }
    }
}
