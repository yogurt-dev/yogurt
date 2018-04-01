package com.github.jyoghurt.drools.service.knowledge.filter;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaFilter;

/**
 * user:zjl
 * date: 2017/3/2.
 */
public class RuleNameFilter implements AgendaFilter {
    private String ruleName;

    public RuleNameFilter(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public boolean accept(Activation activation) {
        String ruleName = activation.getRule().getName();
        if (ruleName.startsWith(this.ruleName)) {
            return true;
        }
        return false;
    }
}
