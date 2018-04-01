package com.github.jyoghurt.wechatbasic.common.util;

import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.exception.BaseErrorException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * ͨ�ù�����
 *
 * @author yutao
 * @date 2013-10-17
 */
public class CommonUtil {
    private static Logger log = LoggerFactory.getLogger(CommonUtil.class);
    /*获取token的url*/
    public final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * http协议请求微信端
     *
     * @param requestUrl
     * @param requestMethod
     * @param outputStr
     * @return
     * @throws WeChatException
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) throws WeChatException {
        JSONObject jsonObject;
        StringBuffer buffer = new StringBuffer();
        try {
            //SSL登录证书
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //GET/POST
            conn.setRequestMethod(requestMethod);
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("http协议连接失败！", ce);
            throw new WeChatException("1", "网络连接失败");
        } catch (Exception e) {
            log.error("http协议错误", e);
            throw new WeChatException("1", "http协议错误");
        }
        if (jsonObject.get("errcode") != null && !"0".equals(jsonObject.get("errcode").toString())) {
            if (jsonObject.get("errcode").toString().equals("40001")) {
                return tokenAgainRequest(requestUrl, requestMethod, outputStr);
            }
            log.error("微信接口调用异常！*****error:" + jsonObject.get("errcode") + "*****errmsg:" + jsonObject.get("errmsg")
                    +"****stackTrace:"+Thread.currentThread().getStackTrace().toString());
            throw new WeChatException(jsonObject.get("errcode").toString(), jsonObject.get("errmsg").toString());
        }
        return jsonObject;
    }

    private static JSONObject tokenAgainRequest(String requestUrl, String requestMethod, String outputStr) throws
            WeChatException {
        JSONObject jsonObject;
        //重新拼接请求url
        String requestParam = requestUrl.split("access_token=")[0];
        requestUrl = requestParam + "access_token=" + WeixinUtil.reGetAccessToken().getToken();
        StringBuffer buffer = new StringBuffer();
        try {
            //SSL登录证书
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //GET/POST
            conn.setRequestMethod(requestMethod);
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
            log.error("http协议连接失败！", ce);
            throw new WeChatException("1", "网络连接失败");
        } catch (Exception e) {
            log.error("http协议错误", e);
            throw new WeChatException("1", "http协议错误");
        }
        if (jsonObject.get("errcode") != null && !"0".equals(jsonObject.get("errcode").toString())) {
            log.error("微信接口调用异常！*****error:" + jsonObject.get("errcode") + "*****errmsg:" + jsonObject.get("errmsg")
                    +"****stackTrace:"+Thread.currentThread().getStackTrace().toString());
            throw new WeChatException(jsonObject.get("errcode").toString(), jsonObject.get("errmsg").toString());
        }
        return jsonObject;
    }

    /**
     * 封装双form 根据url上传多媒体的http请求
     *
     * @param requestUrl
     * @param filePath
     * @return
     * @throws WeChatException
     */
    public static JSONObject httpUploadMedia(String requestUrl, String filePath) throws WeChatException {
        String boundary = "------------7da2e536604c8";
        JSONObject jsonObject = new JSONObject();
        try {
            URL uploadUrl = new URL(requestUrl);
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
            uploadConn.setDoOutput(true);
            uploadConn.setDoInput(true);
            uploadConn.setRequestMethod("POST");
            // 设置请求头Content-Type
            uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            // 获取媒体文件上传的输出流（往微信服务器写数据）
            OutputStream outputStream = uploadConn.getOutputStream();
            URL mediaUrl = new URL(filePath);
            HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();
            meidaConn.setDoOutput(true);
            meidaConn.setRequestMethod("GET");
            // 从请求头中获取内容类型
            String contentType = meidaConn.getHeaderField("Content-Type");
            // 根据内容类型判断文件扩展名
            String fileExt = CommonUtil.getFileExt(meidaConn.getHeaderField("Content-Type"));
            // 请求体开始
            outputStream.write(("--" + boundary + "\r\n").getBytes());
            outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"file1%s\"\r\n", fileExt).getBytes());
            outputStream.write(String.format("Content-Type: %s\r\n\r\n", contentType).getBytes());
            // 获取媒体文件的输入流（读取文件）
            BufferedInputStream bis = new BufferedInputStream(meidaConn.getInputStream());
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = bis.read(buf)) != -1) {
                // 将媒体文件写到输出流（往微信服务器写数据）
                outputStream.write(buf, 0, size);
            }
            // 请求体结束
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
            outputStream.close();
            bis.close();
            meidaConn.disconnect();
            // 获取媒体文件上传的输入流（从微信服务器读数据）
            InputStream inputStream = uploadConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            uploadConn.disconnect();
            // 使用JSON-lib解析返回结果
            jsonObject = JSONObject.fromObject(buffer.toString());
            if (jsonObject.get("errcode") != null) {
                if (!WeiXinConstants.SUCCESSCODE.equals((jsonObject.get("errcode").toString()))) {
                    throw new WeChatException(jsonObject.get("errcode").toString(), jsonObject.get("errmsg").toString());
                }
            }
        } catch (Exception e) {
            log.error("/***********************************************上传文件失败!***********************************************/");
            throw new WeChatException(e);
        }
        return jsonObject;
    }

    /**
     * 根据本地路径上传文件
     *
     * @param requestUrl
     * @param filePath
     * @return
     * @throws WeChatException
     */
    public static JSONObject uploadBylocal(String requestUrl, String filePath) {
        JSONObject result = null;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new BaseErrorException("文件不存在");
        }
        try {
            /** * 第一部分 */
            URL urlObj = new URL(requestUrl);
            // 连接
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
            /** * 设置关键值 */
            con.setRequestMethod("POST");
            // 以Post方式提交表单，默认get方式
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            // post方式不能使用缓存
            // 设置请求头信息
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            // 设置边界
            String BOUNDARY = "--" + System.currentTimeMillis();
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            // 请求正文信息
            // 第一部分：
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            // 必须多两道线
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"media\";filename=\"" + file.getName() + "\"\r\n");
            sb.append("Content-Type:image/jpeg;charset=UTF-8\r\n\r\n");
            byte[] head = sb.toString().getBytes("utf-8");
            // 获得输出流
            OutputStream out = new DataOutputStream(con.getOutputStream());
            // 输出表头
            out.write(head);
            // 文件正文部分
            // 把文件已流文件的方式 推入到url中
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();
            // 结尾部分
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
            // 定义最后数据分隔线
            out.write(foot);
            out.flush();
            out.close();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = null;
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = JSONObject.fromObject(buffer.toString());
            }
            if (result.get("errcode") != null && !"0".equals(result.get("errcode"))) {
                throw new WeChatException(result.get("errcode").toString(), result.get("errmsg").toString());
            }
        } catch (IOException e) {
            log.error("**************************文件上传异常！************************");
            throw new WeChatException("数据读取异常");
        } finally {
            return result;
        }
    }

    /**
     * 下载媒体文件
     *
     * @param requestUrl
     * @param downLoadPath
     * @return
     * @throws WeChatException
     */
    public static String downLoadMedia(String requestUrl, String downLoadPath) throws WeChatException {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            // 根据内容类型获取扩展名
            String fileExt = CommonUtil.getFileExt(conn.getHeaderField("Content-Type"));
            downLoadPath = downLoadPath + fileExt;
            // 将mediaId作为文件名
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(downLoadPath));
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = bis.read(buf)) != -1)
                fos.write(buf, 0, size);
            fos.close();
            bis.close();
            conn.disconnect();
            log.info("下载媒体文件成功，filePath=" + downLoadPath);
        } catch (Exception e) {
            downLoadPath = null;
            log.error("下载媒体文件失败：{}", e);
            throw new WeChatException("下载媒体文件失败" + requestUrl);
        }
        return downLoadPath;
    }

    /**
     * 获取永久素材库 http协议
     *
     * @param requestUrl
     * @param outputStr
     * @param downLoadPath
     * @param fileExt
     * @return
     */
    public static String downLoadMaterial(String requestUrl, String outputStr, String downLoadPath, String fileExt) {
        try {
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 将mediaId作为文件名
            downLoadPath = downLoadPath + fileExt;
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(downLoadPath));
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = bis.read(buf)) != -1)
                fos.write(buf, 0, size);
            fos.close();
            bis.close();
            conn.disconnect();
            log.info("下载媒体文件成功，filePath=" + downLoadPath);
        } catch (Exception e) {
            log.error("下载媒体文件失败，filePath=" + downLoadPath);
            throw new WeChatException("***********************下载媒体文件失败***************");
        } finally {
            return downLoadPath;
        }
    }

    /**
     * url根据utf-8编码
     *
     * @param source
     * @return
     */
    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据报文判断文件类型
     *
     * @param contentType
     * @return
     */
    public static String getFileExt(String contentType) {
        String fileExt = "";
        if (contentType.indexOf("image/jpeg") > -1)
            fileExt = ".jpg";
        else if (contentType.indexOf("audio/mpeg") > -1)
            fileExt = ".mp3";
        else if (contentType.indexOf("audio/amr") > -1)
            fileExt = ".amr";
        else if (contentType.indexOf("video/mp4") > -1)
            fileExt = ".mp4";
        else if (contentType.indexOf("video/mpeg4") > -1)
            fileExt = ".mp4";
        return fileExt;
    }
}