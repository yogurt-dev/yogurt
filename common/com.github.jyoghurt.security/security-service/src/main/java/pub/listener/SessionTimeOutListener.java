package pub.listener;

import com.github.jyoghurt.security.securityScoketBean.SecurityScoketBean;
import com.github.jyoghurt.core.utils.SpringContextUtils;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by zhangjl on 2015/12/21.
 */

public class SessionTimeOutListener implements HttpSessionListener {
    public void sessionCreated(HttpSessionEvent se) {
        se.getSession();
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        SecurityScoketBean securityScoketBean= (SecurityScoketBean)SpringContextUtils.getBean("securityScoketBean");
        /*session超时后监听落锁*/
        securityScoketBean.sendSessionTimeOutMsg(se.getSession().getId());
    }
}
