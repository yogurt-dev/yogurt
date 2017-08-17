package com.github.jyoghurt.annex.service;


import com.github.jyoghurt.annex.domain.AnnexT;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件上传服务层
 *
 */
public interface UploadAnnexService extends BaseService<AnnexT> {
     HttpResultEntity uploadAnnex(MultipartFile file, String entityClassName, String expand)  ;
}
