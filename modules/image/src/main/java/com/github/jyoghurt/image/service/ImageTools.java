package com.github.jyoghurt.image.service;

/**
 * Created by Administrator on 2015/2/3.
 */

import com.github.jyoghurt.image.domain.ImageConfig;
import com.github.jyoghurt.image.enums.ImgExceptionEnums;
import com.github.jyoghurt.core.exception.BaseErrorException;
import org.apache.commons.lang3.StringUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * ImageMagick的路径
 */
public class ImageTools {


    /**
     * 根据坐标裁剪图片
     *
     * @param srcPath 要裁剪图片的路径
     * @param newPath 裁剪图片后的路径
     * @param x       起始横坐标
     * @param y       起始纵坐标
     * @param x1      结束横坐标
     * @param y1      结束纵坐标
     */

    public static void cutImage(String srcPath, String newPath, int x, int y, int x1, int y1) {
        int width = x1 - x;
        int height = y1 - y;
        IMOperation op = new IMOperation();
        op.addImage(srcPath);
        /**
         * width：  裁剪的宽度
         * height： 裁剪的高度
         * x：       裁剪的横坐标
         * y：       裁剪的挫坐标
         */
        op.crop(width, height, x, y);
        op.addImage(newPath);
        ConvertCmd convert = new ConvertCmd();

        // linux下不要设置此值，不然会报错
//        //convert.setSearchPath(imageMagickPath);

        try {
            convert.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new BaseErrorException(ImgExceptionEnums.ERROR_9004.getMessage(),e);
        }
    }

    /**
     * 根据尺寸缩放图片
     *
     * @param width   缩放后的图片宽度
     * @param height  缩放后的图片高度
     * @param srcPath 源图片路径
     * @param newPath 缩放后图片的路径
     */
    public static void cutImage(Integer width, Integer height, String srcPath, String newPath) {
        IMOperation op = new IMOperation();

        op.resize(width, height);
        op.addImage(srcPath.replace("/", File.separator));
        op.addImage(newPath.replace("/", File.separator));
        ConvertCmd convert = new ConvertCmd();
        // 设置imageMagick系统路径
        if (System.getProperty("os.name").startsWith("Windows")) {
            convert.setSearchPath(System.getenv("IMAGE_MAGICK_PATH"));
        }
        // linux下不要设置此值，不然会报错
        //convert.setSearchPath(imageMagickPath);
        try {
            convert.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new BaseErrorException(ImgExceptionEnums.ERROR_9004.getMessage(),e);
        }

    }


    /**
     * 根据宽度缩放图片
     *
     * @param width    缩放后的图片宽度
     * @param height   缩放后的图片高度
     * @param srcInput 源图片字节流
     * @param newPath  缩放后图片的路径
     * @param fileName 缩放后图片名称
     */


    public static void cutImage(Integer width, Integer height, BufferedImage srcInput, String newPath, String
            fileName, IMOperation op) {
        if (width != null || height != null) {
            //命令有先后顺序，先进行压缩
            op.resize(width, height);
//            op.gravity("center");
//            op.background("White");
//            op.extent(width, height);
        }
        op.addImage();//设置为输入流
        ConvertCmd convert = new ConvertCmd();
        // 设置imageMagick系统路径
        if (System.getProperty("os.name").startsWith("Windows")) {
            convert.setSearchPath(System.getenv("IMAGE_MAGICK_PATH"));
        }
        convert.setAsyncMode(false);
        op.addImage(StringUtils.join(newPath, File.separator, fileName));
        try {
            convert.run(op, srcInput);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new BaseErrorException(ImgExceptionEnums.ERROR_9004.getMessage(),e);
        }
    }

    /**
     * 切图处理入口
     *
     * @param imageConfig   功能模块的图片大小
     * @param bufferedImage 图片字节流
     * @param newPath       缩略图保存地址
     * @param fileName      原始图片名称
     * @return
     * @
     */
    public static String cutImage(ImageConfig imageConfig, BufferedImage bufferedImage, String newPath,
                                  String fileName) {

        //根据枚举值的width压缩图片，如果原图width小于枚举width时，保持原尺寸
        //判断是否切图
        IMOperation imOperation = new IMOperation();
        if (imageConfig.getCrop()) {
            imOperation.gravity("center");
            //实际长宽比大于默认长宽比时：width=defaultScale*实际高度 height = 实际高度
            double defaultScale = new Double(imageConfig.getWidth()) / new Double(imageConfig.getHeight());
            Integer cropWidth;
            Integer cropHeight;
            if ((new Double(bufferedImage.getWidth()) / new Double(bufferedImage.getHeight())) > defaultScale) {
                cropWidth = Double.valueOf(Math.ceil(defaultScale * bufferedImage.getHeight())).intValue();
                cropHeight = bufferedImage.getHeight();
            } else {
                cropWidth = bufferedImage.getWidth();
                cropHeight = Double.valueOf(Math.ceil(bufferedImage.getWidth() / defaultScale)).intValue();
            }

            imOperation.crop(cropWidth, cropHeight, 0, 0);
        }
        Integer width = imageConfig.getRealWidth(bufferedImage.getWidth());
        Integer height = imageConfig.getRealHeight(bufferedImage.getWidth(), bufferedImage.getHeight());
        String sizeEnumName = imageConfig.getModuleName();
        //补齐高度信息
        if (sizeEnumName.endsWith("x")) {
            sizeEnumName += height;
        }
        String replaceFileName = StringUtils.join(fileName.substring(0, fileName.lastIndexOf(".")), "_",
                sizeEnumName, imageConfig.getExtension());

        cutImage(width, height, bufferedImage, newPath, replaceFileName, imOperation);
        return replaceFileName;
    }

    public static void main(String[] args) {
//        File file = new File("D:\\63f0740a-3368-434f-9236-a86e28ce3154.png");
//        BufferedImage bis = null;
//        try {
//            bis = ImageIO.read(file);
//            bis.getWidth();
//            bis.getHeight();
//        } catch (Exception e) {
//            try {
//                ThumbnailConvert tc = new ThumbnailConvert();
//                tc.setCMYK_COMMAND(file.getPath());
//                Image image = null;
//                image = Toolkit.getDefaultToolkit().getImage(file.getPath());
//                MediaTracker mediaTracker = new MediaTracker(new Container());
//                mediaTracker.addImage(image, 0);
//                mediaTracker.waitForID(0);
////                image.getRealWidth(null);
////                image.getRealHeight(null);
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
//
//        System.out.println(cutImage(ImageSizeEnum.ARTICLE, bis, "D:", "22.jpg1"));

//        cutImage(720,"D:\\4.jpg","D:\\2.jpg");
        cutImage(1024, 720, "D:\\4.jpg", "D:\\3.jpg");
//        addImgText("D://1.jpg");
    }
}
