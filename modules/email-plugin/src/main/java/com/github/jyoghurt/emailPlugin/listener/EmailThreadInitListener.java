package com.github.jyoghurt.emailPlugin.listener;

import com.github.jyoghurt.emailPlugin.message.line.EmailSendByConfigThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class EmailThreadInitListener implements ServletContextListener {
    private EmailSendByConfigThread emailSendByConfigThread;

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        if (emailSendByConfigThread != null && emailSendByConfigThread.isInterrupted()) {
            emailSendByConfigThread.interrupt();
        }

    }

    @Override
    public void contextInitialized(ServletContextEvent e) {
        if (emailSendByConfigThread == null) {
            emailSendByConfigThread = new EmailSendByConfigThread();
            emailSendByConfigThread.start();
        }
    }

}
