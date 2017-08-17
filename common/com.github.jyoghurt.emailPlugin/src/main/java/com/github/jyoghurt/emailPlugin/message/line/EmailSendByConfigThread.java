package com.github.jyoghurt.emailPlugin.message.line;

import com.github.jyoghurt.email.domain.EmailMsg;
import com.github.jyoghurt.email.util.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;


public class EmailSendByConfigThread extends Thread {
    Logger logger = LoggerFactory.getLogger(EmailSendByConfigThread.class);
    public static Queue msgQueue = new LinkedList();  //邮件队列

    public void run() {

        while (!this.isInterrupted()) {
            if (!EmailSendByConfigThread.msgQueue.isEmpty()) {
                EmailMsg msg = (EmailMsg) EmailSendByConfigThread.msgQueue.poll();
                try {
                    EmailUtil.sendFileMail(msg);
                } catch (Exception e) {
                    logger.error("Email发送失败！,发送信息内容:{}", msg.toString(), e);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Email发送线程异常！", e);
            }
        }

    }

}
