package com.github.jyoghurt.qqEmail.domain;

/**
 * Created by zhangjl on 2015/11/9.
 */
public class SyncUnit {
    /*更新动作类型，1=DEL, 2=ADD, 3=MOD*/
    private String Action;
    /*源部门（注：部门用'/' 分隔根部门不用加部门最多5 级，单个部门字符不超过64 个字符）*/
    private String SrcPath;
    /*目标部门*/
    private String Dstpath;

    public String getAction() {
        return Action;
    }

    public SyncUnit setAction(String action) {
        Action = action;
        return this;
    }

    public String getDstpath() {
        return Dstpath;
    }

    public SyncUnit setDstpath(String dstpath) {
        Dstpath = dstpath;
        return this;
    }

    public String getSrcPath() {
        return SrcPath;
    }

    public SyncUnit setSrcPath(String srcPath) {
        SrcPath = srcPath;
        return this;
    }
}
