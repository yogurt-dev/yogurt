package com.github.jyoghurt.activiti.compontent.audit.controller;


import com.github.jyoghurt.activiti.business.exception.RepetitionSubmitException;
import com.github.jyoghurt.activiti.compontent.audit.domain.WorkflowAudit;
import com.github.jyoghurt.activiti.compontent.audit.servive.WorkflowAuditService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 审核控制器
 *
 */
@RestController
@LogContent(module = "审核")
@RequestMapping("/workflowAudit")
public class WorkflowAuditController extends BaseController {


	/**
	 * 审核服务类
	 */
	@Resource
	private WorkflowAuditService workflowAuditService;

	/**
	 * 列出审核
	 */
	@LogContent("查询审核")
	@RequestMapping(value = "/findWorkflowAuditList/{auditedId}/{auditedClass}",method=RequestMethod.GET)
	public HttpResultEntity<?> findWorkflowAuditList(@PathVariable String auditedId,@PathVariable String auditedClass)   {
        return getSuccessResult(workflowAuditService.findWorkflowAuditList(auditedId,auditedClass));
	}



	/**
	 * 添加审核
	 */
	@LogContent("添加审核")
	@RequestMapping(value = "/addAudit/{taskId}/{activitiId}",method= RequestMethod.POST)
	public HttpResultEntity<?> add(@RequestBody WorkflowAudit workflowAudit, @PathVariable String taskId, @PathVariable String activitiId) throws RepetitionSubmitException {
		workflowAuditService.subAudit(workflowAudit,taskId,activitiId);
        return getSuccessResult();
	}
//
//	/**
//	 * 编辑审核
//	 */
//	@LogContent("编辑审核")
//	@RequestMapping(method=RequestMethod.PUT)
//	public HttpResultEntity<?> edit(@RequestBody WorkflowAudit workflowAudit)   {
//		workflowAuditService.updateForSelective(workflowAudit);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个审核
//	 */
//	@LogContent("删除审核")
//	@RequestMapping(value = "/{businessKey}",method=RequestMethod.DELETE)
//	public HttpResultEntity<?> delete(@PathVariable String businessKey)   {
//		workflowAuditService.delete(businessKey);
//		return getSuccessResult();
//	}
//
//    /**
//     * 查询单个审核
//	 */
//	 @LogContent("查询单个审核")
//	 @RequestMapping(value = "/{businessKey}",method=RequestMethod.GET)
//	 public HttpResultEntity<?> get(@PathVariable String businessKey)   {
//		 return getSuccessResult(workflowAuditService.find(businessKey));
//	 }
}
