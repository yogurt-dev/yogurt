package com.github.yogurt.core.utils;

import com.github.yogurt.core.po.BasePO;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: jtwu
 * @Date: 2019/6/26 13:37
 */
public class QueryDSLUtils {
	public static Predicate[] getPredicatesByNotNullFileds(BasePO basePO, EntityPathBase entityPathBase){
		List<Predicate> list = new ArrayList();
		Arrays.stream(JpaUtils.getNotNullProperties(basePO)).forEach(fieldName ->{
			SimpleExpression expression = (SimpleExpression) JpaUtils.getValue(entityPathBase,fieldName);
			list.add(expression.eq(JpaUtils.getValue(basePO,fieldName)));
		});
		return (Predicate[]) list.toArray(new Predicate[list.size()]);
	}
}
