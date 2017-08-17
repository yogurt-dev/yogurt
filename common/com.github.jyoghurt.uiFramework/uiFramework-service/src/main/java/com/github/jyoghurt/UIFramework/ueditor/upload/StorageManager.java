package com.github.jyoghurt.UIFramework.ueditor.upload;

import com.github.jyoghurt.UIFramework.ueditor.define.BaseState;
import com.github.jyoghurt.UIFramework.ueditor.define.State;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class StorageManager {
    public static final int BUFFER_SIZE = 8192;

    public StorageManager() {
    }

    public static State saveBinaryFile(byte[] data, String path) {
        File file = new File(path);
        State state = valid(file);
        if(!state.isSuccess()) {
            return state;
        } else {
            try {
                BufferedOutputStream ioe = new BufferedOutputStream(new FileOutputStream(file));
                ioe.write(data);
                ioe.flush();
                ioe.close();
            } catch (IOException var5) {
                return new BaseState(false, 4);
            }

            BaseState state1 = new BaseState(true, file.getAbsolutePath());
            state1.putInfo("size", (long)data.length);
            state1.putInfo("title", file.getName());
            return state1;
        }
    }

    public static State saveFileByInputStream(InputStream is, String path, long maxSize) {
        State state = null;
        File tmpFile = getTmpFile();
        byte[] dataBuf = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(is, 8192);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), 8192);
            boolean count = false;

            int count1;
            while((count1 = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count1);
            }

            bos.flush();
            bos.close();
            if(tmpFile.length() > maxSize) {
                tmpFile.delete();
                return new BaseState(false, 1);
            } else {
                state = saveTmpFile(tmpFile, path);
                if(!state.isSuccess()) {
                    tmpFile.delete();
                }

                return state;
            }
        } catch (IOException var10) {
            return new BaseState(false, 4);
        }
    }

    public static State saveFileByInputStream(InputStream is, String path) {
        State state = null;
        File tmpFile = getTmpFile();
        byte[] dataBuf = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(is, 8192);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), 8192);
            boolean count = false;

            int count1;
            while((count1 = bis.read(dataBuf)) != -1) {
                bos.write(dataBuf, 0, count1);
            }

            bos.flush();
            bos.close();
            state = saveTmpFile(tmpFile, path);
            if(!state.isSuccess()) {
                tmpFile.delete();
            }

            return state;
        } catch (IOException var8) {
            return new BaseState(false, 4);
        }
    }

    private static File getTmpFile() {
        File tmpDir = FileUtils.getTempDirectory();
        String tmpFileName = String.valueOf(Math.random() * 10000.0D).replace(".", "");
        return new File(tmpDir, tmpFileName);
    }

    private static State saveTmpFile(File tmpFile, String path) {
        BaseState state = null;
        File targetFile = new File(path);
        if(targetFile.canWrite()) {
            return new BaseState(false, 2);
        } else {
            try {
                FileUtils.moveFile(tmpFile, targetFile);
            } catch (IOException var5) {
                return new BaseState(false, 4);
            }

            state = new BaseState(true);
            state.putInfo("size", targetFile.length());
            state.putInfo("title", targetFile.getName());
            return state;
        }
    }

    private static State valid(File file) {
        File parentPath = file.getParentFile();
        return !parentPath.exists() && !parentPath.mkdirs()?new BaseState(false, 3):(!parentPath.canWrite()?new BaseState(false, 2):new BaseState(true));
    }
}
