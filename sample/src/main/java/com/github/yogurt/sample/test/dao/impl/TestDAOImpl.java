package com.github.yogurt.sample.test.dao.impl;

import org.jooq.TableField;
import com.github.yogurt.sample.test.po.TestPO;
import com.github.yogurt.sample.test.dao.TestDAO;
import com.github.yogurt.core.dao.impl.BaseDAOImpl;
import com.github.yogurt.sample.test.dao.jooq.TestRecord;
import org.springframework.stereotype.Service;

import static com.github.yogurt.sample.test.dao.jooq.Test.TEST;
/**
 * @author Administrator
 */
@Service
public class TestDAOImpl extends BaseDAOImpl<TestPO, TestRecord>  implements TestDAO{

    @Override
    public TableField getId() {
    return TEST.ID;
    }

    @Override
    public Class<TestPO> getType() {
        return TestPO.class;
    }
}
