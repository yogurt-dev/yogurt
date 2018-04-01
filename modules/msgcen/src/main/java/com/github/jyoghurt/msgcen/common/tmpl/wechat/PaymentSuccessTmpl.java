package com.github.jyoghurt.msgcen.common.tmpl.wechat;

/**
 * user:dell
 * date: 2016/6/14.
 */


import com.github.jyoghurt.msgcen.common.enums.WechatTmplConfig;
import com.github.jyoghurt.msgcen.common.utils.WeChatTmplUtil;
import com.github.jyoghurt.wechatbasic.common.templet.BaseTpl;
import com.github.jyoghurt.wechatbasic.common.templet.FirstTpl;
import com.github.jyoghurt.wechatbasic.common.templet.KeyWord;
import com.github.jyoghurt.wechatbasic.common.templet.RemarkTpl;

/**
 * 模板ID:ZwqJqNhIcEeDRg0nm8LRR0ctTdyz2AfmRXcUZ-DGZzY
 * 开发者调用模板消息接口时需提供模板ID
 * 标题订单支付成功通知
 * 行业IT科技 - 互联网|电子商务
 * 详细内容
 * {{first.DATA}}
 * 订单编号：{{keyword1.DATA}}
 * 支付金额：{{keyword2.DATA}}
 * {{remark.DATA}}
 * 在发送时，需要将内容中的参数（{{.DATA}}内为参数）赋值替换为需要的信息
 * 内容示例
 * 您的订单已支付成功，我们会尽快为您发货。
 * 订单编号：S201509230008
 * 支付金额：6727.00元
 * 请耐心等待收货，收到货后记得回来确认哦。
 */
public class PaymentSuccessTmpl extends BaseTpl {
    public PaymentSuccessTmpl() {
        this.setTmplId(WeChatTmplUtil.getTmplId(WechatTmplConfig.PaymentSuccessTmpl));
        this.setFirst("您的订单已支付成功，我们会尽快为您发货。");
        this.setRemark("请耐心等待收货，收到货后记得回来确认哦。");
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
     * 支付金额
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


    public void setKeyword1(String keyword1) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword1);
        this.keyword1 = keyWord;
    }

    public KeyWord getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(KeyWord keyword1) {
        this.keyword1 = keyword1;
    }

    public KeyWord getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(KeyWord keyword2) {
        this.keyword2 = keyword2;
    }

    public void setKeyword2(String keyword2) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword2);
        this.keyword2 = keyWord;
    }

    public RemarkTpl getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        RemarkTpl remarkTpl = new RemarkTpl();
        remarkTpl.setValue(remark);
        this.remark = remarkTpl;
    }

    public void setRemark(RemarkTpl remark) {
        this.remark = remark;
    }

}
