package com.github.yogurt.sample.test.po;

import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.sample.test.enums.TypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class TestPO extends BasePO<TestPO>{
    /**
    *  姓名
    */
    @Column(name = "name")
	private String name;
    /**
    *  类型(N:否,Y:是)
    */
    @Column(name = "type")
	private TypeEnum type;
    /**
    *  日期
    */
    @Column(name = "time")
	private java.time.LocalDateTime time;
}
