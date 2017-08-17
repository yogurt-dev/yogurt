package com.github.jyoghurt.annex.service.impl;


import com.github.jyoghurt.annex.dao.AnnexTMapper;
import com.github.jyoghurt.annex.domain.AnnexT;
import com.github.jyoghurt.annex.service.UploadAnnexService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.HttpResultHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service("uploadAnnexService")
public class UploadAnnexServiceImpl extends ServiceSupport<AnnexT, AnnexTMapper> implements UploadAnnexService {
    @Value("${uploadPath}")
    private String uploadPath;

    @Value("${downloadPath}")
    private String downloadPath;

    @Value("${uploadMaxLength}")
    private String uploadMaxLength;
    //附件地址
    public static String annexPath = "annex";
    @Resource
    private UploadAnnexService uploadAnnexService;

    @Autowired
    private AnnexTMapper annexTMapper;

    @Override
    public AnnexTMapper getMapper() {
        return annexTMapper;
    }

    @Override
    public void delete(Serializable id) {
        getMapper().delete(AnnexT.class, id);
    }

    @Override
    public AnnexT find(Serializable id) {
        return getMapper().selectById(AnnexT.class, id);
    }

    @Override
    public HttpResultEntity uploadAnnex(MultipartFile file, String entityClassName, String expand) {
        if (file.getSize() > Long.valueOf(uploadMaxLength)) {
            throw new BaseErrorException("上传文件不能超过512M");
        }
        try {
            //获取文件名称
            String fileName = file.getOriginalFilename();
            if (StringUtils.isEmpty(fileName)) {
                return HttpResultHandle.getSuccessResult("上传文件失败");
            }
            //生成文件路径
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String dirPath = dateFormat.format(new Date());
            String path = StringUtils.join(uploadPath, File.separator, annexPath, File.separator, dirPath, File.separator);
            String downLoad = StringUtils.join(downloadPath, "/", annexPath, "/", dirPath, "/");
            File gallerySaveDir = new File(path);
            if (!gallerySaveDir.exists()) {
                gallerySaveDir.mkdirs();// 如果目录不存在就创建
            }
            InputStream inputStream = file.getInputStream();

            OutputStream bos = new FileOutputStream(path + fileName);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.close();
            inputStream.close();
            AnnexT annexT = new AnnexT();
            annexT.setAnnexPath(downLoad + fileName);
            annexT.setAttachmentSize(String.valueOf(file.getSize()));
            annexT.setEntityName(entityClassName);
            annexT.setExpand(expand);
            annexT.setFileName(fileName);
            uploadAnnexService.save(annexT);
            return HttpResultHandle.getSuccessResult(annexT);
        } catch (IOException e) {
            throw new BaseErrorException("上传附件异常", e);
        }
    }
}
