package pub.utils;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityPlugin;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: pub.utils
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2015-11-19 13:38
 */
@Component
public class SecurityCustomUtils {

    /**
     * 执行同步组织机构动作
     *
     * @param securityUnitT
     * @param action        操作标识
     * @
     */
    public static void syncUnit(SecurityUnitT securityUnitT, List<SecurityUnitT>
            allUnit, SysVarEnum action) {
        Map<String, SecurityPlugin> impls = SpringContextUtils.getBeans(SecurityPlugin.class);
        for (String key : impls.keySet()) {
            SecurityPlugin q = impls.get(key);
            Map<String, String> txInfo = getTXInfo(securityUnitT, allUnit);
            q.syncUnit(securityUnitT, txInfo.get("CLIENTID"), txInfo.get("CLIENTSECRET"), action);
        }
    }

    /**
     * 同步人员信息
     *
     * @param securityUserT
     * @param securityUnit
     * @param securityUnitTs
     * @param action
     * @
     */
    private static void syncUser(SecurityUserT securityUserT,
                                 SecurityUnitT
                                         securityUnit, List<SecurityUnitT>
                                         securityUnitTs, SysVarEnum action) {
        Map<String, SecurityPlugin> impls = SpringContextUtils.getBeans(SecurityPlugin.class);
        for (String key : impls.keySet()) {
            SecurityPlugin q = impls.get(key);
            Map<String, String> txInfo = getTXInfo(securityUnit, securityUnitTs);
            try {
                q.syncUser(securityUserT, securityUnit, txInfo.get("CLIENTID"), txInfo.get("CLIENTSECRET"), action);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 同步企业邮箱
     *
     * @param action
     */
    public static void syncTencentUser(SecurityUserT userInfo, SysVarEnum action) {

        SecurityUserTService securityUserTService = (SecurityUserTService) SpringContextUtils.getBean("securityUserTService");
        SecurityUnitTService securityUnitTService = (SecurityUnitTService) SpringContextUtils.getBean("securityUnitTService");

        SecurityUserT securityUserT = new SecurityUserT();

        switch (action){
            case ADD_OPPERTYPE:
                securityUserT = userInfo;
                break;
            case MODIFY_OPPERTYPE:
                securityUserT = userInfo;
                break;
            case DELETE_OPPERTYPE:
                securityUserT = securityUserTService.find(userInfo.getUserId());
                break;
        }

        SecurityUnitT securityUnitT = securityUnitTService.find(securityUserT.getBelongOrg());


        if (!StringUtils.isEmpty(securityUnitT.getCompType())) {
            try {
                SecurityCustomUtils.syncUser(securityUserT, securityUnitT, securityUnitTService.findAll(new
                        SecurityUnitT()), action);
                //数据更新成功，同步企业邮箱成功
                securityUserT.setTxstate("0");
            } catch (Exception e) {
                //腾讯企业邮箱失败，本地数据库更新成功
                securityUserT.setTxstate("1");
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取腾讯企业邮箱信息
     *
     * @param securityUnitT
     * @return
     */
    private static Map<String, String> getTXInfo(SecurityUnitT securityUnitT, List<SecurityUnitT> securityUnitTs) {
        Map<String, String> info = new HashMap();
        if (securityUnitT.getCompType().equals(SysVarEnum.DEPARTMENT_COMPTYPE.getCode())) {
            SecurityUnitT securityUnitT1 = Encrypter.getFathCompanyInfo(securityUnitTs, securityUnitT);
            info.put("CLIENTID", securityUnitT1.getClientId());
            info.put("CLIENTSECRET", securityUnitT1.getClientSecret());
        }

        if (securityUnitT.getCompType().equals(SysVarEnum.COMPANY_COMPTYPE.getCode())) {
            info.put("CLIENTID", securityUnitT.getClientId());
            info.put("CLIENTSECRET", securityUnitT.getClientSecret());
        }
        return info;
    }


}
