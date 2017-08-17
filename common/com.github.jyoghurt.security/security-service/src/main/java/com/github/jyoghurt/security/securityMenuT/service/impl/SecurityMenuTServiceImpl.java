package com.github.jyoghurt.security.securityMenuT.service.impl;

import com.github.jyoghurt.security.securityMenuT.dao.SecurityMenuTMapper;
import com.github.jyoghurt.security.securityMenuT.domain.SecurityMenuT;
import com.github.jyoghurt.security.securityMenuT.service.SecurityMenuTService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.utils.SessionUtils;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("securityMenuTService")
public class SecurityMenuTServiceImpl extends ServiceSupport<SecurityMenuT, SecurityMenuTMapper> implements SecurityMenuTService {
    @Autowired
    private SecurityMenuTMapper securityMenuTMapper;

    @Override
    public SecurityMenuTMapper getMapper() {
        return securityMenuTMapper;
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecurityMenuT.class, id);
    }

    @Override
    public SecurityMenuT find(Serializable id)  {
        return getMapper().selectById(SecurityMenuT.class, id);
    }

    @Override
    public Map<String, List<SecurityMenuT>> queryMenuByRoleId(String pId, String roleId)  {
        return menuListAnalize(getMapper().queryMenuByRoleId(pId, roleId));
    }

    @Override
    public Map<String, List<SecurityMenuT>> queryMenuByRoleIdAdmin(String roleId)  {
        return menuListAnalize(getMapper().queryMenuByRoleIdAdmin(roleId));
    }

    @Override
    public Map<String, List<SecurityMenuT>> getUserMenu(HttpSession session) {
        //1、定义容器
        Map<String,List<SecurityMenuT>> result = new HashMap<>();
        //2、获取登录人包含的左侧菜单，放入容器
        List<SecurityMenuT> userMenu = (List)session.getAttribute(SessionUtils.SESSION_MENU);
        result.put("userMenu",userMenu);
        //3、获取提醒菜单返回
        List<SecurityMenuT> gTask = new ArrayList();
        if(!CollectionUtils.isEmpty(userMenu)){
            for(SecurityMenuT securityMenuT:userMenu){
                if("0".equals(securityMenuT.getIsGtask())){
                    gTask.add(securityMenuT);
                }
            }
        }
        result.put("gTask",gTask);
        return result;
    }

    /**
     * 菜单分炼
     *
     * @param securityMenuTs
     * @return
     * @
     */
    private Map<String, List<SecurityMenuT>> menuListAnalize(List<SecurityMenuT> securityMenuTs) {
        Map<String, List<SecurityMenuT>> menus = new HashMap();
        if (CollectionUtils.isNotEmpty(securityMenuTs)) {
            List<SecurityMenuT> selectedMenu = new ArrayList();
            //所有菜单
            menus.put("all", securityMenuTs);
            for (SecurityMenuT m : securityMenuTs) {
                if (m.getRoleId() != null) {
                    selectedMenu.add(m);
                }
            }
            menus.put("selected", selectedMenu);
        }
        return menus;
    }

}
