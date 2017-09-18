package com.github.jyoghurt.msgcen.common.tmpl.wechat;

/**
 * user:dell
 * date: 2016/6/15.
 */


import com.github.jyoghurt.msgcen.common.enums.WechatTmplConfig;
import com.github.jyoghurt.msgcen.common.utils.WeChatTmplUtil;
import com.github.jyoghurt.wechatbasic.common.templet.BaseTpl;
import com.github.jyoghurt.wechatbasic.common.templet.FirstTpl;
import com.github.jyoghurt.wechatbasic.common.templet.KeyWord;
import com.github.jyoghurt.wechatbasic.common.templet.RemarkTpl;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 模板ID:UR2bSmYLurzaBQ95CicG-QOop5pY_VPddK2aS617xNI
 * 开发者调用模板消息接口时需提供模板ID
 * 标题充值通知
 * 行业IT科技 - 互联网|电子商务
 * 详细内容
 * {{first.DATA}}
 * <p>
 * {{accountType.DATA}}：{{account.DATA}}
 * 充值金额：{{amount.DATA}}
 * 充值状态：{{result.DATA}}
 * {{remark.DATA}}
 * 在发送时，需要将内容中的参数（{{.DATA}}内为参数）赋值替换为需要的信息
 * 内容示例
 * 您好，您已成功进行话费充值。
 * <p>
 * 手机号：13912345678
 * 充值金额：50元
 * 充值状态：充值成功
 * 备注：如有疑问，请致电13912345678联系我们。
 */
public class RechargeTmpl extends BaseTpl {

    public RechargeTmpl() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.setTmplId(WeChatTmplUtil.getTmplId(WechatTmplConfig.RechargeTmpl));
        this.setFirst("您好，您于" + sdf.format(new Date()) + "完成一笔充值交易");
        this.setRemark("如有疑问，请致电400-015-5567");
    }

    /**
     * 头部标题
     */
    private FirstTpl first;
    /**
     * 扩展字段（目前标准模板为会员号键）
     */
    private KeyWord accountType;
    /**
     * 扩展字段（目前标准模板为会员号值）
     */
    private KeyWord account;
    /**
     * 充值金额
     */
    private KeyWord amount;
    /**
     * 充值状态
     */
    private KeyWord result;
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

    public KeyWord getAccountType() {
        return accountType;
    }

    public void setAccountType(KeyWord accountType) {
        this.accountType = accountType;
    }

    public void setAccountType(String accountType) {
        KeyWord keyWord = new KeyWord();
        keyWord.setColor("#000000");
        keyWord.setValue(accountType);
        this.accountType = keyWord;
    }

    public KeyWord getAccount() {
        return account;
    }

    public void setAccount(String account) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(account);
        this.account = keyWord;
    }

    public KeyWord getAmount() {
        return amount;
    }

    public void setAmount(KeyWord amount) {
        this.amount = amount;
    }

    public void setAmount(String amount) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(amount + "元");
        this.amount = keyWord;
    }

    public KeyWord getResult() {
        return result;
    }

    public void setResult(KeyWord result) {
        this.result = result;
    }

    public void setResult(String result) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(result);
        this.result = keyWord;
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
