package com.github.jyoghurt.msgcen.common.tmpl.wechat;

import com.github.jyoghurt.msgcen.common.enums.WechatTmplConfig;
import com.github.jyoghurt.msgcen.common.utils.WeChatTmplUtil;
import com.github.jyoghurt.wechatbasic.common.templet.BaseTpl;
import com.github.jyoghurt.wechatbasic.common.templet.FirstTpl;
import com.github.jyoghurt.wechatbasic.common.templet.KeyWord;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/21
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class Xyorder extends BaseTpl {

    public Xyorder() {
        this.setTmplId(WeChatTmplUtil.getTmplId(WechatTmplConfig.Xyorder));
    }

    /**
     * 下单时间
     */
    private KeyWord keyword1;
    /**
     * 订单编号
     */
    private KeyWord keyword2;

    /**
     * 订单金额
     */
    private KeyWord keyword3;
    /**
     * 课程名称
     */
    private KeyWord keyword4;

    /**
     * 会员名称
     */
    private KeyWord keyword5;
    /**
     * 联系人手机号
     */
    private KeyWord keyword6;


    public void setKeyword1(KeyWord keyword1) {
        this.keyword1 = keyword1;
    }

    public void setKeyword1(String keyword1) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword1);
        this.keyword1 = keyWord;
    }
    public void setKeyword2(KeyWord keyword2) {
        this.keyword2 = keyword2;
    }

    public void setKeyword2(String keyword2) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword2);
        this.keyword2 = keyWord;
    }
    public void setKeyword3(KeyWord keyword3) {
        this.keyword3 = keyword3;
    }

    public void setKeyword3(String keyword3) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword3);
        this.keyword3 = keyWord;
    }
    public void setKeyword4(KeyWord keyword4) {
        this.keyword4 = keyword4;
    }

    public void setKeyword4(String keyword4) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword4);
        this.keyword4 = keyWord;
    }
    public void setKeyword5(KeyWord keyword5) {
        this.keyword5 = keyword5;
    }

    public void setKeyword5(String keyword5) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword5);
        this.keyword5 = keyWord;
    }
    public void setKeyword6(KeyWord keyword6) {
        this.keyword6 = keyword6;
    }

    public void setKeyword6(String keyword6) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword6);
        this.keyword6 = keyWord;
    }

    public KeyWord getKeyword1() {
        return keyword1;
    }

    public KeyWord getKeyword2() {
        return keyword2;
    }

    public KeyWord getKeyword3() {
        return keyword3;
    }

    public KeyWord getKeyword4() {
        return keyword4;
    }

    public KeyWord getKeyword5() {
        return keyword5;
    }

    public KeyWord getKeyword6() {
        return keyword6;
    }
}
