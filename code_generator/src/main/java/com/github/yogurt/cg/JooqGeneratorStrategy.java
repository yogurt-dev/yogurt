package com.github.yogurt.cg;

import org.apache.commons.lang3.StringUtils;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;


public class JooqGeneratorStrategy extends DefaultGeneratorStrategy {
    @Override
    public String getJavaPackageName(Definition definition, Mode mode) {
        if (mode == Mode.RECORD) {
            return super.getJavaPackageName(definition, mode).replace("tables.records", "dao.jooq");
        }
        if (!StringUtils.endsWith(super.getJavaPackageName(definition, mode), "tables")) {
            return super.getJavaPackageName(definition, mode) + ".dao.jooq";
        }
        return super.getJavaPackageName(definition, mode).replace("tables", "dao.jooq");
    }
}