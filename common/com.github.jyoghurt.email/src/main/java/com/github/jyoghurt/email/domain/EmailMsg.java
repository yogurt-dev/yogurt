package com.github.jyoghurt.email.domain;

import java.io.File;
import java.util.List;

/**
 * Created by zhangjl on 2015/11/25.
 */
public class EmailMsg {
    private String host;//发送方邮箱地址  temple：smtp.126.com
    private String userName;//发送方实际用户名
    private String showName;//发送方显示名称
    private String passWord;//发送方密码
    private List<String> targetAddress;//发送地址列表
    private String subject;//发送标题
    private String EmailContent;//发送内容
    private List<File> fileList;//附件列表
    private List<File> contentFileList;//邮件内容中包含的图片

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public List<String> getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(List<String> targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmailContent() {
        return EmailContent;
    }

    public EmailMsg setEmailContent(String emailContent) {
        EmailContent = emailContent;
        return this;
    }

    public List<File> getContentFileList() {
        return contentFileList;
    }

    public void setContentFileList(List<File> contentFileList) {
        this.contentFileList = contentFileList;
    }

    @Override
    public String toString() {
        return "EmailMsg{" +
                "host='" + host + '\'' +
                ", userName='" + userName + '\'' +
                ", showName='" + showName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", targetAddress=" + targetAddress +
                ", subject='" + subject + '\'' +
                ", EmailContent='" + EmailContent + '\'' +
                ", fileList=" + fileList +
                ", contentFileList=" + contentFileList +
                '}';
    }
}
