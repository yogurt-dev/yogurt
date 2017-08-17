package com.github.jyoghurt.UIFramework.service;

import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.result.HttpResultEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhangjl on 2015/12/15.
 */
public interface UeditorService {
    HttpResultEntity<?>  uploadImg(HttpServletRequest request);

    String changeRelativePath(String data) ;
}
