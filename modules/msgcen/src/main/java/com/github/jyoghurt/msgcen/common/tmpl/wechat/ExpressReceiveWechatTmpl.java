package com.github.jyoghurt.msgcen.common.tmpl.wechat;


import com.github.jyoghurt.msgcen.common.enums.WechatTmplConfig;
import com.github.jyoghurt.msgcen.common.utils.WeChatTmplUtil;
import com.github.jyoghurt.wechatbasic.common.templet.BaseTpl;
import com.github.jyoghurt.wechatbasic.common.templet.FirstTpl;
import com.github.jyoghurt.wechatbasic.common.templet.KeyWord;
import com.github.jyoghurt.wechatbasic.common.templet.RemarkTpl;

public class ExpressReceiveWechatTmpl extends BaseTpl {

    public ExpressReceiveWechatTmpl() {
        this.setTmplId(WeChatTmplUtil.getTmplId(WechatTmplConfig.ExpressReceiveWechatTmpl));
        this.setFirst("您有一份快递送达驴鱼社区，请确认是否需要拆包检查!");
    }
    /**
     * 头部信息
     */
    private FirstTpl first;
    /**
     * 快递单号
     */
    private KeyWord keyword1;
    /**
     * 快递公司
     */
    private KeyWord keyword2;
    /**
     * 时间
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

    public void setKeyword1(String keyword1) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword1);
        this.keyword1 = keyWord;
    }

    public KeyWord getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(String keyword2) {
        KeyWord keyWord = new KeyWord();
        keyWord.setValue(keyword2);
        this.keyword2 = keyWord;
    }

    public KeyWord getKeyword3() {
        return keyword3;
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

    public RemarkTpl getRemark() {
        return remark;
    }
}
