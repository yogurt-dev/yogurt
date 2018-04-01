package com.github.jyoghurt.common.payment.common.utils;


import com.github.jyoghurt.core.exception.BaseErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class QrCodeUtil {
    @Value("${uploadPath}")
    private String uploadPath;

    @Value("${downloadPath}")
    private String downloadPath;

    public String createQrCode(String qrCode, String outTradeNo) {
        return createQrCode(qrCode, outTradeNo, 298, 0);
    }

    public String createQrCode(String qrCode, String outTradeNo, int width) {
        return createQrCode(qrCode, outTradeNo, width);
    }

    private String createQrCode(String qrCode, String outTradeNo, int width, int againTime) {
        //生成文件路径
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dirPath = dateFormat.format(new Date());
        String path = StringUtils.join(uploadPath, File.separator, "qrCode", File.separator, dirPath, File.separator);
        File gallerySaveDir = new File(path);
        if (!gallerySaveDir.exists()) {
            if (!gallerySaveDir.mkdirs()) {
                throw new BaseErrorException("创建二维码文件夹失败");
            }// 如果目录不存在就创建
        }
        ZxingUtils.getQRCodeImge(qrCode, width, StringUtils.join(path, File.separator, outTradeNo, sdf.format(new Date()), ".png"));
        String qrCodePath = StringUtils.join(downloadPath, "/", "qrCode", "/", dirPath, "/", outTradeNo, sdf.format(new Date()), ".png");
        String findPath = StringUtils.join(uploadPath, "/", "qrCode", "/", dirPath, "/", outTradeNo, sdf.format(new Date()), "" + ".png");
        if (!checkFile(findPath, 0)) {
            if (againTime > 3) {
                return "x.png";
            }
            againTime++;
            createQrCode(qrCode, outTradeNo, width, againTime);
        }
        return qrCodePath;
    }

    /**
     * 验证文件是否存在
     *
     * @param downloadPath 下载地址
     * @param againTime    重复次数
     * @return true为存在  false为不存在
     */
    private boolean checkFile(String downloadPath, int againTime) {
        if (againTime > 3) {
            return false;
        }
        File file = new File(downloadPath);
        if (file.exists()) {
            return true;
        }
        againTime++;
        return checkFile(downloadPath, againTime);
    }
}
