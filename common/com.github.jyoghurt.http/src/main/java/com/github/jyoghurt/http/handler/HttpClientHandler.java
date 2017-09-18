package com.github.jyoghurt.http.handler;

import com.github.jyoghurt.core.exception.BaseErrorException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.github.jyoghurt.http.constants.FrameConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

/**
 * user:zjl
 * date: 2016/10/28.
 */
public class HttpClientHandler {
    //连接超时时间，默认10秒
    private int socketTimeout = 10000;

    //传输超时时间，默认30秒
    private int connectTimeout = 30000;

    //请求器的配置
    private RequestConfig requestConfig;

    //HTTP请求器
    private CloseableHttpClient httpClient;

    /**
     * 初始化http
     */
    private void init() {
        httpClient = HttpClients.custom().build();
        //根据默认超时限制初始化requestConfig
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
    }

    /**
     * 初始化https
     *
     * @param certificatePath 证书路径
     * @param keyPassword     证书密码
     */
    private void init(String certificatePath, String keyPassword) {
        FileInputStream instream = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            instream = new FileInputStream(new File(certificatePath));
            keyStore.load(instream, keyPassword.toCharArray());
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, keyPassword.toCharArray()).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            //根据默认超时限制初始化requestConfig
            requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        } catch (Exception e) {
            throw new BaseErrorException("https客户端初始化失败", e);
        } finally {
            try {
                if (null == instream) {
                    throw new BaseErrorException("未找到路径对应证书，证书路径:{0}", certificatePath);
                }
                instream.close();
            } catch (IOException e) {
                throw new BaseErrorException("关闭流失败", e);
            }
        }
    }

    /**
     * 通过Http post xml数据
     *
     * @param url             请求地址
     * @param xmlObj          要提交的XML数据对象
     * @param certificatePath 证书路径
     * @param keyPassword     证书密码
     * @return 回包的实际数据
     */

    public HttpResponse sendPost(String url, String certificatePath, String keyPassword, Object xmlObj) {
        init(certificatePath, keyPassword);
        HttpPost httpPost = new HttpPost(url);
        //解决XStream对出现双下划线的bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver(FrameConstants.CHARSET, new XmlFriendlyNameCoder("-_",
                "_")));
        //将要提交给API的数据对象转换成XML格式数据Post给API
        String postDataXML = xStreamForRequestPostData.toXML(xmlObj);
        //得指明使用编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(postDataXML, FrameConstants.CHARSET);
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);
        //设置请求器的配置
        httpPost.setConfig(requestConfig);
        return getResponse(httpClient, httpPost);
    }

    /**
     * 通过Http post xml数据
     *
     * @param url    请求地址
     * @param xmlObj 要提交的XML数据对象
     * @return 回包的实际数据
     */

    public HttpResponse sendPost(String url, Object xmlObj) {
        init();
        HttpPost httpPost = new HttpPost(url);
        //解决XStream对出现双下划线的bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver(FrameConstants.CHARSET, new XmlFriendlyNameCoder("-_",
                "_")));
        //将要提交给API的数据对象转换成XML格式数据Post给API
        String postDataXML = xStreamForRequestPostData.toXML(xmlObj);
        //得指明使用编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(postDataXML, FrameConstants.CHARSET);
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.setEntity(postEntity);
        //设置请求器的配置
        httpPost.setConfig(requestConfig);
        return getResponse(httpClient, httpPost);
    }

    /**
     * 通过Http post xml数据
     *
     * @param url             请求地址
     * @param certificatePath 证书路径
     * @param keyPassword     证书密码
     * @return 回包的实际数据
     */

    public HttpResponse sendGet(String url, String certificatePath, String keyPassword) {
        init(certificatePath, keyPassword);
        HttpGet httpGet = new HttpGet(url);
        //设置请求器的配置
        httpGet.setConfig(requestConfig);
        return getResponse(httpClient, httpGet);
    }

    /**
     * 通过Http post xml数据
     *
     * @param url 请求地址
     * @return 回包的实际数据
     */

    public HttpResponse sendGet(String url) {
        init();
        HttpGet httpGet = new HttpGet(url);
        //设置请求器的配置
        httpGet.setConfig(requestConfig);
        return getResponse(httpClient, httpGet);
    }

    /**
     * 获取response信息
     *
     * @param httpClient http客户端
     * @param httpGet    httpGet参数
     * @return response
     */
    private HttpResponse getResponse(CloseableHttpClient httpClient, HttpGet httpGet) {
        try {
            return httpClient.execute(httpGet);
        } catch (Exception e) {
            throw new BaseErrorException("请求微信支付Http异常", e);
        } finally {
            httpGet.abort();
        }
    }


    /**
     * 获取response信息
     *
     * @param httpClient http客户端
     * @param httpPost   httpPost参数
     * @return response
     */
    private HttpResponse getResponse(CloseableHttpClient httpClient, HttpPost httpPost) {
        try {
            return httpClient.execute(httpPost);
        } catch (Exception e) {
            throw new BaseErrorException("请求微信支付Http异常", e);
        } finally {
            httpPost.abort();
        }
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }
}
