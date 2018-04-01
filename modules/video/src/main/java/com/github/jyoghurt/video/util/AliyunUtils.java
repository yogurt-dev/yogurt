package com.github.jyoghurt.video.util;


import com.aliyuncs.auth.ISigner;
import com.aliyuncs.http.FormatType;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/29
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class AliyunUtils {


    public static String buildPublicParam(String AccessKey) {

        return StringUtils.join("&Version=2017-03-21",
                "&AccessKeyId=", AccessKey,
                "&SignatureMethod=Hmac-SHA1",
                "&SignatureVersion=1.0",
                "&Timestamp=", getTimestamp(),
                "&SignatureNonce=", UUID.randomUUID().toString());
    }

    public static String getVersion() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }


    public static String getTimestamp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(new Date());
    }
}
