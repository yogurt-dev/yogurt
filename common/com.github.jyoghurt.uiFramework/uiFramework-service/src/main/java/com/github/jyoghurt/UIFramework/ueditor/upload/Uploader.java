package com.github.jyoghurt.UIFramework.ueditor.upload;

import com.github.jyoghurt.UIFramework.ueditor.define.State;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


public class Uploader {
    private HttpServletRequest request = null;
    private Map<String, Object> conf = null;
    private String upLoadPath;
    private String downLoadPath;
    private CommonsMultipartFile uploadFile;

    public Uploader(HttpServletRequest request, Map<String, Object> conf,String upLoadPath,String downLoadPath,CommonsMultipartFile uploadFile) {
        this.request = request;
        this.conf = conf;
        this.upLoadPath=upLoadPath;
        this.downLoadPath=downLoadPath;
        this.uploadFile=uploadFile;
    }

    public final State doExec() {
        String filedName = (String)this.conf.get("fieldName");
        State state = null;
        if("true".equals(this.conf.get("isBase64"))) {
            state = Base64Uploader.save(this.request.getParameter(filedName), this.conf);
        } else {
            state = BinaryUploader.save(this.request, this.conf,this.upLoadPath,this.downLoadPath,this.uploadFile);
        }

        return state;
    }
}