package com.github.jyoghurt.http.util;

import com.github.jyoghurt.core.exception.BaseErrorException;

import com.github.jyoghurt.http.enums.HttpRequestType;
import com.github.jyoghurt.http.handler.HttpClientHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * user:zjl
 * date: 2016/10/28.
 */
public class HttpClientUtils {
    /**
     * get请求
     *
     * @param url 请求url
     * @return response
     */
    public static HttpResponse get(String url) {
        return getRespone(HttpRequestType.GET, url, null);
    }

    /**
     * get请求
     *
     * @param url             请求url
     * @param certificatePath 证书路径
     * @param keyPassword     证书密码
     * @return response
     */
    public static HttpResponse get(String url, String certificatePath, String keyPassword) {
        return getRespone(HttpRequestType.GET, url, certificatePath, keyPassword, null);
    }

    /**
     * post请求
     *
     * @param url 请求url
     * @param obj 请求参数对象
     * @return response
     */
    public static HttpResponse post(String url, Object obj) {
        return getRespone(HttpRequestType.POST, url, obj);
    }

    /**
     * post请求
     *
     * @param url             请求url
     * @param certificatePath 证书路径
     * @param keyPassword     证书密码
     * @param obj             请求参数对象
     * @return response
     */
    public static HttpResponse post(String url, String certificatePath, String keyPassword, Object obj) {
        return getRespone(HttpRequestType.POST, url, certificatePath, keyPassword, obj);
    }

    /**
     * 获取String类型的response信息
     * 将response解析成String
     *
     * @param response response信息
     * @return String类型的response信息
     */
    public static String parseResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            throw new BaseErrorException("解析responseIO异常", e);
        }
    }

    public static Map<String, Object> parseResponseToMap(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        try {
            return XMLParserUtil.getMapFromXML(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            throw new BaseErrorException("解析responseIO异常", e);
        }
    }

    /**
     * 通过Http post xml数据
     *
     * @param httpRequestType 请求类型
     * @param url             请求地址
     * @param obj             要提交的XML数据对象
     * @return 回包的实际数据
     */
    private static HttpResponse getRespone(HttpRequestType httpRequestType, String url, Object obj) {
        switch (httpRequestType) {
            case GET:
                return new HttpClientHandler().sendGet(url);
            case POST:
                return new HttpClientHandler().sendPost(url, obj);
            case PUT:
            case DELETE:
            default:
                return null;
        }
    }

    /**
     * 通过Https post xml数据
     *
     * @param httpRequestType 请求类型
     * @param url             请求地址
     * @param certificatePath 证书路径
     * @param keyPassword     证书密码
     * @param obj             要提交的XML数据对象
     * @return 回包的实际数据
     */
    private static HttpResponse getRespone(HttpRequestType httpRequestType, String url, String certificatePath, String keyPassword, Object obj) {
        switch (httpRequestType) {
            case GET:
                return new HttpClientHandler().sendGet(url, certificatePath, keyPassword);
            case POST:
                return new HttpClientHandler().sendPost(url, certificatePath, keyPassword, obj);
            case PUT:
            case DELETE:
            default:
                return null;
        }
    }
}
