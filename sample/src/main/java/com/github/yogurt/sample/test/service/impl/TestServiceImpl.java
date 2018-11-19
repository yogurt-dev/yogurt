package com.github.yogurt.sample.test.service.impl;

import com.github.yogurt.core.service.impl.BaseServiceImpl;
import com.github.yogurt.sample.test.dao.TestDAO;
import com.github.yogurt.sample.test.po.TestPO;
import com.github.yogurt.sample.test.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 */
@Slf4j
@Service("testService")
public class TestServiceImpl extends BaseServiceImpl<TestPO> implements TestService {
	@Autowired
    private TestDAO testDAO;

}
