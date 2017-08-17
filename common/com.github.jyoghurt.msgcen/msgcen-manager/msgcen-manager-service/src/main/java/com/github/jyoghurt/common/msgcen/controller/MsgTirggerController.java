package com.github.jyoghurt.common.msgcen.controller;

import com.github.jyoghurt.common.msgcen.common.utils.MsgRegularUtil;
import com.github.jyoghurt.common.msgcen.domain.MsgTirggerT;
import com.github.jyoghurt.common.msgcen.common.utils.MsgTriggerRuleParseUtil;
import com.github.jyoghurt.common.msgcen.service.MsgTirggerService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.script.ScriptException;


/**
 * 消息触发器配置控制器
 */
@RestController
@LogContent(module = "消息触发器配置")
@RequestMapping("/msgTirgger")
public class MsgTirggerController extends BaseController {


    /**
     * 消息触发器配置服务类
     */
    @Resource
    private MsgTirggerService msgTirggerService;

    /**
     * 列出消息触发器配置
     */
    @LogContent("查询消息触发器配置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpResultEntity<?> list(MsgTirggerT msgTirggerT, QueryHandle queryHandle) {
        return getSuccessResult(msgTirggerService.getData(msgTirggerT.setDeleteFlag(false), queryHandle.configPage()
                .addOperatorHandle("tirggerSubject", OperatorHandle.operatorType.LIKE,msgTirggerT.getTirggerSubject())
                .addOrderBy("createDateTime",
                "desc")));

    }


    /**
     * 添加消息触发器配置
     */
    @LogContent("添加消息触发器配置")
    @RequestMapping(method = RequestMethod.POST)
    public HttpResultEntity<?> add(@RequestBody MsgTirggerT msgTirggerT) {
        msgTirggerService.save(msgTirggerT);
        return getSuccessResult();
    }

    /**
     * 编辑消息触发器配置
     */
    @LogContent("编辑消息触发器配置")
    @RequestMapping(method = RequestMethod.PUT)
    public HttpResultEntity<?> edit(@RequestBody MsgTirggerT msgTirggerT) {
        msgTirggerService.updateForSelective(msgTirggerT);
        return getSuccessResult();
    }

    /**
     * 删除单个消息触发器配置
     */
    @LogContent("删除消息触发器配置")
    @RequestMapping(value = "/{tirggerCode}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> logicDelete(@PathVariable String tirggerCode) {
        msgTirggerService.logicDelete(tirggerCode);
        return getSuccessResult();
    }

    /**
     * 查询单个消息触发器配置
     */
    @LogContent("查询单个消息触发器配置")
    @RequestMapping(value = "/{tirggerCode}", method = RequestMethod.GET)
    public HttpResultEntity<?> get(@PathVariable String tirggerCode) {
        return getSuccessResult(msgTirggerService.find(tirggerCode));
    }

    /**
     * 查询单个消息触发器配置
     */
    @LogContent("查询单个消息触发器配置")
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public HttpResultEntity<?> test() throws ScriptException {
        MsgTirggerT msgTirggerT = msgTirggerService.find("FIRST");
        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        obj1.put("memberId", 123);
        obj.put("member", obj1);
        MsgTriggerRuleParseUtil.parseTriggerRule(MsgRegularUtil.replaceDoubleContentByJson(msgTirggerT.getTirggerRule(), obj));
        return getSuccessResult();
    }
}
