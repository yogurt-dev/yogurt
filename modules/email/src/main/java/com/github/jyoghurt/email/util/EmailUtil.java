package com.github.jyoghurt.email.util;


import com.github.jyoghurt.email.domain.EmailMsg;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailUtil {
    public static void sendFileMail(EmailMsg emailMsg) throws MessagingException {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
        senderImpl.setHost(emailMsg.getHost());
        senderImpl.setUsername(emailMsg.getUserName());
        senderImpl.setPassword(emailMsg.getPassWord());
        messageHelper.setFrom(emailMsg.getShowName());
        messageHelper.setSubject(emailMsg.getSubject());
        //如果为带附件的邮件类型
        if (null != emailMsg.getContentFileList() && emailMsg.getContentFileList().size() > 0) {
            messageHelper.setText(emailMsg.getEmailContent().toString(), true);
            if (CollectionUtils.isNotEmpty(emailMsg.getContentFileList())) {
                for (File file : emailMsg.getContentFileList()) {
                    messageHelper.addInline(file.getName(), file);
                }
            }
        } else {
            messageHelper.setText(emailMsg.getEmailContent(), true);
        }
        if (CollectionUtils.isNotEmpty(emailMsg.getFileList())) {
            for (File file : emailMsg.getFileList()) {
                try {
                    messageHelper.addAttachment(MimeUtility.encodeWord(file.getName()), file);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        messageHelper.setTo((emailMsg.getTargetAddress().toArray(new String[emailMsg.getTargetAddress().size()])));
        senderImpl.send(mailMessage);
    }

    public static void main(String[] arges) throws MessagingException {


        try {
            JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
            MimeMessage mimeMessge = senderImpl.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessge, true, "utf-8");

            Properties props = new Properties();
//            props.put("mail.smtp.auth", "true");
            senderImpl.setHost("smtp.exmail.qq.com");
            senderImpl.setUsername("server@lvyushequ.com");
            senderImpl.setPassword("lvYU2015");
//            senderImpl.setJavaMailProperties(props);

            mimeMessageHelper.setFrom("server@lvyushequ.com");
            mimeMessageHelper.setSubject("数字证书发送测试");
            mimeMessageHelper.setText("1、将邮件的附件另存为本地文件；2、在本地磁盘中找到该文件，并双击；3、在弹出的安装提示窗体中，点击下一步，直至安装完成！", true);
            mimeMessageHelper.setTo("zhangjianlin@lvyushequ.com");

            FileSystemResource img = new FileSystemResource(new File("C:\\/张剑林\\/项目\\/驴鱼项目运维升级流程v1.0.jpg"));
            File file = new File("C:\\/张剑林\\/项目\\/驴鱼项目运维升级流程v1.0.jpg");
            mimeMessageHelper.addAttachment(MimeUtility.encodeWord("驴鱼项目运维升级流程v1.0.jpg"), file);
            senderImpl.send(mimeMessge);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        String path = getClass().getResource("/").getPath();
        return path;
    }


}
