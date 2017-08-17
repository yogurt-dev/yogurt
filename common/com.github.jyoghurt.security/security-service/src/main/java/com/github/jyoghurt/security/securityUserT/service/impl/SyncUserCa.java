package com.github.jyoghurt.security.securityUserT.service.impl;

import com.github.jyoghurt.email.enums.EmailType;
import com.github.jyoghurt.emailPlugin.common.util.EmailPluginUtil;
import com.github.jyoghurt.security.enums.EmailDetailConfig;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import pub.utils.SecurityPluginUtil;
import pub.utils.SysVarEnum;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.cxm.regulation.service.impl
 * @Description: 数字证书服务
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-01-21 10:59
 */
//@Service
public class SyncUserCa extends SecurityPluginUtil {

    @Resource
    public SecurityUserTService securityUserTService;




    @Override
    public String syncUser(SecurityUserT securityUserT, SecurityUnitT securityUnit, String clientId, String clientSecret, SysVarEnum action)   {

        return null;
    }

    /**
     * 发送邮件
     * @param securityUserT
     * @param file
     */
    private static void sendEmail(SecurityUserT securityUserT, File file) {
        //存放附件
        List<File> files = new ArrayList();
        Map<String, Object> mailContent = new HashMap();

        //组装html内容
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<body>");
        html.append("<h3>以下为证书安装方式</h3>");
        html.append("<div>1、请将邮件中的附件，保存到本地</div>");
        html.append("<br>");
        html.append("<div>2、【存储位置】默认为“当前用户”，点击下一步</div>");
        html.append("<div><img src='cid:step1.png'></div>");
        html.append("<br>");
        html.append("<div>3、文件名用默认值，直接点击【下一步】</div>");
        html.append("<div><img src='cid:step2.png'></div>");
        html.append("<br>");
        html.append("<div>4、填写密码（lvyuwoaini），其他不用更改，点击【下一步】</div>");
        html.append("<div><img src='cid:step3.png'></div>");
        html.append("<br>");
        html.append("<div>5、选择“根据证书类型，自动选择证书存储”，点击【下一步】</div>");
        html.append("<div><img src='cid:step4.png'></div>");
        html.append("<br>");
        html.append("<div>6、点击【完成】按钮，系统提示“导入成功”</div>");
        html.append("<div><img src='cid:step5.png'></div>");
        html.append("<br>");
        html.append("<div>7、重启浏览器后，访问系统</div>");
        html.append("</body>");
        html.append("</html>");

        mailContent.put("desc", html.toString());
        List<String> mailTo = new ArrayList();
        mailTo.add(securityUserT.getEmailAddr());
        files.add(file);

        List<File> contentFiles = new ArrayList();
        String path = System.getProperty("user.dir").replace("bin", "webapps");


        File dir = new File(path+File.separatorChar+"cxm"+File.separatorChar+"img"+File.separatorChar+"ca");


        File[] tempList = dir.listFiles();
        //添加图片内容
        for(File iFile:tempList){
            contentFiles.add(iFile);
        }

        EmailPluginUtil.pushEmailMsg(EmailDetailConfig.CACONTENT, EmailType.FILEEMAIL, mailContent, mailTo, files,
                contentFiles);
    }


    /**
     * 获得图片路径
     * @return
     */
    public String getPath(){
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        if(path.startsWith("/")||path.startsWith("\\")){
            path = path.substring(1,path.length());
        }
        return path;
    }


    public static void main(String args[]){
SyncUserCa syncUserCa = new SyncUserCa();
        String path = syncUserCa.getPath();

        File dir = new File(path+File.separatorChar+"ca"+File.separatorChar);

        File[] tempList = dir.listFiles();
        //添加图片内容
        for(File iFile:tempList){
            System.out.println(iFile.getName());
        }
    }



}
