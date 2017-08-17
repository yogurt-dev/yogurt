package com.github.jyoghurt.UIFramework.service.impl;

import com.github.jyoghurt.UIFramework.service.UeditorService;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangjl on 2015/12/15.
 */
@Service("ueditorService")
public class UeditorServiceImpl implements UeditorService {
    @Value("${downloadPath}")
    private String downloadPath;
    private static Logger logger = LoggerFactory.getLogger(UeditorServiceImpl.class);

    public HttpResultEntity<?> uploadImg(HttpServletRequest request)  {

        return new HttpResultEntity<>();
    }

    @Override
    public String changeRelativePath(String data)  {
        try {
            //正则匹配获取imgurl
            List<String> imgUrls = getImgSrc(data);
            for (String imgUrl : imgUrls) {
                String[] arges = imgUrl.split(downloadPath);
                if (arges.length > 1) {
                    String localURL =arges[0];
                    data = data.replace(localURL, "");
                }
            }
            logger.info("上传图片返回的内容为:" + data);
            return data;
        } catch (Exception e) {
            logger.info("将ueditor中的本地图片路径置成相对路径异常", e);
        }
        return data;
    }

    /*正则匹配html中的img src路径*/
    private static final Pattern PATTERN = Pattern.compile("<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static List getImgSrc(String html) {
        logger.info("==================");
        logger.info("开始正则匹配图片src");
        logger.info("==================");
        Matcher matcher = PATTERN.matcher(html);
        List list = new ArrayList();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group == null) {
                continue;
            }
            logger.info("==========================");
            logger.info("开始正则匹配特殊字符");
            logger.info("==========================");
            //   这里可能还需要更复杂的判断,用以处理src="...."内的一些转义符
            if (group.startsWith("'")) {
                logger.info("==========================");
                logger.info("开始正则匹配特殊字符'");
                logger.info("==========================");
                list.add(group.substring(1, group.indexOf("'", 1)));
            } else if (group.startsWith("\"")) {
                logger.info("==========================");
                logger.info("开始正则匹配特殊字符\"");
                logger.info("==========================");
                list.add(group.substring(1, group.indexOf("\"", 1)));
            } else {
                logger.info("==========================");
                logger.info("开始正则匹配特殊字符\\s");
                logger.info("==========================");
                list.add(group.split("\\s")[0]);
            }
            logger.info("==========================");
            logger.info("开始正则匹配特殊字符完成");
            logger.info("==========================");
        }
        logger.info("================");
        logger.info("则匹配图片src结束");
        logger.info("================");
        return list;
    }

    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
