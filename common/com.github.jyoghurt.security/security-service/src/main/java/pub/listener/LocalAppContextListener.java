package pub.listener;

import com.github.jyoghurt.security.onlineManage.service.SecurityOnlineTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: pub.listener
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-02-05 14:46
 */
@WebListener
public class LocalAppContextListener implements ServletContextListener {

    Logger logger = LoggerFactory.getLogger(LocalAppContextListener.class);

    private SecurityOnlineTService securityOnlineTService;



    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
