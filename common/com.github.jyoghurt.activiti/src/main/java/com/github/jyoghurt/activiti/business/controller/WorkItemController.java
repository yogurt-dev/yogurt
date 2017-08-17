package com.github.jyoghurt.activiti.business.controller;

import com.github.jyoghurt.activiti.business.module.WorkItem;
import com.github.jyoghurt.core.controller.BaseController;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dell on 2016/1/21.
 */
public class WorkItemController extends BaseController {
    public WorkItem getWorkItem(HttpServletRequest request) {
        WorkItem workItem = (WorkItem) JSONObject.toBean(JSONObject.fromObject(request.getParameter("workItem")), WorkItem.class);
        return workItem;
    }

}
