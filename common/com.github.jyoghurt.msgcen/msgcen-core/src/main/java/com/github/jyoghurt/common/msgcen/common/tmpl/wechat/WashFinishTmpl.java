package com.github.jyoghurt.common.msgcen.common.tmpl.wechat;

/**
 * user:dell
 * date: 2016/6/15.
 */


import com.github.jyoghurt.common.msgcen.common.enums.WechatTmplConfig;
import com.github.jyoghurt.common.msgcen.common.utils.WeChatTmplUtil;
import com.github.jyoghurt.wechatbasic.common.templet.BaseTpl;
import com.github.jyoghurt.wechatbasic.common.templet.FirstTpl;
import com.github.jyoghurt.wechatbasic.common.templet.KeyWord;
import com.github.jyoghurt.wechatbasic.common.templet.RemarkTpl;

import java.math.BigDecimal;

/**
 * 模板ID:dZj7fifIFNmwkSaP0zgh_wkN0RUsxFW28Qtc55Yp3Jw
 * 开发者调用模板消息接口时需提供模板ID
 * 标题衣物洗涤完成提醒
 * 行业消费品 - 消费品
 * 详细内容
 * {{first.DATA}}
 * 地址：{{keyword1.DATA}}
 * 完成时间：{{keyword2.DATA}}
 * {{remark.DATA}}
 * 在发送时，需要将内容中的参数（{{.DATA}}内为参数）赋值替换为需要的信息
 * 内容示例
 * 衣物洗涤完成提醒
 * 订单编号：SN123456789
 * 订单金额：100元
 * 洗涤完成时间：2016.1.1
 * 您好，您的衣物已洗涤完成。感谢您的惠顾！
 */
public class WashFinishTmpl extends BaseTpl {

    public WashFinishTmpl() {
        this.setTmplId(WeChatTmplUtil.getTmplId(WechatTmplConfig.WashFinishTmpl));
        this.setRemark("有问题请致电400-015-5567");
    }

    /**
     * 头部标题
     */
    private FirstTpl first;
    /**
     * 订单编号
     */
    private KeyWord keyword1;
    /**
     * 订单金额
     */
    private KeyWord keyword2;
    /**
     * 备注
     */
    private RemarkTpl remark = new RemarkTpl();

    public FirstTpl getFirst() {
        return first;
    }

    public void setFirst(String first) {
        FirstTpl firstTpl = new FirstTpl();
        firstTpl.setValue(first);
        this.first = firstTpl;
    }

    public void setFirst(FirstTpl first) {
        this.first = first;
    }

    public KeyWord getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(KeyWord keyword1) {
        this.keyword1 = keyword1;
    }

    public void setKeyword1(String keyword1) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword1);
        this.keyword1 = keyWord;
    }


    public void setKeyword2(String keyword2) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword2);
        this.keyword2 = keyWord;
    }

    public KeyWord getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(KeyWord keyword2) {
        this.keyword2 = keyword2;
    }

    public void setKeyword2(BigDecimal keyword2) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword2.toString() + "元");
        this.keyword2 = keyWord;
    }

    public void setRemark(String remark) {
        RemarkTpl remarkTpl = new RemarkTpl();
        remarkTpl.setValue(remark);
        this.remark = remarkTpl;
    }

    public void setRemark(RemarkTpl remark) {
        this.remark = remark;
    }

    public RemarkTpl getRemark() {
        return remark;
    }
}
