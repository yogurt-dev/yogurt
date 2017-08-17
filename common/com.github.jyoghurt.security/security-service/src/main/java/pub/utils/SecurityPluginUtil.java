package pub.utils;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityPlugin;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: pub.utils
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-01-11 12:58
 */
public abstract class SecurityPluginUtil implements SecurityPlugin{
    @Override
    public String syncUser(SecurityUserT securityUserT, SecurityUnitT securityUnit, String clientId, String
            clientSecret, SysVarEnum action)   {
        return null;
    }

    @Override
    public String syncUnit(SecurityUnitT securityUnitT, String clientId, String clientSecret, SysVarEnum action)   {
        return null;
    }

    @Override
    public void syncHumFlow(SecurityUserT securityUserT,SysVarEnum operType)  {

    }
}
