package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.domain.WeChatMpnewsMsgT;
import com.github.jyoghurt.weChat.domain.WeChatMsgT;
import com.github.jyoghurt.weChat.service.WeChatMpnewsMsgTService;
import com.github.jyoghurt.weChat.service.WeChatMsgTService;
import com.github.jyoghurt.wechatbasic.common.pojo.AccessToken;
import com.github.jyoghurt.wechatbasic.common.pojo.MaterialNewsMap;
import com.github.jyoghurt.wechatbasic.common.util.AdvancedUtil;
import com.github.jyoghurt.wechatbasic.common.util.DateUtil;
import com.github.jyoghurt.wechatbasic.common.util.WeiXinConstants;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.handle.SQLJoinHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.github.jyoghurt.wechatbasic.common.util.WeixinUtil.getAccessToken;


/**
 * 微信消息记录表控制器
 */
@RestController
@LogContent("微信公众平台管理")
@RequestMapping("/weChatMsgT")
public class WeChatMsgTController extends BaseController {


    /**
     * 微信消息记录表服务类
     */
    @Resource
    private WeChatMsgTService weChatMsgTService;
    /**
     * 微信消息记录表服务类
     */
    @Resource
    private WeChatMpnewsMsgTService weChatMpnewsMsgTService;
    @Autowired
    private SecurityUnitTService securityUnitTService;

    //
//	/**
//	 * 列出微信消息记录表
////	 */
//    @LogContent("查询微信消息记录表")
//    @RequestMapping(value = "/list/{atype}", method = RequestMethod.GET)
//    public HttpResultEntity<?> list(WeChatMsgT weChatMsgT, QueryHandle queryHandle, @PathVariable String atype)
//              {
//        String sql = "";
//        SecurityUserT usert = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
//        //订阅号
//        if ((WeChatAccountType.TYPE_SUBSCRIPTION).toString().equals(atype)) {
//            sql = " and accountType='" + WeChatAccountType.TYPE_SUBSCRIPTION + "' and unitId='" + usert
//                    .getBelongOrg() + "'";
//        } else if ((WeChatAccountType.TYPE_SERVICE).toString().equals(atype)) {
//            sql = " and accountType='" + WeChatAccountType.TYPE_SERVICE + "' and unitId='" + usert.getBelongCompany().getUnitId() + "'";
//        } else if ((WeChatAccountType.TYPE_ENTERPRISE).toString().equals(atype)) {
//            sql = " and accountType='" + WeChatAccountType.TYPE_ENTERPRISE + "' and unitId='" + usert.getBelongCompany().getUnitId() + "'";
//        }
//
//        return getSuccessResult(weChatMsgTService.getData(weChatMsgT, queryHandle.configPage()
//                .addWhereSql("msgtype='text'" + sql)
//                .addOrderBy("createDateTime", "desc")));
//
//    }

    //
//
//	/**
//	 * 添加微信消息记录表
//	 */
//	@LogContent("添加微信消息记录表")
//	@RequestMapping(method=RequestMethod.POST)
//	public HttpResultEntity<?> add(WeChatMsgT weChatMsgT)   {
//		weChatMsgTService.save(weChatMsgT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑微信消息记录表
//	 */
    @LogContent("编辑微信消息记录表")
    @RequestMapping(value = "/{ifsend}", method = RequestMethod.PUT)
    public HttpResultEntity<?> edit(@RequestBody WeChatMsgT weChatMsgT, @PathVariable String ifsend)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        weChatMsgT.setUnitId(userT.getBelongCompany().getUnitId());
        weChatMsgTService.updateForSelective(weChatMsgT);
        if ("send".equals(ifsend)) {
            userT.getBelongCompany().getUnitId();
            weChatMsgTService.sendWeChatText(weChatMsgT, userT.getBelongCompany().getUnitId());
        }
        return getSuccessResult();
    }

//

    /**
     * 删除单个微信消息记录表
     */
    @LogContent("删除微信消息记录表")
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delete(@PathVariable String messageId)   {
        weChatMsgTService.delete(messageId);
        return getSuccessResult();
    }

    /**
     * 删除单个微信消息记录表
     */
    @LogContent("删除微信消息记录表")
    @RequestMapping(value = "/deleteNew/{messageId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> deleteNew(@PathVariable String messageId)   {
        weChatMsgTService.delete(messageId);
        weChatMpnewsMsgTService.deleteByMessageId(messageId);
        return getSuccessResult();
    }
//

    /**
     * 查询单个微信消息记录表
     */
    @LogContent("查询单个微信消息记录表")
    @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
    public HttpResultEntity<?> get(@PathVariable String messageId)   {
        return getSuccessResult(weChatMsgTService.find(messageId));
    }

    @LogContent("添加微信文本消息记录表")
    @RequestMapping(value = "/addTextMsg/{ifsend}/{ty}/{textContent}", method = RequestMethod.POST)
    public HttpResultEntity<?> addTextMsg(@PathVariable String textContent, @PathVariable String ifsend, @PathVariable
            String ty)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        WeChatMsgT weChatMsgT = new WeChatMsgT();
        weChatMsgT.setUnitId(userT.getBelongCompany().getUnitId());
        weChatMsgT.setAccountType(WeChatAccountType.TYPE_SUBSCRIPTION);
        weChatMsgT.setMsgtype("text");
        weChatMsgT.setState("0");
        weChatMsgT.setTextContent(textContent);
        weChatMsgTService.add(weChatMsgT);
        if ("send".equals(ifsend)) {
            userT.getBelongCompany().getUnitId();
            weChatMsgTService.sendWeChatText(weChatMsgT, userT.getBelongCompany().getUnitId());
        }
        return getSuccessResult();

    }

    /*
* 保存微信消息接口
* */
    @LogContent("添加微信图文消息记录表")
    @RequestMapping(value = "/addMpNews/{ifsend}/{ty}", method = RequestMethod.POST)
    public HttpResultEntity<?> addMpNews(@RequestBody WeChatMsgT weChatMsgT, @PathVariable String ifsend, @PathVariable String ty)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        weChatMsgT.setAccountType(WeChatAccountType.TYPE_SUBSCRIPTION);
        weChatMsgT.setState("0");
        weChatMsgT.setUnitId(userT.getBelongCompany().getUnitId());
        weChatMsgTService.addMpNews(weChatMsgT);
        if ("send".equals(ifsend)) {
            userT.getBelongCompany().getUnitId();
            weChatMsgTService.sendWeChat(weChatMsgT, userT.getBelongCompany().getUnitId());

        }
        return getSuccessResult();
    }


    /**
     * 编辑图文消息
     */
    @LogContent("编辑微信图文消息记录表")
    @RequestMapping(value = "/editMpnews/{ifsend}", method = RequestMethod.PUT)
    public HttpResultEntity<?> editMpnews(@RequestBody WeChatMsgT weChatMsgT, @PathVariable String ifsend)   {
        //前台传递了父对象messageId下的子对象 所以需根据父对象messageId自己查询一次父对象 根据前台传递的子对象封装后 均用自己查询后的父对象操作
        WeChatMsgT weChatMsgTEdit = weChatMsgTService.find(weChatMsgT.getMessageId());
        weChatMsgTEdit.setList(weChatMsgT.getList());
        List<WeChatMpnewsMsgT> list = weChatMsgTEdit.getList();
        for (int i = 0; i < list.size(); i++) {
            if ("".equals(list.get(i).getMpnewsId())) {
                WeChatMpnewsMsgT weChatMpnewsMsgT1 = new WeChatMpnewsMsgT();
                weChatMpnewsMsgT1.setMessageId(weChatMsgTEdit.getMessageId());
                weChatMpnewsMsgT1.setNewsTitle(list.get(i).getNewsTitle());
                weChatMpnewsMsgT1.setContent(list.get(i).getContent());
                weChatMpnewsMsgT1.setThumUrl(list.get(i).getThumUrl());
                weChatMpnewsMsgT1.setShowCoverPic(list.get(i).getShowCoverPic());
                weChatMpnewsMsgTService.save(weChatMpnewsMsgT1);
            } else {
                weChatMpnewsMsgTService.updateForSelective(list.get(i));
            }
        }
        if ("send".equals(ifsend)) {
            SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
            userT.getBelongCompany().getUnitId();
            weChatMsgTService.sendWeChat(weChatMsgTEdit, userT.getBelongCompany().getUnitId());

        }
        return getSuccessResult();
    }


    @LogContent("查询消息记录表")
    @RequestMapping(value = "/getNewsList/{atype}", method = RequestMethod.GET)
    public HttpResultEntity<?> getNewsList(WeChatMsgT weChatMsgT, QueryHandle
            queryHandle, @PathVariable WeChatAccountType weChatAccountType)
              {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        if (null != weChatMsgT.getTitle() && !"".equals(weChatMsgT.getTitle())) {//查询条件中填写了标题查询
            WeChatMpnewsMsgT weChatMpnewsMsgT = new WeChatMpnewsMsgT();
            weChatMpnewsMsgT.setNewsTitle(weChatMsgT.getTitle());
            weChatMsgT.setTitle(null);
        }
        String sql = "";
        switch (weChatAccountType) {
            case TYPE_SUBSCRIPTION:
                sql = " and t.accountType='TYPE_SUBSCRIPTION' ";
                break;
            case TYPE_SERVICE:
                sql = " and t.accountType='TYPE_SERVICE' ";
                break;
            case TYPE_ENTERPRISE:
                sql = " and t.accountType='TYPE_ENTERPRISE' ";
                break;
            default:
                sql = "";
        }
        QueryResult<WeChatMsgT> queryResult = weChatMsgTService.getData(weChatMsgT, queryHandle.configPage()
                .addJoinHandle("tab2.*", SQLJoinHandle.JoinType.JOIN, "WeChatMpnewsMsgT tab2 on t.messageId=tab2.messageId WHERE t.unitId ='" + userT.getBelongCompany().getUnitId() + "'" + sql)
                .addOrderBy("createDateTime", "desc").addOrderBy("tab2.sort", "asc")
                .addOperatorHandle("t2.textContent", OperatorHandle.operatorType.LIKE));//模糊查询
        return getSuccessResult(queryResult);
    }

    @LogContent("查询消息记录表")
    @RequestMapping(value = "/getUserInfo/{OPENID}", method = RequestMethod.GET)
    public HttpResultEntity<?> getUserInfo(@PathVariable String OPENID)   {
        // 第三方用户唯一凭证
        String appId = "wx53207d4366d1f9b2";
        // 第三方用户唯一凭证密钥
        String appSecret = "4a2d380ac441cc4ce52bdee5912a7a2e";

        AccessToken token = getAccessToken(appId, appSecret);
        return getSuccessResult(AdvancedUtil.getUserInfo(token.getToken(), OPENID));
    }

    @LogContent("查询微信公告剩余次数")
    @RequestMapping(value = "/remnantTimes/{atype}", method = RequestMethod.GET)
    public HttpResultEntity<?> remnantTimes(WeChatMsgT weChatMsgT, QueryHandle queryHandle, @PathVariable String atype) {
        SecurityUserT usert = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);

            //订阅号
            if ((WeChatAccountType.TYPE_SUBSCRIPTION).toString().equals(atype)) {
                Date date = DateUtil.getTimesmorning();//当天时间0点
                Date datebeginend = DateUtil.getTimesnight();//当天时间24点
                List<WeChatMsgT> list = weChatMsgTService.findAll(weChatMsgT, queryHandle.addWhereSql(""));

            } else if ((WeChatAccountType.TYPE_SERVICE).toString().equals(atype)) { //服务号

            } else if ((WeChatAccountType.TYPE_ENTERPRISE).toString().equals(atype)) {

            } else {

            }

        return getSuccessResult();
    }

    /*
    @LogContent("向微信端发送公告")
	@RequestMapping(value = "/sendWeChat",method=RequestMethod.GET)
	public void sendWeChat(WeChatMsgT weChatMsgT)  {
		weChatMsgTService.sendWeChat(weChatMsgT);
*/
        /*
* 保存微信消息接口
* */

    /**
     * @param queryHandle       分页信息
     * @param weChatAccountType 微信类型 /订阅号/服务号/企业号
     * @return
     * @
     */
    @LogContent("查询图文列表")
    @RequestMapping(value = "/batchgetMaterialNews/{weChatAccountType}", method = RequestMethod.GET)
    public HttpResultEntity<?> batchgetMaterialNews(QueryHandle queryHandle, @PathVariable WeChatAccountType
            weChatAccountType)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        try {
            return getSuccessResult(weChatMsgTService.batchgetMaterialNews(queryHandle.configPage(), weChatAccountType, unitT));
        } catch (WeChatException e) {
            HttpResultEntity<?> httpResultEntity = new HttpResultEntity<>();
            httpResultEntity.setErrorCode("0");
            httpResultEntity.setMessage(WeiXinConstants.weChatErrorMap.get(e.getErrorCode()) == null ? e.getErrorCode
                    () : WeiXinConstants.weChatErrorMap.get(e.getErrorCode()));
            return httpResultEntity;
        }
    }

    /**
     * @param materialNewsMap   图文消息数据
     * @param ifsend            是否群发
     * @param weChatAccountType 微信类型 /订阅号/服务号/企业号
     * @return
     * @
     */
    @LogContent("添加微信图文消息至素材库")
    @RequestMapping(value = "/addMaterialNews/{ifsend}/{weChatAccountType}", method = RequestMethod.POST)
    public HttpResultEntity<?> uploadMaterialNews(@RequestBody MaterialNewsMap materialNewsMap, @PathVariable String ifsend,
                                                  @PathVariable WeChatAccountType weChatAccountType)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        try {
            return getSuccessResult(weChatMsgTService.uploadMaterialNews(materialNewsMap, weChatAccountType, unitT, ifsend));
        } catch (WeChatException e) {
            HttpResultEntity<?> httpResultEntity = new HttpResultEntity<>();
            httpResultEntity.setErrorCode("0");
            httpResultEntity.setMessage(WeiXinConstants.weChatErrorMap.get(e.getErrorCode()) == null ? e.getErrorCode
                    () : WeiXinConstants.weChatErrorMap.get(e.getErrorCode()));
            return httpResultEntity;
        }
    }

    /**
     * @param materialNewsMap   图文消息数据
     * @param ifsend            是否群发
     * @param weChatAccountType 微信类型 /订阅号/服务号/企业号
     * @return
     * @
     */
    @LogContent("修改微信图文消息至素材库")
    @RequestMapping(value = "/updateMaterialNews/{ifsend}/{weChatAccountType}", method = RequestMethod.PUT)
    public HttpResultEntity<?> updateMaterialNews(@RequestBody MaterialNewsMap materialNewsMap, @PathVariable String ifsend,
                                                  @PathVariable WeChatAccountType weChatAccountType)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        try {
            return getSuccessResult(weChatMsgTService.updateMaterialNews(materialNewsMap, weChatAccountType, unitT, ifsend));
        } catch (WeChatException e) {
            HttpResultEntity<?> httpResultEntity = new HttpResultEntity<>();
            httpResultEntity.setErrorCode("0");
            httpResultEntity.setMessage(WeiXinConstants.weChatErrorMap.get(e.getErrorCode()) == null ? e.getErrorCode
                    () : WeiXinConstants.weChatErrorMap.get(e.getErrorCode()));
            return httpResultEntity;
        }
    }

    @LogContent("从素材库删除素材")
    @RequestMapping(value = "/delMaterial/{media_id}/{weChatAccountType}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delMaterial(@PathVariable String media_id, @PathVariable WeChatAccountType weChatAccountType)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        try {
            weChatMsgTService.delMaterial(media_id, weChatAccountType, unitT);
            return getSuccessResult();
        } catch (WeChatException e) {
            HttpResultEntity<?> httpResultEntity = new HttpResultEntity<>();
            httpResultEntity.setErrorCode("0");
            httpResultEntity.setMessage(WeiXinConstants.weChatErrorMap.get(e.getErrorCode()) == null ? e.getErrorCode
                    () : WeiXinConstants.weChatErrorMap.get(e.getErrorCode()));
            return httpResultEntity;
        }
    }

    @LogContent("根据media_id获取素材")
    @RequestMapping(value = "/getMaterial/{media_id}/{weChatAccountType}/{weChatMsgTypeEnum}", method = RequestMethod
            .GET)
    public HttpResultEntity<?> getMaterial(@PathVariable String media_id, @PathVariable
            WeChatAccountType weChatAccountType, @PathVariable WeChatMsgTypeEnum weChatMsgTypeEnum)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        try {
            return weChatMsgTService.getMaterial(media_id, weChatAccountType, weChatMsgTypeEnum, unitT);
        } catch (WeChatException e) {
            HttpResultEntity<?> httpResultEntity = new HttpResultEntity<>();
            httpResultEntity.setErrorCode("0");
            httpResultEntity.setMessage(WeiXinConstants.weChatErrorMap.get(e.getErrorCode()) == null ? e.getErrorCode
                    () : WeiXinConstants.weChatErrorMap.get(e.getErrorCode()));
            return httpResultEntity;
        }
    }

    /**
     * @param towxname          接收人微信账号
     * @param media_id          当前图文消息的素材库Id
     * @param weChatAccountType 微信类型 /订阅号/服务号/企业号
     * @return
     * @
     */
    @LogContent("图文消息预览")
    @RequestMapping(value = "/preView/{towxname}/{media_id}/{weChatAccountType}", method = RequestMethod.POST)
    public HttpResultEntity<?> preView(@PathVariable String towxname, @PathVariable String media_id, @PathVariable WeChatAccountType weChatAccountType)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        try {
            weChatMsgTService.preView(towxname, media_id, weChatAccountType, unitT);
            return getSuccessResult();
        } catch (WeChatException e) {
            HttpResultEntity<?> httpResultEntity = new HttpResultEntity<>();
            httpResultEntity.setErrorCode("0");
            httpResultEntity.setMessage(WeiXinConstants.weChatErrorMap.get(e.getErrorCode()) == null ? e.getErrorCode
                    () : WeiXinConstants.weChatErrorMap.get(e.getErrorCode()));
            return httpResultEntity;
        }
    }

}
