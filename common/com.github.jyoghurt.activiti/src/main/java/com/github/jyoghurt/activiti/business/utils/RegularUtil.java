package com.github.jyoghurt.activiti.business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * user:dell
 * date: 2016/9/28.
 */
public class RegularUtil {
    /**
     * 正则匹配获取大括号内的内容
     *
     * @param rule 需解析的规则
     * @return 正则匹配对象
     */
    public static List<String> getContentInBaces(String rule) {
        String re = "\\{([^\\]]+)\\}";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(rule);
        List<String> list = new ArrayList<>();
        if (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                list.add(m.group(i));
            }
        }
        return list;
    }

    /**
     * 正则匹配获取方括号内的内容
     *
     * @param rule 需解析的规则
     * @return 正则匹配对象
     */
    public static List<String> getContentInBrackets(String rule) {
        String re = "\\[([^\\]]+)\\]";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(rule);
        List<String> list = new ArrayList<>();
        if (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                list.add(m.group(i));
            }
        }
        return list;
    }

    /**
     * 匹配获取{{}}中的内容
     *
     * @param var 需要替换的字符串
     * @return 匹配内容集合
     */
    public static List<String> getDoubleContent(String var) {
        String re = "(?<=\\{\\{)[^\\}\\}]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(var);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            String str = m.group();
            list.add(str);
        }
        return list;
    }

    /**
     * 根据[[]]进行解析r
     *
     * @param var 需要替换的字符串
     * @return 被解析后的var
     */
    public static List<String> getDoubleBracketsContent(String var) {
        String re = "(?<=\\[\\[)[^\\]\\]]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(var);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            String str = m.group();
            list.add(str);
        }
        return list;
    }

    public static void main(String[] args) {
        String re = "(?<=\\{\\{)[^\\}\\}]+";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher("function(){\n" +
                "if({{member.memberId}}!=null){\n" +
                "return WECHAT-WASH-UPDATE-STATUS\n" +
                "}\n" +
                "}");
        List<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }
        System.out.print("");
    }

}
