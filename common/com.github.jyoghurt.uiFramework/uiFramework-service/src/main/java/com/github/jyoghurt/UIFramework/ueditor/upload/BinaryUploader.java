package com.github.jyoghurt.UIFramework.ueditor.upload;

import com.github.jyoghurt.UIFramework.ueditor.PathFormat;
import com.github.jyoghurt.UIFramework.ueditor.define.BaseState;
import com.github.jyoghurt.UIFramework.ueditor.define.FileType;
import com.github.jyoghurt.UIFramework.ueditor.define.State;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



public class BinaryUploader {
    public BinaryUploader() {
    }

    public static final State save(HttpServletRequest request, Map<String, Object> conf,String upLoadPath,String
            downLoadPath,CommonsMultipartFile uploadFile) {
        FileItemStream fileStream = null;
        boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;
        if(!ServletFileUpload.isMultipartContent(request)) {
            return new BaseState(false, 5);
        } else {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            if(isAjaxUpload) {
                upload.setHeaderEncoding("UTF-8");
            }
            try {
                if(uploadFile == null) {
                    return new BaseState(false, 7);
                } else {
                    String savePath = (String)conf.get("savePath");
                    String originFileName = uploadFile.getFileItem().getName();
                    String suffix = FileType.getSuffixByFilename(originFileName);
                    originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
                    savePath = savePath + suffix;
                    long maxSize = ((Long)conf.get("maxSize")).longValue();
                    if(!validType(suffix, (String[])conf.get("allowFiles"))) {
                        return new BaseState(false, 8);
                    } else {
                        savePath = PathFormat.parse(savePath, originFileName);
                        String physicalPath = upLoadPath+savePath;
                        InputStream is = uploadFile.getInputStream();
                        State storageState = StorageManager.saveFileByInputStream(is, physicalPath, maxSize);
                        is.close();
                        if(storageState.isSuccess()) {
                            storageState.putInfo("url",downLoadPath+PathFormat.format(savePath));
                            storageState.putInfo("type", suffix);
                            storageState.putInfo("original", originFileName + suffix);
                        }
                        return storageState;
                    }
                }
            } catch (IOException var15) {
                return new BaseState(false, 4);
            }
        }
    }

    private static boolean validType(String type, String[] allowTypes) {
        List list = Arrays.asList(allowTypes);
        return list.contains(type);
    }
}