package com.github.jyoghurt.activiti.business.service;

import com.github.jyoghurt.activiti.business.exception.WorkFlowException;
import com.github.jyoghurt.activiti.business.module.WorkItem;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by dell on 2016/1/7.
 */
public interface BulidPageService {
     JSONObject build(String procInsId, WorkItem workItem, SecurityUserT userT) throws WorkFlowException, UnsupportedEncodingException;
}
