package pub.utils;


//import com.df.security.service.security.domain.SecurityRole;
//import com.df.security.service.security.domain.SecurityManager;

import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pub.enums.FetchManagerTypeEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.motorInsurance.pub.utils
 * @Description: Session工具类
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2015-09-02 15:00
 */
public final class SessionUtils {

    protected static final Logger logger = LoggerFactory.getLogger(SessionUtils.class);

    public static final String SESSION_MANAGER = "session_manager";//管理员
    public static final String SESSION_MENU = "session_menu";//菜单
    private static final String SESSION_ROLE = "session_role";//角色
    private static final String SESSION_RESOURCE = "session_resource";//资源编码

    /**
     * 设置session的值
     *
     * @param key   键
     * @param value 值
     */
    public static void setAttr(String key, Object value) {
        getHttpServletRequest().getSession(true).setAttribute(key, value);
    }


    /**
     * 获取session的值
     *
     * @param request
     * @param key
     */
    public static Object getAttr(HttpServletRequest request, String key) {
        return request.getSession(true).getAttribute(key);
    }

    /**
     * 删除Session值
     *
     * @param request
     * @param key
     */
    public static void removeAttr(HttpServletRequest request, String key) {
        request.getSession(true).removeAttribute(key);
    }

    /**
     * 设置用户信息 到session
     *
     * @param manager 管理员
     */
    public static void setManager(SecurityUserT manager) {
        getHttpServletRequest().getSession(true).setAttribute(SESSION_MANAGER, manager);
        getHttpServletRequest().getSession(true).setAttribute(BaseEntity.OPERATOR_ID, manager.getUserId());
        getHttpServletRequest().getSession(true).setAttribute(BaseEntity.OPERATOR_NAME, manager.getUserName());
    }


    /**
     * 从session中获取用户信息
     *
     * @return SecurityManager
     */
    public static SecurityUserT getManager() {
        return getManager(getHttpServletRequest());
    }

    /**
     * 获取当前登录用户
     * @param typeEnum 类型
     * @return
     */
    public static SecurityUserT getManager(FetchManagerTypeEnum typeEnum) {
        SecurityUserTService securityUserTService = (SecurityUserTService)SpringContextUtils.getBean("securityUserTService");
        switch(typeEnum){
            case SESSION:
                return getManager();
            case DATABASE:
                return securityUserTService.find(getManager().getUserId());
            default:
                return getManager();
        }
    }

    /**
     * 从session中获取用户信息
     *
     * @return SecurityManager
     */
    public static SecurityUserT getManager(HttpServletRequest request) {
        return (SecurityUserT) request.getSession(true).getAttribute(SESSION_MANAGER);
    }

    /**
     * 查询当前资源编码，这里只的是医院编码
     *
     * @param request
     * @return
     */
    public static String getResourceId(HttpServletRequest request) {
        Object obj = request.getSession(true).getAttribute(SESSION_RESOURCE);
        if (null == obj) {
            return StringUtils.EMPTY;
        }
        return (String) obj;
    }

    /**
     * 设置资源编码
     *
     * @param request
     * @param resourceId
     * @return
     */
    public static void setResource(HttpServletRequest request, String resourceId) {
        request.getSession(true).setAttribute(SESSION_RESOURCE, resourceId);
    }

    public static void setResource( String resourceId) {
        setResource(getHttpServletRequest(),resourceId);
    }

    /**
     * 获取request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


}