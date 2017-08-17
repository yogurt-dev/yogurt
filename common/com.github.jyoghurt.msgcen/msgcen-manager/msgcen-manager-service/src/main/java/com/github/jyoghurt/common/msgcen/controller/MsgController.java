package com.github.jyoghurt.common.msgcen.controller;

import com.github.jyoghurt.common.msgcen.domain.MsgT;
import com.github.jyoghurt.common.msgcen.service.MsgService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 消息发送对象控制器
 */
@RestController
@LogContent(module = "消息发送对象")
@RequestMapping("/msg")
public class MsgController extends BaseController {


    /**
     * 消息发送对象服务类
     */
    @Resource
    private MsgService msgService;

    /**
     * 列出消息发送对象
     */
    @LogContent("查询消息发送对象")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpResultEntity<?> list(MsgT msgT, QueryHandle queryHandle) {
        return getSuccessResult(msgService.getData(msgT.setDeleteFlag(false), queryHandle.configPage().search().addOrderBy("createDateTime",
                "desc")));

    }


    /**
     * 添加消息发送对象
     */
    @LogContent("添加消息发送对象")
    @RequestMapping(method = RequestMethod.POST)
    public HttpResultEntity<?> add(@RequestBody MsgT msgT) {
        msgService.save(msgT);
        return getSuccessResult();
    }

    /**
     * 编辑消息发送对象
     */
    @LogContent("编辑消息发送对象")
    @RequestMapping(method = RequestMethod.PUT)
    public HttpResultEntity<?> edit(@RequestBody MsgT msgT) {
        msgService.updateForSelective(msgT);
        return getSuccessResult();
    }

    /**
     * 删除单个消息发送对象
     */
    @LogContent("删除消息发送对象")
    @RequestMapping(value = "/{msgId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> logicDelete(@PathVariable String msgId) {
        msgService.logicDelete(msgId);
        return getSuccessResult();
    }

    /**
     * 查询单个消息发送对象
     */
    @LogContent("查询单个消息发送对象")
    @RequestMapping(value = "/{msgId}", method = RequestMethod.GET)
    public HttpResultEntity<?> get(@PathVariable String msgId) {
        return getSuccessResult(msgService.find(msgId));
    }
}
