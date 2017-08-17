package com.github.jyoghurt.common.msgcen.controller;

import com.github.jyoghurt.common.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.common.msgcen.service.MsgTmplService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.QueryResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 消息模板配置控制器
 */
@RestController
@LogContent(module = "消息模板配置")
@RequestMapping("/msgTmpl")
public class MsgTmplController extends BaseController {


    /**
     * 消息模板配置服务类
     */
    @Resource
    private MsgTmplService msgTmplService;

    /**
     * 列出消息模板配置
     */
    @LogContent("查询消息模板配置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpResultEntity<?> list(MsgTmplT msgTmplT, QueryHandle queryHandle) {
        QueryResult<MsgTmplT> queryResult = msgTmplService.getData(msgTmplT.setDeleteFlag(false), queryHandle
                .configPage()
                .addOperatorHandle("tmplSubject", OperatorHandle.operatorType.LIKE,msgTmplT.getTmplSubject())
                .addOrderBy("createDateTime", "desc"));
 /*       for (MsgTmplT msgTmpl : queryResult.getData()) {
            msgTmpl.setTmplStatusValue(msgTmpl.getTmplStatus() ? "是" : "否");
            msgTmpl.setTmplTypeValue(msgTmpl.getTmplType().getTypeValue());
        }*/
        return getSuccessResult(queryResult);

    }


    /**
     * 添加消息模板配置
     */
    @LogContent("添加消息模板配置")
    @RequestMapping(method = RequestMethod.POST)
    public HttpResultEntity<?> add(@RequestBody MsgTmplT msgTmplT) {
        msgTmplService.save(msgTmplT);
        return getSuccessResult();
    }

    /**
     * 编辑消息模板配置
     */
    @LogContent("编辑消息模板配置")
    @RequestMapping(method = RequestMethod.PUT)
    public HttpResultEntity<?> edit(@RequestBody MsgTmplT msgTmplT) {
        msgTmplService.updateForSelective(msgTmplT);
        return getSuccessResult();
    }

    /**
     * 删除单个消息模板配置
     */
    @LogContent("删除消息模板配置")
    @RequestMapping(value = "/{prikey}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> logicDelete(@PathVariable String prikey) {
        msgTmplService.logicDelete(prikey);
        return getSuccessResult();
    }

    /**
     * 查询单个消息模板配置
     */
    @LogContent("查询单个消息模板配置")
    @RequestMapping(value = "/{prikey}", method = RequestMethod.GET)
    public HttpResultEntity<?> get(@PathVariable String prikey) {
        return getSuccessResult(msgTmplService.find(prikey));
    }
}
