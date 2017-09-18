package com.github.jyoghurt.msgcen.common.utils;

import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.JPAUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * user:dell
 * date: 2016/9/28.
 */
public class MsgRegularUtil {
    /**
     * 匹配获取{{}}中的内容
     *
     * @param var       需要替换的字符串
     * @param obj       参数对象集合
     * @param tmplParam 模板解析参数对象
     * @return 匹配内容集合
     */
    public static List<String> getSmsDoubleContent(String var, JSONObject obj, JSONObject tmplParam) {
        String re = "(?<=\\{\\{)[^\\}\\}]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(var);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            JSONObject repObj = obj;
            JSONObject repTmplObj = tmplParam;
            String replaceStr = m.group();
            List<String> replaceList = Arrays.asList(replaceStr.split("\\."));
            for (int i = 0; i < replaceList.size(); i++) {
                //若存在模板规则解析消息 则优先走模板规则解析消息
                if (null != repTmplObj.get(replaceList.get(i))) {
                    if (i == replaceList.size() - 1) {
                        list.add(repTmplObj.get(replaceList.get(i)).toString());
                        continue;
                    }
                    repTmplObj = JSONObject.fromObject(tmplParam.get(replaceList.get(i)));
                    continue;
                }
                //若没有模板规则解析的消息 则正常解析参数中的消息
                if (null == repObj.get(replaceList.get(i))) {
                    throw new BaseErrorException("解析json模板参数失败,并未传key为：{0}的参数", replaceList.get(i));
                }
                if (i == replaceList.size() - 1) {
                    list.add(repObj.get(replaceList.get(i)).toString());
                    continue;
                }
                repObj = JSONObject.fromObject(repObj.get(replaceList.get(i)));
            }
        }
        return list;
    }

    /**
     * 根据{{}}进行解析，获取发送对象中的参数并反射至t
     *
     * @param var   需要解析的字符串
     * @param t     目标对象
     * @param param 参数对象集合
     * @return 被解析参数替换后的var
     */
    public static JSONObject replaceWeChatDoubleContent(String var, Object t, JSONObject param) {
        String re = "(?<=\\{\\{)[^\\}\\}]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(var);
        while (m.find()) {
            String fieldName = m.group();
            if (null == param.get(fieldName)) {
                continue;
            }
            MsgJPAUtil.setValue(t, fieldName, param.get(fieldName).toString());
        }
        return JSONObject.fromObject(t);
    }

    /**
     * 根据{{}}进行解析，获取obj中的参数并替换至var
     * 此方法会将没有解析到的key替换成null
     *
     * @param var       需要替换的字符串
     * @param obj       参数对象集合
     * @param tmplParam 模板参数对象集合
     * @return 被解析参数替换后的var
     */
    public static String replaceEmailDoubleContent(String var, JSONObject obj, JSONObject tmplParam) {
        String re = "(?<=\\{\\{)[^\\}\\}]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(var);
        while (m.find()) {
            JSONObject repObj = obj;
            JSONObject repTmplObj = tmplParam;
            String replaceStr = m.group();
            List<String> replaceList = Arrays.asList(replaceStr.split("\\."));
            for (int i = 0; i < replaceList.size(); i++) {
                //若存在模板规则解析消息 则优先走模板规则解析消息
                if (null != repTmplObj.get(replaceList.get(i))) {
                    //找到值则直接替换
                    if (i == replaceList.size() - 1) {
                        var = var.replace("{{" + replaceStr + "}}", repTmplObj.get(replaceList.get(i)).toString());
                        continue;
                    }
                    //未找到值则递归替换下次json
                    repTmplObj = JSONObject.fromObject(tmplParam.get(replaceList.get(i)));
                    continue;
                }
                if (null == repObj.get(replaceList.get(i))) {
                    var = var.replace("{{" + replaceStr + "}}", "null");
                    continue;
                }
                if (i == replaceList.size() - 1) {
                    var = var.replace("{{" + replaceStr + "}}", repObj.get(replaceList.get(i)).toString());
                    continue;
                }
                repObj = JSONObject.fromObject(repObj.get(replaceList.get(i)));
            }
        }
        return var;
    }

    /**
     * 根据{{}}进行解析，获取obj中的参数并替换至var
     *
     * @param var 需要替换的字符串
     * @param obj 参数对象集合
     * @return 被解析参数替换后的var
     */
    public static String replaceDoubleContent(String var, JSONObject obj) {
        String re = "(?<=\\{\\{)[^\\}\\}]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(var);
        JSONObject repObj = obj;
        while (m.find()) {
            String replaceStr = m.group();
            List<String> replaceList = Arrays.asList(replaceStr.split("\\."));
            for (int i = 0; i < replaceList.size(); i++) {
                if (null == repObj.get(replaceList.get(i))) {
                    continue;
                }
                if (i == replaceList.size() - 1) {
                    var = var.replace("{{" + replaceStr + "}}", repObj.get(replaceList.get(i)).toString());
                    continue;
                }
                repObj = JSONObject.fromObject(repObj.get(replaceList.get(i)));
            }
        }
        return var;
    }

    /**
     * 根据{{}}进行解析，获取obj中的参数并替换至var
     * 此方法会将没有解析到的key替换成null
     *
     * @param var 需要替换的字符串
     * @param obj 参数对象集合
     * @return 被解析参数替换后的var
     */
    public static String replaceDoubleContentByJson(String var, JSONObject obj) {
        String re = "(?<=\\{\\{)[^\\}\\}]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(var);
        JSONObject repObj = obj;
        while (m.find()) {
            String replaceStr = m.group();
            List<String> replaceList = Arrays.asList(replaceStr.split("\\."));
            for (int i = 0; i < replaceList.size(); i++) {
                if (null == repObj.get(replaceList.get(i))) {
                    var = var.replace("{{" + replaceStr + "}}", "null");
                    continue;
                }
                if (i == replaceList.size() - 1) {
                    var = var.replace("{{" + replaceStr + "}}", repObj.get(replaceList.get(i)).toString());
                    continue;
                }
                repObj = JSONObject.fromObject(repObj.get(replaceList.get(i)));
            }
        }
        return var;
    }

    /**
     * 根据[[]]进行解析，获取发送对象中的参数并替换至var
     *
     * @param var    需要替换的字符串
     * @param target 参数对象集合
     * @return 被解析参数替换后的var
     */
    public static String replaceDoubleContentByT(String var, Object target) {
        String re = "(?<=\\[\\[)[^\\]\\]]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(var);
        while (m.find()) {
            String fieldName = m.group();
            String field;
            if (target instanceof String) {
                field = HtmlUtil.convertEmpty(target).toString();
            } else if (target instanceof JSONObject) {
                JSONObject json = (JSONObject) target;
                field = HtmlUtil.convertEmpty(json.get(fieldName)).toString();
            } else if (target instanceof Map) {
                Map<String, String> map = (Map<String, String>) target;
                field = HtmlUtil.convertEmpty(map.get(fieldName)).toString();
            } else {
                Object value = JPAUtils.getValue(target, fieldName);
                field = HtmlUtil.convertEmpty(value).toString();
            }

            var = var.replace("[[" + fieldName + "]]", StringUtils.isEmpty(field) ? "null" : field);
        }
        return var;
    }
}
