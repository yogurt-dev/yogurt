package com.github.jyoghurt.cg;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ClassDefinition {
    /**
     * 类名
     */
    private String className;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 字段列表
     */
    private List<FieldDefinition> fieldDefinitions;

    /**
     * 主键
     */
    private FieldDefinition priKey ;


}
