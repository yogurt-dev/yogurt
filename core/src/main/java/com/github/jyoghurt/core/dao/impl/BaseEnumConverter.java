package com.github.jyoghurt.core.dao.impl;

import com.github.jyoghurt.core.enums.BaseEnum;
import org.jooq.Converter;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public class BaseEnumConverter<T extends Serializable,U extends BaseEnum> implements Converter<T, U> {

    @Override
    public U from(T databaseObject) {
        return null;
    }

    @Override
    public T to(U userObject) {
        return null;
    }

    @Override
    public Class<T> fromType() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public Class<U> toType() {
        return (Class<U>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
}
