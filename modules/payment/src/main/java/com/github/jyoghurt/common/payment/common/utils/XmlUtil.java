package com.github.jyoghurt.common.payment.common.utils;


/**
 * Created by dell on 2016/3/11.
 */
public class XmlUtil {
    /**
     * 根据Map拼接xml的body部分
     *
     * @param var1 xml key
     * @param var2 xml value
     * @return "<var1>var2</var1>"
     */
    public static String createXmlBody(String var1, String var2) {
        return createXmlBody(var1, var2, false);
    }

    /**
     * 根据Map拼接xml的body部分
     *
     * @param var1   xml key
     * @param var2   xml value
     * @param ignore 是否xml解析是忽略 即生成CDATA
     * @return "<var1>var2</var1>" or <var1><![CDATA[var2]]></var1>
     */
    public static String createXmlBody(String var1, String var2, Boolean ignore) {
        return ignore ? new String().join("<", var1, ">", "<![CDATA[", var2, "]]>", "</", var1, ">") :
                new String().join("<", var1, ">", var2, "</", var1, ">");
    }
}
