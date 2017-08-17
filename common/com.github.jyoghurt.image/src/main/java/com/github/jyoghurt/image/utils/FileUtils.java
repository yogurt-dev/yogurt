//package com.df.image.utils;
//
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//
///**
// * @Project: 驴鱼社区-车险帮
// * @Package: com.df.image.utils
// * @Description:
// * @author: baoxiaobing@lvyushequ.com
// * @date: 2016-03-23 21:09
// */
//public class FileUtils {
//
//    /**
//     * 将文件转成base64 字符串
//     * @param path 文件路径
//     * @return  *
//     * @
//     */
//
//    public static String encodeBase64File(String path)   {
//        File file = new File(path);;
//        FileInputStream inputFile = new FileInputStream(file);
//        byte[] buffer = new byte[(int) file.length()];
//        inputFile.read(buffer);
//        inputFile.close();
//        return new BASE64Encoder().encode(buffer);
//
//    }
//
//    /**
//     * 将base64字符解码保存文件
//     * @param base64Code
//     * @param targetPath
//     * @
//     */
//
//    public static void decoderBase64File(String base64Code, String targetPath)
//              {
//        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
//        FileOutputStream out = new FileOutputStream(targetPath);
//        out.write(buffer);
//        out.close();
//
//    }
//    /**
//     * 将base64字符保存文本文件
//     * @param base64Code
//     * @param targetPath
//     * @
//     */
//
//    public static void toFile(String base64Code, String targetPath)
//              {
//
//        byte[] buffer = base64Code.getBytes();
//        FileOutputStream out = new FileOutputStream(targetPath);
//        out.write(buffer);
//        out.close();
//    }
//
//    public static void main(String[] args) {
//        try {
//            String base64Code = encodeBase64File("E:\\upload\\WASH\\20160322\\20160322153932_8_original.png");
//            System.out.println(base64Code);
//            decoderBase64File(base64Code, "D:/2.tif");
//            toFile(base64Code, "D:\\three.txt");
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//    }
//}
