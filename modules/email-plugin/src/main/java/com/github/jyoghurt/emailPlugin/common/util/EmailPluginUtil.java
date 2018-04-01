package com.github.jyoghurt.emailPlugin.common.util;

import com.github.jyoghurt.dataDict.service.DataDictUtils;
import com.github.jyoghurt.email.domain.EmailMsg;
import com.github.jyoghurt.emailPlugin.enums.EmailConfig;
import com.github.jyoghurt.emailPlugin.message.line.EmailSendByConfigThread;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.ChainMap;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmailPluginUtil {
    static Logger logger = LoggerFactory.getLogger(EmailPluginUtil.class);
    /*正则匹配email内容中的参数*/
    private static final Pattern PATTERN = Pattern.compile("\\{([^\\{\\}]*)\\}");


    /**
     * 邮件根据配置调用接口Api
     *
     * @param dictValueCode     字典项编码 用来查询内容的匹配规则
     * @param emailType         邮件类型
     * @param paramMap          发送内容所需参数
     * @param targetAddressList 发送目标的邮箱地址集合
     * @param files             附件列表
     * @param contentFiles      邮件正文图片
     */
    public static void pushEmailMsg(Enum dictValueCode, Enum emailType, Map<String, Object> paramMap, List<String>
            targetAddressList, List<File> files, List<File> contentFiles) {
        EmailSendByConfigThread.msgQueue.offer(createEmailMsg(dictValueCode, emailType, paramMap, targetAddressList,
                files, contentFiles));
    }

    /**
     * 邮件根据配置调用接口Api
     *
     * @param emailMsg 邮件信息
     */
    public static void pushEmailMsg(EmailMsg emailMsg) {
        EmailSendByConfigThread.msgQueue.offer(emailMsg);
    }

    /**
     * 构造方法 构造邮件消息实体
     *
     * @param dictValueCode     字典项编码 用来查询内容的匹配规则
     * @param paramMap          发送内容所需参数
     * @param targetAddressList 发送目标的邮箱地址集合
     * @param files             附件列表
     */
    private static EmailMsg createEmailMsg(Enum dictValueCode, Enum emailType, Map<String, Object> paramMap,
                                           List<String> targetAddressList, List<File> files, List<File> contentFiles) {
        EmailMsg emailMsg = new EmailMsg();
        Gson gson = new Gson();
        EmailDetail emailDetail = gson.fromJson(DataDictUtils.getDataDictValue(dictValueCode).getDictValueName(), EmailDetail.class);
        emailMsg.setHost(DataDictUtils.getDataDictValue(EmailConfig.EMAILHOST).getDictValueName());
        emailMsg.setUserName(DataDictUtils.getDataDictValue(EmailConfig.SENDERACCOUNT).getDictValueName());
        emailMsg.setPassWord(DataDictUtils.getDataDictValue(EmailConfig.SENDERPASSWORD).getDictValueName());
        emailMsg.setShowName(DataDictUtils.getDataDictValue(EmailConfig.SHOWNAME).getDictValueName());
        emailMsg.setTargetAddress(Arrays.asList(DataDictUtils.getDataDictValue(EmailConfig.TARGETADDRESS).getDictValueName().split(",")));
        emailMsg.setSubject(emailDetail.getSubject());
        if (CollectionUtils.isNotEmpty(files)) {
            emailMsg.setFileList(files);
        }
        if (CollectionUtils.isNotEmpty(contentFiles)) {
            emailMsg.setContentFileList(contentFiles);
        }

        if (targetAddressList != null) {
            emailMsg.setTargetAddress(targetAddressList);
        }
        String content = emailDetail.getContent();
        for (String param : getParams(emailDetail.getContent())) {
            if (paramMap != null && paramMap.get(param) != null) {
                content = content.replace("${" + param + "}", paramMap.get(param).toString());
                continue;
            }
            logger.error(dictValueCode + "枚举|" + param + "配置出错");
        }
        emailMsg.setEmailContent(content);
        return emailMsg;
    }

    /**
     * 正则解析${}内的参数
     *
     * @param content 需要解析的内容
     * @return 参数项列表
     */
    private static List<String> getParams(String content) {
        Matcher matcher = PATTERN.matcher(content);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group == null) {
                continue;
            }
            list.add(group);
        }
        return list;
    }

    /**
     * 实体转map
     *
     * @param objs 参数对象集合
     * @return Map
     */
    public static ChainMap<String, Object> getValueMap(Object... objs) {
        try {
            ChainMap<String, Object> map = new ChainMap<>();
            for (Object obj : objs) {
                if (null == obj) {
                    continue;
                }
                for (Class<?> c = obj.getClass(); Object.class != c; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if (null == value) {
                            continue;
                        }
                        if (field.getType().isAssignableFrom(String.class) && StringUtils.isEmpty((String) value)) {
                            continue;
                        }
                        map.put(field.getName(), value);

                    }
                }

            }
            return map;
        } catch (Exception e) {
            throw new BaseErrorException("Object to Map convert Error", e);
        }

    }
}
