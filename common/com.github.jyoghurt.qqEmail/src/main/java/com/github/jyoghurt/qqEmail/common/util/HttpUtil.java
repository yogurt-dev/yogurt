package com.github.jyoghurt.qqEmail.common.util;

import com.alibaba.fastjson.JSON;
import com.github.jyoghurt.qqEmail.Exception.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author zhangjl
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    /**
     * 使用Get方式获取数据
     *
     * @param url     URL包括参数，http://HOST/XX?XX=XX&XXX=XXX
     * @param charset
     * @return
     */
    public static String sendGet(String url, String charset) throws EmailException {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), charset));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("同步腾讯企业邮箱失败",e);
            throw new EmailException("-1", "http协议异常！");
        } finally {// 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                throw new EmailException();
            }
        }
        return result;
    }

    /**
     * POST请求，字符串形式数据
     *
     * @param url     请求地址
     * @param param   请求数据
     * @param charset 编码方式
     */
    public static String sendPostUrl(String url, String param, String charset) throws EmailException {
        return sendPostUrl(url, param, charset, null);
    }

    /**
     * POST请求，字符串形式数据
     *
     * @param url       请求地址
     * @param param     请求数据
     * @param charset   编码方式
     * @param headerMap 头部信息
     */
    public static String sendPostUrl(String url, String param, String charset, Map<String, String> headerMap) throws EmailException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            /*根据headerMap封装头部消息*/
            if (null != headerMap) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), charset));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("同步腾讯企业邮箱失败",e);
            throw new EmailException("1", "同步腾讯企业邮箱失败！");
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            if (result == null) {
                throw new EmailException(JSON.parseObject(result).get("errcode").toString(), JSON.parseObject(result)
                        .get("errmsg").toString());
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                logger.error("同步腾讯企业邮箱失败",ex);
                throw new EmailException("1", "同步腾讯企业邮箱失败！");
            }
        }
        return result;
    }

    /**
     * POST请求，Map形式数据
     *
     * @param url     请求地址
     * @param param   请求数据
     * @param charset 编码方式
     */
    public static String sendPost(String url, Map<String, String> param,
                                  String charset) throws EmailException {

        StringBuffer buffer = new StringBuffer();
        if (param != null && !param.isEmpty()) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                buffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue()))
                        .append("&");

            }
        }
        buffer.deleteCharAt(buffer.length() - 1);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(buffer);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), charset));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            logger.error("同步腾讯企业邮箱失败",e);
            throw new EmailException("1", "同步腾讯企业邮箱失败！");
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            if (JSON.parseObject(result).get("errcode") != null) {
                throw new EmailException(JSON.parseObject(result).get("errcode").toString(), JSON.parseObject(result)
                        .get("errmsg").toString());
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new EmailException("-1", "http协议异常！");
            }
        }
        return result;
    }
}
