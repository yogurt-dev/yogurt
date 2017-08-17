package com.github.jyoghurt.security.securityUserT.controller;

import com.github.jyoghurt.security.annotations.AbnormalLogContent;
import com.github.jyoghurt.security.annotations.IgnoreAuthentication;
import com.github.jyoghurt.security.exception.SecurityException;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserResourceR.domain.SecurityUserResourceR;
import com.github.jyoghurt.security.securityUserResourceR.service.SecurityUserResourceService;
import com.github.jyoghurt.security.securityUserRoleR.domain.SecurityUserRoleR;
import com.github.jyoghurt.security.securityUserRoleR.service.SecurityUserRoleRService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.handler.WechatHandler;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.QueryResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;
import pub.utils.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用户控制器
 */
@RestController
@LogContent(module = "系统管理")
@RequestMapping("/service/securityUserT")
public class SecurityUserTController extends BaseController implements ApplicationContextAware {
    /*========================================================资源引入==================================================================*/
    @Resource
    private SecurityUserTService securityUserTService;

    @Resource
    private SecurityUnitTService securityUnitTService;

    @Resource
    private SecurityUserRoleRService securityUserRoleRService;

    @Resource
    private SecurityUserResourceService securityUserResourceService;

    @Resource
    private HttpSession session;

    private ApplicationContext applicationContext;

    @Value("${devMode}")
    private String devMode;


	/*========================================================用户管理业务控制器==================================================================*/


    /**
     * 新增用户
     *
     * @param securityUserT 用户模型
     * @return
     * @
     */

    @LogContent("新增用户")
    @RequestMapping(value = "/addXD", method = RequestMethod.POST)
    public HttpResultEntity<?> addXD(@RequestBody SecurityUserT securityUserT)   {
        return getSuccessResult(securityUserTService.addXD(securityUserT));
    }


    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return
     * @
     */
    @LogContent("删除用户")
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delete(@PathVariable String userId)   {
        securityUserTService.delete(userId);
        return getSuccessResult();
    }

    /**
     * 更新用户的头像信息
     *
     * @return
     * @
     */
    @LogContent("更新用户的头像信息")
    @RequestMapping(value = "/updateUserImg", method = RequestMethod.PUT)
    public HttpResultEntity<?> updateUserImg(@RequestBody SecurityUserT securityUserT)   {
        securityUserTService.updateForSelective(securityUserT);
        return getSuccessResult();
    }

    /**
     * 删除用户
     *
     * @param email
     * @return
     * @
     */
    @LogContent("删除用户")
    @RequestMapping(value = "/checkEmail", method = RequestMethod.POST)
    public HttpResultEntity<?> checkEmail(@RequestBody String email)   {

        SecurityUserT securityUserT = new SecurityUserT();

        String[] args = email.split("splitChar");
        String userId = "";
        if (args.length > 1) {
            userId = args[1];
        }

        SecurityUserT securityUserT1 = securityUserTService.find(userId);

        email = args[0];

        email = email.substring(email.indexOf("=") + 1, email.length());

        if (email.contains("%40")) {
            String[] che = email.split("%40");
            email = che[0].concat("@").concat(che[1]);
        }
        securityUserT.setEmailAddr(email);
        List<SecurityUserT> securityUserTs = securityUserTService.findAll(securityUserT, new QueryHandle());
        if (securityUserT1 != null) {
            if (CollectionUtils.isNotEmpty(securityUserTs) && !email.equals(securityUserT1.getEmailAddr())) {
                return getSuccessResult(1);
            }
        } else {
            if (CollectionUtils.isNotEmpty(securityUserTs)) {
                return getSuccessResult(1);
            }
        }
        return getSuccessResult(0);
    }


    /**
     * 编辑用户
     *
     * @param userId  用户Id
     * @param roleIds 角色数组
     * @return
     * @
     */
    @LogContent("编辑用户")
    @RequestMapping(value = "/editUserRole/{userId}/{roleIds}", method = RequestMethod.POST)
    @ResponseBody
    public HttpResultEntity<?> editUserRole(@PathVariable String userId, @PathVariable String roleIds)   {
        //刪除当前用户与资源的所有关系
        securityUserRoleRService.deleteRelByUserId(userId);
        //添加当前用户与角色修改后的关系
        String[] roleId = roleIds.split(",");
        for (int i = 0; i < roleId.length; i++) {
            SecurityUserRoleR userRoleR = new SecurityUserRoleR();
            userRoleR.setUserId(userId);
            userRoleR.setRoleId(roleId[i]);
            securityUserRoleRService.save(userRoleR);
        }
        return getSuccessResult();
    }

    /**
     * 检查当前登录用户是否有门店资源
     * @return
     */
    @LogContent("检查当前登录用户是否有门店资源")
    @RequestMapping(value = "/checkLoginUserHasStoreResource/{resourceType}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> checkLoginUserHasStoreResource(@PathVariable String resourceType) {
        List<String> resources = securityUserResourceService.fetchResourcesStr(SessionUtils.getManager().getUserId(),
                resourceType);
        return getSuccessResult(resources.size());
    }


    /**
     * 编辑用户
     *
     * @param securityUserT 用户模型
     * @return
     * @
     */
    @LogContent("编辑用户")
    @RequestMapping(value = "/editXD", method = RequestMethod.POST)
    public HttpResultEntity<?> editXD(@RequestBody SecurityUserT securityUserT)   {
        return getSuccessResult(securityUserTService.editXD(securityUserT));
    }

    /**
     * 编辑用户
     *
     * @param userIdN 用户ID userAccount 用户账户 passwd 密码
     * @return
     * @
     */
    @LogContent("更新账户信息")
    @RequestMapping(value = "/updateAccountInfo/{userIdN}/{userAccount}/{passwd}", method = RequestMethod.POST)
    @ResponseBody
    public HttpResultEntity<?> updateAccountInfo(@PathVariable String userIdN, @PathVariable String userAccount,
                                                 @PathVariable String
                                                         passwd)
              {
        securityUserTService.updateAccountInfoAndTencentSync(userIdN,userAccount,passwd);
        return getSuccessResult(userIdN);
    }


    /**
     * 修改密码业务-检查用户输入的密码是否正确
     *
     * @param opasswd 用户输入的密码
     * @return
     * @
     */
    @LogContent("检查用户输入的密码是否正确")
    @RequestMapping(value = "/opasswdCheck/{opasswd}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> checkPasswdIsCorrect(@PathVariable String opasswd)   {
        SecurityUserT user = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        user.setPasswd(Encrypter.MD5(opasswd));
        List<SecurityUserT> users = securityUserTService.findAll(new SecurityUserT().setUserAccount(user
                .getUserAccount()).setPasswd( Encrypter.MD5(opasswd)));
        if (CollectionUtils.isNotEmpty(users)) {
            return getSuccessResult();
        }
        return getSuccessResult("-2");
    }


    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return
     * @
     */
    @LogContent("查询用户")
    @RequestMapping(value = "/getUserById/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> queryUserById(@PathVariable String userId)   {
        return getSuccessResult(securityUserTService.queryUserById(userId));
    }


    /**
     * 用户账户重复校验
     *
     * @param account
     * @return
     * @
     */
    @LogContent("用户账户重复校验")
    @RequestMapping(value = "/userMulCheck/{account}", method = RequestMethod.GET)
    public HttpResultEntity<?> queryUserByAccount(@PathVariable String account)   {
        return getSuccessResult(securityUserTService.queryUserByAccount(account));
    }

//    /**
//     * 用户账户重复校验
//     *
//     * @param unitId
//     * @return
//     * @
//     */
//    @LogContent("根据单位ID查询下属单位所有人员")
//    @RequestMapping(value = "/usersByComp/{unitId}", method = RequestMethod.GET)
//    public HttpResultEntity<?> queryContentUserByCompId(@PathVariable String unitId)   {
//        return getSuccessResult(securityUserTService.queryContentUserByCompId(unitId));
//    }

    /**
     * 查询所有公司除了 当前登录用户所属公司
     *
     * @return
     * @
     */
    @LogContent("查询所有公司除了 当前登录用户所属公司")
    @RequestMapping(value = "/companyExcludeCurrentUser", method = RequestMethod.GET)
    public HttpResultEntity<?> companyExcludeCurrentUser()   {
        List<SecurityUnitT> securityUnitTs = securityUnitTService.queryCompanyExcludeCurrentUser((SecurityUserT) session
                .getAttribute(SessionUtils.SESSION_MANAGER));
        return getSuccessResult(securityUnitTs);
    }

    /**
     * 查询用户下包含的资源
     *
     * @return
     * @
     */
    @LogContent("查询用户下包含的资源")
    @RequestMapping(value = "/userResourceByUserId", method = RequestMethod.GET)
    public HttpResultEntity<?> userResourceByUserId(SecurityUserResourceR securityUserResourceR, QueryHandle queryHandle)   {
        QueryResult<SecurityUserResourceR> securityUnitTs = securityUserResourceService.getData(securityUserResourceR,
                queryHandle.configPage().addOrderBy("t.resourceType","desc"));
        return getSuccessResult(securityUnitTs);
    }

    /**
     * 清除用户信息缓存
     */
    @LogContent("清除用户信息缓存")
    @RequestMapping(value = "/cacheEvict", method = RequestMethod.GET)
    public HttpResultEntity<?> cacheEvict() throws Exception {
        securityUserTService.cacheEvict();
        return getSuccessResult();

    }




    /**
     * 生成用户登录校验码，发送邮件
     *
     * @return
     * @
     */
    @LogContent("生成用户登录校验码，发送邮件")
    @RequestMapping(value = "/generateUserLoginVerification", method = RequestMethod.GET)
    @IgnoreAuthentication
    public HttpResultEntity<?> generateUserLoginVerification(String userAccount) throws SecurityException {
        securityUserTService.generateUserLoginVerification(userAccount);
        return getSuccessResult();
    }


    /**
     * 获取当前登录用户
     *
     * @return
     * @
     */
    @LogContent("获取当前登录用户")
    @RequestMapping(value = "/getLoginUser", method = RequestMethod.GET)
    @IgnoreAuthentication
    public HttpResultEntity<?> getLoginUser() throws SecurityException {
        SecurityUserT loginUser = SessionUtils.getManager();
        return getSuccessResult(loginUser);
    }



    /**
     * 系统账户与微信账户绑定
     *
     * @return
     * @
     */
    @LogContent("系统账户与微信账户绑定")
    @RequestMapping(value = "/boundWechat", method = RequestMethod.GET)
    @IgnoreAuthentication
    public void boundWechat(String code,String state) throws IOException {
        try {
            securityUserTService.boundWechat(code,state);
            WechatHandler.gotoPageAndAlert(request,response,"/index.html","微信绑定成功");
        } catch (SecurityException e) {
            WechatHandler.gotoPageAndAlert(request,response,"/login.html",e.getMessage());
        }

    }

    @LogContent("微信登录")
    @RequestMapping(value = "/wechatLogin", method = RequestMethod.GET)
    @IgnoreAuthentication
    public void wechatLogin(String code,String state) throws IOException {
        try {
            securityUserTService.wechatLogin(code,state);
            WechatHandler.gotoPage(request,response,"/index.html");
        } catch (SecurityException e) {
            logger.info("微信平台重复回调",e);
            WechatHandler.gotoPageAndAlert(request,response,"/login.html",e.getMessage());
        }
    }

	/*==================================================================================登录业务==============================================================================*/

    /**
     * 用户登录
     */
    @IgnoreAuthentication
    @LogContent("用户登录校验")
    @AbnormalLogContent("用户登录")
    @RequestMapping(value = "/login/{userAccount}/{passwd}/{verificationCode}", method = RequestMethod.GET)
    public HttpResultEntity<?> login(@PathVariable String userAccount, @PathVariable String passwd,@PathVariable
                                     String verificationCode)   {
        try {
            return getSuccessResult(securityUserTService.login(userAccount,passwd,verificationCode,devMode));
        } catch (SecurityException e) {
            logger.warn(e.getMessage());
            return getErrorResult();
        }
    }

    /**
     * 用户注销
     *
     * @return
     * @
     */
    @LogContent("用户注销")
    @IgnoreAuthentication
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> logout()   {
        //清除用户信息
        session.removeAttribute(SessionUtils.SESSION_MANAGER);
        //清除用户信息
        session.removeAttribute(SessionUtils.SESSION_MENU);
        session.removeAttribute(BaseEntity.OPERATOR_ID);
        session.removeAttribute(BaseEntity.OPERATOR_NAME);
        return getSuccessResult();
    }

    /**
     * 获得系统时间
     *
     * @return
     * @
     */
    @IgnoreAuthentication
    @RequestMapping(value = "/getSysDateTime", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> getSysDateTime()   {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date(System.currentTimeMillis());
        String time = sdf.format(now);
        return getSuccessResult(time);
    }


    /**
     * 获取session中的用户信息
     *
     * @return
     * @
     */
    @LogContent("查询用户信息")
    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public HttpResultEntity<?> getUser()   {
        return getSuccessResult(SessionUtils.getManager());
    }


    /**
     * 修改密码业务-保存密码
     *
     * @param npasswd 修改有的密码
     * @return
     * @
     */
    @LogContent("修改密码")
    @RequestMapping(value = "/pwdModify/{npasswd}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> modifyPassword(@PathVariable String npasswd)   {
        SecurityUserT user = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        user.setPasswd(Encrypter.MD5(npasswd));
        securityUserTService.update(user);
        return getSuccessResult();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
