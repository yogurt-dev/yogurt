package com.github.jyoghurt.common.msgcen.common.tmpl.wechat;

import com.github.jyoghurt.common.msgcen.common.enums.WechatTmplConfig;
import com.github.jyoghurt.common.msgcen.common.utils.WeChatTmplUtil;
import com.github.jyoghurt.dataDict.service.DataDictHandler;
import com.github.jyoghurt.wechatbasic.common.templet.BaseTpl;
import com.github.jyoghurt.wechatbasic.common.templet.FirstTpl;
import com.github.jyoghurt.wechatbasic.common.templet.KeyWord;
import com.github.jyoghurt.wechatbasic.common.templet.RemarkTpl;

/**
 * user:zjl
 * date: 2016/12/26.
 * 详细内容
 * {{first.DATA}}
 * 验证码：{{keyword1.DATA}}
 * 充值账户：{{keyword2.DATA}}
 * 充值金额：{{keyword3.DATA}}
 * {{remark.DATA}}
 * 在发送时，需要将内容中的参数（{{.DATA}}内为参数）赋值替换为需要的信息
 * 内容示例
 * 您好，您正在使用充值卡充值。
 * 验证码：654321
 * 充值账户：15898345332
 * 充值金额：500元
 * 如有疑问，请致电400-015-5567
 */
public class RechargeAuthCodeTmpl extends BaseTpl {

    public RechargeAuthCodeTmpl() {
        this.setTmplId(WeChatTmplUtil.getTmplId(WechatTmplConfig.RechargeAuthCodeTmpl));
        this.setRemark("如有疑问请致电" + DataDictHandler.get400Tel() + "。");
    }

    /**
     * 头部标题
     */
    private FirstTpl first;
    /**
     * 送件时间
     */
    private KeyWord keyword1;
    /**
     * 送件人
     */
    private KeyWord keyword2;
    /**
     * 联系方式
     */
    private KeyWord keyword3;
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

    public KeyWord getKeyword3() {
        return keyword3;
    }

    public void setKeyword3(KeyWord keyword3) {
        this.keyword3 = keyword3;
    }

    public void setKeyword3(String keyword3) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword3);
        this.keyword3 = keyWord;
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
