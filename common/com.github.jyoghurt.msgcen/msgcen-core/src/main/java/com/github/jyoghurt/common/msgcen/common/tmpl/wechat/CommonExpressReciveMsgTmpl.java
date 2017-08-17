package com.github.jyoghurt.common.msgcen.common.tmpl.wechat;

import com.github.jyoghurt.common.msgcen.common.enums.WechatTmplConfig;
import com.github.jyoghurt.common.msgcen.common.utils.WeChatTmplUtil;
import com.github.jyoghurt.wechatbasic.common.templet.BaseTpl;
import com.github.jyoghurt.wechatbasic.common.templet.FirstTpl;
import com.github.jyoghurt.wechatbasic.common.templet.KeyWord;
import com.github.jyoghurt.wechatbasic.common.templet.RemarkTpl;

/**
 * 模板ID_tWrkEHydf9PIOMZ5UOkvBaQ2HE7-IuBomAm3ou8ILU
 * 开发者调用模板消息接口时需提供模板ID
 * 标题快递代收通知
 * 行业IT科技 - 互联网|电子商务
 * 详细内容
 * {{first.DATA}}
 * 快递单号：{{keyword1.DATA}}
 * 时间：{{keyword2.DATA}}
 * {{remark.DATA}}
 * 在发送时，需要将内容中的参数（{{.DATA}}内为参数）赋值替换为需要的信息
 * 内容示例
 * 【驴鱼社区】已接收您的快递包裹
 * 快递单号：231564496786165465
 * 时间：2016年6月17日9:45
 * 请及时到店提取或登录系统预约配送。
 */
public class CommonExpressReciveMsgTmpl extends BaseTpl {

    public CommonExpressReciveMsgTmpl() {
        this.setTmplId(WeChatTmplUtil.getTmplId(WechatTmplConfig.CommonExpressReciveMsgTmpl));
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
     * 时间
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

    public void setRemark(String remark) {
        RemarkTpl remarkTpl = new RemarkTpl();
        remarkTpl.setValue(remark);
        this.remark = remarkTpl;
    }

    public RemarkTpl getRemark() {
        return remark;
    }
}

