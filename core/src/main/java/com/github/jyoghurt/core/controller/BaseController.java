package com.github.jyoghurt.core.controller;


import com.github.jyoghurt.core.exception.BaseException;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.HttpResultHandle;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * 控制器基类
 *
 * @author JiangYingxu
 */
public class BaseController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
    }

    static {
        //定义允许上传的文件扩展名
        HashMap<String, String> extMap = new HashMap<String, String>();
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        extMap.put("flash", "swf,flv");
        extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
        extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");

    }



    public HttpResultEntity<?> getSuccessResult() {
        return HttpResultHandle.getSuccessResult();
    }


    public HttpResultEntity<?> getSuccessResult(Object result) {
        return HttpResultHandle.getSuccessResult(result);
    }


    public static HttpResultEntity<?> getErrorResult() {
        return HttpResultHandle.getErrorResult();
    }


    public static HttpResultEntity<?> getErrorResult(Object result) {
        return HttpResultHandle.getErrorResult(result);
    }

    public static HttpResultEntity<?> getErrorResult(BaseException e){
        if(null==e||null==e.getExceptionBody()){
            return getErrorResult();
        }
        return HttpResultHandle.getErrorResult(e.getErrorCode(),e.getExceptionBody().getMessage());
    }

}
